package com.example.chatbot;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private TextView subTitleTextView;
    private GridLayout languageGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        subTitleTextView = findViewById(R.id.subTitle);
        languageGrid = findViewById(R.id.languageGrid);

        // Set Grid Columns Dynamically
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            languageGrid.setColumnCount(4); // 4 columns in landscape
        } else {
            languageGrid.setColumnCount(2); // 2 columns in portrait
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // 🔘 onClick method to be used in XML
    public void onLanguageSelected(View view) {
        Intent intent = null;

        int id = view.getId();
        if (id == R.id.lang_english) {
            intent = new Intent(this, EnglishActivity.class);
        } else if (id == R.id.lang_marathi) {
            intent = new Intent(this, MarathiActivity.class);
        } else if (id == R.id.lang_hindi) {
            intent = new Intent(this, HindiActivity.class);
        }

        if (intent != null) {
            startActivity(intent);
        }
    }
}
