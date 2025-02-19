package com.example.OneMeal.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.OneMeal.R;
import com.example.OneMeal.dao.DAO;
import com.example.OneMeal.form.NGO;
import com.example.OneMeal.util.Constants;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    EditText e1,e2,e3,e4,e5,e6,e7;
    Button b1;

    String[] userTypes={"NGO","DONOR"};
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        e1=(EditText)findViewById(R.id.registerUserName);
        e2=(EditText)findViewById(R.id.registerPassword);
        e3=(EditText)findViewById(R.id.registerConPass);
        e4=(EditText)findViewById(R.id.registerEmail);
        e5=(EditText)findViewById(R.id.registerMobile);
        e6=(EditText)findViewById(R.id.registerName);
        e7=(EditText)findViewById(R.id.registerAddress);

        Spinner spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item,userTypes);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        b1=(Button)findViewById(R.id.registerButton);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username=e1.getText().toString();
                String password=e2.getText().toString();
                String conformPassword=e3.getText().toString();
                String email=e4.getText().toString();
                String mobile=e5.getText().toString();
                String name=e6.getText().toString();
                String address=e7.getText().toString();

                if(username==null|| password==null|| conformPassword==null|| email==null|| mobile==null|| name==null|| address==null)
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
                else if(!isValid(email))
                {
                    Toast.makeText(getApplicationContext(),"Invalid Email",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    NGO user=new NGO();

                    user.setUsername(username);
                    user.setPassword(password);
                    user.setEmail(email);
                    user.setMobile(mobile);
                    user.setName(name);
                    user.setAddress(address);
                    user.setType(type);

                    DAO dao=new DAO();

                    try
                    {
                        dao.addObject(Constants.USER_DB,user,user.getUsername());
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
        type=userTypes[position];
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public static boolean isValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
}
