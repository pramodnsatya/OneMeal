package com.example.OneMeal.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.OneMeal.form.Volunteer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import com.example.OneMeal.R;
import com.example.OneMeal.dao.DAO;
import com.example.OneMeal.util.Constants;
import com.example.OneMeal.util.Session;

public class VolunteerLogin extends AppCompatActivity {

    private Session session;
    EditText e1,e2;
    Button b1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(getApplicationContext());

        setContentView(R.layout.activity_volunteer_login);

        e1=(EditText)findViewById(R.id.volunteerUserId);
        e2=(EditText)findViewById(R.id.volunteerLoginPassword);
        b1=(Button)findViewById(R.id.loginConfirm1);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String username=e1.getText().toString();
                final String password=e2.getText().toString();

                if(username==null|| password==null || username.length()<=0|| password.length()<=0)
                {
                    Toast.makeText(getApplicationContext(),"Please Enter UserName and Password",Toast.LENGTH_SHORT).show();
                }
                else {

                    Toast.makeText(getApplicationContext(),"Username:"+username,Toast.LENGTH_SHORT).show();

                    DAO d = new DAO();
                    d.getDBReference(Constants.AGENT_DB).child(username).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Volunteer volunteer = (Volunteer) dataSnapshot.getValue(Volunteer.class);

                            if (volunteer == null) {
                                Toast.makeText(getApplicationContext(), "Invalid UserName ", Toast.LENGTH_SHORT).show();
                            } else if (volunteer != null && volunteer.getPassword().equals(password)) {

                                session.setusename(volunteer.getUsername());
                                session.setRole("Volunteer");

                                Intent i= new Intent(getApplicationContext(), VolunteerHome.class);
                                startActivity(i);

                            } else {
                                Toast.makeText(getApplicationContext(), "Invalid Password", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }
}
