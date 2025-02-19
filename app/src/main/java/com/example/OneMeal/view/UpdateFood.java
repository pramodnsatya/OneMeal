package com.example.OneMeal.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.OneMeal.R;
import com.example.OneMeal.dao.DAO;
import com.example.OneMeal.form.Food;
import com.example.OneMeal.util.Constants;
import com.example.OneMeal.util.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class UpdateFood extends AppCompatActivity {

    EditText updateFoodStatus;
    EditText updateFoodCount;
    Button updateFoodSubmit;
    Button updateFoodCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_update_food);

        final Session s = new Session(getApplicationContext());

        updateFoodCount = (EditText) findViewById(R.id.updateFoodMemberCount);
        updateFoodStatus = (EditText) findViewById(R.id.updateFoodStatus);
        updateFoodSubmit = (Button) findViewById(R.id.updateFoodSubmit);
        updateFoodCancel = (Button) findViewById(R.id.updateFoodCancel);

        Intent i = getIntent();
        savedInstanceState = i.getExtras();

        final String foodid = savedInstanceState.getString("foodid");

        updateFoodSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String status = updateFoodStatus.getText().toString();
                String count = updateFoodCount.getText().toString();

                if (status == null || count ==null) {
                    Toast.makeText(getApplicationContext(), "Please Enter Food Status or Count", Toast.LENGTH_SHORT).show();
                } else {

                    DAO dao = new DAO();
                    dao.getDBReference(Constants.FOOD_DB).child(foodid).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Food food=dataSnapshot.getValue(Food.class);

                            if(food!=null)
                            {
                                food.setStatus(status);
                                food.setMembercount(count);
                                dao.addObject(Constants.FOOD_DB,food,foodid);

                                Intent i = new Intent(getApplicationContext(), DonorHome.class);
                                startActivity(i);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        updateFoodCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), DonorHome.class);
                startActivity(i);
            }
        });
    }
}
