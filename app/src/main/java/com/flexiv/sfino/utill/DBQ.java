package com.flexiv.sfino.utill;

public class DBQ {
    /*Rep Table*/
    public static final String CREATE_TBL_REP = "CREATE TABLE [TBL_REP] (\n" +
            "[RepCode] TEXT  NOT NULL PRIMARY KEY,\n" +
            "[RepName] TEXT  NOT NULL,\n" +
            "[Discode] TEXT  NOT NULL,\n" +
            "[DisName] TEXT  NOT NULL,\n" +
            "[Status] TEXT DEFAULT 'A' NOT NULL,\n" +
            "[Password] TEXT  NOT NULL,\n" +
            "[Auth] INTEGER DEFAULT '0' NOT NULL,\n" +
            "[DeviceIMI] TEXT  NULL\n" +
            ")";
    public static final String _TBL_REP = "TBL_REP";
    public static final String _TBL_REP_RepCode = "RepCode";
    public static final String _TBL_REP_RepName = "RepName";
    public static final String _TBL_REP_Discode = "Discode";
    public static final String _TBL_REP_DisName = "DisName";
    public static final String _TBL_REP_Status = "Status";
    public static final String _TBL_REP_Password = "Password";
    public static final String _TBL_REP_Auth = "Auth";
    public static final String _TBL_REP_DeviceIMI = "DeviceIMI";

    /*Area Table*/
    public static final String CREATE_TBLM_AREA = "CREATE TABLE [TBLM_AREA] (\n" +
            "[AreaCode] TEXT  NOT NULL PRIMARY KEY,\n" +
            "[AreaName] TEXT  NOT NULL,\n" +
            "[DisCode] TEXT  NOT NULL\n" +
            ")";
    public static final String _TBLM_AREA = "TBLM_AREA";
    public static final String _TBLM_AREA_AreaCode = "AreaCode";
    public static final String _TBLM_AREA_AreaName = "AreaName";
    public static final String _TBLM_AREA_DisCode = "DisCode";

    /*Customer Table*/
    public static final String CREATE_TBLM_CUSTOMER = "CREATE TABLE [TBLM_CUSTOMER] (\n" +
            "[CusCode] TEXT  NOT NULL,\n" +
            "[Discode] TEXT  NOT NULL,\n" +
            "[CusName] TEXT  NOT NULL,\n" +
            "[AreaCode] TEXT  NOT NULL,\n" +
            "[VAT] BOOLEAN  NOT NULL,\n" +
            "PRIMARY KEY ([CusCode],[Discode])\n" +
            ")";
    public static final String _TBLM_CUSTOMER = "TBLM_CUSTOMER";
    public static final String _TBLM_CUSTOMER_CusCode = "CusCode";
    public static final String _TBLM_CUSTOMER_Discode = "Discode";
    public static final String _TBLM_CUSTOMER_CusName = "CusName";
    public static final String _TBLM_CUSTOMER_AreaCode = "AreaCode";
    public static final String _TBLM_CUSTOMER_VAT = "VAT";


    /*Distributor Table*/

    public static final  String CREATE_TBLM_DISTRIBUTOR = "CREATE TABLE [TBLM_DISTRIBUTOR] (\n" +
            "[Discode] TEXT  NULL PRIMARY KEY,\n" +
            "[DisName] TEXT  NULL\n" +
            ")";
    public static final String _TBLM_DISTRIBUTOR_Discode = "Discode";
    public static final String _TBLM_DISTRIBUTOR_DisName = "DisName";
}
