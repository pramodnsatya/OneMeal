package com.example.OneMeal.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.OneMeal.R;
import com.example.OneMeal.dao.DAO;
import com.example.OneMeal.form.Food;
import com.example.OneMeal.util.Constants;
import com.example.OneMeal.util.MapUtil;
import com.example.OneMeal.util.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListNearBy extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_near_by);

        listView=(ListView) findViewById(R.id.FoodsNearbyList);
        final Session s=new Session(getApplicationContext());

        Intent i=getIntent();
        savedInstanceState=i.getExtras();

        final String[] userLatLongs=savedInstanceState.getString("latlang").split(",");

        final DAO dao=new DAO();

        //dao.setDataToAdapterList(listView, Food.class, Constants.COMPLAINTS_DB,"user");

        DAO d=new DAO();
        d.getDBReference(Constants.FOOD_DB).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final Map<String,Object> map=new HashMap<String,Object>();
                final Map<String,String> viewMap=new HashMap<String,String>();

                int i=0;

                for (DataSnapshot snapshotNode: dataSnapshot.getChildren()) {

                    Food food=(Food)snapshotNode.getValue(Food.class);

                    if(food!=null)
                    {
                        String[] latLongs=food.getGeolocation().split(",");

                        float distance = getDistanceFromCurrentPosition(new Double(userLatLongs[0]), new Double(userLatLongs[1]), new Double(latLongs[0]), new Double(latLongs[1]));

                        if(distance<10000)
                        {
                            viewMap.put(i + ")"+food.getName(),food.getId());
                            i++;

                            if(i==10)
                            {
                                break;
                            }
                        }
                    }
                }

                ArrayList<String> al=new ArrayList<String>(viewMap.keySet());
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(listView.getContext(),
                        android.R.layout.simple_list_item_1, (al.toArray(new String[al.size()])));

                listView.setAdapter(adapter);
                s.setViewMap(MapUtil.mapToString(viewMap));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String item = listView.getItemAtPosition(i).toString();
                item= MapUtil.stringToMap(s.getViewMap()).get(item);

                Intent intent=new Intent(getApplicationContext(),ViewFood.class);
                intent.putExtra("foodid",item);
                startActivity(intent);
            }
        });
    }

    public static float getDistanceFromCurrentPosition(double lat1,double lng1, double lat2, double lng2)
    {
        double earthRadius = 3958.75;

        double dLat = Math.toRadians(lat2 - lat1);

        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
                * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = earthRadius * c;

        int meterConversion = 1609;

        return new Float(dist * meterConversion).floatValue();

    }
}
