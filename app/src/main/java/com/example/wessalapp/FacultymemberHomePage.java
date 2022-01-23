package com.example.wessalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.gridlayout.widget.GridLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Objects;

public class FacultymemberHomePage extends AppCompatActivity {


    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;


    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;


    GridLayout maingraidlayout2,gridlayout1;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facultymember_home_page);


        maingraidlayout2 = findViewById(R.id.gridlayout2);
        gridlayout1 = findViewById(R.id.gridlayout1);

        drawerLayout = findViewById(R.id.admin_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);


        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        navigationView = findViewById(R.id.faculty_nav);

        navigationView.setNavigationItemSelectedListener((item) -> {

            switch (item.getItemId()) {
                case R.id.Club_requests:
                    startActivity(new Intent(getApplicationContext(), ClubRequests.class));
                    break;

                case R.id.faculty_home:
                    startActivity(new Intent(getApplicationContext(), FacultymemberHomePage.class));
                    break;
                case R.id.Create_enent:
                    Intent i = new Intent(getApplicationContext(), CreateActivity.class);
                    i.putExtra("club_id", "no_club");
                    startActivity(i);
                    break;
                case R.id.log_out:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    fAuth.signOut();

                    break;

            }
            return true;

        });

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        fStore.collection("Clubs").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {

                            return;
                        } else {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();


                            for (int i = 0; i < list.size(); i++) {


                                if (list.get(i).getString("Accepted").equals("Accepted"))
                                    viewclub(list.get(i));


                            }
                        }


                    }
                });




        fStore.collection("Activity").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {

                            return;
                        } else {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();


                            for (int i = 0; i < list.size(); i++) {


                                if (list.get(i).getString("club_id").equals("no_club")) {
                                    viewActivity(list.get(i));

                                }

                            }
                        }
                    }
                });



    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void viewclub(DocumentSnapshot documentSnapshot) {

        String clubid = documentSnapshot.getId();

        View view = getLayoutInflater().inflate(R.layout.show_club_activity, null);


        TextView clubname = view.findViewById(R.id.act_club_name);
        ImageView act_club_img = view.findViewById(R.id.act_club_img);

        clubname.setText(documentSnapshot.getString("Club_name"));


        storageReference.child("images/" + clubid).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .into(act_club_img);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                });


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), ClubPage.class);
                i.putExtra("club_id", clubid);
                startActivity(i);

            }
        });


        maingraidlayout2.addView(view);

    }


    private void viewActivity(DocumentSnapshot documentSnapshot) {

        String event_id = documentSnapshot.getId();

        View view = getLayoutInflater().inflate(R.layout.show_club_activity, null);


        TextView event_name = view.findViewById(R.id.act_club_name);
        ImageView act_club_img = view.findViewById(R.id.act_club_img);

        event_name.setText(documentSnapshot.getString("Activity_name"));


        storageReference.child("images/" + event_id).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .into(act_club_img);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                });


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AcctivityPage.class);
                i.putExtra("activity_id", event_id);
                startActivity(i);

            }
        });


        gridlayout1.addView(view);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}