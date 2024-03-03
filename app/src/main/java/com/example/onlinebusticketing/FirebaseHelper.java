package com.example.onlinebusticketing;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {

    private DatabaseReference databaseReference;

    public FirebaseHelper() {
        // Initialize the Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    // Write data to the database at a specific path
    public void writeData(String value, String... pathSegments) {
        DatabaseReference childReference = databaseReference;

        // Traverse the path and create child references
        for (String segment : pathSegments) {
            childReference = childReference.child(segment);
        }

        // Set the value at the final child reference
        childReference.setValue(value); // Replace 'value' with the actual value you want to write
    }

    // Read data from the database and return it directly
//    public String readData(String... pathSegments) {
//        DatabaseReference childReference = databaseReference;
//
//        // Traverse the path and create child references
//        for (String segment : pathSegments) {
//            childReference = childReference.child(segment);
//        }
//
//        // Read data synchronously
//        DataSnapshot dataSnapshot = childReference.get();
//        if (dataSnapshot.exists()) {
//            return dataSnapshot.getValue(String.class);
//        } else {
//            return null; // or handle the case when data does not exist
//        }
//    }
}
