package com.example.icmdisplayshowcollecteddata;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;



public class DisplayData extends AppCompatActivity {
    final public String website = "https://finepointmobile.com/api/inventory/v1/message";
    private TextView all_collected_data;
    private String served_data;
    private Button updateButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collected_data);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL(website);
                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                    served_data = in.readLine();

                } catch (MalformedURLException e) {
                    served_data = "MalformedURLException";
                } catch (IOException e) {
                    served_data = "IOException";
                }
            }
        });
        thread.start();

        updateButton = (Button) findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                all_collected_data = (TextView) findViewById(R.id.allCollectedData);
                all_collected_data.setText(served_data);
            }
        });


    }
}