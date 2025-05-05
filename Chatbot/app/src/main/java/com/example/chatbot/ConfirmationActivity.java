package com.example.chatbot;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ConfirmationActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView aadharTextView;
    private TextView doctorTextView;
    private TextView dateTextView;
    private Button confirmButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        // Initialize views
        nameTextView = findViewById(R.id.titleTextView);
        aadharTextView = findViewById(R.id.aadharTextView);
        doctorTextView = findViewById(R.id.doctorTextView);
        dateTextView = findViewById(R.id.dateTextView);
        confirmButton = findViewById(R.id.confirmButton);
        cancelButton = findViewById(R.id.cancelButton);

        // Get data from intent
        String name = getIntent().getStringExtra("NAME");
        String aadharNumber = getIntent().getStringExtra("AADHAR_NUMBER");
        String doctorName = getIntent().getStringExtra("DOCTOR_NAME");
        String appointmentDate = getIntent().getStringExtra("APPOINTMENT_DATE");

        // Set data to views
        nameTextView.setText("नाम: " + name);

        // Mask part of the Aadhar number for security
        String maskedAadhar = "";
        if (aadharNumber != null && aadharNumber.length() == 12) {
            maskedAadhar = "XXXX XXXX " + aadharNumber.substring(8);
        } else {
            maskedAadhar = aadharNumber;
        }
        aadharTextView.setText("आधार: " + maskedAadhar);

        doctorTextView.setText("डॉक्टर: " + doctorName);
        dateTextView.setText("अपॉइंटमेंट तारीख: " + appointmentDate);

        // Set button click listeners
        confirmButton.setOnClickListener(v -> {
            // Send confirmation to backend (simulated here)
            Toast.makeText(this, "अपॉइंटमेंट की पुष्टि हो गई है। आपको एसएमएस प्राप्त होगा।", Toast.LENGTH_LONG).show();
            finish();
        });

        cancelButton.setOnClickListener(v -> {
            Toast.makeText(this, "अपॉइंटमेंट रद्द कर दिया गया है", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}