package com.inhatc.mapsosa;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class Firebase extends AppCompatActivity implements View.OnClickListener {
    FirebaseDatabase myFirebase;                // Firebase object
    DatabaseReference myDB_Reference = null;    // Firebase DB reference

    // HashMap<String, Object> childNode = null;
    HashMap<String, Object> Phone_Num = null;
    PhoneNumInfo objCustomerInfo = null;

    TextView txtFirebase;                       // Textview object
    Button btnInsert;                           // Button object
    EditText edtPhoneNum;                   // EditText object
    String strHeader = "Phone Number";  // Firebase Key
    String strCName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);

        txtFirebase = (TextView) findViewById(R.id.txtFirebase);
        edtPhoneNum = (EditText) findViewById(R.id.edtPhoneNum);

        btnInsert = (Button) findViewById(R.id.btnInsert);
        btnInsert.setOnClickListener(this);
        myFirebase = FirebaseDatabase.getInstance();    // Get FirebaseDatabase instance
        myDB_Reference = myFirebase.getReference();     // Get Firebase reference
        Phone_Num = new HashMap<>();               // Create HashMap
    }



    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnInsert:
                strCName = edtPhoneNum.getText().toString();
                if (!strCName.equals("")){
                    Phone_Num.put("Phone Number", strCName);
                    mSet_FirebaseDatabase(true);    // App -> Firebase DB
                    mGet_FirebaseDatabase();              // Firebase DB -> App
                }
                edtPhoneNum.setText("");
                break;
            default:
                break;
        }
    }

    // Data : App -> Firebase DB
    public void mSet_FirebaseDatabase(boolean bFlag) {
        // bFlag = true(add)/false(delete)
        if (bFlag){
            myDB_Reference.child(strHeader).child(strCName).setValue(Phone_Num);
        }
    }

    // Data : Firebase DB -> App
    public void mGet_FirebaseDatabase() {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                txtFirebase.setText("연동된 번호 : ");
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String strKey = postSnapshot.getKey();
                    txtFirebase.append("\n - Phone Number : " + strKey);
                }
            }

            @Override
            public void onCancelled(DatabaseError dbError) {
                // Failed to read value
                Log.w("TAG : ", "Failed to read value.", dbError.toException());
            }
        };
        Query sortbyName = FirebaseDatabase.getInstance().getReference()
                .child(strHeader).orderByChild(strCName);
        sortbyName.addListenerForSingleValueEvent(postListener);
    }
}
