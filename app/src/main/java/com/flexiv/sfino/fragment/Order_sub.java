package com.flexiv.sfino.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.flexiv.sfino.Order;
import com.flexiv.sfino.R;
import com.flexiv.sfino.model.Bean_OrderPromotion;
import com.flexiv.sfino.model.Bean_PromotionDetails;
import com.flexiv.sfino.model.Modal_Batch;
import com.flexiv.sfino.model.TBLT_ORDDTL;
import com.flexiv.sfino.utill.DBHelper;
import com.flexiv.sfino.utill.SharedPreference;
import com.google.android.material.textfield.TextInputLayout;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class Order_sub extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_sub, container, false);
    }


    private Order context;
    private Modal_Batch item;

    //Components
    TextView textView_itemName;
    TextView textView_itemCode;
    TextView textView_BatchCode;
    TextView textView_Price;

    Button button_add;

    TextInputLayout textInputLayoutQty1;

    EditText dis_pre;
    EditText edt_disVal;
    EditText edt_FI1;

    EditText txt_avlQty;
    EditText edt_Qtyx;

    ArrayList<Bean_PromotionDetails> pronoArr;
    ArrayList<Bean_PromotionDetails> pronoOtherArr;
    boolean[] checkedItems = new boolean[]{};
    String[] listPromo = new String[]{};
    ArrayList<Integer> mPromotions = new ArrayList<>();
    ArrayList<Bean_OrderPromotion> PromoOtherArr = new ArrayList<>();
    ArrayList<String> tempPromoArrFor_trackML = new ArrayList<>();
    int deals = 0;
    String varQry = "";
    String varFreeQry = "";
    //Free Issue Algo
    Bean_OrderPromotion orderPromotion_NF = null;

    //Promo Items
    CheckBox checkBox_nf;
    EditText editText_SugNF;
    Button buttonpromo,buttonNFD;//showNFDetails
    EditText textView_selected_promo;
    TextView textViewDealCount;

    RadioButton radioButton_proPromo;
    RadioButton radioButton_CoverPromo;
    RadioGroup radioGroup;

    InputMethodManager imm;

    public Order_sub(Order context, Modal_Batch item) {
        this.context = context;
        this.item = item;
        context.changeNavButton(2);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        textView_itemName = view.findViewById(R.id.textView_itemName);
        textView_itemCode = view.findViewById(R.id.textView_itemCode);
        textView_BatchCode = view.findViewById(R.id.textView_BatchCode);
        textView_Price = view.findViewById(R.id.textView_Price);
        txt_avlQty = view.findViewById(R.id.txt_avlQty);
        dis_pre = view.findViewById(R.id.dis_pre);
        edt_disVal = view.findViewById(R.id.edt_disVal);
        edt_FI1 = view.findViewById(R.id.edt_FI1);
        edt_Qtyx = view.findViewById(R.id.edt_Qtyx);
        textInputLayoutQty1 = view.findViewById(R.id.textInputLayoutQty1);
        button_add = view.findViewById(R.id.button3);
        //Promo ITems
        checkBox_nf = view.findViewById(R.id.checkBox_nf);
        editText_SugNF = view.findViewById(R.id.editText_SugNF);
        buttonpromo = view.findViewById(R.id.buttonpromo);
        textView_selected_promo = view.findViewById(R.id.textView_selected_promo);
        textViewDealCount = view.findViewById(R.id.textViewDealCount);
        buttonNFD = view.findViewById(R.id.buttonNFD);

        buttonNFD.setOnClickListener(v -> {
            showNFDetails(view);
        });

        radioButton_proPromo = view.findViewById(R.id.radioButton_proPromo);
        radioButton_CoverPromo = view.findViewById(R.id.radioButton_CoverPromo);
        radioGroup = view.findViewById(R.id.radioGroup);



        button_add.setOnClickListener(view1 -> createItem());

        edt_Qtyx.setOnFocusChangeListener((view1, b) -> dis_pre.setText(""));

        dis_pre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    edt_disVal.setText(SharedPreference.df.format(GenDisVal(charSequence)));
                } else {
                    edt_disVal.getText().clear();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edt_Qtyx.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && !pronoArr.isEmpty()) {
                    calcNF(new BigDecimal(s.toString()));
//                    calcOtherPromo(new BigDecimal(s.toString()));
                }
                if (s.length() > 0 && !pronoOtherArr.isEmpty()) {
//                    calcNF(new BigDecimal(s.toString()));
                    calcOtherPromo(new BigDecimal(s.toString()));
                }

            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            for (int i = 0; i < checkedItems.length; i++) {
                checkedItems[i] = false;
            }
            mPromotions.clear();
            textView_selected_promo.setText("");
            edt_disVal.getText().clear();
            dis_pre.getText().clear();
            textViewDealCount.setText("");
            PromoOtherArr.clear();
        });


        setItem();
    }

    private void setItem() {
        textView_BatchCode.setText(item.getBatchNo());
        textView_itemCode.setText(item.getItemCode());
        textView_itemName.setText(item.getDesc());
        textView_Price.setText(SharedPreference.df.format(item.getRetialPrice()));
        txt_avlQty.setText(SharedPreference.df.format(item.getSHI()));

        checkBox_nf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edt_FI1.setEnabled(true);
                    checkBox_nf.setText("N/F Enabled");
                    editText_SugNF.setVisibility(View.VISIBLE);
                    if (edt_Qtyx.getText().toString().length() > 0) {
                        calcNF(new BigDecimal(edt_Qtyx.getText().toString()));
                    }
                } else {
                    edt_FI1.setEnabled(false);
                    edt_FI1.setText("0");
                    editText_SugNF.setText("0");
                    checkBox_nf.setText("N/F Disabled");
                    checkBox_nf.setTextColor(Color.RED);
                    orderPromotion_NF = null;
                }
            }
        });



        new Thread(() -> {
            DBHelper DBH = new DBHelper(context);
            pronoArr = DBH.getNFPromo(item.getItemCode());
            pronoOtherArr = DBH.getOtherPromo(item.getItemCode());

            context.runOnUiThread(() -> {
                if (!pronoArr.isEmpty()) {
                    checkBox_nf.setText("N/F Enabled");
                    checkBox_nf.setChecked(true);
                    checkBox_nf.setEnabled(true);
                    editText_SugNF.setVisibility(View.VISIBLE);
                } else {
                    edt_FI1.setEnabled(false);
                }
            });
        }).start();

        buttonpromo.setOnClickListener(v -> {

            //Keyboard Hide
            imm.hideSoftInputFromWindow(Objects.requireNonNull(context.getCurrentFocus()).getWindowToken(), 0);

            if (listPromo.length >= 1) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                mBuilder.setTitle("Promotions");
                mBuilder.setMultiChoiceItems(listPromo, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            if (!mPromotions.contains(which)) {
                                mPromotions.add(which);
                            } else {
                                mPromotions.remove(which);
                            }
                        }
                    }
                });
                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                            StringBuilder promo = new StringBuilder();
