package com.example.wessalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.gridlayout.widget.GridLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

public class StudentHomePage extends AppCompatActivity {


    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;


    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;


    GridLayout student_clubs, student_events;
String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home_page);


        drawerLayout = findViewById(R.id.student_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);


        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        navigationView = findViewById(R.id.student_nav);

        navigationView.setNavigationItemSelectedListener((item) -> {

            switch (item.getItemId()) {
                case R.id.student_home_page:
                    startActivity(new Intent(getApplicationContext(), StudentHomePage.class));
                    break;
                case R.id.Create_Club:
                    startActivity(new Intent(getApplicationContext(), CreateClub.class));

                    break;
                case R.id.student_profile:
                    startActivity(new Intent(getApplicationContext(), StudentProfile.class));
                    break;
                case R.id.log_out:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    fAuth.signOut();
                    break;

            }
            return true;

        });


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        student_clubs = findViewById(R.id.student_clubs);
        student_events = findViewById(R.id.student_events);

       userid= fAuth.getCurrentUser().getUid();
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
                                    viewClub(list.get(i));


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

    private void viewClub(DocumentSnapshot documentSnapshot) {

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


                if (!documentSnapshot.contains(userid)){


                    Intent i = new Intent(getApplicationContext(), RegisterInClub.class);
                    i.putExtra("club_id", clubid);
                    startActivity(i);
                } else {


                    Intent i = new Intent(getApplicationContext(), ClubPage.class);
                    i.putExtra("club_id", clubid);
                    startActivity(i);
                }

            }
        });


        student_clubs.addView(view);

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



                if (!documentSnapshot.contains(userid)){


                    Intent i = new Intent(getApplicationContext(), RegisterInAcctivity.class);
                    i.putExtra("activity_id", event_id);
                    startActivity(i);
                } else {

                    Intent i = new Intent(getApplicationContext(), AcctivityPage.class);
                    i.putExtra("activity_id", event_id);
                    startActivity(i);
                }


            }
        });


        student_events.addView(view);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}