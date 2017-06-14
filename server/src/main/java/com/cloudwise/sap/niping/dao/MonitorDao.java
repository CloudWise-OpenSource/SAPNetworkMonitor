package com.cloudwise.sap.niping.dao;

import com.cloudwise.sap.niping.common.entity.Monitor;
import org.jvnet.hk2.annotations.Service;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

import java.util.Date;
import java.util.List;

@Service
@UseStringTemplate3StatementLocator
public interface MonitorDao {
    @SqlQuery("SELECT COUNT(1) FROM SNM_MONITOR WHERE MONITOR_ID = :monitorId")
    int countMonitor(@Bind("monitorId") String monitorId);

    @SqlUpdate("INSERT INTO SNM_MONITOR (MONITOR_ID, ACCOUNT_ID, NAME, COUNTRY, PROVINCE, CITY, ISP, AREA, IP, NIPING_T, STATUS, CREATION_TIME, MODIFIED_TIME) " +
            " VALUES (:monitorId, :accountId, :name, :country, :province, :city, :isp, :area, :ip, :nipingT, :status, :creationTime, :modifiedTime)")
    void insertMonitor(@BindBean Monitor monitor);

    @SqlUpdate("UPDATE SNM_MONITOR SET STATUS = :status, MODIFIED_TIME = :modifiedTime")
    void updateAllMonitorsStatus(@Bind("status") int status, @Bind("modifiedTime") Date modifiedTime);

    @SqlUpdate("UPDATE SNM_MONITOR SET STATUS = :status, MODIFIED_TIME = :modifiedTime WHERE MONITOR_ID IN (<monitorIds>)")
    void updateMonitorsStatus(@BindIn("monitorIds") List<String> monitorIds, @Bind("status") int status, @Bind("modifiedTime") Date modifiedTime);

    @SqlUpdate("UPDATE SNM_MONITOR SET ACCOUNT_ID = :accountId, NAME = :name, COUNTRY = :country, PROVINCE = :province, CITY = :city, ISP = :isp, AREA = :area, " +
            " IP = :ip, NIPING_T = :nipingT, MODIFIED_TIME = :modifiedTime WHERE MONITOR_ID = :monitorId")
    void updateMonitorNiping(@BindBean Monitor monitor);
}
