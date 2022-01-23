package com.example.wessalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AcctivityPage extends AppCompatActivity {


    TextView activity_description2, act_date, act_time;

    ImageView imageview;

    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    String activity_id, account_type,name;

    int attendance_c;
    GridLayout attendance_grid;
    Button btn_Attend,Announce;
    List<String> arr = new ArrayList<String>();

    LinearLayout linear_discussion;

    ImageView bt_add_comment;
    EditText add_comment;

    long count=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acctivity_page);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            activity_id = extras.getString("activity_id");

        }


        imageview = findViewById(R.id.activityimg);

        activity_description2 = findViewById(R.id.activity_description2);
        act_date = findViewById(R.id.act_date);
        act_time = findViewById(R.id.act_time);
        attendance_grid = findViewById(R.id.attendance_grid);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        fStore.collection("Users").document(fAuth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        account_type=documentSnapshot.getString("Acount_type");
                        name=documentSnapshot.getString("FullName");

                    }
                });

        fStore.collection("Activity").document(activity_id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        activity_description2.setText(documentSnapshot.getString("Activity_description"));
                        activity_description2.setMovementMethod(new ScrollingMovementMethod());
                        act_date.setText(documentSnapshot.getString("date"));
                        act_time.setText(documentSnapshot.getString("time"));

                        Map<String, Object> Attendancedata = (Map<String, Object>) documentSnapshot.get("Attendance");

                        long Attendance_count = (long) Attendancedata.get("Attendance_count");
                        attendance_c = (int) Attendance_count;
                        for (int i = 0; i < Attendance_count; i++) {
                            arr.add(Attendancedata.get(String.valueOf(i+1)).toString());
                            TextView tv = new TextView(getApplicationContext());

                            tv.setText(Attendancedata.get(String.valueOf(i+1)).toString());

                            tv.setTextSize(20f);
                            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            tv.setTextColor(Color.BLACK);
                            tv.setPadding(10,10,10,10);

                            attendance_grid.addView(tv);

                        }

                        storageReference.child("images/" + activity_id).getDownloadUrl()
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


        btn_Attend = findViewById(R.id.btn_Attend);
        btn_Attend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(account_type.equals("Faculty member")){
                    Toast.makeText(getApplicationContext(), attendance_c+" students will be joining the event", Toast.LENGTH_SHORT).show();

                }
                else if(account_type.equals("Student")){


                           if(arr.contains(name))
                           {
                               Toast.makeText(getApplicationContext(), "Hi "+name+" you have already recorded  attendance  ", Toast.LENGTH_SHORT).show();


                           }
                           else
                           {


                               DocumentReference activity_ref = fStore.collection("Activity").document(activity_id);

                               Map<String, Object> Attendancedata = new HashMap<>();
                               Attendancedata.put("Attendance_count",attendance_c+1 );
                               Attendancedata.put(String.valueOf(attendance_c+1),name );
                                for(int i=0;i<arr.size();i++)
                                {
                                    Attendancedata.put(String.valueOf(i+1),arr.get(i) );

                                }


                               Map<String, Object> activitydata = new HashMap<>();

                               activitydata.put("Attendance",Attendancedata);
                               activity_ref.update(activitydata).addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void unused) {

                                       Toast.makeText(getApplicationContext(), " Recording Attendance Done  ", Toast.LENGTH_LONG).show();

                                   }
                               });







                           }







                }


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



        DocumentReference club_ref = fStore.collection("Activity").document(activity_id);

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

}