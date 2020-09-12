package com.officialshopwala.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddProductActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser user;

    SpinnerItemAdapter quantityTypeSpinnerAdapter;
    static SpinnerItemAdapter categoryTypeSpinnerAdapter;

    ArrayList<SpinnerItems> quantityTypeSpinnerItemList = new ArrayList<>();
    ArrayList<SpinnerItems> categorySpinnerItemList = new ArrayList<>();

    Spinner quantityTypeSpinner;
    Spinner categorySpinner;

    SpinnerItems productQuantityType;
    SpinnerItems productCategoryType;

    ImageView AddProductImageView;
    EditText AddProductName;
    EditText AddProductPrice;
    EditText AddProductDescription;
    Button AddProductSaveButton;

    long productId = 1000;

    String PhoneNumber;

    public void addProduct (View view) {

        String name = AddProductName.getText().toString();
        int price = Integer.parseInt(AddProductPrice.getText().toString());
        String quantityType = "";
        if(productQuantityType!=null) {
            quantityType = productQuantityType.getSpinnerItemName();
        }
        String productCategory="";
        if (productCategoryType !=null){
            productCategory = productCategoryType.getSpinnerItemName();
        }


        String description = AddProductDescription.getText().toString();

        if (name != null && price > 0 && quantityType!=null) {

            if (productId >= 1000) {

                PhoneNumber = "+919000990098";
                if (user != null) {
                    PhoneNumber = user.getPhoneNumber();
                }

                databaseReference.child("ProductsActive").child(String.valueOf(productId)).child("productId").setValue(productId);
                databaseReference.child("ProductsActive").child(String.valueOf(productId)).child("seller").setValue(PhoneNumber);

                databaseReference.child("Sellers").child(PhoneNumber).child("Products").child(String.valueOf(productId)).child("productId").setValue(productId);
                databaseReference.child("Sellers").child(PhoneNumber).child("Products").child(String.valueOf(productId)).child("name").setValue(name);
                databaseReference.child("Sellers").child(PhoneNumber).child("Products").child(String.valueOf(productId)).child("price").setValue(price);
                databaseReference.child("Sellers").child(PhoneNumber).child("Products").child(String.valueOf(productId)).child("quantityType").setValue(quantityType);
                databaseReference.child("Sellers").child(PhoneNumber).child("Products").child(String.valueOf(productId)).child("productCategory").setValue(productCategory);
                databaseReference.child("Sellers").child(PhoneNumber).child("Products").child(String.valueOf(productId)).child("description").setValue(description).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddProductActivity.this, "Product Added", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

            }
        }
    }

    public void addProductBackButtonCicked(View view) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        user = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference.child("ProductsActive").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    productId = 1000 + dataSnapshot.getChildrenCount() + 1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        initialiseViews();
        setSpinners();

    }

    public void initialiseViews() {
        quantityTypeSpinner = findViewById(R.id.editProductQuantityTypeSpinner);
        categorySpinner = findViewById(R.id.editChoseCtgSpinner);
        AddProductImageView = findViewById(R.id.editProductimageView);
        AddProductName = findViewById(R.id.editProductProductName);
        AddProductPrice = findViewById(R.id.editProductPrice);
        AddProductDescription = findViewById(R.id.editProductDescription);
        AddProductSaveButton = findViewById(R.id.updateCategorySaveButton);
    }

    private void setSpinners() {
        initCategoryList();
        initQuantityTypeList();

        quantityTypeSpinnerAdapter = new SpinnerItemAdapter(this, quantityTypeSpinnerItemList);
        categoryTypeSpinnerAdapter = new SpinnerItemAdapter(this, categorySpinnerItemList);

        quantityTypeSpinner.setAdapter(quantityTypeSpinnerAdapter);
        categorySpinner.setAdapter(categoryTypeSpinnerAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                productCategoryType = (SpinnerItems) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        quantityTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                productQuantityType = (SpinnerItems)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void initCategoryList() {
        categorySpinnerItemList.clear();
        SQLiteDatabase myDatabase = this.openOrCreateDatabase("CATEGORY_NAME_DATABASE", MODE_PRIVATE, null);
          Cursor c = myDatabase.rawQuery("SELECT * FROM categoryNamesTable", null);
          int nameIndex = c.getColumnIndex("name");

          if(c!=null && c.moveToFirst()) {
              Log.i("first", c.getString(nameIndex));
              for ( int i = 0 ; i < 100 ; i++ ) {
                  if (c.moveToNext()) {
                      categorySpinnerItemList.add(new SpinnerItems(c.getString(nameIndex)));
                  } else {
                      break;
                  }
              }
          }
          c.close();
    }

    private void initQuantityTypeList() {
        quantityTypeSpinnerItemList.add(new SpinnerItems(getString(R.string.AddProductPerUnit)));
        quantityTypeSpinnerItemList.add(new SpinnerItems(getString(R.string.AddProductPerKg)));
        quantityTypeSpinnerItemList.add(new SpinnerItems(getString(R.string.AddProductPerGm)));
        quantityTypeSpinnerItemList.add(new SpinnerItems(getString(R.string.AddProductPerLtr)));
        quantityTypeSpinnerItemList.add(new SpinnerItems(getString(R.string.AddProductPerMl)));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        initCategoryList();
    }
}