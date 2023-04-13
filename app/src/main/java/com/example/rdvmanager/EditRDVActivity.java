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

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.util.Calendar;

public class EditRDVActivity extends BaseActivity {

    private EditText mTitleEditText;
    private EditText mDescriptionEditText;
    private EditText mLocationEditText;
    private DatePicker mDateEditText;
    private TimePicker mTimeEditText;
    private EditText mPhoneEditText;

    private EditText mContactEditText;
    private TextView mStatus;
    private Button mSaveButton;
    private Button mDeleteButton;

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

        // Date and Time
        String date = selectedRDV.getDate();
        String time = selectedRDV.getTime();

// Split the date and time strings to extract the individual components
        String[] dateParts = date.split("/");
        String[] timeParts = time.split(":");

        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]) - 1; // Subtract 1 since months are indexed from 0
        int year = Integer.parseInt(dateParts[2]);
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        // Date
        mDateEditText = findViewById(R.id.edit_date_picker);
        // Time
        mTimeEditText = findViewById(R.id.edit_time_picker);
// Set the date and time values in the DatePicker and TimePicker
        if (mDateEditText != null) {
            mDateEditText.updateDate(year, month, day);
        }

        if (mTimeEditText != null) {
            mTimeEditText.setHour(hour);
            mTimeEditText.setMinute(minute);
        }

        // On met à jour le xml avec les informations du RDV sélectionné
        mTitleEditText = findViewById(R.id.edit_title);
        if (selectedRDV != null && mTitleEditText != null) {
            mTitleEditText.setText(selectedRDV.getTitle());
        }
        // Contact
        mContactEditText = findViewById(R.id.edit_contact);
        if (selectedRDV != null && mContactEditText != null) {
            mContactEditText.setText(selectedRDV.getContact());
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

        // Num. téléphone
        mPhoneEditText = findViewById(R.id.edit_phone);
        if (selectedRDV != null && mPhoneEditText != null) {
            mPhoneEditText.setText(String.valueOf(selectedRDV.getPhoneNumber()));
        }
        // Status
        mStatus = findViewById(R.id.textView_RDVStatus);
        if(selectedRDV.isDone()) mStatus.setText("RDV is done.");
        else mStatus.setText("RDV is not done yet.");

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
                selectedRDV.setPhoneNumber(mPhoneEditText.getText().toString());
                selectedRDV.setAddress(mLocationEditText.getText().toString());
                selectedRDV.setContact(mContactEditText.getText().toString());

                int year = mDateEditText.getYear();
                int month = mDateEditText.getMonth()+1;
                int day = mDateEditText.getDayOfMonth();
                int hour = mTimeEditText.getHour();
                int minute = mTimeEditText.getMinute();

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

        // Delete button
        mDeleteButton = findViewById(R.id.button_delete);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditRDVActivity.this);
                builder.setTitle("Confirmation de suppression");
                builder.setMessage("Etes-vous sûr de vouloir supprimer ce RDV ?");
                builder.setPositiveButton("Oui", (dialog, which) -> {
                    // supprimer le RDV de la base de données et de la liste
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

