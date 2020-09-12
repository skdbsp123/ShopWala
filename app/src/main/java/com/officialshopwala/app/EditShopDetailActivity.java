package com.officialshopwala.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditShopDetailActivity extends AppCompatActivity {

    ImageView shopImageView;
    TextView shopNameTextView;
    EditText shopLinkEditText;
    EditText shopPhoneNumberEditText;
    EditText shopAddressEditText;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser user;
    String phoneNumber;

    String shopName = "";
    String shopLink = "";
    String shopAddress = "";

    public void backFromEditBusinessDetail (View view) {
        finish();
    }

    public void saveBusinessDetailChange (View view) {
        shopAddress = shopAddressEditText.getText().toString();
        databaseReference.child(phoneNumber).child("businessAddress").setValue(shopAddress).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(EditShopDetailActivity.this, "Shop Detail Saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shop_detail);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Sellers");
        user = FirebaseAuth.getInstance().getCurrentUser();
        phoneNumber = "+919000990098";
        if ( user != null) {
            phoneNumber = user.getPhoneNumber();
        }
        initViews();
        initNonEditable();
    }

    private void initNonEditable() {
        databaseReference.child(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    if (keyNode.getKey().equals("businessName")) {
                        shopName = keyNode.getValue(String.class);
                        shopNameTextView.setText(shopName);
                        Log.i("shopName", keyNode.getValue(String.class));
                    }
                    if (keyNode.getKey().equals("businessAddress")) {
                        shopAddress = keyNode.getValue(String.class);
                        shopAddressEditText.setText(shopAddress);
                        Log.i("shopadress", keyNode.getValue(String.class));
                    }
                    if (keyNode.getKey().equals("businessLink")) {
                        shopLink = keyNode.getValue(String.class);
                        shopLinkEditText.setText(shopLink);
                        Log.i("shopLink", keyNode.getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        shopPhoneNumberEditText.setText(phoneNumber);
    }

    private void initViews() {
        shopImageView = findViewById(R.id.businessShopImageView);
        shopNameTextView = findViewById(R.id.editBusinessDetailshopNameTextView);
        shopLinkEditText = findViewById(R.id.editBusinessDetailShopLinkEditText);
        shopPhoneNumberEditText = findViewById(R.id.editBusinessDetailShopPhoneNumberEditText);
        shopAddressEditText = findViewById(R.id.editBusinessDetailShopAddressEditText);
    }
}