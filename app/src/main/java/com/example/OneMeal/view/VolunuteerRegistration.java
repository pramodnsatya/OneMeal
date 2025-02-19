package com.example.OneMeal.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.OneMeal.R;
import com.example.OneMeal.dao.DAO;
import com.example.OneMeal.form.Volunteer;
import com.example.OneMeal.util.Constants;

import java.util.ArrayList;
public class VolunuteerRegistration extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    EditText e1,e2,e3,e4,e5,e6;
    Button b1;
    String orphanage;
    ArrayList<String> al=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_volunteer_registration);

        e1=(EditText)findViewById(R.id.registerUserName1);
        e2=(EditText)findViewById(R.id.registerPassword1);
        e3=(EditText)findViewById(R.id.registerConPass1);
        e4=(EditText)findViewById(R.id.registerEmail1);
        e5=(EditText)findViewById(R.id.registerMobile1);
        e6=(EditText)findViewById(R.id.registerName1);

        b1=(Button)findViewById(R.id.registerButton1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username=e1.getText().toString();
                String password=e2.getText().toString();
                String conformPassword=e3.getText().toString();
                String email=e4.getText().toString();
                String mobile=e5.getText().toString();
                String name=e6.getText().toString();

                if(username==null|| password==null|| conformPassword==null|| email==null|| mobile==null|| name==null)
                {
                    Toast.makeText(getApplicationContext(),"Please Enter Valid Data",Toast.LENGTH_SHORT).show();
                }
                else if(mobile.length()<10|| mobile.length()>12)
                {
                    Toast.makeText(getApplicationContext(),"Invalid Mobile",Toast.LENGTH_SHORT).show();
                }
                else if(!password.equals(conformPassword))
                {
                    Toast.makeText(getApplicationContext(),"Password Mismatch",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Volunteer volunteer =new Volunteer();

                    volunteer.setUsername(username);
                    volunteer.setPassword(password);
                    volunteer.setEmail(email);
                    volunteer.setMobile(mobile);
                    volunteer.setName(name);

                    DAO dao=new DAO();

                    try
                    {
                        dao.addObject(Constants.AGENT_DB, volunteer, volunteer.getUsername());

                        Toast.makeText(getApplicationContext(),"Registration Successful",Toast.LENGTH_SHORT).show();

                        Intent i=new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(i);
                    }
                    catch (Exception ex)
                    {
                        Toast.makeText(getApplicationContext(),"Registration Error",Toast.LENGTH_SHORT).show();
                        Log.v("User Registration Ex", ex.toString());
                        ex.printStackTrace();
                    }

                }
            }
        });
    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        orphanage=al.get(position);
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}
