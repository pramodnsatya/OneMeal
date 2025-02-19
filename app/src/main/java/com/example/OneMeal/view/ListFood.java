package com.example.OneMeal.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.OneMeal.R;
import com.example.OneMeal.dao.DAO;
import com.example.OneMeal.form.Food;
import com.example.OneMeal.util.Constants;
import com.example.OneMeal.util.MapUtil;
import com.example.OneMeal.util.Session;

public class ListFood extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_food);

        listView=(ListView) findViewById(R.id.FoodList);
        final Session s=new Session(getApplicationContext());

        final DAO dao=new DAO();

        dao.setDataToAdapterList(listView, Food.class, Constants.FOOD_DB,"employee");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String item = listView.getItemAtPosition(position).toString();
                item= MapUtil.stringToMap(s.getViewMap()).get(item);

                Intent intent=new Intent(getApplicationContext(),ViewFood.class);
                intent.putExtra("foodid",item);
                startActivity(intent);
            }
        });
    }
}