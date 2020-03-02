package com.flexiv.sfino.model;

import java.io.Serializable;

public class Modal_Rep implements Serializable {

    private String TBL_REP;
    private String RepCode;
    private String RepName;
    private String Discode;
    private String Status;
    private String Password;
    private int Auth;
    private String DeviceIMI;

    public String getTBL_REP() {
        return TBL_REP;
    }

    public void setTBL_REP(String TBL_REP) {
        this.TBL_REP = TBL_REP;
    }

    public String getRepCode() {
        return RepCode;
    }

    public void setRepCode(String repCode) {
        RepCode = repCode;
    }

    public String getRepName() {
        return RepName;
    }

    public void setRepName(String repName) {
        RepName = repName;
    }

    public String getDiscode() {
        return Discode;
    }

    public void setDiscode(String discode) {
        Discode = discode;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public int getAuth() {
        return Auth;
    }

    public void setAuth(int auth) {
        Auth = auth;
    }

    public String getDeviceIMI() {
        return DeviceIMI;
    }

    public void setDeviceIMI(String deviceIMI) {
        DeviceIMI = deviceIMI;
    }


//    public void getRepDetails(String deviceIMI, String repCode, String password, Login context) {
//        this.Context = context;
//        DBHelper dbHelper = new DBHelper(Context);
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        try (Cursor cursor = db.query(DBQ._TBL_REP,
//                new String[]{DBQ._TBL_REP_RepCode, DBQ._TBL_REP_Auth, DBQ._TBL_REP_DeviceIMI, DBQ._TBL_REP_Discode, DBQ._TBL_REP_RepName},
//                DBQ._TBL_REP_RepCode + " = ? AND " + DBQ._TBL_REP_Password + " = ? AND " + DBQ._TBL_REP_Status + "=?",
//                new String[]{repCode, password, "A"}, null, null, null)) {
//            if (cursor.moveToNext()) {
//                this.Auth = cursor.getInt(cursor.getColumnIndex(DBQ._TBL_REP_Auth));
//
//                if (Auth != 1) {
//                    DeviceIMI = cursor.getString(cursor.getColumnIndex(DBQ._TBL_REP_DeviceIMI));
//                    if (!this.DeviceIMI.equals(deviceIMI)) {
//                        this.Status = "0";
//                        this.MSG = "Can not Sing in Using this Device.\nPlease Use the device provide by the Distributor";
//
//                    }
//                }
//                this.RepCode = cursor.getString(cursor.getColumnIndex(DBQ._TBL_REP_RepCode));
//                this.Discode = cursor.getString(cursor.getColumnIndex(DBQ._TBL_REP_Discode));
//                this.RepName = cursor.getString(cursor.getColumnIndex(DBQ._TBL_REP_RepName));
//                this.Status = "1";
//            } else {
//                this.Status = "0";
//                this.MSG = "Invalid RepCode Or Password";
//            }
//        }
//    }
//
//    public boolean getRepFromAPI(String repCode, SQLiteDatabase db){
//        String url = "http://";
//        System.out.println(url);
//
//        RequestQueue rq = Volley.newRequestQueue(Context);
//
//        JsonObjectRequest jr = new JsonObjectRequest(
//                Request.Method.GET, url, null,
//                response -> {
//                    System.out.println("Rest Response :" + response.toString());
//                    Gson gson = new Gson();
//                    Modal_Rep modal_rep = gson.fromJson(response.toString(),Modal_Rep.class);
//                    if(modal_rep.MSG.equals("valid")){
//                        this.MSG = modal_rep.MSG;
//                        this.Status = modal_rep.Status;
//                        this.RepName = modal_rep.RepName;
//                        this.Discode = modal_rep.Discode;
//                        this.RepCode = modal_rep.RepCode;
//                        this.DeviceIMI = modal_rep.DeviceIMI;
//                        this.Auth = modal_rep.Auth;
//                        this.Password = modal_rep.Password;
//                        SaveRepToDB(db);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        System.out.println("Rest Errr :" + error.toString());
//                    }
//                }
//        ) {
//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> params = new HashMap<>();
//                params.put("Content-Type", "application/json");
//                return params;
//            }
//        };
//
//        rq.add(jr);
//        return false;
//    }
//
//    private void SaveRepToDB(SQLiteDatabase db){
//
//        ContentValues c = new ContentValues();
//        c.put(DBQ._TBL_REP_Auth,this.Auth);
//        c.put(DBQ._TBL_REP_DeviceIMI,this.DeviceIMI);
//        c.put(DBQ._TBL_REP_Discode,this.Discode);
//        c.put(DBQ._TBL_REP_Password,this.Password);
//        c.put(DBQ._TBL_REP_RepCode,this.RepCode);
//        c.put(DBQ._TBL_REP_RepName,this.RepName);
//        c.put(DBQ._TBL_REP_Status,this.Status);
//        db.insert(DBQ._TBL_REP,null,c);
//    }
}
