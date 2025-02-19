package com.example.OneMeal.view;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.OneMeal.R;
import com.example.OneMeal.dao.DAO;
import com.example.OneMeal.form.Food;
import com.example.OneMeal.form.FoodRequest;
import com.example.OneMeal.form.NGO;
import com.example.OneMeal.util.Constants;
import com.example.OneMeal.util.Session;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Locale;

public class ViewFood extends AppCompatActivity {

    Button menuDeleteFood;
    Button viewFoodBack;
    Button updatefood;
    Button sendfoodrequest;
    ImageView imageView;

    String foodAddress="";
    String donormobile="";

    private FusedLocationProviderClient mFusedLocationClient;

    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private String txtLocation;
    private String lat;
    private String lang;

    private boolean isGPS = false;

    TextView t1,t2,t3,t4,t5,t6,t7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_food);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds

        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();

                        txtLocation = wayLatitude + "," + wayLongitude;

                        if (mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };

        menuDeleteFood=(Button) findViewById(R.id.menuDeleteFood);
        viewFoodBack=(Button) findViewById(R.id.viewFoodBack);
        updatefood=(Button) findViewById(R.id.updateFood);
        sendfoodrequest=(Button) findViewById(R.id.sendfoodrequest);

        t1=(TextView) findViewById(R.id.foodviewname);
        t2=(TextView)findViewById(R.id.foodviewpreparedtime);
        t3=(TextView)findViewById(R.id.foodviewmembercount);
        t4=(TextView)findViewById(R.id.foodviewstatus);
        t5=(TextView)findViewById(R.id.foodviewpostedby);
        t6=(TextView)findViewById(R.id.foodviewlocation);
        t7=(TextView)findViewById(R.id.foodviewmobile);

        imageView = (ImageView) findViewById(R.id.foodviewimage);

        final Session s = new Session(getApplicationContext());

        Intent i = getIntent();
        savedInstanceState = i.getExtras();
        final String foodid = savedInstanceState.getString("foodid");


        if(s.getRole().equals("DONOR"))
        {
            sendfoodrequest.setEnabled(false);
        }

        DAO d=new DAO();
        d.getDBReference(Constants.FOOD_DB).child(foodid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Food food=dataSnapshot.getValue(Food.class);

                if(!food.getPostedby().equals(s.getusename()))
                {
                    menuDeleteFood.setEnabled(false);
                    updatefood.setEnabled(false);
                }

                if(food!=null)
                {
                    String[] foodLocation=food.getLocation().split(",");

                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                    try {

                        addresses = geocoder.getFromLocation(new Double(foodLocation[0]),new Double(foodLocation[1]), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalCode = addresses.get(0).getPostalCode();
                        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                        if(address!=null)
                        {
                            foodAddress=foodAddress+address+"\n";
                        }

                        if(city!=null)
                        {
                            foodAddress=foodAddress+city+"\n";
                        }

                        if(state!=null)
                        {
                            foodAddress=foodAddress+state+"\n";
                        }

                        if(country!=null)
                        {
                            foodAddress=foodAddress+country+"\n";
                        }

                        if(postalCode!=null)
                        {
                            foodAddress=foodAddress+postalCode+"\n";
                        }

                        if(knownName!=null)
                        {
                            foodAddress=foodAddress+knownName+"\n";
                        }
                    }
                    catch(Exception e)
                    {
                        Log.v("onemeal ","in on success ");
                    }

                    Log.v("onemeal Address ",foodAddress);

                    t1.setText("Name: "+food.getName());
                    t2.setText("Prepared Time: "+food.getPreparedtime());
                    t3.setText("Description: "+food.getMembercount());
                    t4.setText("Status: "+food.getStatus());
                    t5.setText("Posted By: "+food.getPostedby());
                    t6.setText("Location: "+foodAddress);
                    t7.setText("Mobile: "+food.getMobile());

                    donormobile=food.getMobile();
                    foodAddress=foodAddress;

                    StorageReference ref = DAO.getStorageReference().child("images/" + food.getImage());
                    long ONE_MEGABYTE = 1024 * 1024 *5;
                    ref.getBytes(ONE_MEGABYTE)
                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {

                                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                                    if(bm!=null)
                                    {
                                        imageView.setImageBitmap(bm);
                                    }
                                    else
                                    {
                                        Log.v("onemeal ","bm null");
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                            Log.v("onemeal ","image reading failure");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        menuDeleteFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final DAO dao=new DAO();
                dao.deleteObject(Constants.FOOD_DB,foodid);

                Intent i= new Intent(getApplicationContext(), DonorHome.class);
                startActivity(i);
            }
        });

        updatefood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),UpdateFood.class);
                intent.putExtra("foodid",foodid);
                startActivity(intent);
            }
        });

        viewFoodBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=null;

                String role=s.getRole();

                if(role.equals("DONOR"))
                {
                    i= new Intent(getApplicationContext(), DonorHome.class);
                }else if(role.equals("NGO"))
                {
                    i= new Intent(getApplicationContext(), NGOHome.class);
                }else if(role.equals("Volunteer"))
                {
                    i= new Intent(getApplicationContext(), VolunteerHome.class);
                }
                startActivity(i);
            }
        });

        sendfoodrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DAO d = new DAO();
                d.getDBReference(Constants.USER_DB).child(s.getusename()).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        NGO user = (NGO) dataSnapshot.getValue(NGO.class);

                        if (user == null) {
                            Toast.makeText(getApplicationContext(), "Invalid UserName ", Toast.LENGTH_SHORT).show();
                        } else if (user != null) {

                            if (!isGPS) {
                                Toast.makeText(getApplicationContext(), "Please turn on GPS", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            getLocation();

                            final Session s = new Session(getApplicationContext());

                            if (txtLocation != null && lat!=null & lang!=null) {

                                Toast.makeText(getApplicationContext(), "LatLangs:"+lat+"\t"+lang, Toast.LENGTH_SHORT).show();

                                String[] requestLocation=txtLocation.split(",");

                                Geocoder geocoder;
                                List<Address> addresses;
                                geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                                String requestAddress="";

                                try {

                                    addresses = geocoder.getFromLocation(new Double(lat),new Double(lang), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                    String city = addresses.get(0).getLocality();
                                    String state = addresses.get(0).getAdminArea();
                                    String country = addresses.get(0).getCountryName();
                                    String postalCode = addresses.get(0).getPostalCode();
                                    String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                                    if(address!=null)
                                    {
                                        requestAddress=requestAddress+address+"\n";
                                    }

                                    if(city!=null)
                                    {
                                        requestAddress=requestAddress+city+"\n";
                                    }

                                    if(state!=null)
                                    {
                                        requestAddress=requestAddress+state+"\n";
                                    }

                                    if(country!=null)
                                    {
                                        requestAddress=requestAddress+country+"\n";
                                    }

                                    if(postalCode!=null)
                                    {
                                        requestAddress=requestAddress+postalCode+"\n";
                                    }

                                    if(knownName!=null)
                                    {
                                        requestAddress=requestAddress+knownName+"\n";
                                    }
                                }
                                catch(Exception e)
                                {
                                    Log.v("onemeal ","in on success ");
                                }

                                DAO dao = new DAO();

                                FoodRequest foodRequest = new FoodRequest();
                                foodRequest.setFoodrequestid(dao.getUnicKey(Constants.FOOD_REQUESTS_DB));
                                foodRequest.setSourcelocation(foodAddress);
                                foodRequest.setDestinationlocation(requestAddress);
                                foodRequest.setRequestdby(user.getMobile());
                                foodRequest.setDonatedby(donormobile);

                                try {

                                    dao.addObject(Constants.FOOD_REQUESTS_DB, foodRequest, foodRequest.getFoodrequestid());

                                    Intent i = new Intent(getApplicationContext(), NGOHome.class);
                                    startActivity(i);
                                    Toast.makeText(getApplicationContext(), "Food Request Sent Successfully", Toast.LENGTH_SHORT).show();
                                } catch (Exception ex) {
                                    Toast.makeText(getApplicationContext(), "Food Request Failed", Toast.LENGTH_SHORT).show();
                                    ex.printStackTrace();
                                }
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Unable to get Location", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid User", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(ViewFood.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(ViewFood.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ViewFood.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);
        } else {

            mFusedLocationClient.getLastLocation().addOnSuccessListener(ViewFood.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        lat=wayLatitude+"";
                        lang=wayLongitude+"";
                        txtLocation = wayLatitude + "," + wayLongitude;
                    } else {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                }
            });
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mFusedLocationClient.getLastLocation().addOnSuccessListener(ViewFood.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                wayLatitude = location.getLatitude();
                                wayLongitude = location.getLongitude();
                                txtLocation=wayLatitude+","+wayLongitude;
                            } else {
                                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }
                        }
                    });

                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
}