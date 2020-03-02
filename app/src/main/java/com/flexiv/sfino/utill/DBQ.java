package com.flexiv.sfino.utill;

public class DBQ {
    public static final String CREATE_TBL_REP = "CREATE TABLE [TBL_REP] (\n" +
            "[RepCode] TEXT  NOT NULL PRIMARY KEY,\n" +
            "[RepName] TEXT  NOT NULL,\n" +
            "[Discode] TEXT  NOT NULL,\n" +
            "[Status] TEXT DEFAULT 'A' NOT NULL,\n" +
            "[Password] TEXT  NOT NULL,\n" +
            "[Auth] INTEGER DEFAULT '0' NOT NULL,\n" +
            "[DeviceIMI] TEXT  NULL\n" +
            ")";
    public static final String _TBL_REP = "TBL_REP";
    public static final String _TBL_REP_RepCode = "RepCode";
    public static final String _TBL_REP_RepName = "RepName";
    public static final String _TBL_REP_Discode = "Discode";
    public static final String _TBL_REP_Status = "Status";
    public static final String _TBL_REP_Password = "Password";
    public static final String _TBL_REP_Auth = "Auth";
    public static final String _TBL_REP_DeviceIMI = "DeviceIMI";
}
