package com.example.gotravelapp.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gotravelapp.R;

public class MainActivity extends AppCompatActivity {

    public static int numAlert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button  = findViewById(R.id.begin);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //launches new activity
                Intent i = new Intent(MainActivity.this, VacationList.class);
                startActivity(i);
            }
        });
    }
}