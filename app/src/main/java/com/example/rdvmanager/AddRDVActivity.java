package com.example.rdvmanager;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.Date;

public class AddRDVActivity extends AppCompatActivity {

    private EditText mTitleEditText;
    private EditText mDescriptionEditText;
    private EditText mAddressEditText;
    private EditText mContactEditText;
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;
    private RadioButton mChoice1;
    private RadioButton mChoice2;
    private RadioButton mChoice3;

    static int delay;

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
        mChoice1 = findViewById(R.id.choice1);
        mChoice2 = findViewById(R.id.choice2);
        mChoice3 = findViewById(R.id.choice3);

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



                if(mChoice1.isChecked()) delay = 5000; // 86400000 // 1 day
                if(mChoice2.isChecked()) delay = 172800000; // 2 days
                if(mChoice3.isChecked()) delay = 604800000; // 1 week

                if (title.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Specify a Title to save !", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Ajout notification
                CreateNotification(view, delay);

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

    public void CreateNotification(View view, int delay) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM}, PERMISSION_REQUEST_CODE);
            return;
        }
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("title", mTitleEditText.getText().toString());
        intent.putExtra("description", mDescriptionEditText.getText().toString());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        int year = mDatePicker.getYear();
        int month = mDatePicker.getMonth();
        int day = mDatePicker.getDayOfMonth();
        int hour = mTimePicker.getHour();
        int minute = mTimePicker.getMinute();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        long timeInMillis = calendar.getTimeInMillis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis - delay, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis - delay, pendingIntent);
        }
    }


    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission accordée, créez la notification
                CreateNotification(null, delay);
            } else {
                // Permission refusée, affichez un message d'erreur
                Toast.makeText(this, "Permission to set exact alarms denied. Notification won't be set.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

