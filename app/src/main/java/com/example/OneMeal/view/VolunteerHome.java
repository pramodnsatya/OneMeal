package com.example.OneMeal.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.OneMeal.MainActivity;
import com.example.OneMeal.R;
import com.example.OneMeal.util.Session;

public class VolunteerHome extends AppCompatActivity {

    Button agentlogout;
    Button agentviewdeliverys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_home);

        agentlogout=(Button) findViewById(R.id.agentlogout);
        agentviewdeliverys = (Button) findViewById(R.id.agentviewdeliveries);


        final Session s = new Session(getApplicationContext());


        agentviewdeliverys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),ListFoodRequests.class);
                startActivity(i);
            }
        });

        agentlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                s.loggingOut();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
    }
}
