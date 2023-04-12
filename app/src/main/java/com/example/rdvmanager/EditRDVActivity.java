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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.util.Calendar;

public class EditRDVActivity extends AppCompatActivity {

    private EditText mTitleEditText;
    private EditText mDescriptionEditText;
    private EditText mLocationEditText;
    private DatePicker mDateEditText;
    private TimePicker mTimeEditText;
    private EditText mPhoneEditText;
    private TextView mStatus;
    private Button mSaveButton;
    private Button mLocationButton;
    private Button mPhoneButton;

    private int mYear;
    private int mMonth;
    private int mDay;

    @SuppressLint({"WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_rdv);

        // Get the RDV ID from the intent extras
        long rdvId = getIntent().getExtras().getLong("rdv_Id", -1);
        if (rdvId == -1) {
            Toast.makeText(this, "Error : ID from RDV unprovided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // Get the RDV from the database
        RDVDAO rdvDAO = new RDVDAO(this);
        rdvDAO.open();
        RDV selectedRDV = rdvDAO.getRDVById(rdvId);
        if (selectedRDV == null) {
            Toast.makeText(this, "Error : RDV not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        rdvDAO.close();

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
        mDateEditText = findViewById(R.id.edit_date_picker);
        // Time
        mTimeEditText = findViewById(R.id.edit_time_picker);
        // Num. téléphone
        mPhoneEditText = findViewById(R.id.edit_phone);
        if (selectedRDV != null && mPhoneEditText != null) {
            mPhoneEditText.setText(String.valueOf(selectedRDV.getPhoneNumber()));
        }
        // Status
        mStatus = findViewById(R.id.textView_RDVStatus);
        if(selectedRDV.isDone()) mStatus.setText("RDV is done.");
        else mStatus.setText("RDV is not done.");

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
                    selectedRDV.setDate(mDateEditText.getDayOfMonth() + "/" + mDateEditText.getMonth() + "/" + mDateEditText.getYear());
                    selectedRDV.setTime(mTimeEditText.getHour() + ":" +mTimeEditText.getMinute());

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

    public void launchPhoneCall(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mPhoneEditText.getText()));
        startActivity(intent);
    }


    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                mDateEditText.updateDate(year,month,day);
            }
        }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }
}

