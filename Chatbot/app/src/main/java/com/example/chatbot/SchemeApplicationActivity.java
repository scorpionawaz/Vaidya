package com.example.chatbot;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SchemeApplicationActivity extends AppCompatActivity {

    private TextView schemeNameTextView;
    private TextView nameTextView;
    private TextView aadharTextView;
    private TextView dobTextView;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheme_application);

        // Initialize UI components
        schemeNameTextView = findViewById(R.id.schemeNameTextView);
        nameTextView = findViewById(R.id.nameTextView);
        aadharTextView = findViewById(R.id.aadharTextView);
        dobTextView = findViewById(R.id.dobTextView);
//        submitButton = findViewById(R.id.submitButton);

        // Get data from intent
        String schemeName = getIntent().getStringExtra("SCHEME_NAME");
        String extractedName = getIntent().getStringExtra("EXTRACTED_NAME");
        String extractedAadhar = getIntent().getStringExtra("EXTRACTED_AADHAR");
        String extractedDOB = getIntent().getStringExtra("EXTRACTED_DOB");

        // Set the data to UI
        schemeNameTextView.setText(schemeName);
        nameTextView.setText(extractedName);
        aadharTextView.setText(extractedAadhar);
        dobTextView.setText(extractedDOB);

        // Set up submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Here you would implement the actual submission logic
                Toast.makeText(SchemeApplicationActivity.this,
                        "Application submitted successfully for " + schemeName,
                        Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}