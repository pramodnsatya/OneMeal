package com.example.OneMeal.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.OneMeal.R;
import com.example.OneMeal.dao.DAO;
import com.example.OneMeal.form.Food;
import com.example.OneMeal.form.NGO;
import com.example.OneMeal.util.Constants;
import com.example.OneMeal.util.Session;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class AddFood extends AppCompatActivity {

    EditText addFoodName;
    EditText addFoodPrepared;
    EditText addMemeberCount;

    private ImageView imageView;

    Button addFoodSubmit;
    Button addFoodCancel;
    Button btnChoose;

    private static final int Image_Capture_Code = 1;
    private Uri imageUri;

    private FusedLocationProviderClient mFusedLocationClient;

    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private String txtLocation;
    private String lat;
    private String lang;

    private boolean isGPS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_food);

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

        final Session s = new Session(getApplicationContext());

        addFoodName = (EditText) findViewById(R.id.addFoodName);
        addFoodPrepared = (EditText) findViewById(R.id.addFoodPrepared);
        addMemeberCount = (EditText) findViewById(R.id.addFoodMemberCount);

        btnChoose = (Button) findViewById(R.id.chooseimagebutton);
        addFoodSubmit = (Button) findViewById(R.id.addFoodSubmit);
        addFoodCancel = (Button) findViewById(R.id.addFoodCancel);

        imageView = (ImageView) findViewById(R.id.postFoodImgView);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cInt, Image_Capture_Code);
            }
        });

        addFoodSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DAO d=new DAO();
                d.getDBReference(Constants.USER_DB).child(s.getusename()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        NGO user=snapshot.getValue(NGO.class);

                        if(user!=null)
                        {
                            Log.v("food ", "1");
                            if (!isGPS) {
                                Toast.makeText(getApplicationContext(), "Please turn on GPS", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Log.v("food ", "2");

                            getLocation();

                            final Session s = new Session(getApplicationContext());

                            Log.v("food ", txtLocation + "");

                            Log.v("food ", "3");

                            if (txtLocation != null && lat!=null & lang!=null) {
                                Log.v("food ", "4");

                                String name = addFoodName.getText().toString();
                                String prepared = addFoodPrepared.getText().toString();
                                String memberCount = addMemeberCount.getText().toString();

                                if (name == null || prepared == null || memberCount == null) {
                                    Toast.makeText(getApplicationContext(), "Please Enter Valid Data", Toast.LENGTH_SHORT).show();
                                } else {

                                    Log.v("food ", "5");

                                    String imageName = UUID.randomUUID().toString();

                                    DAO dao = new DAO();

                                    Food food = new Food();
                                    food.setId(dao.getUnicKey(Constants.FOOD_DB));
                                    food.setName(name);
                                    food.setPreparedtime(prepared);
                                    food.setMembercount(memberCount);
                                    food.setPostedby(s.getusename());
                                    food.setStatus("open");
                                    food.setGeolocation(lat+","+lang);
                                    food.setImage(imageName);
                                    food.setLocation(txtLocation);
                                    food.setMobile(user.getMobile());

                                    try {

                                        dao.addObject(Constants.FOOD_DB, food, food.getId());
                                        uploadImage(imageName);

                                        Intent i = new Intent(getApplicationContext(), DonorHome.class);
                                        startActivity(i);
                                        Toast.makeText(getApplicationContext(), "Food Posted Successfully", Toast.LENGTH_SHORT).show();
                                    } catch (Exception ex) {
                                        Toast.makeText(getApplicationContext(), "Food Failed", Toast.LENGTH_SHORT).show();
                                        ex.printStackTrace();
                                    }
                                }
                            } else {
                                Log.v("food ", "8");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        addFoodCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), DonorHome.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v("onemeal", "in activity results return");

        if (requestCode == Image_Capture_Code) {
            if (resultCode == RESULT_OK) {
                Log.v("onemeal", "result ok");
                Bitmap bp = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(bp);
                imageUri = getImageUri(getApplicationContext(), bp);
            } else if (resultCode == RESULT_CANCELED) {
                Log.v("onemeal", "result cancelled");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            }
        }
        Log.v("onemeal", "at last");

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
            }
        }
    }

    private void uploadImage(String fileName) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference ref = DAO.getStorageReference().child("images/" + fileName);

        ref.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    }
                });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(AddFood.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(AddFood.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddFood.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);
        } else {

            mFusedLocationClient.getLastLocation().addOnSuccessListener(AddFood.this, new OnSuccessListener<Location>() {
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

                    mFusedLocationClient.getLastLocation().addOnSuccessListener(AddFood.this, new OnSuccessListener<Location>() {
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
