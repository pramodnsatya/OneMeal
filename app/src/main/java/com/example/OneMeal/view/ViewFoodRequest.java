package com.example.OneMeal.view;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.OneMeal.R;
import com.example.OneMeal.dao.DAO;
import com.example.OneMeal.form.FoodRequest;
import com.example.OneMeal.util.Constants;
import com.example.OneMeal.util.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ViewFoodRequest extends AppCompatActivity {

    Button viewFoodRequestBack;
    TextView t1,t2,t3,t4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_food_request);

        viewFoodRequestBack=(Button) findViewById(R.id.viewFoodrequestBack);

        t1=(TextView) findViewById(R.id.foodrequestsource);
        t2=(TextView)findViewById(R.id.foodrequestdestination);
        t3=(TextView)findViewById(R.id.foodrequestsourcerequestedby);
        t4=(TextView)findViewById(R.id.foodrequestsourcedonatedby);

        final Session s = new Session(getApplicationContext());

        Intent i = getIntent();
        savedInstanceState = i.getExtras();
        final String foodrequestid = savedInstanceState.getString("foodrequestid");

        DAO d=new DAO();
        d.getDBReference(Constants.FOOD_REQUESTS_DB).child(foodrequestid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                FoodRequest foodRequest=dataSnapshot.getValue(FoodRequest.class);

                if(foodRequest!=null)
                {
                    t1.setText("Source Location: "+foodRequest.getSourcelocation());
                    t2.setText("Destination Location: "+foodRequest.getDestinationlocation());
                    t3.setText("Source Mobile: "+foodRequest.getRequestdby());
                    t4.setText("Destination Mobile: "+foodRequest.getDonatedby());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        viewFoodRequestBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(getApplicationContext(), VolunteerHome.class);
                startActivity(i);
            }
        });
    }
}