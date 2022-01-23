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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ClubRequests extends AppCompatActivity {


    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;



    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    LinearLayout club_requests_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_requests);

        club_requests_list = findViewById(R.id.club_requests_list);

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




                                if (list.get(i).getString("Accepted").equals("Not_Accepted"))
                                    viewclub(list.get(i));



                            }
                        }


                    }
                });







    }


    private void viewclub(DocumentSnapshot documentSnapshot) {

        String student_id = documentSnapshot.getString("manager_id");

        View view = getLayoutInflater().inflate(R.layout.club_card, null);


        TextView req_club_name = view.findViewById(R.id.req_club_name);
        TextView req_club_description = view.findViewById(R.id.req_club_description);
        TextView clum_stu_name = view.findViewById(R.id.clum_stu_name);



        TextView accept = view.findViewById(R.id.accept);
        TextView reject = view.findViewById(R.id.reject);
        TextView accepted = view.findViewById(R.id.accepted);





        ImageView req_club_img=view.findViewById(R.id.req_club_img);

        req_club_name.setText(  documentSnapshot.getString("Club_name"));
        req_club_description.setText(  documentSnapshot.getString("Club_description"));




        fStore.collection("Users").document(student_id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        clum_stu_name.setText(documentSnapshot.getString("FullName"));
                    }
                });



        storageReference.child("images/" + documentSnapshot.getString("Club_id")).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .into(req_club_img);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                });



        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DocumentReference club_ref = fStore.collection("Clubs").document(documentSnapshot.getId());

                Map<String, Object> clubdata = new HashMap<>();
                clubdata.put("Accepted", "Accepted");
                clubdata.put( fAuth.getCurrentUser().getUid(), "ok");


                club_ref.update(clubdata).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Toast.makeText(getApplicationContext(), "Club Accepted", Toast.LENGTH_LONG).show();

                        accepted.setText("  Accepted  ");
                        accept.setText("");
                        reject.setText("");

                    }
                });




            }
        });


        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DocumentReference club_ref = fStore.collection("Clubs").document(documentSnapshot.getId());



                club_ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Toast.makeText(getApplicationContext(), "Club rejected", Toast.LENGTH_LONG).show();
                        club_requests_list.removeView(view);


                    }
                });




            }
        });







        club_requests_list.addView(view);

    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}