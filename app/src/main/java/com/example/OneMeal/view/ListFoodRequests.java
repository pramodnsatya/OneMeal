package com.example.OneMeal.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.OneMeal.R;
import com.example.OneMeal.dao.DAO;
import com.example.OneMeal.form.FoodRequest;
import com.example.OneMeal.util.Constants;
import com.example.OneMeal.util.MapUtil;
import com.example.OneMeal.util.Session;

public class ListFoodRequests extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_food_requests);

        listView=(ListView) findViewById(R.id.FoodRequestList);
        final Session s=new Session(getApplicationContext());

        final DAO dao=new DAO();

        dao.setDataToAdapterList(listView, FoodRequest.class, Constants.FOOD_REQUESTS_DB,"food request");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String item = listView.getItemAtPosition(position).toString();
                item= MapUtil.stringToMap(s.getViewMap()).get(item);

                Intent intent=new Intent(getApplicationContext(),ViewFoodRequest.class);
                intent.putExtra("foodrequestid",item);
                startActivity(intent);
            }
        });
    }
}