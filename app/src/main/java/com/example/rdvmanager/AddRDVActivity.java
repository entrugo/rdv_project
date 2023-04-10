package com.example.rdvmanager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;

public class AddRDVActivity extends AppCompatActivity {

    private EditText mTitleEditText;
    private EditText mDescriptionEditText;
    private EditText mAddressEditText;
    private EditText mContactEditText;
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rdv);

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

                if(title.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Specify a Title to save !", Toast.LENGTH_SHORT).show();
                    return;
                }

                RDV rdv = new RDV(title, ""+day+"/"+month+"/"+year, ""+hour+":"+minute, contact, address, description, false);

                RDVDAO rdvDAO = new RDVDAO(getApplicationContext());
                rdvDAO.open();
                long id= rdvDAO.addRDV(rdv);
                rdv.setId(id);
                Toast.makeText(AddRDVActivity.this, "New RDV added with ID " + rdv.getId(), Toast.LENGTH_SHORT).show();

                rdvDAO.close();


                Intent intent = new Intent(AddRDVActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}

