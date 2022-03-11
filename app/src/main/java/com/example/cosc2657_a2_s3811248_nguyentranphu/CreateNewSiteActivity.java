package com.example.cosc2657_a2_s3811248_nguyentranphu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class CreateNewSiteActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    TextInputEditText dateTextInputEditText, locationTextInputEditText, descriptionTextInputEditText;
    Button createBtn;
    FireBaseHandler fireBaseHandler = new FireBaseHandler();
    FirebaseAuth authenticator = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_site);

        dateTextInputEditText = findViewById(R.id.dateTextInputEditText);
        locationTextInputEditText = findViewById(R.id.locationTextInputEditText);
        descriptionTextInputEditText = findViewById(R.id.descriptionTextInputEditText);

        createBtn = findViewById(R.id.createBtn);

        dateTextInputEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });


        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationTextInputEditText.getText().length() !=0 && dateTextInputEditText.length() != 0){
                    String location = locationTextInputEditText.getText().toString();
                    String date = dateTextInputEditText.getText().toString();
                    String description = descriptionTextInputEditText.getText().toString();
                    setSiteData(location, date, description);
                } else if (locationTextInputEditText.getText().length() == 0){
                    locationTextInputEditText.setError("Please enter the location");
                    locationTextInputEditText.requestFocus();
                } else {
                    dateTextInputEditText.setError("Please choose a date");
                    dateTextInputEditText.requestFocus();
                }
            }
        });
    }
    private void setSiteData(String location, String date, String description) {
        String host = authenticator.getCurrentUser().getEmail();
        List<Address> addresses = null;
        GeoPoint geoPoint;
        Geocoder geocoder = new Geocoder(CreateNewSiteActivity.this);
        try {
            addresses = geocoder.getFromLocationName(location, 1);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            Address address = addresses.get(0);
            geoPoint = new GeoPoint(address.getLatitude(), address.getLongitude());
            fireBaseHandler.addNewSite(host, location, date, description, geoPoint);
            Intent intent = new Intent(CreateNewSiteActivity.this, MainActivity.class);
            intent.putExtra("resMessage","Congrats! New Site for Covid Testing Created");
            setResult(RESULT_OK,intent);
            finish();
        } else {
            Toast.makeText(CreateNewSiteActivity.this,"Address Not Found, please try again!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month++;
        String dayStr = "" + dayOfMonth, monthStr = "" + month;
        if (month < 10) {
            monthStr = "0"  + month;
        }
        if (dayOfMonth < 10) {
            dayStr = "0" + dayOfMonth;
        }
        String setDate = dayStr + "/" + monthStr +  "/" + year;
        dateTextInputEditText.setText(setDate);
    }
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}