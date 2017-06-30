package com.cloudwise.sap.niping.resource;

import com.cloudwise.sap.niping.common.utils.ShortUUID;
import com.cloudwise.sap.niping.common.vo.InstallInfo;
import com.cloudwise.sap.niping.exception.NiPingException;
import com.cloudwise.sap.niping.filter.NiPingAuthFilter;
import com.cloudwise.sap.niping.service.AuthService;
import com.cloudwise.sap.niping.view.InstallView;
import io.dropwizard.jersey.sessions.Session;
import liquibase.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.cloudwise.sap.niping.common.constant.Result.SUCCESS;

@Slf4j
@Path("/api/install")
@Consumes(MediaType.APPLICATION_JSON)
public class InstallResource {

    @Inject
    private AuthService authService;

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_HTML)
    public InstallView gotoInstall(@Context HttpServletRequest request, @Session HttpSession session) {
        String accountId = NiPingAuthFilter.getAccountId(session);
        try {
            InstallInfo installInfo = InstallInfo.builder()
                    .apiUrl(request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort())
                    .token(authService.getTokenByAccountId(accountId))
                    .build();

            return new InstallView(SUCCESS, installInfo);
        } catch (NiPingException e) {
            return new InstallView(e, null);
        }

    }

    @GET
    @Path("/download/{token}/{type}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response download(@PathParam("token") String token, @PathParam("type") String type, @Context HttpServletRequest request) {

        String downloadDirName = "monitor";
        File downloadDir = new File(downloadDirName);
        log.info("Download file path {}", downloadDir.getAbsolutePath());
        if (!downloadDir.exists()) {
            return Response.serverError().build();
        }

        String zipDirName = MD5Util.computeMD5(token + "_" + request.getServerName() + "_" + request.getServerPort() + "_" + type);
        String zipName = "sap-network-monitor.zip";
        File zip = new File(downloadDirName + File.separatorChar + zipDirName + File.separatorChar + zipName);
        File zipDir = new File(downloadDirName + File.separatorChar + zipDirName);
        SystemType systemType = null;

        if (!zip.exists()) {
            String tmpCompressDirPath = downloadDirName + File.separatorChar + token + "_" + type + "_" + ShortUUID.generate();
            File tmpZipDir = new File(tmpCompressDirPath);
            try {
                FileUtils.forceMkdir(tmpZipDir);
            } catch (IOException e) {
                log.error("download file error: cannot mkdir {} error: {}", tmpCompressDirPath, ExceptionUtils.getMessage(e));
                return Response.serverError().build();
            }

            File tmpZip = null;
            try {
                File originConfigFile = new File(downloadDirName + File.separatorChar + "config.ini");
                File configFile = new File(tmpCompressDirPath + File.separatorChar + "config.ini");
                FileUtils.copyFile(originConfigFile, configFile);

                String accessToken = "Zb3cVv0qzeNhYZwYbdC";
                String heartbeatServerUrl = "http://10.0.5.78:8080";
                String dataServerUrl = "http://10.0.5.78:8080";

                FileBasedConfigurationBuilder<INIConfiguration> builder = new FileBasedConfigurationBuilder<>(INIConfiguration.class);
                builder.configure(new FileBasedBuilderParametersImpl().setFile(configFile));
                INIConfiguration config = null;
                try {
                    config = builder.getConfiguration();
                } catch (ConfigurationException e) {
                    log.error("download fail to parse config file error: {}", ExceptionUtils.getMessage(e));
                    return Response.serverError().build();
                }

                accessToken = config.getString("monitorInfo.accessToken");
                heartbeatServerUrl = config.getString("serverInfo.heartbeatServerUrl");
                dataServerUrl = config.getString("serverInfo.dataServerUrl");
                log.info("parse config ini file get accessToken {}, heartbeatServerUrl {}, dataServerUrl {}", accessToken, heartbeatServerUrl, dataServerUrl);

                FileUtils.writeStringToFile(configFile, FileUtils.readFileToString(configFile, StandardCharsets.UTF_8)
                                .replaceAll(accessToken, token)
                                .replaceAll(heartbeatServerUrl, request.getScheme() + "://" + request.getServerName() + ":" + request
                                        .getServerPort())
                                .replaceAll(dataServerUrl, request.getScheme() + "://" + request.getServerName() + ":" + request
                                        .getServerPort()),
                        StandardCharsets.UTF_8, false);

                tmpZip = new File(tmpCompressDirPath + File.separatorChar + zipName);
                ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(tmpZip));

                systemType = SystemType.valueOf(type);
                File monitorFile = new File(downloadDirName + File.separatorChar + systemType.getName());
                zipOut.putNextEntry(new ZipEntry(configFile.getName()));
                IOUtils.write(FileUtils.readFileToByteArray(configFile), zipOut);

                zipOut.putNextEntry(new ZipEntry(monitorFile.getName()));
                IOUtils.write(FileUtils.readFileToByteArray(monitorFile), zipOut);
                IOUtils.closeQuietly(zipOut);

                FileUtils.moveDirectory(tmpZipDir, zipDir);

            } catch (IOException e) {
                log.error("download file error: generate zip file {}", ExceptionUtils.getMessage(e));
                return Response.serverError().build();
            }
        }

        InputStream is = null;

        if (zip.exists()) {
            try {
                is = new FileInputStream(zip);
            } catch (FileNotFoundException e) {
                log.error("download file error: {}", ExceptionUtils.getMessage(e));
                return Response.serverError().build();
            }
        }

        String systemTypeString = "";
        if (null != systemType) {
            systemTypeString = "-" + systemType.name();
        }

        return Response.ok(is)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"sap-network-monitor" + systemTypeString + ".zip\"")
                .build();
    }

    enum SystemType {
        linux32 {
            @Override
            public String getName() {
                return "sapmonitor32";
            }
        }, linux64 {
            @Override
            public String getName() {
                return "sapmonitor";
            }
        }, win32 {
            @Override
            public String getName() {
                return "sapmonitor32.exe";
            }
        }, win64 {
            @Override
            public String getName() {
                return "sapmonitor.exe";
            }
        };

        public abstract String getName();
    }
}