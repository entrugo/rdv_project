package com.example.rdvmanager;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.util.Calendar;

public class EditRDVActivity extends AppCompatActivity {

    private EditText mTitleEditText;
    private EditText mDescriptionEditText;
    private EditText mLocationEditText;
    private EditText mDateEditText;
    private EditText mPhoneEditText;
    private Button mSaveButton;
    private Button mLocationButton;
    private Button mPhoneButton;

    private int mYear;
    private int mMonth;
    private int mDay;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_rdv);

        // Get the selected RDV from the intent extras
        RDV selectedRDV = (RDV) getIntent().getSerializableExtra("selectedRDV");

        // On met à jour le xml avec les informations du RDV sélectionné
        mTitleEditText = findViewById(R.id.edit_title);
        if (selectedRDV != null && mTitleEditText != null) {
            mTitleEditText.setText(selectedRDV.getTitle());
        }
        // Description
        mDescriptionEditText = findViewById(R.id.edit_description);
        if (selectedRDV != null && mDescriptionEditText != null) {
            mDescriptionEditText.setText(selectedRDV.getDescription());
        }
        // Adresse
        mLocationEditText = findViewById(R.id.edit_location);
        if (selectedRDV != null && mLocationEditText != null) {
            mLocationEditText.setText(selectedRDV.getAddress());
        }
        // Date
        mDateEditText = findViewById(R.id.edit_date_picker2);
        if (selectedRDV != null && mDateEditText != null) {
            mDateEditText.setText(DateFormat.getDateInstance().format(selectedRDV.getDate()));
        }
        // Num. téléphone
        mPhoneEditText = findViewById(R.id.edit_phone);
        if (selectedRDV != null && mPhoneEditText != null) {
            mPhoneEditText.setText(DateFormat.getDateInstance().format(selectedRDV.getPhoneNumber()));
        }

        mDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Initialize the Save button and set its onClick listener to update the RDV in the database and return to the main activity
        mSaveButton = findViewById(R.id.button_save);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    selectedRDV.setTitle(mTitleEditText.getText().toString());
                    selectedRDV.setDescription(mDescriptionEditText.getText().toString());
                    selectedRDV.setAddress(mLocationEditText.getText().toString());
                    // selectedRDV.setDate(Calendar.getInstance().set(mYear, mMonth, mDay).getTime());
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(mYear, mMonth, mDay);
                    selectedRDV.setDate(String.valueOf(calendar.getTime()));

                    RDVDAO rdvDAO = new RDVDAO(EditRDVActivity.this);
                    rdvDAO.open();
                    rdvDAO.updateRDV(selectedRDV);
                    rdvDAO.close();

                    finish();
            }
        });
    }

    public void launchMaps(View view) {
        String map = "http://maps.google.co.in/maps?q=" + mLocationEditText.getText().toString() ;
        Uri gmmIntentUri = Uri.parse(map);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;

                Calendar calendar = Calendar.getInstance();
                calendar.set(mYear, mMonth, mDay);
                mDateEditText.setText(DateFormat.getDateInstance().format(calendar.getTime()));
            }
        }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }
}

