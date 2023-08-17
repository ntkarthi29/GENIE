package com.example.genie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.Manifest;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.FirebaseApp;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FusedLocationProviderClient fusedLocationProviderClient;
    private final int REQUEST_CODE = 100;

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText locationEditText;
    private Button getLocationButton;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameEditText = findViewById(R.id.name_sign_up);
        emailEditText = findViewById(R.id.email_sign_up);
        passwordEditText = findViewById(R.id.password_sign_up);
        locationEditText = findViewById(R.id.get_location_sign_up);
        getLocationButton = findViewById(R.id.get_location_btn);
        submit = findViewById(R.id.submit_sign_up);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://genie-5eec8-default-rtdb.asia-southeast1.firebasedatabase.app/");
        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) { getLastLocation(); }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Pending
                User user = new User(
                        nameEditText.toString(),
                        emailEditText.toString(),
                        passwordEditText.toString(),
                        locationEditText.toString()
                );
                addDatatoFirebase(user.getName(), user.getEmail(), user.getPassword(), user.getLocation());
            }
        });
    }

    private void getLastLocation() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location != null) {
                                Geocoder geocoder = new Geocoder(SignUpActivity.this, Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                    locationEditText.setText(addresses.get(0).getAddressLine(0));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    });
        }
        else {
            askPermission();
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(SignUpActivity.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }
            else{
                Toast.makeText(this, "Location Access Required", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void addDatatoFirebase(String name, String email, String address, String location) {

        User user = new User();

        user.setName(name);
        user.setEmail(email);
        user.setPassword(address);
        user.setLocation(location);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                databaseReference.setValue(user);
                Toast.makeText(SignUpActivity.this, "data added", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(SignUpActivity.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}