package com.example.rdvmanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;

public class EditRDVActivity extends BaseActivity {

    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText locationEditText;
    private DatePicker dateEditText;
    private TimePicker timeEditText;
    private EditText phoneEditText;

    private EditText contactEditText;
    private TextView status;
    private Button saveButton;
    private Button deleteButton;

    private int aYear;
    private int aMonth;
    private int aDay;

    @SuppressLint({"WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_rdv);

        long rdvId = getIntent().getExtras().getLong("rdv_Id", -1);
        if (rdvId == -1) {
            Toast.makeText(this, "Error : ID from RDV unprovided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        RDVDAO rdvDAO = new RDVDAO(this);
        rdvDAO.open();
        RDV selectedRDV = rdvDAO.getRDVById(rdvId);
        if (selectedRDV == null) {
            Toast.makeText(this, "Error : RDV not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        rdvDAO.close();
        String date = selectedRDV.getDate();
        String time = selectedRDV.getTime();
        String[] dateParts = date.split("/");
        String[] timeParts = time.split(":");

        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]) - 1;
        int year = Integer.parseInt(dateParts[2]);
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);


        dateEditText = findViewById(R.id.edit_date_picker);
        timeEditText = findViewById(R.id.edit_time_picker);
        if (dateEditText != null) {
            dateEditText.updateDate(year, month, day);
        }

        if (timeEditText != null) {
            timeEditText.setHour(hour);
            timeEditText.setMinute(minute);
        }

        titleEditText = findViewById(R.id.edit_title);
        if (selectedRDV != null && titleEditText != null) {
            titleEditText.setText(selectedRDV.getTitle());
        }

        contactEditText = findViewById(R.id.edit_contact);
        if (selectedRDV != null && contactEditText != null) {
            contactEditText.setText(selectedRDV.getContact());
        }

        descriptionEditText = findViewById(R.id.edit_description);
        if (selectedRDV != null && descriptionEditText != null) {
            descriptionEditText.setText(selectedRDV.getDescription());
        }

        locationEditText = findViewById(R.id.edit_location);
        if (selectedRDV != null && locationEditText != null) {
            locationEditText.setText(selectedRDV.getAddress());
        }


        phoneEditText = findViewById(R.id.edit_phone);
        if (selectedRDV != null && phoneEditText != null) {
            phoneEditText.setText(String.valueOf(selectedRDV.getPhoneNumber()));
        }

        status = findViewById(R.id.textView_RDVStatus);
        if(selectedRDV.isDone()) status.setText("Le RDV est passé.");
        else status.setText("Le RDV n'a pas encore eu lieu.");

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        saveButton = findViewById(R.id.button_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedRDV.setTitle(titleEditText.getText().toString());
                selectedRDV.setDescription(descriptionEditText.getText().toString());
                selectedRDV.setPhoneNumber(phoneEditText.getText().toString());
                selectedRDV.setAddress(locationEditText.getText().toString());
                selectedRDV.setContact(contactEditText.getText().toString());

                int year = dateEditText.getYear();
                int month = dateEditText.getMonth()+1;
                int day = dateEditText.getDayOfMonth();
                int hour = timeEditText.getHour();
                int minute = timeEditText.getMinute();

                selectedRDV.setDate("" + day + "/" + month + "/" + year);
                selectedRDV.setTime("" + hour + ":" + minute);

                RDVDAO rdvDAO = new RDVDAO(EditRDVActivity.this);
                rdvDAO.open();
                rdvDAO.updateRDV(selectedRDV);
                rdvDAO.close();

                Intent intent = new Intent(EditRDVActivity.this, MainActivity.class);
                startActivity(intent);

                finish();
            }
        });

        deleteButton = findViewById(R.id.button_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditRDVActivity.this);
                builder.setTitle("Confirmation de suppression");
                builder.setMessage("Etes-vous sûr de vouloir supprimer ce RDV ?");
                builder.setPositiveButton("Oui", (dialog, which) -> {

                    RDVDAO rdvDAO = new RDVDAO(EditRDVActivity.this);
                    rdvDAO.open();
                    rdvDAO.deleteRDV(selectedRDV);
                    rdvDAO.close();
                    Intent intent = new Intent(EditRDVActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                });
                builder.setNegativeButton("Non", null);
                builder.show();
            }
        });
    }

    public void launchMaps(View view) {
        String map = "http://maps.google.co.in/maps?q=" + locationEditText.getText().toString() ;
        Uri gmmIntentUri = Uri.parse(map);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    public void launchPhoneCall(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneEditText.getText()));
        startActivity(intent);
    }


    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        aYear = c.get(Calendar.YEAR);
        aMonth = c.get(Calendar.MONTH);
        aDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                dateEditText.updateDate(year,month,day);
            }
        }, aYear, aMonth, aDay);

        datePickerDialog.show();
    }




}

