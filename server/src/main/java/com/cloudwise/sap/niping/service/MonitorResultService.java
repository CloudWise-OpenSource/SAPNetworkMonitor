package com.cloudwise.sap.niping.service;

import com.cloudwise.sap.niping.common.entity.Monitor;
import com.cloudwise.sap.niping.common.entity.MonitorNiPingResult;
import com.cloudwise.sap.niping.common.entity.Task;
import com.cloudwise.sap.niping.common.vo.Metrics;
import com.cloudwise.sap.niping.common.vo.Page;
import com.cloudwise.sap.niping.common.vo.RestfulReturnResult;
import com.cloudwise.sap.niping.dao.MonitorNiPingResultDao;
import com.cloudwise.sap.niping.exception.NiPingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.assertj.core.util.Lists;
import org.jvnet.hk2.annotations.Service;
import org.skife.jdbi.v2.exceptions.DBIException;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.cloudwise.sap.niping.common.constant.Result.DBError;
import static java.util.stream.Collectors.*;

@Service
@Slf4j
public class MonitorResultService {

    @Inject
    MonitorNiPingResultDao monitorNiPingResultDao;

    public List<MonitorNiPingResult> listByTaskId(String accountId, String taskId, Long startTime) throws NiPingException {
        if (null == startTime || startTime == 0) {
            try {
                List<MonitorNiPingResult> monitorNiPingResults = monitorNiPingResultDao.selectByTaskId(accountId, taskId,
                        MonitorNiPingResult.Type.PERFORMANCE.getValue(), ">");
                DecimalFormat formatter = new DecimalFormat("#0.000");
                if (CollectionUtils.isNotEmpty(monitorNiPingResults)) {
                    monitorNiPingResults = monitorNiPingResults.stream().map((item) -> {
                        item.setAv2String(formatter.format(item.getAv2()));
                        item.setTr2String(formatter.format(item.getTr2()));
                        item.setNoTime(true);
                        return item;
                    }).collect(Collectors.toList());
                }
                return monitorNiPingResults;
            } catch (DBIException e) {
                log.error("list result by accountId {} taskId {} startDate error: {}", accountId, taskId, startTime, ExceptionUtils
                        .getMessage(e));
                throw new NiPingException(DBError);
            }
        }

        List<MonitorNiPingResult> monitorNiPingResults = null;
        try {
            monitorNiPingResults = monitorNiPingResultDao.selectByTaskId(accountId, taskId, startTime, MonitorNiPingResult.Type.PERFORMANCE
                    .getValue(), ">=");
        } catch (DBIException e) {
            log.error("list result by accountId {} taskId {} startDate error: {}", accountId, taskId, startTime, ExceptionUtils
                    .getMessage(e));
            throw new NiPingException(DBError);
        }

        List<MonitorNiPingResult> results = null;
        if (CollectionUtils.isNotEmpty(monitorNiPingResults)) {
            results = Lists.newArrayList();
            Map<String, List<MonitorNiPingResult>> resultsMap = monitorNiPingResults.stream().collect(Collectors.groupingBy(
                    (item) -> {
                        return item.getTaskId() + "_" + item.getMonitorId();
                    },
                    Collectors.toList()
            ));
            DecimalFormat formatter = new DecimalFormat("#0.000");
            for (String resultTaskId : resultsMap.keySet()) {
                List<MonitorNiPingResult> value = resultsMap.get(resultTaskId);
                int size = value.size();
                double av2Sum = 0;
                double tr2Sum = 0;
                int noerrcount = 0;
                for (MonitorNiPingResult item : value) {
                    av2Sum += item.getAv2();
                    tr2Sum += item.getTr2();
                    if (0 == item.getErrno()) {
                        noerrcount++;
                    }
                }
                MonitorNiPingResult result = value.get(0);
                result.setAv2String(formatter.format(av2Sum / size));
                result.setTr2String(formatter.format(tr2Sum / size));
                result.setUsableString(formatter.format(Double.valueOf(noerrcount) / size * 100) + "%");
                results.add(result);
            }
        }

        return results;
    }

    public List<Task> listTasks(String accountId) throws NiPingException {
        try {
            return monitorNiPingResultDao.selectTasks(accountId);
        } catch (DBIException e) {
            log.error("list tasks by accountId {} error: {}", accountId, ExceptionUtils.getMessage(e));
            throw new NiPingException(DBError);
        }
    }

    public List<Monitor> listMonitors(String accountId, String taskId, String country, String province, String city) throws NiPingException {
        try {
            return monitorNiPingResultDao.selectMonitors(accountId, taskId, condition(country, province, city, null));
        } catch (DBIException e) {
            log.error("list monitors by accountId {} taskId {} country {} province {} city {} error: {}", accountId, taskId, country, province, city, ExceptionUtils.getMessage(e));
            throw new NiPingException(DBError);
        }
    }

    public List<String> listCountries(String accountId) throws NiPingException {
        try {
            return monitorNiPingResultDao.selectCountries(accountId);
        } catch (DBIException e) {
            log.error("list tasks by accountId {} error: {}", accountId, ExceptionUtils.getMessage(e));
            throw new NiPingException(DBError);
        }
    }

