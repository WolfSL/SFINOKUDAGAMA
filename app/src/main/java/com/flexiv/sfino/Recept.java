package com.flexiv.sfino;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.flexiv.sfino.adapter.Adapter_Cus_area;
import com.flexiv.sfino.adapter.Adapter_INV_list_payment;
import com.flexiv.sfino.adapter.Adapter_OUTCUS_list;
import com.flexiv.sfino.model.Card_cus_area;
import com.flexiv.sfino.model.DefModal;
import com.flexiv.sfino.model.DefModalList;
import com.flexiv.sfino.model.TBLT_CUSRECEIPTDTL;
import com.flexiv.sfino.model.TBLT_CUSRECEIPTHED;
import com.flexiv.sfino.model.TBLT_SALINVDET;
import com.flexiv.sfino.model.TBLT_SALINVHED;
import com.flexiv.sfino.utill.DBHelper;
import com.flexiv.sfino.utill.PrinterCommands;
import com.flexiv.sfino.utill.SharedPreference;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Recept extends AppCompatActivity {

    String TAG = "RECEIPT";
    //Components
    ImageButton imageButton_done;
    ImageButton imageButton_back;

    TextView textView_cusName;
    TextView textView_Area;
    TextView textView_Bank;
    private TextView textView_InvNo;


    private RecyclerView cusRecyclerView;
    private Adapter_Cus_area cusAdapter;
    private RecyclerView.LayoutManager cusLayoutManager;
    private TextView itemSelectTitle, textView_INVRefNo, textView_dueAmt, textView_netAmt, textView_bal;
    private TextView dateText;
    private Spinner bankSpinner;
    final Calendar myCalendar = Calendar.getInstance();

    private EditText editTextGiven, editTextPaid, editTextChNo;

    private DefModal cus;
    public static DefModal curInv;

    private int paytype = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreference.COM_BANK = null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recept);

        editTextGiven = findViewById(R.id.editTextNumberDecimal);
        editTextPaid = findViewById(R.id.editTextPaid);
        editTextChNo = findViewById(R.id.editTextTextPersonName);

        initRecview();

        imageButton_done = findViewById(R.id.imageButton_done);
        TextView tv = findViewById(R.id.textViewPS2);
        if (MainMenu.mBluetoothSocket != null)
            if (MainMenu.mBluetoothSocket.isConnected()) {

                tv.setText("Connected");
            }

        imageButton_done.setOnClickListener(view -> {
            if (tv.getText().equals("Connected")) {
                try {
                    createAndPostReceipt(view);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else
                Toast.makeText(Recept.this, "Please Connet the printer", Toast.LENGTH_LONG).show();
        });

        editTextPaid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    double du = Double.parseDouble(textView_dueAmt.getText().toString());
                    double paid = Double.parseDouble(s.toString());

                    if (du < paid) {
                        editTextPaid.getText().clear();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextGiven.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (dtl.size() == 0) {
                    if (s.length() > 0) {
                        textView_bal.setText(s.toString());
                    } else {
                        textView_bal.setText("");
                    }
                } else {
                    if (s.length() > 0) {
                        double paid = 0;
                        double giv = Double.parseDouble(s.toString());
                        for (TBLT_CUSRECEIPTDTL i : dtl) {
                            paid = paid + i.getPaidAmt();
                        }
                        textView_bal.setText(SharedPreference.df.format(giv - paid));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Intent intent = getIntent();
        cus = (DefModal) intent.getSerializableExtra("hed");
        if (cus != null) {
            textView_cusName = findViewById(R.id.textView_cusName);
            textView_cusName.setText(cus.getVal2());
        }

        textView_Bank = findViewById(R.id.textView_Bank);
        textView_bal = findViewById(R.id.textView_bal);
        textView_dueAmt = findViewById(R.id.textView_dueAmt);
        textView_netAmt = findViewById(R.id.textView_netAmt);

        bankSpinner = findViewById(R.id.spinner2);

        textView_INVRefNo = findViewById(R.id.textView_INVRefNo);

        dateText = findViewById(R.id.textViewDate);
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };
        dateText.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            new DatePickerDialog(Recept.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        bankSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0 ) {
                    hideBank(View.GONE);
                    editTextGiven.setEnabled(true);
                } else if(position==3){
                    hideBank(View.GONE);
                    editTextGiven.setText(cus.getVal5());
                    editTextGiven.setEnabled(false);
                }else {
                    hideBank(View.VISIBLE);
                    editTextGiven.setEnabled(true);
                }
                paytype = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });
    }

    private void updateLabel() {
        String myFormat = "yyyy/MM/dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateText.setText(sdf.format(myCalendar.getTime()));
    }


    public void openBankSelector(View v) {
        SharedPreference.COM_CUSTOMER = null;
        openDialogAreaCusSelector(new DBHelper(this).getBanks(), "SELECT Bank", 2);
    }

    private void openDialogAreaCusSelector(ArrayList<Card_cus_area> arr_cus, String title, int type) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this, R.style.mydialog);

        View row = getLayoutInflater().inflate(R.layout.item_selector, null);
        cusRecyclerView = row.findViewById(R.id.itemSelectorRecView);


        alertBuilder.setView(row);
        AlertDialog dialog = alertBuilder.create();


        cusRecyclerView.setHasFixedSize(true);
        cusLayoutManager = new LinearLayoutManager(this);
        cusAdapter = new Adapter_Cus_area(arr_cus, type, dialog);
        cusRecyclerView.setLayoutManager(cusLayoutManager);
        cusRecyclerView.setAdapter(cusAdapter);

        itemSelectTitle = row.findViewById(R.id.itemselectTitale);
        itemSelectTitle.setText(title);
        SearchView sv = row.findViewById(R.id.searchview);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                cusAdapter.getFilter().filter(newText);
                return false;
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                if (SharedPreference.COM_BANK != null) {
                    textView_Bank.setText(SharedPreference.COM_BANK.getTxt_name());
                }
            }
        });
    }


    private void hideBank(int visibilyty) {
        findViewById(R.id.textView21).setVisibility(visibilyty);
        findViewById(R.id.textView_Bank).setVisibility(visibilyty);
        findViewById(R.id.imageButton3).setVisibility(visibilyty);
        findViewById(R.id.textViewDate).setVisibility(visibilyty);
        findViewById(R.id.editTextTextPersonName).setVisibility(visibilyty);
        findViewById(R.id.textView24).setVisibility(visibilyty);
        findViewById(R.id.textView26).setVisibility(visibilyty);
    }

    private ArrayList<DefModal> arr = new ArrayList<>();

    private RecyclerView INVRecyclerView;
    private Adapter_OUTCUS_list InvAdapter;
    private RecyclerView.LayoutManager INVLayoutManager;
    private TextView itemSelectTitleINV;

    private ProgressDialog dialog;

    public void downloadInv(View v) {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading... Please Wait!");
        dialog.show();
        String url = SharedPreference.URL + "Payment/getCusInvoice?cusCode=" + cus.getVal1() + "&disCode=" + SharedPreference.COM_REP.getDiscode() + "&repCode=" + SharedPreference.COM_REP.getRepCode();
        System.out.println(url);

        RequestQueue rq = Volley.newRequestQueue(this);

        JsonObjectRequest jr = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    Gson gson = new Gson();
                    DefModalList master = gson.fromJson(response.toString(), DefModalList.class);

                    if (master != null) {
                        openDialogINVSelector(master.getList(), "SELECT INVOICE", 2);
                    }

                    dialog.dismiss();
                },
                error -> {
                    Toast.makeText(Recept.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    System.out.println("Rest Errr :" + error.toString());
                    dialog.dismiss();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        rq.add(jr);
    }

    private void openDialogINVSelector(List<DefModal> arr_cus, String title, int type) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this, R.style.mydialog);

        View row = getLayoutInflater().inflate(R.layout.item_selector, null);
        INVRecyclerView = row.findViewById(R.id.itemSelectorRecView);


        alertBuilder.setView(row);
        AlertDialog dialog = alertBuilder.create();


        INVRecyclerView.setHasFixedSize(true);
        INVLayoutManager = new LinearLayoutManager(this);
        InvAdapter = new Adapter_OUTCUS_list(arr_cus, type, this, dialog);
        INVRecyclerView.setLayoutManager(INVLayoutManager);
        INVRecyclerView.setAdapter(InvAdapter);

        itemSelectTitleINV = row.findViewById(R.id.itemselectTitale);
        itemSelectTitleINV.setText(title);
        SearchView sv = row.findViewById(R.id.searchview);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                InvAdapter.getFilter().filter(newText);
                return false;
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                if (curInv != null) {
                    System.out.println(curInv.getVal1());
                    textView_INVRefNo.setText(curInv.getVal2());
                    textView_netAmt.setText(curInv.getVal3());
                    textView_dueAmt.setText(curInv.getVal4());
                    editTextPaid.setText(curInv.getVal4());
                }
            }
        });
    }

    //new Section
    private ArrayList<TBLT_CUSRECEIPTDTL> dtl = new ArrayList<>();
    private Adapter_INV_list_payment invPayment_adaptor;
    private RecyclerView.LayoutManager invPaymentManager;
    private RecyclerView recViewInvItems;

    private void initRecview() {
        recViewInvItems = findViewById(R.id.recViewInvItems);
        invPaymentManager = new LinearLayoutManager(this);
        invPayment_adaptor = new Adapter_INV_list_payment(dtl, 1, this);
        recViewInvItems.setLayoutManager(invPaymentManager);
        recViewInvItems.setAdapter(invPayment_adaptor);
    }

    public void addItem(View view) {
        try {
            if (Double.parseDouble(textView_bal.getText().toString().replace(",","")) >= Double.parseDouble(editTextPaid.getText().toString())) {
                double dueAmt = Double.parseDouble(textView_dueAmt.getText().toString()) - Double.parseDouble(editTextPaid.getText().toString());

                TBLT_CUSRECEIPTDTL dtl_o = new TBLT_CUSRECEIPTDTL();
                Date date = Calendar.getInstance().getTime();
                String strDate = SharedPreference.dateFormat.format(date);
                dtl_o.setCancelAmt(0);
                dtl_o.setCancelDate(strDate);
                dtl_o.setDiscode(SharedPreference.COM_REP.getDiscode());

                dtl_o.setDueAmt(Double.parseDouble(textView_dueAmt.getText().toString()));
                dtl_o.setInvDate(curInv.getVal5());
                dtl_o.setNetAmt(Double.parseDouble(textView_netAmt.getText().toString()));
                dtl_o.setPaidAmt(Double.parseDouble(editTextPaid.getText().toString()));
                dtl_o.setRefNo(curInv.getVal2());
                dtl_o.setRepCode(SharedPreference.COM_REP.getRepCode());
                dtl_o.setDiscode(SharedPreference.COM_REP.getDiscode());
                if (dueAmt <= 0) {
                    dtl_o.setStatus("A");
                } else {
                    dtl_o.setStatus("E");
                }
                dtl_o.setSupCode("");
                dtl_o.setTrnDate(strDate);

                for(TBLT_CUSRECEIPTDTL a : dtl){
                    if(a.getRefNo().equals(dtl_o.getRefNo())){
                        Toast.makeText(this,"Duplicate Invoice",Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                dtl.add(dtl_o);
                invPayment_adaptor.notifyDataSetChanged();


                double bal = Double.parseDouble(textView_bal.getText().toString().replace(",","")) - Double.parseDouble(editTextPaid.getText().toString());
                textView_bal.setText(SharedPreference.ds_formatter.format(bal));

                editTextPaid.getText().clear();
            } else {
                editTextGiven.requestFocus();
                editTextGiven.setError("Can not over Given Amt!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            removeItem(viewHolder.getAdapterPosition());
            invPayment_adaptor.notifyDataSetChanged();

        }
    };

    public void removeItem(int pos) {
        TBLT_CUSRECEIPTDTL obj = dtl.get(pos);
        dtl.remove(pos);
        refreshItems(obj);
    }

    public void refreshItems(TBLT_CUSRECEIPTDTL obj) {
        double bal = Double.parseDouble(textView_bal.getText().toString().replace(",","")) + obj.getPaidAmt();
        textView_bal.setText(SharedPreference.df.format(bal));
    }

    public void createAndPostReceipt(View v) throws JSONException {
        Date date = Calendar.getInstance().getTime();
        String strDate = SharedPreference.dateFormat.format(date);
        TBLT_CUSRECEIPTHED hed = new TBLT_CUSRECEIPTHED();
        if (editTextGiven.getText().toString().equals("") || editTextGiven.getText() == null) {
            return;
        }

        double give = Double.parseDouble(editTextGiven.getText().toString());
        double bal = Double.parseDouble(textView_bal.getText().toString().replace(",",""));

        if (bal < 0) {
            return;
        }



        hed.setPayType(paytype);
        System.out.println(paytype);
        if (paytype == 1 || paytype==2) {
            if (SharedPreference.COM_BANK == null) {
                return;
            }
            hed.setBankCode(SharedPreference.COM_BANK.getTxt_code());
            hed.setBankDate(dateText.getText().toString());
            hed.setChequeNo(editTextChNo.getText().toString());
            hed.setChqDeposit("Y");
        } else {
            hed.setBankCode("");
            hed.setBankDate(strDate);
            hed.setChequeNo("");
            hed.setChqDeposit("N");
        }

        hed.setCancelAmt(0);
        hed.setCancelDate("");
        hed.setCardNo("");
        hed.setCardType("");

        hed.setCusCode(cus.getVal1());
        hed.setDiscode(SharedPreference.COM_REP.getDiscode());
        hed.setIsInvoice(1);
        hed.setNetAmt(give - bal);
        hed.setPaidAmt(give);
        hed.setRecDocType("I");
        hed.setReceiptNo(SharedPreference.COM_REP.getRepCode().concat(SharedPreference.dateFormat2.format(date)));
        hed.setRefNo(SharedPreference.COM_REP.getRepCode().concat(SharedPreference.dateFormat2.format(date)));
        hed.setStatus("P");
        hed.setCardNo("");
        hed.setCardType("");
        hed.setTrnDate(strDate);
        hed.setDtl(dtl);

        upload(hed);

    }

    ProgressDialog pd;
    public void upload(TBLT_CUSRECEIPTHED cusrec) throws JSONException {
        pd = new ProgressDialog(this);
        pd.setMessage("Processing..");
        pd.show();
        Gson gson = new Gson();

        JSONObject obj = new JSONObject(gson.toJson(cusrec));


        Log.i(TAG, obj.toString());
        RequestQueue rq = Volley.newRequestQueue(this);

        String url = SharedPreference.URL + "Payment/upload";


        JsonObjectRequest jr = new JsonObjectRequest(
                Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Rest Response :" + response.toString());

                        DefModal defModal = gson.fromJson(response.toString(), DefModal.class);
                        if (defModal.getVal1().equals("S")) {
                            p2(cusrec);
                        } else if (defModal.getVal1().equals("E")) {
                            Toast.makeText(Recept.this, defModal.getVal2(), Toast.LENGTH_LONG).show();

                        }

                        pd.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Rest Errr :" + error.toString());
                        Toast.makeText(Recept.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        pd.dismiss();

                    }
                }
        ) { //no semicolon or coma
            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        rq.add(jr);
        System.out.println("------------------------------------------------------------------------");

    }

    //Print COnfig
    public boolean p2(TBLT_CUSRECEIPTHED obj) {
        try {
            OutputStream os = MainMenu.mBluetoothSocket
                    .getOutputStream();

            printConfig("Kudagama Stores Pvt Limited", 1, 2, 1, os, true);
            printNewLine(os);
            //printConfig("--------------------------------------------------------------", 3, 1, 1, os,true);
            printConfig(SharedPreference.address, 3, 1, 1, os, true);
            printConfig("Tel : ".concat(SharedPreference.tp), 3, 1, 1, os, true);
            printConfig("----------------------------------------------", 3, 1, 1, os, true);
            printConfig("++ PAYMENT RECEIPT ++", 1, 1, 1, os, true);
            printNewLine(os);
            printConfig("Receipt Ref No. ".concat(obj.getReceiptNo()), 2, 1, 0, os, true);
            printConfig("Route : ".concat(SharedPreference.COM_AREA.getTxt_name()), 2, 1, 0, os, true);
            printConfig("Customer : ".concat(textView_cusName.getText().toString()), 2, 1, 0, os, true);
            printConfig("Payment Type : ".concat(bankSpinner.getSelectedItem().toString()), 2, 2, 0, os, true);
            printConfig(obj.getTrnDate().concat(" | Rep : ").concat(SharedPreference.COM_REP.getRepName()), 2, 1, 0, os, true);
            printConfig("----------------------------------------------", 3, 1, 1, os, true);
            printConfig("LN  INV.No/Net       Due     Payed    Balance", 2, 2, 0, os, true);
            printConfig("- - - - - - - - - - - - - - - - - - - - - - - ", 3, 1, 1, os, true);
            int i = 1;
            String s;

            for (TBLT_CUSRECEIPTDTL o : obj.getDtl()) {

                s = i + "  " + o.getRefNo() + " - (" + SharedPreference.ds_formatter.format(o.getNetAmt()) + ")";
                printConfig(s, 2, 1, 0, os, true);
//                        printConfig(SharedPreference.ds_formatter.format(o.getNetAmt()) + "   ", 2, 1, 1, os, false);
                printConfig(SharedPreference.ds_formatter.format(o.getDueAmt()) + "\t", 2, 1, 1, os, false);
                printConfig(SharedPreference.ds_formatter.format(o.getPaidAmt()) + "\t", 2, 1, 1, os, false);
                printConfig(SharedPreference.ds_formatter.format(o.getDueAmt() - o.getPaidAmt()) + "  ", 2, 1, 2, os, true);
                // printConfig("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -", 3, 1, 1, os,true);

                i++;

            }
            printConfig("----------------------------------------------", 3, 1, 1, os, true);

            printConfig("Given Amount :\t ".concat(SharedPreference.ds_formatter.format(obj.getPaidAmt())) + "  ", 2, 1, 2, os, true);
            //printNewLine(os);
            // printConfig("Line Discount :    ".concat(SharedPreference.ds_formatter.format(linedis)) + "  ", 2, 1, 2, os, true);
            printNewLine(os);
            printConfig("Paid Amount :\t ".concat(SharedPreference.ds_formatter.format(obj.getNetAmt())) + "  ", 1, 1, 2, os, true);
            printNewLine(os);
            printConfig("Over Payment :\t ".concat(SharedPreference.ds_formatter.format(obj.getPaidAmt() - obj.getNetAmt())) + "  ", 2, 1, 2, os, true);
            printConfig("Customer Total Due :\t ".concat(SharedPreference.ds_formatter.format(Double.parseDouble(cus.getVal3()) - obj.getNetAmt())) + "  ", 2, 1, 2, os, true);
            printConfig("----------------------------------------------", 3, 1, 1, os, true);
            printNewLine(os);

            printConfig("Software - Flexiv.lk | Salesforce Android", 3, 1, 1, os, true);
            printNewLine(os);
            printNewLine(os);



            this.finish();
        } catch (Exception ex) {
            Toast.makeText(this,"Printer Error",Toast.LENGTH_LONG).show();
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    protected void printConfig(String bill, int size, int style, int align, OutputStream outputStream, boolean LF) throws IOException {
        //size 1 = large, size 2 = medium, size 3 = small
        //style 1 = Regular, style 2 = Bold
        //align 0 = left, align 1 = center, align 2 = right

            byte[] format = new byte[]{27, 33, 0};
            byte[] change = new byte[]{27, 33, 0};

            outputStream.write(format);

            //different sizes, same style Regular
            if (size == 1 && style == 1)  //large
            {
                change[2] = (byte) (0x10); //large
                outputStream.write(change);
            } else if (size == 2 && style == 1) //medium
            {
                //nothing to change, uses the default settings
            } else if (size == 3 && style == 1) //small
            {
                change[2] = (byte) (0x3); //small
                outputStream.write(change);
            }

            //different sizes, same style Bold
            if (size == 1 && style == 2)  //large
            {
                change[2] = (byte) (0x10 | 0x8); //large
                outputStream.write(change);
            } else if (size == 2 && style == 2) //medium
            {
                change[2] = (byte) (0x8);
                outputStream.write(change);
            } else if (size == 3 && style == 2) //small
            {
                change[2] = (byte) (0x3 | 0x8); //small
                outputStream.write(change);
            }


            switch (align) {
                case 0:
                    //left align
                    outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    break;
                case 1:
                    //center align
                    outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    break;
                case 2:
                    //right align
                    outputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    break;
            }
            bill = "  " + bill;
            outputStream.write(bill.getBytes());
            if (LF)
                outputStream.write(PrinterCommands.LF);

    }

    protected void printNewLine(OutputStream outputStream) throws IOException {
            outputStream.write(PrinterCommands.FEED_LINE);
    }
}