//                            promo.append(listPromo[i]).append("\n");
                        PromoOtherArr.clear();
                        deals = 0;
                        tempPromoArrFor_trackML.clear();
                        textView_selected_promo.getText().clear();
                        textViewDealCount.setText("");
                        for (int i : mPromotions) {
                            //Apply Promotions
                            for (Bean_PromotionDetails b : pronoOtherArr) {

                                String name1 = "\n" + b.getPromoCode() + " (avl.Deals : " + b.getNoOfDealsQtyBalance() + ")\n" + b.getPromoDesc();
                                String name2 = b.getPromoCode() + " (avl.Deals : " + b.getNoOfDealsQtyBalance() + ")";

                                if (name1.equals(listPromo[i]) || name2.equals(listPromo[i])) {
                                    if (b.getNoOfDealsQtyBalance() == 0) {
                                        checkedItems[i] = false;
                                    } else {
                                        setOtherPromotion(b);
                                    }
                                    System.out.println("No fo Deals : " + b.getNoOfDealsQtyBalance());

                                }
                            }
                        }

                        textViewDealCount.setText("No. Of Deals used : " + deals);
                    }
                });
                mBuilder.setNegativeButton("Dismiss", (dialog, which) -> dialog.dismiss());
                mBuilder.setNeutralButton("Clear All", (dialog, which) -> {
                    for (int i = 0; i < checkedItems.length; i++) {
                        checkedItems[i] = false;
                    }
                    mPromotions.clear();
                    textView_selected_promo.setText("");
                    PromoOtherArr.clear();
                    deals = 0;
                    edt_disVal.getText().clear();
                    tempPromoArrFor_trackML.clear();
                    textViewDealCount.setText("");
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
    }

    private void createItem() {
        Order.hideKeyboard(context);

        if(edt_Qtyx.getText().length()<=0){
            textInputLayoutQty1.setError("Please Enter Qty");
            return;
        }
        TBLT_ORDDTL obj = new TBLT_ORDDTL();
        obj.setItemCode(item.getItemCode());
        obj.setItemName(item.getDesc());
        obj.setBATCH(item.getBatchNo());
        obj.setUnitPrice(item.getRetialPrice());
        obj.setDiscPer(Double.parseDouble(dis_pre.getText().length()>0?dis_pre.getText().toString():"0"));
        obj.setDiscAmt(Double.parseDouble(edt_disVal.getText().length()>0?edt_disVal.getText().toString():"0"));
        obj.setItQty(Double.parseDouble(edt_Qtyx.getText().toString()));
        obj.setUsedQty(0);
        obj.setDate(SharedPreference.dateFormat.format(Calendar.getInstance().getTime()));
        obj.setCusCode(SharedPreference.COM_CUSTOMER.getTxt_code());
        //todo
        obj.setSysFQTY(Double.parseDouble(editText_SugNF.getText().length()>0?editText_SugNF.getText().toString():"0"));
        obj.setFQTY(Double.parseDouble(edt_FI1.getText().length()>0?edt_FI1.getText().toString():"0"));
        double TF = 0;

        obj.setRecordLine(0);
        obj.setAmount((item.getRetialPrice() * obj.getItQty())-obj.getDiscAmt());

        ArrayList<Bean_OrderPromotion> pr = getOrderPromoArray();
        for (Bean_PromotionDetails bean : pr) {
            if(bean.getPTCode().equals("TF"))
                TF = TF + bean.getFQTY().doubleValue();
        }
        obj.setTradeFQTY(TF);
        obj.setTotalQty(obj.getItQty()+obj.getTradeFQTY()+obj.getFQTY());
        obj.setPromotions(pr);

        context.setItemList(obj);

        context.GoBackWithItems();
    }

    private double GenDisVal(CharSequence s) {
        if (edt_Qtyx.getText().length()>0) {
            double subTot = item.getRetialPrice() * Double.parseDouble(edt_Qtyx.getText().toString());
            return subTot * (Double.parseDouble(s.toString()) / 100);

        }
        return 0;

    }


    //PROMO CAL
    //TODO
    private void calcNF(BigDecimal rqty) {
        orderPromotion_NF = null;
        double bal_qty = rqty.doubleValue();
        int fqty = 0;
        boolean first = true;
        Bean_PromotionDetails pre_bean = null;
        Bean_PromotionDetails pre_bean2 = null;
        for (Bean_PromotionDetails bean : pronoArr) {
            pre_bean2 = bean;
            if (bal_qty >= bean.getQtyFrom().intValue() && first) {

                fqty = (int)(bal_qty / bean.getQtyFrom().doubleValue() * bean.getFQTY().doubleValue());
                break;
            }

            if (bal_qty >= bean.getQtyFrom().intValue()) {
                fqty += bean.getFQTY().intValue();
                bal_qty -= (bean.getQtyFrom().intValue());

            }

            if (pre_bean != null) {
                if (bal_qty >= bean.getQtyFrom().intValue()) {
                    fqty += (pre_bean.getFQTY().intValue());
                    bal_qty -= (pre_bean.getQtyFrom().intValue());

                }
            }

            pre_bean = bean;
            first = false;
        }
        String fq = String.valueOf(fqty);
        System.out.println(fq);
        editText_SugNF.setText(fq);
        edt_FI1.setText(fq);

        Log.wtf("WTF", fqty + "");
        if (fqty > 0) {
            Log.e("Test FQ", "Done! " + fq);

            orderPromotion_NF = new Bean_OrderPromotion();
            orderPromotion_NF.setPromoNo(pre_bean2.getPromoNo());
            orderPromotion_NF.setItemCode(item.getItemCode());
            orderPromotion_NF.setDealCode(pre_bean2.getPromoCode());
            orderPromotion_NF.setNoOfDeals(1);
            orderPromotion_NF.setSysFQty(new BigDecimal(fqty));
            orderPromotion_NF.setDiscode(pre_bean2.getDiscode());
            orderPromotion_NF.setPTCode(pre_bean2.getPTCode());
            orderPromotion_NF.setDocNo("");
            orderPromotion_NF.setDocType(5);
            orderPromotion_NF.setCusCode(SharedPreference.COM_CUSTOMER.getTxt_code());
            Log.w("CODE", SharedPreference.COM_CUSTOMER.getTxt_code());
            orderPromotion_NF.setPromoDesc(pre_bean2.getPromoDesc());
            orderPromotion_NF.setFreeItem(BigDecimal.ZERO);
        }
    }


    private void calcOtherPromo(BigDecimal rqty) {
        for (int i = 0; i < checkedItems.length; i++) {
            checkedItems[i] = false;
        }
        mPromotions.clear();
        textView_selected_promo.getText().clear();

        ArrayList<String> promoName = new ArrayList<>();
        for (Bean_PromotionDetails i : pronoOtherArr) {
            if (rqty.compareTo(i.getQtyFrom()) != -1) {
                String name1 = "\n" + i.getPromoCode() + " (avl.Deals : " + i.getNoOfDealsQtyBalance() + ")\n" + i.getPromoDesc();
                String name2 = i.getPromoCode() + " (avl.Deals : " + i.getNoOfDealsQtyBalance() + ")";
                if (!(promoName.contains(name1) || promoName.contains(name2))) {
                    if (i.getNoOfDealsQtyBalance() != 0) {
                        promoName.add(name1);
                    } else {
                        promoName.add(name2);
                    }
                }
            }
        }
        listPromo = new String[promoName.size()];
        listPromo = promoName.toArray(listPromo);
        checkedItems = new boolean[listPromo.length];
        dis_pre.getText().clear();
        edt_disVal.getText().clear();
        PromoOtherArr.clear();
        deals = 0;
        tempPromoArrFor_trackML.clear();
        textViewDealCount.setText("");
    }

    private void setOtherPromotion(Bean_PromotionDetails b) {
        Bean_OrderPromotion orderPromotion_OT;
        int lp = 0;
        BigDecimal rqQty = new BigDecimal(edt_Qtyx.getText().toString());

        if (radioButton_CoverPromo.isChecked()) {
            lp = b.getFQTY().intValue();
            if (!tempPromoArrFor_trackML.contains(b.getPromoCode())) {
                if (rqQty.compareTo(b.getQtyFrom()) > -1) {
                    deals += 1;
                }
                tempPromoArrFor_trackML.add(b.getPromoCode());
            }
        } else if (radioButton_proPromo.isChecked()) {

            //count deals
            if (!tempPromoArrFor_trackML.contains(b.getPromoCode())) {
                if (rqQty.compareTo(b.getQtyFrom()) > -1) {
                    deals += rqQty.intValue() / b.getQtyFrom().intValue();
                }
                tempPromoArrFor_trackML.add(b.getPromoCode());
            }

            //Calculate FQ
            int deals = 0;
            if (rqQty.compareTo(b.getQtyFrom()) > -1) {
                deals = rqQty.intValue() / b.getQtyFrom().intValue();
            }
            lp = b.getFQTY().intValue() * deals;
            // lp = new BigDecimal(rqQty.doubleValue() / b.getQtyFrom().doubleValue() * b.getFQTY().doubleValue());
        }

        if (b.getPTCode().equals("LP")) {
            Toast.makeText(context, "Cannot Process Line Percentage Discount", Toast.LENGTH_LONG).show();
        } else if (b.getPTCode().equals("LR")) {
            edt_disVal.setText(String.valueOf(lp));
        }

        if (deals <= b.getNoOfDealsQtyBalance()) {
            StringBuilder promo = new StringBuilder();
            promo.append(b.getPromoCode()).append(" : ").append(b.getPromoDesc());
            promo.append("\n");
            promo.append("*Eligible : ").append(lp).append(" (").append(b.getPTCode()).append(")");
            if (textView_selected_promo.getText().toString().equals("")) {
                textView_selected_promo.setText(promo.toString());
            } else {
                textView_selected_promo.setText(textView_selected_promo.getText().append("\n\n").append(promo.toString()));
            }


            orderPromotion_OT = new Bean_OrderPromotion();
            orderPromotion_OT.setQty(new BigDecimal(edt_Qtyx.getText().toString()));
            orderPromotion_OT.setFQTY(new BigDecimal(lp));
            orderPromotion_OT.setPromoNo(b.getPromoNo());
            orderPromotion_OT.setItemCode(item.getItemCode());
            orderPromotion_OT.setDealCode(b.getPromoCode());
            orderPromotion_OT.setNoOfDeals(1);
            orderPromotion_OT.setSysFQty(new BigDecimal(lp));
            orderPromotion_OT.setDiscode(b.getDiscode());
            orderPromotion_OT.setPTCode(b.getPTCode());
            orderPromotion_OT.setDocNo("");
            orderPromotion_OT.setDocType(5);
            orderPromotion_OT.setCusCode(SharedPreference.COM_CUSTOMER.getTxt_code());
            orderPromotion_OT.setPromoDesc(b.getPromoDesc());
            orderPromotion_OT.setFreeItem(BigDecimal.ZERO);
            PromoOtherArr.add(orderPromotion_OT);
        } else {
            if (!tempPromoArrFor_trackML.contains(b.getPromoCode())) {
                if (rqQty.compareTo(b.getQtyFrom()) > -1) {
                    deals -= rqQty.intValue() / b.getQtyFrom().intValue();
                }
                tempPromoArrFor_trackML.add(b.getPromoCode());
            }
            new AlertDialog.Builder(context)
                    .setTitle("Not enough Promotion Deals")
                    .setMessage("Please Renew your deals.\nPromo Code : " + b.getPromoCode())
                    .setIcon(R.drawable.ic_baseline_warning_24)
                    .setCancelable(true)
                    .setNegativeButton("OK", null).show();
        }
    }

    public void showNFDetails(View v){
        String msg = "";
        for (Bean_PromotionDetails bean : pronoArr) {

            msg = msg.concat(bean.getQtyFrom().toString()).concat("  To  ").concat(bean.getQtyTo().toString()).concat("  :  ").concat(bean.getFQTY().toPlainString()).concat("\n");

        }


        LayoutInflater inflater= LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.scrollalertdialog, null);

        TextView textview=view.findViewById(R.id.textmsg);
        textview.setText(msg);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Normal Free Issue criteria");
//alertDialog.setMessage("Here is a really long message.");
        alertDialog.setView(view);
        AlertDialog alert = alertDialog.create();
        alert.show();

    }

    private ArrayList<Bean_OrderPromotion> getOrderPromoArray() {
        ArrayList<Bean_OrderPromotion> promo = new ArrayList<>();
        if (orderPromotion_NF != null) {
            orderPromotion_NF.setQty(new BigDecimal(edt_Qtyx.getText().toString()));
            orderPromotion_NF.setFQTY(new BigDecimal(edt_FI1.getText().toString()).setScale(0, RoundingMode.HALF_UP));
            promo.add(orderPromotion_NF);
        }
        if (!PromoOtherArr.isEmpty()) {
            promo.addAll(PromoOtherArr);
        }
        return promo;
    }
}

