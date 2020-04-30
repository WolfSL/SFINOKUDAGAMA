package com.flexiv.sfino;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.flexiv.sfino.adapter.Adapter_Batch;
import com.flexiv.sfino.adapter.Adapter_Item;
import com.flexiv.sfino.fragment.Order_main;
import com.flexiv.sfino.fragment.Order_sub;
import com.flexiv.sfino.model.Modal_Batch;
import com.flexiv.sfino.model.Modal_Item;
import com.flexiv.sfino.utill.SharedPreference;

import java.util.ArrayList;

public class Order extends AppCompatActivity {

    //Components
    ImageButton imageButton_done;
    ImageButton imageButton_back;

    TextView textView_cusName;
    TextView textView_Area;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        imageButton_done = findViewById(R.id.imageButton_done);
        imageButton_back = findViewById(R.id.imageButton_back);

        textView_cusName = findViewById(R.id.textView_cusName);
        textView_Area = findViewById(R.id.textView_Area);

        if(SharedPreference.COM_AREA!=null && SharedPreference.COM_CUSTOMER!=null){
            textView_cusName.setText(SharedPreference.COM_CUSTOMER.getTxt_name());
            textView_Area.setText(SharedPreference.COM_AREA.getTxt_name());
        }else{
            Toast.makeText(this,"Customer or Area Cannot be Empty",Toast.LENGTH_LONG).show();
            onBackPressed();
        }