    public List<String> listProvinces(String accountId, String country) throws NiPingException {
        try {
            return monitorNiPingResultDao.selectProvinces(accountId, country);
        } catch (DBIException e) {
            log.error("list province accountId {} country {} error: {}", accountId, country, ExceptionUtils.getMessage(e));
            throw new NiPingException(DBError);
        }
    }

    public List<String> listCities(String accountId, String country, String province) throws NiPingException {
        try {
            return monitorNiPingResultDao.selectCities(accountId, country, province);
        } catch (DBIException e) {
            log.error("list city accountId {} country {} province {} error: {}", accountId, country, province, ExceptionUtils.getMessage
                    (e));
            throw new NiPingException(DBError);
        }
    }

    public String join(String country, String province, String city) {
        String join = "";
        if (StringUtils.isNotBlank(country) || StringUtils.isNotBlank(province) || StringUtils.isNotBlank(city)) {
            join = " INNER JOIN SNM_MONITOR M ON M.MONITOR_ID = M.MONITOR_ID ";
        }
        return join;
    }

    public String condition(String country, String province, String city, Integer type) {
        String condition = "";
        if (StringUtils.isNotBlank(country) || StringUtils.isNotBlank(province) || StringUtils.isNotBlank(city) || null != type) {
            StringBuilder conditionBuilder = new StringBuilder();
            if (StringUtils.isNotBlank(country)) {
                conditionBuilder.append(" AND M.COUNTRY = '" + country + "' ");
            }
            if (StringUtils.isNotBlank(province)) {
                conditionBuilder.append(" AND M.province = '" + province + "' ");
            }
            if (StringUtils.isNotBlank(city)) {
                conditionBuilder.append(" AND M.CITY = '" + city + "' ");
            }
            if (null != type) {
                conditionBuilder.append(" AND R.TYPE = '" + type + "' ");
            }
            condition = conditionBuilder.toString();
        }
        return condition;
    }

    public String page(long offset, long size) {
        return "LIMIT " + offset + "," + size;
    }

    public long time(long time) {
        return System.currentTimeMillis() - time * 60 * 1000;
    }

    public List<Metrics> listTRMetrics(String accountId, String taskId, String monitorId, long time) throws NiPingException {
        try {
            return monitorNiPingResultDao.selectTRMetrics(accountId, taskId, monitorId, MonitorNiPingResult.Type.PERFORMANCE.getValue(), time(time), ">=");
        } catch (DBIException e) {
            log.error("list tr metrics accountId {} taskId {} monitorId {} time {} error: {}",
                    accountId, taskId, monitorId, time, ExceptionUtils.getMessage(e));
            throw new NiPingException(DBError);
        }
    }

    public List<Metrics> listAVMetrics(String accountId, String taskId, String monitorId, long time) throws NiPingException {
        try {
            return monitorNiPingResultDao.selectAVMetrics(accountId, taskId, monitorId, MonitorNiPingResult.Type.PERFORMANCE.getValue(), time(time), ">=");
        } catch (DBIException e) {
            log.error("list tr metrics accountId {} taskId {} monitorId {} time {} error: {}",
                    accountId, taskId, monitorId, time, ExceptionUtils.getMessage(e));
            throw new NiPingException(DBError);
        }
    }

    public Optional<Map<Integer, Map<Boolean, Long>>> getUsable(String accountId, String taskId, String monitorId, long time) throws NiPingException {

        Map<Integer, Map<Boolean, Long>> map = null;
        List<MonitorNiPingResult> results = null;
        try {
            results = monitorNiPingResultDao.getMonitorNiPingResult(accountId, taskId, monitorId, time(time), ">=");
        } catch (DBIException e) {
            log.error("getUsable accountId {} taskId {} monitorId {} time {} error: {}",
                    accountId, taskId, monitorId, time, ExceptionUtils.getMessage(e));
            throw new NiPingException(DBError);
        }
        if (CollectionUtils.isNotEmpty(results)) {
            map = results.stream().collect(groupingBy(MonitorNiPingResult::getType, partitioningBy(MonitorNiPingResult::isUsable, counting())));
        }
        return Optional.ofNullable(map);
    }

    public Page<MonitorNiPingResult> list(String accountId, String taskId, String monitorId, long time, Integer type, Long pageNo, Long pageSize) throws NiPingException {
        Page<MonitorNiPingResult> page = new Page<>(pageNo, pageSize);
        try {
            if (pageNo != null || pageSize != null) {
                long totalCount = monitorNiPingResultDao.count(accountId, taskId, monitorId, time(time), condition(null, null, null, type), ">=");
                if (totalCount > 0) {
                    page.setTotalCount(totalCount);
                    page.setData(monitorNiPingResultDao.select(accountId, taskId, monitorId, time(time), condition(null, null, null, type), this.page(page.getOffset(), page.getPageSize()), ">="));
                }
            }
            else {
                page.setData(monitorNiPingResultDao.selectAll(accountId, taskId, monitorId, time(time), condition(null, null, null, type), ">="));
            }

        } catch (DBIException e) {
            log.error("list results accountId {} taskId {} monitorId {} time {} type {} pageNo {} pageSize" +
                            " {} error: {}",
                    accountId, taskId, monitorId, time, type, pageNo, pageSize, ExceptionUtils.getMessage(e));
            throw new NiPingException(DBError);
        }
        return page;
    }
}