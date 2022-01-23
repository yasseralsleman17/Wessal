package com.example.wessalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {


    EditText fullName, email, password, userid;
    Button register_bt, login_bt;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    CheckBox faculty_member_Check, student_Check;

    String fullname_tx, email_tx, passwors_tx, userid_tx, account_type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        fullName = findViewById(R.id.registerName);
        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.registerPassword);
        userid = findViewById(R.id.registerId);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        register_bt = findViewById(R.id.register_bt);
        login_bt = findViewById(R.id.login_bt);

        faculty_member_Check = findViewById(R.id.Faculty_check);
        student_Check = findViewById(R.id.Student_check);


        student_Check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton CompoundButton, boolean b) {
                if (CompoundButton.isChecked()) {
                    faculty_member_Check.setChecked(false);
                    account_type = "Student";
                }
                if (!CompoundButton.isChecked()) {
                    account_type = "";
                }
            }
        });

        faculty_member_Check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton CompoundButton, boolean b) {

                if (CompoundButton.isChecked()) {
                    student_Check.setChecked(false);
                    account_type = "Faculty member";
                }
                if (!CompoundButton.isChecked()) {
                    account_type = "";
                }
            }
        });


        register_bt.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {


                fullname_tx = fullName.getText().toString();
                email_tx = email.getText().toString();
                passwors_tx = password.getText().toString();
                userid_tx = userid.getText().toString();


                if (fullname_tx.isEmpty()) {
                    fullName.setError("FullName is Required");
                    return;
                }
                if (email_tx.isEmpty()) {
                    email.setError("Email is Required");
                    return;
                }
                if (passwors_tx.isEmpty()) {
                    password.setError("Password is Required");
                    return;
                }
                if (passwors_tx.length() < 6) {
                    password.setError("Password should be at least 6 characters.");
                    return;
                }
                if (userid_tx.isEmpty()) {
                    userid.setError("User id is Required");
                    return;
                }
                if (account_type.equals("")) {

                    Toast.makeText(getApplicationContext(), "Select your account type", Toast.LENGTH_SHORT).show();
                    return;
                }


                fAuth.createUserWithEmailAndPassword(email_tx, passwors_tx)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                Toast.makeText(getApplicationContext(), "Account Created Successfully", Toast.LENGTH_SHORT).show();

                                FirebaseUser user = fAuth.getCurrentUser();

                                DocumentReference user_ref = fStore.collection("Users").document(user.getUid());

                                Map<String, Object> userdata = new HashMap<>();

                                userdata.put("FullName", fullname_tx);
                                userdata.put("UserEmail", email_tx);
                                userdata.put("Id", userid_tx);
                                userdata.put("Password", passwors_tx);
                                userdata.put("Acount_type", account_type);

                                user_ref.set(userdata)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {


                                                if (account_type.equals("Student")) {
                                                    startActivity(new Intent(getApplicationContext(), StudentHomePage.class));

                                                } else if (account_type.equals("Faculty member")) {
                                                    startActivity(new Intent(getApplicationContext(), FacultymemberHomePage.class));


                                                }

                                            }
                                        });


                            }


                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(Register.this, "Faild to Create Account,try again later", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        login_bt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LogIn.class));

            }
        });


    }
}