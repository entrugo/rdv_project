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
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class AddRDVActivity extends BaseActivity {

    private EditText titleEditText;
    private EditText phoneNumber;
    private EditText descriptionEditText;
    private EditText addressEditText;
    private EditText contactEditText;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private RadioButton choice1;
    private RadioButton choice2;
    private RadioButton choice3;
    static String CHANNEL_ID = "channel_01";
    static int NOTIFICATION_ID = 100;
    static int REQUEST_CODE = 200;
    static int delay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rdv);
        CreateNotificationChannel();

        titleEditText = findViewById(R.id.editTextTitle);
        descriptionEditText = findViewById(R.id.editTextDescription);
        phoneNumber = findViewById(R.id.editTextPhone);
        addressEditText = findViewById(R.id.editTextAddress);
        contactEditText = findViewById(R.id.editTextContact);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        choice1 = findViewById(R.id.choice1);
        choice2 = findViewById(R.id.choice2);
        choice3 = findViewById(R.id.choice3);

        Button saveButton = findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                String phoneNumberSt = phoneNumber.getText().toString();
                String address = addressEditText.getText().toString();
                String contact = contactEditText.getText().toString();
                int year = datePicker.getYear();
                int month = datePicker.getMonth()+1;
                int day = datePicker.getDayOfMonth();
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                if(choice1.isChecked()) delay = 86400000;
                if(choice2.isChecked()) delay = 172800000;
                if(choice3.isChecked()) delay = 604800000;

                if (title.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Donner un titre pour sauvegarder!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!choice1.isChecked() && !choice2.isChecked() && !choice3.isChecked()) ;
                else CreateNotification(view, delay);

                RDV rdv = new RDV(title, "" + day + "/" + month + "/" + year, "" + hour + ":" + minute, contact, address, phoneNumberSt, description, false);

                RDVDAO rdvDAO = new RDVDAO(getApplicationContext());
                rdvDAO.open();
                long id = rdvDAO.addRDV(rdv);
                rdv.setId(id);

                rdvDAO.close();


                Intent intent = new Intent(AddRDVActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void CreateNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "RDV Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription("RDV Notification rappel du RDV");
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
        intent.putExtra("title", titleEditText.getText().toString());
        intent.putExtra("description", descriptionEditText.getText().toString());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

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
    private static final int READ_CONTACTS_PERMISSION_REQUEST_CODE = 2;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CreateNotification(null, delay);
            } else {
                Toast.makeText(this, "Permission refusée. La notification n'aura pas lieu.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == READ_CONTACTS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickContact();
            } else {
                Toast.makeText(this, "Permission refusée. Impossible de prendre un contact.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void requestReadContactsPermission(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSION_REQUEST_CODE);
        } else {
            pickContact();
        }
    }

    private static final int PICK_CONTACT_REQUEST_CODE = 3;

    private void pickContact() {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACT_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri contactUri = data.getData();
            String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
            Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{contactUri.getLastPathSegment()}, null);
            if (cursor != null && cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String phoneNumberSt = cursor.getString(numberIndex);
                String contactName = cursor.getString(nameIndex);
                phoneNumber.setText(phoneNumberSt);
                contactEditText.setText(contactName);
                cursor.close();
            }
        }
    }
}

