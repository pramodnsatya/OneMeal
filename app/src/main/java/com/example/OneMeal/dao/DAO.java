package com.example.OneMeal.dao;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.OneMeal.form.Volunteer;
import com.example.OneMeal.form.Food;
import com.example.OneMeal.form.FoodRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.OneMeal.form.NGO;
import com.example.OneMeal.util.Constants;
import com.example.OneMeal.util.MapUtil;
import com.example.OneMeal.util.Session;

public class DAO
{
        public static DatabaseReference getDBReference(String dbName)
        {
            return GetFireBaseConnection.getConnection(dbName);
        }

        public static StorageReference getStorageReference() {
            return GetFireBaseConnection.getStorageReference();
        }

        public String getUnicKey(String dbName)
        {
            return getDBReference(dbName).push().getKey();
        }

        public int addObject(String dbName,Object obj,String key) {

            int result=0;

            try {

                getDBReference(dbName).child(key).setValue(obj);

                result=1;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return result;
        }


        public void setDataToAdapterList(final View view, final Class c, final String dbname, final String userType) {

            Session s=new Session(view.getContext());
            final Map<String,String> viewMap=new HashMap<String,String>();

            getDBReference(dbname).addValueEventListener(new ValueEventListener() {
                int i=1;
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshotNode: dataSnapshot.getChildren()) {

                        Object object=snapshotNode.getValue(c);

                        if(dbname.equals(Constants.USER_DB)) {

                            NGO user = (NGO) object;
                            if(user.getType().equals(userType))
                            {
                                viewMap.put(i + ")"+user.getName(),user.getUsername());
                            }
                        }

                        if(dbname.equals(Constants.AGENT_DB)) {
                            Volunteer volunteer = (Volunteer) object;
                            viewMap.put(i + ")"+ volunteer.getName(), volunteer.getUsername());
                        }

                        if(dbname.equals(Constants.FOOD_REQUESTS_DB)) {
                            FoodRequest foodRequest = (FoodRequest) object;
                            viewMap.put(foodRequest.getFoodrequestid(),foodRequest.getFoodrequestid());
                        }

                        if(dbname.equals(Constants.FOOD_DB)) {

                            Food food=(Food) object;

                            if(s.getRole().equals("donar"))
                            {
                               if(food.getPostedby().equals(s.getusename()))
                               {
                                   viewMap.put(i + ")"+food.getName(),food.getId());
                               }
                            }
                            else
                            {
                                viewMap.put(i + ")"+food.getName(),food.getId());
                            }

                        }

                        i++;
                    }

                    ArrayList<String> al=new ArrayList<String>(viewMap.keySet());

                    if(view instanceof ListView) {

                        Log.v("in list view setting ",al.toString());

                        final ListView myView=(ListView)view;

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(myView.getContext(),
                                android.R.layout.simple_list_item_1, (al.toArray(new String[al.size()])));

                        myView.setAdapter(adapter);
                    }
                    s.setViewMap(MapUtil.mapToString(viewMap));
                    Log.v("after session setting ",al.toString());
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });

        }

        public int deleteObject(String dbName, String key) {

            int result=0;

            try {

                getDBReference(dbName).child(key).removeValue();

                result=1;
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            return 0;
        }
    }


