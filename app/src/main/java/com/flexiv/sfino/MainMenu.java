package com.flexiv.sfino;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.flexiv.sfino.model.Card_cus_area;
import com.flexiv.sfino.utill.SharedPreference;

import java.util.ArrayList;

public class MainMenu extends AppCompatActivity {

    private RecyclerView cusRecyclerView;
    private Adapter_Cus_area cusAdapter;
    private RecyclerView.LayoutManager cusLayoutManager;
    private TextView itemSelectTitle;

    private TextView textViewCustomer;
    private TextView textViewArea;

    private void initComp() {
        //Components
        TextView textViewRep_name = findViewById(R.id.textViewRep_name);
        textViewCustomer = findViewById(R.id.textViewCustomer);
        textViewArea = findViewById(R.id.textViewArea);

        //Arr


        //setArgs
        textViewRep_name.setText(SharedPreference.COM_REP.getRepName());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        initComp();
    }

    public void openAreaSelector(View v) {

        SharedPreference.COM_CUSTOMER  = null;

        ArrayList<Card_cus_area> arr_cus = new ArrayList<>();
        arr_cus.add(new Card_cus_area("test Area", "Nawala"));
        arr_cus.add(new Card_cus_area("test Area", "Kaduwela"));
        arr_cus.add(new Card_cus_area("test Area", "Colombo"));
        arr_cus.add(new Card_cus_area("test Area", "Rajagiriya"));
        arr_cus.add(new Card_cus_area("test Area", "Nawala"));
        arr_cus.add(new Card_cus_area("test Area", "Kaduwela"));
        arr_cus.add(new Card_cus_area("test Area", "Colombo"));
        arr_cus.add(new Card_cus_area("test Area", "Rajagiriya"));
        arr_cus.add(new Card_cus_area("test Area", "Nawala"));
        arr_cus.add(new Card_cus_area("test Area", "Kaduwela"));
        arr_cus.add(new Card_cus_area("test Area", "Colombo"));
        arr_cus.add(new Card_cus_area("test Area", "Rajagiriya"));
        arr_cus.add(new Card_cus_area("test Area", "Nawala"));

        openDialogAreaCusSelector(arr_cus, "SELECT AREA", 0);

    }

    public void openCusSelector(View v) {
        ArrayList<Card_cus_area> arr_cus = new ArrayList<>();
        arr_cus.add(new Card_cus_area("test Pam", "Supun Pharmacy"));
        arr_cus.add(new Card_cus_area("test Pam", "Alpha Pharmacy"));
        arr_cus.add(new Card_cus_area("test Pam", "Beata Pharmacy"));
        arr_cus.add(new Card_cus_area("test Pam", "Gunapala Pharmacy"));
        arr_cus.add(new Card_cus_area("test Pam", "Naula Pharmacy"));
        arr_cus.add(new Card_cus_area("test Pam", "Supun Pharmacy"));
        arr_cus.add(new Card_cus_area("test Pam", "Alpha Pharmacy"));
        arr_cus.add(new Card_cus_area("test Pam", "Beata Pharmacy"));
        arr_cus.add(new Card_cus_area("test Pam", "Gunapala Pharmacy"));
        arr_cus.add(new Card_cus_area("test Pam", "Naula Pharmacy"));
        arr_cus.add(new Card_cus_area("test Pam", "Supun Pharmacy"));
        arr_cus.add(new Card_cus_area("test Pam", "Alpha Pharmacy"));
        arr_cus.add(new Card_cus_area("test Pam", "Beata Pharmacy"));
        arr_cus.add(new Card_cus_area("test Pam", "Gunapala Pharmacy"));
        arr_cus.add(new Card_cus_area("test Pam", "Naula Pharmacy"));
        arr_cus.add(new Card_cus_area("test Pam", "Supun Pharmacy"));
        arr_cus.add(new Card_cus_area("test Pam", "Alpha Pharmacy"));
        arr_cus.add(new Card_cus_area("test Pam", "Beata Pharmacy"));
        arr_cus.add(new Card_cus_area("test Pam", "Gunapala Pharmacy"));
        arr_cus.add(new Card_cus_area("test Pam", "Naula Pharmacy"));

        openDialogAreaCusSelector(arr_cus, "SELECT CUSTOMER", 1);

    }

    private void openDialogAreaCusSelector(ArrayList<Card_cus_area> arr_cus, String title, int type) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);

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

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (SharedPreference.COM_CUSTOMER != null) {
                    textViewCustomer.setText(SharedPreference.COM_CUSTOMER.getTxt_name());
                }else{
                    textViewCustomer.setText("");
                }
                if (SharedPreference.COM_AREA != null) {
                    textViewArea.setText(SharedPreference.COM_AREA.getTxt_name());
                }else{
                    textViewArea.setText("");
                }

            }
        });
    }


}
