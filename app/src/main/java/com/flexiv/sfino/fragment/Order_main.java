package com.flexiv.sfino.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Visibility;

import com.flexiv.sfino.Order;
import com.flexiv.sfino.R;
import com.flexiv.sfino.adapter.Adapter_Oeder_Item;
import com.flexiv.sfino.model.TBLT_ORDDTL;
import com.flexiv.sfino.model.TBLT_ORDERHED;
import com.flexiv.sfino.utill.SharedPreference;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;

import java.util.Calendar;
import java.util.Date;

public class Order_main extends Fragment {
    private final static String TAG = "Order_main";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_main, container, false);
    }

    private TextView textView_total;
    private TextView textView_Nettotal;
    private TextView textView_tax;
    private TextView textView_readOnly_oreder;
    private EditText DisPre, DisAmt;
    private Button button_save;
    private Button button_process;

    private RecyclerView Order_recView;
    private Adapter_Oeder_Item adaper;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton floatingActionButton_OM;

    //Components
    private Order context;


    public Order_main(Order context) {
        this.context = context;
        context.changeNavButton(1);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        constraintLayout3 = view.findViewById(R.id.constraintLayout3);
        textView_total = view.findViewById(R.id.textView10);
        textView_Nettotal = view.findViewById(R.id.textView19);
        textView_tax = view.findViewById(R.id.textView17);
        DisPre = view.findViewById(R.id.editText2);
        DisAmt = view.findViewById(R.id.editText3);
        button_save = view.findViewById(R.id.button_save);
        button_process = view.findViewById(R.id.button2);
        floatingActionButton_OM = view.findViewById(R.id.floatingActionButton_OM);

        textView_readOnly_oreder = view.findViewById(R.id.textView_readOnly_oreder);

        //TODO-----------------------------------
        //SAVE...................................
        button_save.setOnClickListener(view1 -> {
            if (context.getTextView_InvNo().getText().toString().contains("Processing")) {
                String msg = context.Save(CreateORDERHED());
                if (msg.contains("Error")) {
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                } else {
                    context.onBackPressed2();
                    Toast.makeText(context, "Successfully Saved!", Toast.LENGTH_LONG).show();
                }
            } else {
                String msg = context.SaveUpdate(CreateORDERHED_ForUpdate());
                if (msg.contains("Error")) {
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                } else {
                    context.onBackPressed2();
                    Toast.makeText(context, "Successfully Updated!", Toast.LENGTH_LONG).show();
                }
            }

        });
        //---------------------------------------

        Order_recView = view.findViewById(R.id.Order_recView);
        layoutManager = new LinearLayoutManager(context);
        adaper = new Adapter_Oeder_Item(context.getItemList(), context);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(Order_recView);
        Order_recView.setLayoutManager(layoutManager);
        Order_recView.setAdapter(adaper);

        //FAB Action
        floatingActionButton_OM.setOnClickListener(v -> context.LoadFragment_sub());
        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                disTextChange = 1;
                if (charSequence.length() > 0) {
                    DisAmt.setText(SharedPreference.df.format(GenDisVal(charSequence)));
                    //textView_Nettotal.setText(SharedPreference.df.format(total - GenDisVal(charSequence)));
                } else {
                    DisAmt.getText().clear();
                    textView_Nettotal.setText(SharedPreference.df.format(total));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        DisPre.addTextChangedListener(tw);

//        DisAmt.setOnFocusChangeListener((view1, b) -> DisPre.getText().clear());
        DisAmt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    double net = total - Double.parseDouble(DisAmt.getText().toString());
                    double disprt = 100 / total * (Double.parseDouble(DisAmt.getText().toString()));
                    if (disTextChange != 1) {
                        DisPre.removeTextChangedListener(tw);
                        DisPre.setText(SharedPreference.df.format(disprt));
                        DisPre.addTextChangedListener(tw);
                    }
                    textView_Nettotal.setText(SharedPreference.df.format(net));
                } else {
                    if (disTextChange != 1) {
                        DisPre.removeTextChangedListener(tw);
                        DisPre.getText().clear();
                        DisPre.addTextChangedListener(tw);
                    }
                    textView_Nettotal.setText(SharedPreference.df.format(total));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                disTextChange = 0;
                //FIXME
                //button_process.setAlpha(Float.parseFloat("0.3"));
                //button_process.setEnabled(false);
            }
        });

        if (context.getObj() != null) {
            setDetailsFromIntent(context.getObj());
        }


        //Proccess Order
        button_process.setOnClickListener(view1 -> {
            button_process.setEnabled(false);
            try {
                context.UploadOrder();
            } catch (JSONException e) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        });
    }

    int disTextChange = 0;

    double total = 0;

    public void refreshItems() {
        total = 0;
        for (TBLT_ORDDTL obj : context.getItemList()) {
            total = total + obj.getAmount();
        }
        textView_total.setText(SharedPreference.df.format(total));
        double netTotal = total + Double.parseDouble(textView_tax.getText().toString());


        //TODO.....
        if (DisPre.getText().length() > 0 && total > 0) {
            DisAmt.setText(SharedPreference.df.format(GenDisVal(DisPre.getText().toString())));
            textView_Nettotal.setText(SharedPreference.df.format(netTotal - GenDisVal(DisPre.getText().toString())));

        } else {
            DisAmt.getText().clear();
            textView_Nettotal.setText(SharedPreference.df.format(netTotal));
        }
        adaper.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshItems();
    }

    ConstraintLayout constraintLayout3;

    private double GenDisVal(CharSequence s) {
        if (DisPre.getText().length() > 0) {
            System.out.println("--------------" + s.toString());
            System.out.println("--------------" + Double.parseDouble(s.toString()));
            return total * (Double.parseDouble(s.toString()) / 100);
        }
        return 0;

    }

    private TBLT_ORDERHED CreateORDERHED() {
        Date date = Calendar.getInstance().getTime();

        String strDate = SharedPreference.dateFormat.format(date);
        TBLT_ORDERHED obj = new TBLT_ORDERHED();

        obj.setDocNo("");
        obj.setRepCode(SharedPreference.COM_REP.getRepCode());
        obj.setDiscode(SharedPreference.COM_REP.getDiscode());
        obj.setRefNo("");
        obj.setAreaCode(SharedPreference.COM_AREA.getTxt_code());
        obj.setCusCode(SharedPreference.COM_CUSTOMER.getTxt_code());
        obj.setCreateUser(SharedPreference.COM_REP.getRepName());
        obj.setSalesDate(strDate);
        obj.setPayType("CASH");
        obj.setISUSED(false);
        obj.setLocCode("");
        obj.setVatAmt(0);
        obj.setStatus("S");

        obj.setGrossAmt(Double.parseDouble(textView_total.getText().toString()));
        obj.setDiscount(Double.parseDouble(DisAmt.getText().length() <= 0 ? "0" : DisAmt.getText().toString()));
        obj.setDisPer(Double.parseDouble(DisPre.getText().length() <= 0 ? "0" : DisPre.getText().toString()));
        obj.setNetAmt(Double.parseDouble(textView_Nettotal.getText().toString()));


        return obj;
    }

    private TBLT_ORDERHED CreateORDERHED_ForUpdate() {

        TBLT_ORDERHED obj = new TBLT_ORDERHED();
        obj.setDocNo(context.getObj().getDocNo());
        obj.setRepCode(context.getObj().getRepCode());
        obj.setDiscode(context.getObj().getDiscode());
        obj.setRefNo(context.getObj().getRefNo());
        obj.setAreaCode(context.getObj().getAreaCode());
        obj.setCusCode(context.getObj().getCusCode());
        obj.setCreateUser(context.getObj().getCreateUser());
        obj.setSalesDate(context.getObj().getSalesDate());
        obj.setPayType(context.getObj().getPayType());
        obj.setISUSED(false);
        obj.setLocCode(context.getObj().getLocCode());
        obj.setVatAmt(context.getObj().getVatAmt());
        obj.setStatus("S");

        obj.setGrossAmt(Double.parseDouble(textView_total.getText().toString()));
        obj.setDiscount(Double.parseDouble(DisAmt.getText().length() <= 0 ? "0" : DisAmt.getText().toString()));
        obj.setDisPer(Double.parseDouble(DisPre.getText().length() <= 0 ? "0" : DisPre.getText().toString()));
        obj.setNetAmt(Double.parseDouble(textView_Nettotal.getText().toString()));


        return obj;
    }

    public void setDetailsFromIntent(TBLT_ORDERHED hed) {

        System.out.println("--------------------------------------");
        textView_total.setText(SharedPreference.df.format(hed.getGrossAmt()));
        DisPre.setText(SharedPreference.df.format(hed.getDisPer()));
        //DisAmt.setText(SharedPreference.df.format(hed.getDisPer()));
        textView_Nettotal.setText(SharedPreference.df.format(hed.getNetAmt()));


        if (hed.getStatus().equals("A")) {
            textView_readOnly_oreder.setVisibility(View.VISIBLE);
            button_save.setVisibility(View.GONE);
            button_process.setVisibility(View.GONE);
            floatingActionButton_OM.setVisibility(View.GONE);
        }else if (hed.getStatus().equals("E")) {
            textView_readOnly_oreder.setVisibility(View.VISIBLE);
            button_save.setVisibility(View.GONE);
            button_process.setAlpha(1);
            button_process.setEnabled(true);
            floatingActionButton_OM.setVisibility(View.GONE);
        } else {
            button_process.setAlpha(1);
            button_process.setEnabled(true);
        }

    }


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            context.removeItem(viewHolder.getAdapterPosition());
            adaper.notifyDataSetChanged();
            refreshItems();
        }
    };

}
