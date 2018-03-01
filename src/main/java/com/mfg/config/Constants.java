package com.mfg.config;

/**
 * Created by I309908 on 1/16/2017.
 */
public class Constants {
    public static final String codeDebt = "CodeDebt";
    public static final String techIssue = "TechIssue";
    public static final String BCPInternalUrlBase = "https://support.wdf.sap.corp/sap/bc/devdb/internal_incid?sap-client=001";
    public static final String BCPCustomerUrlBase = "https://support.wdf.sap.corp/sap/bc/devdb/customer_incid?sap-client=001";

    public static final String BCP_INTERNAL = "internal";
    public static final String BCP_CUSTOMER = "customer";
    public static final String BCP_INTERBAL_ACC = "internal-acc";

    // certificate path for Https request on local machine
//    public static final String p12File_Path = "C:\\Program Files\\Java\\jdk1.8.0_66\\jre\\lib\\security\\I072223.pfx";
    //certificate path for Https request on server
    public static final String p12File_Path = "C:\\Program Files\\Java\\jdk1.8.0_111\\jre\\lib\\security\\I072223.pfx";
    public static final String p12Password = "changeit";

    public static final String USER_SESSION_KEY = "USER_SESSION";

    public static final String default_cron = "00 00 07 * * ?";
    public static final String default_cron_prefix = "00 ";
    public static final String default_cron_suffix = " * * ?";

    public static final String STATUS_SUCCESSFUL = "Success";
    public static final String STATUS_FAILED = "Fail";

    public static final String DATA_SYNCHRONIZATION_SERVICE = "DATA_SYNCHRONIZATION_SERVICE";
    public static final String DASHBOARD_ID_UPPERCASE = "DASHBOARD_ID";

    public static final String LOGGER_NAME_PREFIX = "DashboardId@";
    public static final String LOGGER_NAME_SUFFIX = "-Version@";

    public static final String SCHEDULER_NAME_PREFIX = "scheduledJob-";
    public static final String TRIGGER_NAME_PREFIX = "trigger-";
    public static final String SCHEDULER_SERVICE_GROUP = "ASE";

    public static final String JOB_THREAD_NAME_PREFIX=  "Dashboard-job@";



}
