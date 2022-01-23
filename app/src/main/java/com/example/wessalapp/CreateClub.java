package com.example.wessalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateClub extends AppCompatActivity {


    Button create_club_bt;
    EditText club_name, club_description;

    ImageView club_img;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;

    String club_name_tx, club_description_tx;

    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_club);


        create_club_bt = findViewById(R.id.create_club_bt);
        club_name = findViewById(R.id.club_name);
        club_description = findViewById(R.id.club_description);

        club_img = findViewById(R.id.club_img);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        club_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosepic();
            }
        });

        create_club_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                club_name_tx = club_name.getText().toString();
                club_description_tx = club_description.getText().toString();


                if (club_name_tx.isEmpty()) {
                    club_name.setError("Email is Required");
                    return;
                }

                if (club_description_tx.isEmpty()) {
                    club_description.setError("Password is Required");
                    return;
                }
                if (imageUri == null) {
                    Toast.makeText(getApplicationContext(), "Add Club image", Toast.LENGTH_SHORT).show();
                    return;
                }


                DocumentReference club_ref = fStore.collection("Clubs").document();

                Map<String, Object> clubdata = new HashMap<>();

                clubdata.put("Club_name", club_name_tx);
                clubdata.put("Club_description", club_description_tx);
                clubdata.put("Club_id", club_ref.getId());
                clubdata.put("manager_id", fAuth.getCurrentUser().getUid());
                clubdata.put("Accepted", "Not_Accepted");
                clubdata.put("Comment_Count", 0);
                clubdata.put(fAuth.getCurrentUser().getUid(), "ok");


                uploadpic(club_ref.getId());

                club_ref.set(clubdata)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Club added successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), StudentHomePage.class));
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Faild to Create club,try again later", Toast.LENGTH_SHORT).show();

                            }
                        });


            }
        });
    }

    private void choosepic() {

        Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            club_img.setImageURI(imageUri);


        }
    }

    private void uploadpic(String id) {

        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading image . . . . ");
        pd.show();


        StorageReference ImagesRef = storageReference.child("images/" + id);

        ImagesRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Image Uploaded.", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Faild to upload image", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progresspercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        pd.setMessage("percent: " + (int) progresspercent + " %");
                    }
                });


    }
}