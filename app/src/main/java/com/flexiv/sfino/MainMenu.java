package com.flexiv.sfino;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.flexiv.sfino.adapter.Adapter_Cus_area;
import com.flexiv.sfino.model.Card_cus_area;
import com.flexiv.sfino.utill.DBHelper;
import com.flexiv.sfino.utill.SharedPreference;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainMenu extends AppCompatActivity {

    private RecyclerView cusRecyclerView;
    private Adapter_Cus_area cusAdapter;
    private RecyclerView.LayoutManager cusLayoutManager;
    private TextView itemSelectTitle;
    private View contextView;
    private TextView textViewCustomer;
    private TextView textViewArea;
    //Card Button
    private CardView cardView_Order;

    private void initComp() {
        //Components
        TextView textViewRep_name = findViewById(R.id.textViewRep_name);
        TextView textViewDis_name = findViewById(R.id.textViewDist_name);
        textViewCustomer = findViewById(R.id.textViewCustomer);
        textViewArea = findViewById(R.id.textViewArea);
        cardView_Order = findViewById(R.id.cardView_Order);
        contextView = findViewById(R.id.context_view);

        //set Click
        cardView_Order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SharedPreference.COM_AREA == null) {
                    Snackbar snackbar = Snackbar
                            .make(contextView, "Area Can not be Empty!", Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.RED)
                            .setAction("Select", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    openAreaSelector(view);
                                }
                            });

                    snackbar.show();
                } else if (SharedPreference.COM_CUSTOMER == null) {
                    Snackbar snackbar = Snackbar
                            .make(contextView, "Customer Can not be Empty!", Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.RED)
                            .setAction("Select", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    openCusSelector(view);
                                }
                            });

                    snackbar.show();
                } else {
                    Intent i = new Intent(MainMenu.this, Order.class);
                    startActivity(i);
                }
            }
        });

        //setArgs
        textViewRep_name.setText(SharedPreference.COM_REP == null ? "Dr.Wolf - Flexiv" : SharedPreference.COM_REP.getRepName());
        textViewDis_name.setText(SharedPreference.COM_REP == null ? "Please Contact Flexiv MicroSystem" : SharedPreference.COM_REP.getDisName());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        initComp();
    }

    public void openAreaSelector(View v) {

        SharedPreference.COM_CUSTOMER = null;
        openDialogAreaCusSelector(new DBHelper(this).getAreas(SharedPreference.disid), "SELECT AREA", 0);

    }

    public void openCusSelector(View v) {

        try {
            ArrayList<Card_cus_area> card = new DBHelper(this).getCustomers(SharedPreference.disid, SharedPreference.COM_AREA.getTxt_code());
            assert card != null;
            if (!card.isEmpty()) {
                openDialogAreaCusSelector(new DBHelper(this).getCustomers(SharedPreference.disid, SharedPreference.COM_AREA.getTxt_code()), "SELECT CUSTOMER", 1);
            } else {
                Snackbar snackbar = Snackbar
                        .make(contextView, "There no Customers Available in this Area. Please Select another Area!", Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.RED)
                        .setAction("Select", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                openAreaSelector(view);
                            }
                        });

                snackbar.show();
            }
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
                if (SharedPreference.COM_CUSTOMER != null) {
                    textViewCustomer.setText(SharedPreference.COM_CUSTOMER.getTxt_name());
                } else {
                    textViewCustomer.setText("");
                }
                if (SharedPreference.COM_AREA != null) {
                    textViewArea.setText(SharedPreference.COM_AREA.getTxt_name());
                } else {
                    textViewArea.setText("");
                }

            }
        });
    }


}
