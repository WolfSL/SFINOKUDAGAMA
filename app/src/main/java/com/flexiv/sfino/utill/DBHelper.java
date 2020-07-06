package com.flexiv.sfino.utill;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.flexiv.sfino.model.Area_Modal;
import com.flexiv.sfino.model.Card_cus_area;
import com.flexiv.sfino.model.Customer_Modal;
import com.flexiv.sfino.model.MasterDataModal;
import com.flexiv.sfino.model.Modal_Batch;
import com.flexiv.sfino.model.Modal_Item;
import com.flexiv.sfino.model.Modal_RepStock;
import com.flexiv.sfino.model.TBLT_ORDDTL;
import com.flexiv.sfino.model.TBLT_ORDERHED;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "SFINO.db";
    private static final int DATABASE_VERSION = 51;


    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public synchronized void close() {
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBQ.CREATE_TBL_REP);
        db.execSQL(DBQ.CREATE_TBLM_AREA);
        db.execSQL(DBQ.CREATE_TBLM_CUSTOMER);
        db.execSQL(DBQ.CREATE_TBLM_ITEM);
        db.execSQL(DBQ.CREATE_TBLM_TBLM_BATCHWISESTOCK);
        db.execSQL(DBQ.CREATE_TABLE_TBLT_ORDERHED);
        db.execSQL(DBQ.CREATE_TABLE_TBLT_ORDDTL);
        db.execSQL(DBQ.CREATE_TABLE_TBLM_REPSTOCK);
        db.execSQL(DBQ.CREATE_TBLT_SALINVDET);
        db.execSQL(DBQ.CREATE_TBLT_SALINVHED);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String DELETE = "drop table if exists ";
        db.execSQL(DELETE + DBQ._TBL_REP);
        db.execSQL(DELETE + DBQ._TBLM_AREA);
        db.execSQL(DELETE + DBQ._TBLM_CUSTOMER);
        db.execSQL(DELETE + DBQ._TBLM_ITEM);
        db.execSQL(DELETE + DBQ._TBLM_BATCHWISESTOCK);
        db.execSQL(DELETE + DBQ._TBLT_ORDERHED);
        db.execSQL(DELETE + DBQ._TBLT_ORDDTL);
        db.execSQL(DELETE + DBQ._TBLM_REPSTOCK);
        db.execSQL(DELETE + DBQ._TBLT_SALINVHED);
        db.execSQL(DELETE + DBQ._TBLT_SALINVDET);

        onCreate(db);
    }


    /*
     * Master Data Insertion
     */
    public boolean insertMasterData(MasterDataModal data) {
        SQLiteDatabase db = null;
        db = this.getWritableDatabase();
        db.beginTransaction();
        try {

//            db.delete(DBQ._TBLM_REPSTOCK,null,null);
//            db.delete(DBQ._TBLM_AREA,null,null);
//            db.delete(DBQ._TBLM_ITEM,null,null);
//            db.delete(DBQ._TBLM_CUSTOMER,null,null);
//            db.delete(DBQ._TBLM_BATCHWISESTOCK,null,null);


            ContentValues c = new ContentValues();

            //Insert Area
            for (Area_Modal area : data.getAreaList()) {

                c.put(DBQ._TBLM_AREA_AreaCode, area.getAreaCode());
                c.put(DBQ._TBLM_AREA_AreaName, area.getAreaName());
                c.put(DBQ._TBLM_AREA_DisCode, area.getDisCode());

                db.replace(DBQ._TBLM_AREA, null, c);
                c.clear();
            }

            //Insert Customer
            for (Customer_Modal cus : data.getCustomerList()) {

                c.put(DBQ._TBLM_CUSTOMER_AreaCode, cus.getAreaCode());
                c.put(DBQ._TBLM_CUSTOMER_CusCode, cus.getCusCode());
                c.put(DBQ._TBLM_CUSTOMER_CusName, cus.getCusName());
                c.put(DBQ._TBLM_CUSTOMER_Discode, cus.getDiscode());
                c.put(DBQ._TBLM_CUSTOMER_VAT, cus.getVAT());

                db.replace(DBQ._TBLM_CUSTOMER, null, c);
                c.clear();
            }

            for (Modal_Item item : data.getModal_Items()) {
                c.put(DBQ._TBLM_ITEM_ItemCode, item.getItemCode());
                c.put(DBQ._TBLM_ITEM_ItemDes, item.getDesc());

                db.replace(DBQ._TBLM_ITEM, null, c);
                c.clear();
            }

            for (Modal_Batch batch : data.getModal_Batches_Stock()) {
                c.put(DBQ._TBLM_BATCHWISESTOCK_DisCode, batch.getDisCode());
                c.put(DBQ._TBLM_BATCHWISESTOCK_BatchNo, batch.getBatchNo());
                c.put(DBQ._TBLM_BATCHWISESTOCK_ItemCode, batch.getItemCode());
                c.put(DBQ._TBLM_BATCHWISESTOCK_RetialPrice, batch.getRetialPrice());
                c.put(DBQ._TBLM_BATCHWISESTOCK_SIH, batch.getSHI());
                db.replace(DBQ._TBLM_BATCHWISESTOCK, null, c);
                c.clear();
            }

            if(data.getModal_Rep_Stock()!=null) {
                for (Modal_RepStock batch : data.getModal_Rep_Stock()) {
                    c.put(DBQ._TBLM_REPSTOCK_DisCode, batch.getDisCode());
                    c.put(DBQ._TBLM_REPSTOCK_BatchNo, batch.getBatchNo());
                    c.put(DBQ._TBLM_REPSTOCK_ItemCode, batch.getItemCode());
                    c.put(DBQ._TBLM_REPSTOCK_RetialPrice, batch.getRetialPrice());
                    c.put(DBQ._TBLM_REPSTOCK_FreeQty, batch.getFreeQty());
                    c.put(DBQ._TBLM_REPSTOCK_RepCode, batch.getRepCode());
                    c.put(DBQ._TBLM_REPSTOCK_Status, batch.getStatus());
                    c.put(DBQ._TBLM_REPSTOCK_SIH, batch.getSIH());
                    db.replace(DBQ._TBLM_REPSTOCK, null, c);
                    c.clear();
                }
            }

            if (data.getDefModal() != null) {

                c.put(DBQ._TBLT_ORDERHED_VatAmt, data.getDefModal().getVatAmt());
                c.put(DBQ._TBLT_ORDERHED_Status, data.getDefModal().getStatus());
                c.put(DBQ._TBLT_ORDERHED_SalesDate, data.getDefModal().getSalesDate());
                c.put(DBQ._TBLT_ORDERHED_RefNo, data.getDefModal().getRefNo());
                c.put(DBQ._TBLT_ORDERHED_PayType, data.getDefModal().getPayType());
                c.put(DBQ._TBLT_ORDERHED_NetAmt, data.getDefModal().getNetAmt());
                c.put(DBQ._TBLT_ORDERHED_LocCode, data.getDefModal().getLocCode());
                c.put(DBQ._TBLT_ORDERHED_ISUSED, data.getDefModal().isISUSED());
                c.put(DBQ._TBLT_ORDERHED_GrossAmt, data.getDefModal().getGrossAmt());
                c.put(DBQ._TBLT_ORDERHED_DocNo, data.getDefModal().getDocNo());
                c.put(DBQ._TBLT_ORDERHED_DisPer, data.getDefModal().getDisPer());
                c.put(DBQ._TBLT_ORDERHED_Discount, data.getDefModal().getDiscount());
                c.put(DBQ._TBLT_ORDERHED_Discode, data.getDefModal().getDiscode());
                c.put(DBQ._TBLT_ORDERHED_CusCode, data.getDefModal().getCusCode());
                c.put(DBQ._TBLT_ORDERHED_CreateUser, data.getDefModal().getCreateUser());
                c.put(DBQ._TBLT_ORDERHED_AreaCode, data.getDefModal().getAreaCode());
                c.put(DBQ._TBLT_ORDERHED_RepCode, data.getDefModal().getRepCode());
                c.put(DBQ._TBLT_ORDERHED_SYNC, true);

                db.replace(DBQ._TBLT_ORDERHED, null, c);
            }

            db.setTransactionSuccessful();
            db.endTransaction();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return true;

    }

    public ArrayList<Card_cus_area> getAreas(String discode) {
        SQLiteDatabase db = null;
        ArrayList<Card_cus_area> area_modals = null;
        try {

            db = this.getReadableDatabase();
            Cursor c = db.query(DBQ._TBLM_AREA, new String[]{DBQ._TBLM_AREA_AreaCode, DBQ._TBLM_AREA_AreaName}, DBQ._TBLM_AREA_DisCode + " = ?"
                    , new String[]{discode}, null, null, null);

            area_modals = new ArrayList<>();
            Card_cus_area area;
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                area = new Card_cus_area(c.getString(c.getColumnIndex(DBQ._TBLM_AREA_AreaCode)),
                        c.getString(c.getColumnIndex(DBQ._TBLM_AREA_AreaName)));
                area_modals.add(area);
            }
            return area_modals;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;

        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public ArrayList<Card_cus_area> getCustomers(String discode, String areacode) throws Exception {
        SQLiteDatabase db = null;
        ArrayList<Card_cus_area> area_modals = null;
        try {

            System.out.println(discode + ", " + areacode);
            db = this.getReadableDatabase();
            Cursor c = db.query(DBQ._TBLM_CUSTOMER, new String[]{DBQ._TBLM_CUSTOMER_CusCode, DBQ._TBLM_CUSTOMER_CusName}, DBQ._TBLM_CUSTOMER_Discode + " = ? and " + DBQ._TBLM_CUSTOMER_AreaCode + " = ?"
                    , new String[]{discode, areacode}, null, null, null);

            area_modals = new ArrayList<>();
            Card_cus_area area;
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                area = new Card_cus_area(c.getString(c.getColumnIndex(DBQ._TBLM_CUSTOMER_CusCode)),
                        c.getString(c.getColumnIndex(DBQ._TBLM_CUSTOMER_CusName)));
                area_modals.add(area);
            }
            c.close();
            return area_modals;

        } catch (Exception ex) {
            throw ex;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public ArrayList<Modal_Item> getItems(String discode,String repcode) throws Exception {
        SQLiteDatabase db = null;
        ArrayList<Modal_Item> item_modals = null;
        try {

            db = this.getReadableDatabase();
            String sql = "select i.ItemCode,i.ItemDes,s.sih from "+DBQ._TBLM_ITEM+" as i left outer join (select ItemCode,sum(SIH) as SIH from TBLM_REPSTOCK where DisCode = '"+discode+"' and RepCode = '"+repcode+"'  group by ItemCode) as s on i.ItemCode = s.ItemCode where s.sih is not null";
            Cursor c = db.rawQuery(sql,null);

            item_modals = new ArrayList<>();
            Modal_Item item;
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                item = new Modal_Item(c.getString(c.getColumnIndex(DBQ._TBLM_ITEM_ItemCode)), c.getString(c.getColumnIndex(DBQ._TBLM_ITEM_ItemDes))
                         , SharedPreference.ds_formatter.format(c.getDouble(2)));
                item_modals.add(item);
            }
            c.close();
            return item_modals;

        } catch (Exception ex) {
            throw ex;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
    public ArrayList<Modal_Item> getItems() throws Exception {
        SQLiteDatabase db = null;
        ArrayList<Modal_Item> item_modals = null;
        try {

            db = this.getReadableDatabase();
           // String sql = "select i.*,s.sih from TBLM_ITEMMASTER as i left outer join (select ItemCode,sum(SIH) as SIH from TBLM_REPSTOCK where DisCode = '"+discode+"' and RepCode = '"+repcode+"'  group by ItemCode) as s on i.ItemCode = s.ItemCode where s.sih is not null order by s.SIH";
            Cursor c = db.query(DBQ._TBLM_ITEM, new String[]{DBQ._TBLM_ITEM_ItemCode, DBQ._TBLM_ITEM_ItemDes}, null
                    , null, null, null, null);

            item_modals = new ArrayList<>();
            Modal_Item item;
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                item = new Modal_Item(c.getString(c.getColumnIndex(DBQ._TBLM_ITEM_ItemCode)), c.getString(c.getColumnIndex(DBQ._TBLM_ITEM_ItemDes)),"");
                item_modals.add(item);
            }
            c.close();
            return item_modals;

        } catch (Exception ex) {
            throw ex;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public ArrayList<Modal_Batch> getBatchWiceStock(String disCode, String itemcode) throws Exception {
        SQLiteDatabase db = null;
        ArrayList<Modal_Batch> item_modals = null;
        try {

            db = this.getReadableDatabase();
            Cursor c = db.rawQuery("SELECT st.*, item.ItemDes FROM TBLM_BATCHWISESTOCK as st left join " + DBQ._TBLM_ITEM + " as item on st.ItemCode = item.ItemCode where st.DisCode = '" + disCode + "' and st.ItemCode = '" + itemcode + "'",
                    null);

            item_modals = new ArrayList<>();
            Modal_Batch batch;
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                batch = new Modal_Batch();
                batch.setDesc(c.getString(c.getColumnIndex("ItemDes")));
                batch.setItemCode(c.getString(c.getColumnIndex(DBQ._TBLM_BATCHWISESTOCK_ItemCode)));
                batch.setBatchNo(c.getString(c.getColumnIndex(DBQ._TBLM_BATCHWISESTOCK_BatchNo)));
                batch.setRetialPrice(c.getDouble(c.getColumnIndex(DBQ._TBLM_BATCHWISESTOCK_RetialPrice)));
                batch.setSHI(c.getDouble(c.getColumnIndex(DBQ._TBLM_BATCHWISESTOCK_SIH)));
                item_modals.add(batch);
            }
            c.close();
            return item_modals;

        } catch (Exception ex) {
            throw ex;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
    public ArrayList<Modal_Batch> getRepStock(String disCode, String itemcode,String repcode) throws Exception {
        SQLiteDatabase db = null;
        ArrayList<Modal_Batch> item_modals = null;
        try {

            db = this.getReadableDatabase();
            String sql = "SELECT st.*, item.ItemDes FROM TBLM_REPSTOCK as st left join " + DBQ._TBLM_ITEM + " as item on st.ItemCode = item.ItemCode where st.DisCode = '" + disCode + "' and st.ItemCode = '" + itemcode + "' and st.RepCode = '"+repcode+"'";

            System.out.println(sql);
            Cursor c = db.rawQuery(sql,
                    null);

            item_modals = new ArrayList<>();
            Modal_Batch batch;
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                batch = new Modal_Batch();
                batch.setDesc(c.getString(c.getColumnIndex("ItemDes")));
                batch.setItemCode(c.getString(c.getColumnIndex(DBQ._TBLM_BATCHWISESTOCK_ItemCode)));
                batch.setBatchNo(c.getString(c.getColumnIndex(DBQ._TBLM_BATCHWISESTOCK_BatchNo)));
                batch.setRetialPrice(c.getDouble(c.getColumnIndex(DBQ._TBLM_BATCHWISESTOCK_RetialPrice)));
                batch.setSHI(c.getDouble(c.getColumnIndex(DBQ._TBLM_BATCHWISESTOCK_SIH)));
                item_modals.add(batch);
            }
            c.close();
            return item_modals;

        } catch (Exception ex) {
            throw ex;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }


    public TBLT_ORDERHED getOrder(String refNo, String poa){
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor c = db.rawQuery("SELECT * FROM " + DBQ._TBLT_ORDERHED + " WHERE " + DBQ._TBLT_ORDERHED_RefNo + " = ? AND "+DBQ._TBLT_ORDERHED_Status + " = ?", new String[]{String.valueOf(refNo),String.valueOf(poa)});

            TBLT_ORDERHED hed = new TBLT_ORDERHED();
            if (c.moveToNext()) {
                hed.setRepCode(c.getString(c.getColumnIndex(DBQ._TBLT_ORDERHED_RepCode)));
                hed.setPayType(c.getString(c.getColumnIndex(DBQ._TBLT_ORDERHED_PayType)));
                hed.setLocCode(c.getString(c.getColumnIndex(DBQ._TBLT_ORDERHED_LocCode)));
                hed.setISUSED(c.getInt(c.getColumnIndex(DBQ._TBLT_ORDERHED_ISUSED)) > 0);
                hed.setNetAmt(c.getDouble(c.getColumnIndex(DBQ._TBLT_ORDERHED_NetAmt)));
                hed.setGrossAmt(c.getDouble(c.getColumnIndex(DBQ._TBLT_ORDERHED_GrossAmt)));
                hed.setDocNo(c.getString(c.getColumnIndex(DBQ._TBLT_ORDERHED_DocNo)));
                hed.setDisPer(c.getDouble(c.getColumnIndex(DBQ._TBLT_ORDERHED_DisPer)));
                hed.setDiscount(c.getDouble(c.getColumnIndex(DBQ._TBLT_ORDERHED_Discount)));
                hed.setDiscode(c.getString(c.getColumnIndex(DBQ._TBLT_ORDERHED_Discode)));
                hed.setCusCode(c.getString(c.getColumnIndex(DBQ._TBLT_ORDERHED_CusCode)));
                hed.setCreateUser(c.getString(c.getColumnIndex(DBQ._TBLT_ORDERHED_CreateUser)));
                hed.setAreaCode(c.getString(c.getColumnIndex(DBQ._TBLT_ORDERHED_AreaCode)));
                hed.setRefNo(refNo);
                hed.setSalesDate(c.getString(c.getColumnIndex(DBQ._TBLT_ORDERHED_SalesDate)));
                hed.setStatus(c.getString(c.getColumnIndex(DBQ._TBLT_ORDERHED_Status)));
                hed.setVatAmt(c.getDouble(c.getColumnIndex(DBQ._TBLT_ORDERHED_VatAmt)));

                c.close();

                List<TBLT_ORDDTL> items = new ArrayList<>();

                c = db.rawQuery("SELECT * FROM " + DBQ._TBLT_ORDDTL + " WHERE " + DBQ._TBLT_ORDDTL_Discode + " = ? " +
                        "AND " + DBQ._TBLT_ORDDTL_DocNo + " = ?", new String[]{String.valueOf(hed.getDiscode()), String.valueOf(hed.getDocNo())});
                TBLT_ORDDTL item;
                while (c.moveToNext()) {
                    item = new TBLT_ORDDTL();

                    item.setAmount(c.getDouble(c.getColumnIndex(DBQ._TBLT_ORDDTL_Amount)));
                    item.setRecordLine(c.getInt(c.getColumnIndex(DBQ._TBLT_ORDDTL_RecordLine)));
                    item.setFQTY(c.getDouble(c.getColumnIndex(DBQ._TBLT_ORDDTL_FQTY)));
                    item.setCusCode(c.getString(c.getColumnIndex(DBQ._TBLT_ORDDTL_CusCode)));
                    item.setDate(c.getString(c.getColumnIndex(DBQ._TBLT_ORDDTL_Date)));
                    item.setUsedQty(c.getDouble(c.getColumnIndex(DBQ._TBLT_ORDDTL_UsedQty)));
                    item.setItQty(c.getDouble(c.getColumnIndex(DBQ._TBLT_ORDDTL_ItQty)));
                    item.setDiscAmt(c.getDouble(c.getColumnIndex(DBQ._TBLT_ORDDTL_DiscAmt)));
                    item.setDiscPer(c.getDouble(c.getColumnIndex(DBQ._TBLT_ORDDTL_DiscPer)));
                    item.setUnitPrice(c.getDouble(c.getColumnIndex(DBQ._TBLT_ORDDTL_UnitPrice)));
                    item.setBATCH(c.getString(c.getColumnIndex(DBQ._TBLT_ORDDTL_BATCH)));
                    item.setItemCode(c.getString(c.getColumnIndex(DBQ._TBLT_ORDDTL_ItemCode)));

                    items.add(item);
                }

                hed.setItemList(items);
                return hed;
            }


        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}
