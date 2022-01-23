package com.example.wessalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClubPage extends AppCompatActivity {


    TextView club_descrip;

    ImageView imageview;

    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    String clubid;
    String name;

    ImageButton add_activity;

    LinearLayout linear_discussion;

    ImageView bt_add_comment;
    EditText add_comment;




    GridLayout gridlayout4;



    long count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_page);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            clubid = extras.getString("club_id");

        }
        imageview = findViewById(R.id.clubimg);

        club_descrip = findViewById(R.id.club_description2);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        fStore.collection("Clubs").document(clubid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        club_descrip.setText(documentSnapshot.getString("Club_description"));
                        club_descrip.setMovementMethod(new ScrollingMovementMethod());


                        storageReference.child("images/" + clubid).getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Glide.with(getApplicationContext())
                                                .load(uri)
                                                .into(imageview);

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {

                                    }
                                });

                     count = (long) documentSnapshot.get("Comment_Count");

                        for (int i=1;i<=count;i++ )
                        {
                            Map<String, Object> data= (Map<String, Object>) documentSnapshot.get(i+"");
                            viewcomments(data);
                        }
                    }
                });

        fStore.collection("Users").document(fAuth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        name=documentSnapshot.getString("FullName");

                    }
                });


        gridlayout4 = findViewById(R.id.gridlayout4);

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

                                if (list.get(i).getString("club_id").equals(clubid)) {
                                    viewActivity(list.get(i));

                                }
                            }
                        }

                    }
                });

        add_activity = findViewById(R.id.add_activity);

        add_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), CreateActivity.class);
                i.putExtra("club_id", clubid);
                startActivity(i);


            }
        });


        linear_discussion = findViewById(R.id.linear_discussion);

        add_comment = findViewById(R.id.add_comment);
        bt_add_comment = findViewById(R.id.bt_add_comment);

        bt_add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String  comment = add_comment.getText().toString();

                if (!comment.isEmpty()) {

                    addcomment(comment,count+1);


                    add_comment.setText("");
                }
            }
        });


    }

    private void viewcomments(Map<String, Object> data) {
        View view = getLayoutInflater().inflate(R.layout.comment_card, null);

        TextView commenttext = view.findViewById(R.id.mycoment);


        commenttext.setText(data.get("name").toString()+"\n"+data.get("Comment").toString());


        linear_discussion.addView(view);


    }


    private void addcomment(String comment, long count) {


        View view = getLayoutInflater().inflate(R.layout.comment_card, null);

        TextView commenttext = view.findViewById(R.id.mycoment);

        commenttext.setText(name+"\n"+comment);



        DocumentReference club_ref = fStore.collection("Clubs").document(clubid);

        Map<String, Object> Comentdata = new HashMap<>();

        Comentdata.put("name",name);
        Comentdata.put("Comment", comment);


        Map<String, Object> club_data = new HashMap<>();
        club_data.put("Comment_Count", count );
        club_data.put(String.valueOf(count ), Comentdata);


        club_ref.update(club_data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

        linear_discussion.addView(view);

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


                if (!documentSnapshot.contains(fAuth.getCurrentUser().getUid())){


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

        gridlayout4.addView(view);
    }

}