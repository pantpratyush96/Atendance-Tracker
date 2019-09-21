package com.example.pratyush.geofencing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private ArrayList<String> mIDArrayList = new ArrayList<>();
    private ArrayList<String> mPSWDArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        final EditText iD_no_text = (EditText)findViewById(R.id.idNo_text);
        final EditText pswd_text = (EditText)findViewById((R.id.password_text));
        final Button mButton5 = (Button)findViewById(R.id.login_button);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        final ArrayAdapter<String> idArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mIDArrayList);
        final ArrayAdapter<String> pswdArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mPSWDArrayList);


        mButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String iD_no = iD_no_text.getText().toString();
                final String pswd = pswd_text.getText().toString();
                if (iD_no.length() != 5){
                    alertDialog.setMessage("Please Enter a Valid College Identification Number!")
                            .setCancelable(false)
                            .setPositiveButton("OK!", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    iD_no_text.setText("");
                                }
                            }).show();
                } else {
                    mButton5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("Login", "Login pressed");
                            mDatabase.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String success = "";
                                    for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                                        String idValue = snapshot.getKey();
                                        String pswdValue = snapshot.getValue().toString();
                                        if (idValue.matches(iD_no) && pswdValue.matches(pswd)){
                                            success = "true";
                                            break;
                                        } else {
                                            success = "false";
                                        }
                                    }
                                    if (success == "true"){
                                        alertDialog.setMessage("Login Successful!")
                                                .setCancelable(false)
                                                .setPositiveButton("OK!", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                                                        startActivity(intent);
                                                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("demo.recgonition.face.facerecognitiondemo");
                                                        if (launchIntent != null)
                                                        {
                                                            startActivity(launchIntent);
                                                        }
                                                        finish();
                                                    }
                                                }).show();
                                    } else if (success == "false") {
                                        alertDialog.setMessage("ID or Password incorrect. Please Check and re-enter!")
                                                .setCancelable(false)
                                                .setPositiveButton("OK!", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        iD_no_text.setText("");
                                                        pswd_text.setText("");
                                                    }
                                                }).show();
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            }
        });
    }
}