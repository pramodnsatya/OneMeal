package com.example.OneMeal.dao;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class GetFireBaseConnection
{
    public static FirebaseDatabase firebaseDatabase=null;
    public static FirebaseStorage firebaseStorage=null;

    public static DatabaseReference getConnection(String database)
    {
        if(firebaseDatabase==null)
        {
            firebaseDatabase=firebaseDatabase.getInstance();
        }

        return firebaseDatabase.getReference(database);
    }

    public static StorageReference getStorageReference()
    {
        if(firebaseStorage==null)
        {
            firebaseStorage=FirebaseStorage.getInstance();
        }

        return firebaseStorage.getReference();
    }
}
