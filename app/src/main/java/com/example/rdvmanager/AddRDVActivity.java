package com.example.rdvmanager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;
import java.util.Date;

public class AddRDVActivity extends AppCompatActivity {

    private EditText mTitleEditText;
    private EditText mDescriptionEditText;
    private EditText mAddressEditText;
    private EditText mContactEditText;
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;

    // Notifications
    static String CHANNEL_ID = "channel_01";
    static int NOTIFICATION_ID = 100;
    static int REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rdv);
        CreateNotificationChannel();

        mTitleEditText = findViewById(R.id.editTextTitle);
        mDescriptionEditText = findViewById(R.id.editTextDescription);
        mAddressEditText = findViewById(R.id.editTextAddress);
        mContactEditText = findViewById(R.id.editTextContact);
        mDatePicker = findViewById(R.id.datePicker);
        mTimePicker = findViewById(R.id.timePicker);

        Button saveButton = findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = mTitleEditText.getText().toString();
                String description = mDescriptionEditText.getText().toString();
                String address = mAddressEditText.getText().toString();
                String contact = mContactEditText.getText().toString();
                int year = mDatePicker.getYear();
                int month = mDatePicker.getMonth();
                int day = mDatePicker.getDayOfMonth();
                int hour = mTimePicker.getHour();
                int minute = mTimePicker.getMinute();

                if (title.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Specify a Title to save !", Toast.LENGTH_SHORT).show();
                    return;
                }

                RDV rdv = new RDV(title, "" + day + "/" + month + "/" + year, "" + hour + ":" + minute, contact, address, description, false);

                RDVDAO rdvDAO = new RDVDAO(getApplicationContext());
                rdvDAO.open();
                long id = rdvDAO.addRDV(rdv);
                rdv.setId(id);
                Toast.makeText(AddRDVActivity.this, "New RDV added with ID " + rdv.getId(), Toast.LENGTH_SHORT).show();

                rdvDAO.close();


                Intent intent = new Intent(AddRDVActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void CreateNotificationChannel() {
        // Create a NotificationChannel, only for API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "RDV Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription("RDV Notification reminds the RDV");
            // register the channel
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void showNotification(View view, int delayInMs) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Titre")
                .setContentText("Contenu")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        // Delay before launching the notification
        notifBuilder.setWhen(System.currentTimeMillis() + delayInMs);

        // notificationId: unique identifier to define
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, notifBuilder.build());
    }
}

