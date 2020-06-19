package com.flexiv.sfino.utill;

public class DBQ {
    /*Rep Table*/
    public static final String CREATE_TBL_REP = "CREATE TABLE [TBL_REP] (\n" +
            "[RepCode] TEXT  NOT NULL PRIMARY KEY,\n" +
            "[RepID] NUMERIC  NOT NULL,\n" +
            "[RepName] TEXT  NOT NULL,\n" +
            "[Discode] TEXT  NOT NULL,\n" +
            "[DisName] TEXT  NOT NULL,\n" +
            "[Status] TEXT DEFAULT 'A' NOT NULL,\n" +
            "[Password] TEXT  NOT NULL,\n" +
            "[Auth] INTEGER DEFAULT '0' NOT NULL,\n" +
            "[DeviceIMI] TEXT  NULL\n" +
            ")";
    public static final String _TBL_REP = "TBL_REP";
    public static final String _TBL_REP_RepID = "RepID";
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


    /*Item Table*/
    public static final String CREATE_TBLM_ITEM = "CREATE TABLE [TBLM_ITEM] (" +
            "[ItemCode] TEXT NOT NULL PRIMARY KEY," +
            "[ItemDes] TEXT NOT NULL)";
    public static final String _TBLM_ITEM_ItemCode = "ItemCode";
    public static final String _TBLM_ITEM_ItemDes = "ItemDes";
    public static final String _TBLM_ITEM = "TBLM_ITEM";


    public static final String CREATE_TBLM_TBLM_BATCHWISESTOCK = "CREATE TABLE [TBLM_BATCHWISESTOCK] (\n" +
            "[DisCode] TEXT NOT NULL,\n" +
            "[ItemCode] TEXT NOT NULL,\n" +
            "[BatchNo] TEXT NOT NULL,\n" +
            "[SIH] REAL,\n" +
            "[RetialPrice] REAL,\n" +
            "PRIMARY KEY([DisCode],[ItemCode],[BatchNo])\n" +
            ");";
    public static final String _TBLM_BATCHWISESTOCK = "TBLM_BATCHWISESTOCK";
    public static final String _TBLM_BATCHWISESTOCK_DisCode = "DisCode";
    public static final String _TBLM_BATCHWISESTOCK_ItemCode = "ItemCode";
    public static final String _TBLM_BATCHWISESTOCK_BatchNo = "BatchNo";
    public static final String _TBLM_BATCHWISESTOCK_SIH = "SIH";
    public static final String _TBLM_BATCHWISESTOCK_RetialPrice = "RetialPrice";


    public static final String CREATE_TABLE_TBLT_ORDERHED = "CREATE TABLE [TBLT_ORDERHED] (\n" +
            "[DocNo] TEXT  NOT NULL,\n" +
            "[Discode] TEXT  NOT NULL,\n" +
            "[AreaCode] TEXT  NULL,\n" +
            "[LocCode] TEXT  NOT NULL,\n" +
            "[SalesDate] TEXT  NULL,\n" +
            "[CusCode] TEXT  NULL,\n" +
            "[RepCode] TEXT  NULL,\n" +
            "[RefNo] TEXT  NULL,\n" +
            "[GrossAmt] NUMERIC  NULL,\n" +
            "[NetAmt] NUMERIC  NULL,\n" +
            "[DisPer] NuMERIC  NULL,\n" +
            "[Discount] NUMERIC  NULL,\n" +
            "[VatAmt] numERIC  NULL,\n" +
            "[PayType] TEXT  NULL,\n" +
            "[CreateUser] TEXT  NULL,\n" +
            "[Status] TeXT  NULL,\n" +
            "[ISUSED] BOOLEAN  NULL,\n" +
            "[SYNC] BOOLEAN DEFAULT 'false' NULL,\n" +
            "PRIMARY KEY ([DocNo],[Discode],[LocCode])\n" +
            ")";
    public static final String _TBLT_ORDERHED = "TBLT_ORDERHED";
    public static final String _TBLT_ORDERHED_DocNo = "DocNo";
    public static final String _TBLT_ORDERHED_Discode = "Discode";
    public static final String _TBLT_ORDERHED_AreaCode = "AreaCode";
    public static final String _TBLT_ORDERHED_LocCode = "LocCode";
    public static final String _TBLT_ORDERHED_SalesDate = "SalesDate";
    public static final String _TBLT_ORDERHED_CusCode = "CusCode";
    public static final String _TBLT_ORDERHED_RepCode = "RepCode";
    public static final String _TBLT_ORDERHED_RefNo = "RefNo";
    public static final String _TBLT_ORDERHED_GrossAmt = "GrossAmt";
    public static final String _TBLT_ORDERHED_NetAmt = "NetAmt";
    public static final String _TBLT_ORDERHED_DisPer = "DisPer";
    public static final String _TBLT_ORDERHED_Discount = "Discount";
    public static final String _TBLT_ORDERHED_VatAmt = "VatAmt";
    public static final String _TBLT_ORDERHED_PayType = "PayType";
    public static final String _TBLT_ORDERHED_CreateUser = "CreateUser";
    public static final String _TBLT_ORDERHED_Status = "Status";
    public static final String _TBLT_ORDERHED_ISUSED = "ISUSED";
    public static final String _TBLT_ORDERHED_SYNC = "SYNC";

    public static final String CREATE_TABLE_TBLT_ORDDTL = "CREATE TABLE [TBLT_ORDDTL] (\n" +
            "[DocNo] TEXT  NULL,\n" +
            "[Discode] text  NULL,\n" +
            "[LocCode] text  NULL,\n" +
            "[ItemCode] text  NULL,\n" +
            "[ItQty] NUMERIC  NULL,\n" +
            "[UnitPrice] NUMERIC  NULL,\n" +
            "[DiscPer] numERIC  NULL,\n" +
            "[DiscAmt] numERIC  NULL,\n" +
            "[Amount] numERIC  NULL,\n" +
            "[CusCode] text  NULL,\n" +
            "[Date] text  NULL,\n" +
            "[RecordLine] INTEGER  NULL,\n" +
            "[UsedQty] NUMERIC  NULL,\n" +
            "[FQTY] numERIC  NULL,\n" +
            "[BATCH] text  NULL,\n" +
            "PRIMARY KEY ([DocNo],[Discode],[LocCode],[ItemCode],[RecordLine])\n" +
            ")";

    public static final String _TBLT_ORDDTL = "TBLT_ORDDTL";
    public static final String _TBLT_ORDDTL_DocNo = "DocNo";
    public static final String _TBLT_ORDDTL_Discode = "Discode";
    public static final String _TBLT_ORDDTL_LocCode = "LocCode";
    public static final String _TBLT_ORDDTL_ItemCode = "ItemCode";
    public static final String _TBLT_ORDDTL_ItQty = "ItQty";
    public static final String _TBLT_ORDDTL_UnitPrice = "UnitPrice";
    public static final String _TBLT_ORDDTL_DiscPer = "DiscPer";
    public static final String _TBLT_ORDDTL_DiscAmt = "DiscAmt";
    public static final String _TBLT_ORDDTL_Amount = "Amount";
    public static final String _TBLT_ORDDTL_CusCode = "CusCode";
    public static final String _TBLT_ORDDTL_Date = "Date";
    public static final String _TBLT_ORDDTL_RecordLine = "RecordLine";
    public static final String _TBLT_ORDDTL_UsedQty = "UsedQty";
    public static final String _TBLT_ORDDTL_FQTY = "FQTY";
    public static final String _TBLT_ORDDTL_BATCH = "BATCH";
}
