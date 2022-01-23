package com.example.wessalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.gridlayout.widget.GridLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ChooseClub extends AppCompatActivity {


    TextView student_name;

    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;


    GridLayout gridl_chose_club;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_club);


        student_name = findViewById(R.id.student_name);
        gridl_chose_club = findViewById(R.id.gridl_chose_club);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        FirebaseUser user = fAuth.getCurrentUser();


        fStore.collection("Users").document(user.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        student_name.setText(documentSnapshot.getString("FullName"));
                    }
                });


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


    }


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


        gridl_chose_club.addView(view);

    }


}