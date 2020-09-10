package com.officialshopwala.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrdersActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    RecyclerView ordersRecyclerView;
    static OrderAdapter orderAdapter;
    static ArrayList<OrderItem> orderItemArrayList = new ArrayList<>();

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    ArrayList<String> dataKeys = new ArrayList<>();


    public void showOrderFilter (View view) {

    }

    public interface DataStatus {
        void DataIsLoaded(ArrayList<OrderItem> orderItemArrayList, ArrayList<String> dataKeys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);

        //set orders as selected
        bottomNavigationView.setSelectedItemId(R.id.bottombar_orders);

        //set on item selected
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.bottombar_home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.bottombar_orders:
                        return true;
                    case R.id.bottombar_products:
                        startActivity(new Intent(getApplicationContext(), ProductsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.bottombar_categories:
                        startActivity(new Intent(getApplicationContext(), CategoriesActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.bottombar_account:
                        startActivity(new Intent(getApplicationContext(), AccountActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }

                return false;
            }
        });

        callRetrieveData();
    }

    private void setOrderRecyclerView() {
        ordersRecyclerView.setHasFixedSize(true);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        orderAdapter = new OrderAdapter(getApplicationContext(), orderItemArrayList);
        ordersRecyclerView.setAdapter(orderAdapter);
    }

    private void callRetrieveData() {
        retrieveDataFromFirebase(new DataStatus() {
            @Override
            public void DataIsLoaded(ArrayList<OrderItem> orderItemArrayList, ArrayList<String> dataKeys) {
                setOrderRecyclerView();
            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });
    }

    private void retrieveDataFromFirebase( final DataStatus dataStatus) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Sellers");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        orderItemArrayList.clear();

        String phoneNumber = "+919000990098";
        databaseReference.child(phoneNumber).child("orders").child("all").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                addOrderToList(dataSnapshot);
                dataStatus.DataIsLoaded(orderItemArrayList, dataKeys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*String phoneNumber = user.getPhoneNumber();

        if (user!=null) {
            databaseReference.child(phoneNumber).child("orders").child("all").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    addOrderToList(dataSnapshot);
                    dataStatus.DataIsLoaded(orderItemArrayList, dataKeys);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }*/
    }

    public void addOrderToList (DataSnapshot dataSnapshot) {
        orderItemArrayList.clear();
        for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
            dataKeys.add(keyNode.getKey());
            OrderItem orderItem = keyNode.getValue(OrderItem.class);
            orderItemArrayList.add(orderItem);
        }
    }
}