        getSupportFragmentManager().beginTransaction().add(R.id.OrderFrame,new Order_main(this)).commit();
    }

    public void changeNavButton(int type){
        if(type==1){
            imageButton_done.animate().alpha(1.0f);
            imageButton_back.setOnClickListener(v -> onBackPressed());
        }else if(type==2){
            imageButton_done.animate().alpha(0.0f);
            imageButton_back.setOnClickListener(v -> {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.animator.in, R.animator.out);
                transaction.replace(R.id.OrderFrame, new Order_main(Order.this)).commit();
            });
        }
    }


    public void LoadFragment_sub() {



        ArrayList<Modal_Item> arr_item = new ArrayList<>();

        arr_item.add(new Modal_Item("FG001",234.12,"Test Item 1"));
        arr_item.add(new Modal_Item("FG002",312.60,"Test Item 2"));arr_item.add(new Modal_Item("FG001",234.12,"Test Item 1"));
        arr_item.add(new Modal_Item("FG002",312.60,"Test Item 2"));arr_item.add(new Modal_Item("FG001",234.12,"Test Item 1"));
        arr_item.add(new Modal_Item("FG002",312.60,"Test Item 2"));arr_item.add(new Modal_Item("FG001",234.12,"Test Item 1"));
        arr_item.add(new Modal_Item("FG002",312.60,"Test Item 2"));arr_item.add(new Modal_Item("FG001",234.12,"Test Item 1"));
        arr_item.add(new Modal_Item("FG002",312.60,"Test Item 2"));arr_item.add(new Modal_Item("FG001",234.12,"Test Item 1"));
        arr_item.add(new Modal_Item("FG002",312.60,"Test Item 2"));arr_item.add(new Modal_Item("FG001",234.12,"Test Item 1"));
        arr_item.add(new Modal_Item("FG002",312.60,"Test Item 2"));arr_item.add(new Modal_Item("FG001",234.12,"Test Item 1"));
        arr_item.add(new Modal_Item("FG002",312.60,"Test Item 2"));arr_item.add(new Modal_Item("FG001",234.12,"Test Item 1"));
        arr_item.add(new Modal_Item("FG002",312.60,"Test Item 2"));arr_item.add(new Modal_Item("FG001",234.12,"Test Item 1"));
        arr_item.add(new Modal_Item("FG002",312.60,"Test Item 2"));arr_item.add(new Modal_Item("FG001",234.12,"Test Item 1"));
        arr_item.add(new Modal_Item("FG002",312.60,"Test Item 2"));
        arr_item.add(new Modal_Item("FG001", 234.12, "Test Item 1"));
        arr_item.add(new Modal_Item("FG002", 312.60, "Test Item 2"));

        openDialogSelector_Items(arr_item, "SELECT ITEM", 0);
    }

    RecyclerView SelectorrecyclerView;
    private Adapter_Item adapter_item;
    private RecyclerView.LayoutManager cusLayoutManager;
    private TextView itemSelectTitle;
    private void openDialogSelector_Items(ArrayList<Modal_Item> arr_cus, String title, int type) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this,R.style.mydialog);

        View row = getLayoutInflater().inflate(R.layout.item_selector, null);
        SelectorrecyclerView = row.findViewById(R.id.itemSelectorRecView);


        alertBuilder.setView(row);
        AlertDialog dialog = alertBuilder.create();


        // SelectorrecyclerView.setHasFixedSize(true);
        cusLayoutManager = new LinearLayoutManager(this);
        adapter_item = new Adapter_Item(arr_cus,dialog,this);
        SelectorrecyclerView.setLayoutManager(cusLayoutManager);
        SelectorrecyclerView.setAdapter(adapter_item);

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
                adapter_item.getFilter().filter(newText);
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

            }
        });
    }


    //Load Batchs
    public void LoadFragment_sub_batchs() {

        ArrayList<Modal_Batch> arr_batch = new ArrayList<>();
        arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G001",22545,410.23));
        arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G002",45.23,44578.22));
        arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G003",10,32535.20));
        arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G004",356.50,5545.20));
        arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G005",2445,120.32));arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G001",22545,410.23));
        arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G002",45.23,44578.22));
        arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G003",10,32535.20));
        arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G004",356.50,5545.20));
        arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G005",2445,120.32));arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G001",22545,410.23));
        arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G002",45.23,44578.22));
        arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G003",10,32535.20));
        arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G004",356.50,5545.20));
        arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G005",2445,120.32));arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G001",22545,410.23));
        arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G002",45.23,44578.22));
        arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G003",10,32535.20));
        arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G004",356.50,5545.20));
        arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G005",2445,120.32));arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G001",22545,410.23));
        arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G002",45.23,44578.22));
        arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G003",10,32535.20));
        arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G004",356.50,5545.20));
        arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G005",2445,120.32));arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G001",22545,410.23));
        arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G002",45.23,44578.22));
        arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G003",10,32535.20));
        arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G004",356.50,5545.20));
        arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G005",2445,120.32));arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G001",22545,410.23));
        arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G002",45.23,44578.22));
        arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G003",10,32535.20));
        arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G004",356.50,5545.20));
        arr_batch.add(new Modal_Batch("FG001",223.98,"Test item 1","G005",2445,120.32));

        openDialogSelector_batchs(arr_batch, "SELECT BATCH", 0);
    }

    private Adapter_Batch adapter_batch;
    private void openDialogSelector_batchs(ArrayList<Modal_Batch> arr_cus, String title, int type) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this,R.style.mydialog);

        View row = getLayoutInflater().inflate(R.layout.item_selector, null);
        SelectorrecyclerView = row.findViewById(R.id.itemSelectorRecView);


        alertBuilder.setView(row);
        AlertDialog dialog = alertBuilder.create();


        // SelectorrecyclerView.setHasFixedSize(true);
        cusLayoutManager = new LinearLayoutManager(this);
        adapter_batch = new Adapter_Batch(arr_cus,dialog,this);
        SelectorrecyclerView.setLayoutManager(cusLayoutManager);
        SelectorrecyclerView.setAdapter(adapter_batch);

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
                adapter_batch.getFilter().filter(newText);
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

            }
        });
    }

    public void LoadOrderSub(Modal_Batch item){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.in, R.animator.out);
        transaction.replace(R.id.OrderFrame, new Order_sub(this,item)).commit();
        //getSupportFragmentManager().beginTransaction().replace(R.id.OrderFrame,new Order_sub(this,item)).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
