package com.example.wessalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import android.icu.util.Calendar;

import java.util.HashMap;
import java.util.Map;

public class CreateActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {


    String clubid;

    Button create_activity_bt;
    EditText activity_name, activity_description,required_number;

    ImageView activity_img;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;

    String activity_name_tx, activity_description_tx,required_number_txt;

    Uri imageUri;
    ImageButton add_time, add_date;
    TextView act_date, act_time;

    String date, time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            clubid = extras.getString("club_id");

        }


        create_activity_bt = findViewById(R.id.create_activity_bt);
        activity_name = findViewById(R.id.activity_name);
        activity_description = findViewById(R.id.activity_description);
        required_number = findViewById(R.id.required_number);

        activity_img = findViewById(R.id.activity_img);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        activity_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosepic();
            }
        });


        act_date = findViewById(R.id.act_date);
        act_time = findViewById(R.id.act_time);

        add_date = findViewById(R.id.add_date);
        add_time = findViewById(R.id.add_time);

        add_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        add_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    showTimePickerDialog();
                }
            }
        });

        create_activity_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                activity_name_tx = activity_name.getText().toString();
                activity_description_tx = activity_description.getText().toString();
                required_number_txt = required_number.getText().toString();


                if (activity_name_tx.isEmpty()) {
                    activity_name.setError("Email is Required");
                    return;
                }

                if (activity_description_tx.isEmpty()) {
                    activity_description.setError("Password is Required");
                    return;
                }
                if (imageUri == null) {
                    Toast.makeText(getApplicationContext(), "Add Activity image", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (date.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Add Activity date", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (time.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Add Activity time", Toast.LENGTH_SHORT).show();
                    return;
                } if (required_number_txt.isEmpty()) {
                    required_number.setError("*This field is Required");

                    return;
                }


                DocumentReference activity_ref = fStore.collection("Activity").document();

                Map<String, Object> Attendancedata = new HashMap<>();
                Attendancedata.put("Attendance_count", 0);


                Map<String, Object> activitydata = new HashMap<>();

                activitydata.put("Activity_name", activity_name_tx);
                activitydata.put("Activity_description", activity_description_tx);
                activitydata.put("Activity_id", activity_ref.getId());
                activitydata.put("manager_id", fAuth.getCurrentUser().getUid());
                activitydata.put("date", date);
                activitydata.put("time", time);
                activitydata.put("required_number", Long.valueOf(required_number_txt));
                activitydata.put("register_number", 1);
                activitydata.put("Attendance", Attendancedata);
                activitydata.put(fAuth.getCurrentUser().getUid(), "ok");
                activitydata.put("club_id", clubid);
                activitydata.put("Comment_Count", 0);


                uploadpic(activity_ref.getId());

                activity_ref.set(activitydata)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Activity Created Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), StudentHomePage.class));
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Failed to Create Activity,try again later", Toast.LENGTH_SHORT).show();

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
            activity_img.setImageURI(imageUri);


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
                });


    }


    public void showDatePickerDialog() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            DatePickerDialog  datePickerDialog = new DatePickerDialog(
                    this,
                    this,
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH));


        datePickerDialog.show();}
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showTimePickerDialog() {
        TimePickerDialog TimePickerDialog = new TimePickerDialog(
                    this,
                    this,
                    Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                    Calendar.getInstance().get(Calendar.MINUTE),
                    DateFormat.is24HourFormat(getApplicationContext()));

        TimePickerDialog.show();
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        date = dayOfMonth + "/" + month + "/" + year;
        act_date.setText(date);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        time = hourOfDay + ":" + minute;
        act_time.setText(time);
    }
}