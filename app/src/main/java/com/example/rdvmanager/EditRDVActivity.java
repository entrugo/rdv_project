package com.example.rdvmanager;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import android.util.Log;

public class EditRDVActivity extends AppCompatActivity {

    private EditText mTitleEditText;
    private EditText mDescriptionEditText;
    private EditText mLocationEditText;
    private EditText mDateEditText;

    private EditText mTimeEditText;
    private Button mSaveButton;

    private int mYear;
    private int mMonth;
    private int mDay;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_rdv);

        // Get the RDV ID from the intent extras
        long rdvId = getIntent().getLongExtra("rdvId", -1);
        if (rdvId == -1) {
            Toast.makeText(this, "Erreur : ID du RDV non fourni", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Log.d("EditRDVActivity", "RDV ID: " + rdvId);

        // Get the RDV from the database
        RDVDAO rdvDAO = new RDVDAO(this);
        rdvDAO.open();
        RDV selectedRDV = rdvDAO.getRDVById(rdvId);
        if (selectedRDV == null) {
            Toast.makeText(this, "Erreur : RDV introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        rdvDAO.close();

        // Initialize the EditText and DatePicker widgets with the current values of the selected RDV
        mTitleEditText = findViewById(R.id.edit_title);
        mTitleEditText.setText(selectedRDV.getTitle());

        mDescriptionEditText = findViewById(R.id.edit_description);
        mDescriptionEditText.setText(selectedRDV.getDescription());

        mLocationEditText = findViewById(R.id.edit_location);
        mLocationEditText.setText(selectedRDV.getAddress());

        mDateEditText = findViewById(R.id.edit_date_picker);
        mDateEditText.setText(selectedRDV.getDate());

        mTimeEditText = findViewById(R.id.edit_time_picker);
        mTimeEditText.setText(selectedRDV.getTime());
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
                selectedRDV.setAddress(mLocationEditText.getText().toString());
                selectedRDV.setDescription(mDescriptionEditText.getText().toString());
                selectedRDV.setDate(mDateEditText.getText().toString());
                selectedRDV.setTime(mTimeEditText.getText().toString());

                rdvDAO.open();
                rdvDAO.updateRDV(selectedRDV);
                rdvDAO.close();

                finish();
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {

                mDateEditText.setText(""+day+"/"+month+"/"+year);
            }
        }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }
}

