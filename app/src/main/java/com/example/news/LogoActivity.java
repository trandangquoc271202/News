package com.example.news;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LogoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                check();
            }
        }, 1000);
    }
    public void check() {
        if (isTableEmpty()) {
            Intent intent = new Intent(LogoActivity.this, Login.class);
            startActivity(intent);
            finish();
        } else {
            SQLiteDatabase db = openOrCreateDatabase("statelogin", MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery("SELECT * FROM login", null);
            try {
                while (cursor.moveToNext()) {
                    @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("idUser"));
                    @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex("username"));
                    @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex("password"));
                    AccountExist(id, username, password);
                }
            } finally {
                cursor.close();
            }
        }
    }
    private void AccountExist(String id, String username, String pass) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.getString("username").equals(username) && documentSnapshot.getString("password").equals(pass)) {
                        Intent intent = new Intent(LogoActivity.this, HomeActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("idUser", id);
                        intent.putExtra("data", bundle);
                        startActivity(intent);
                        finish();
                    } else {
                        SQLiteDatabase db = openOrCreateDatabase("statelogin", MODE_PRIVATE, null);
                        db.delete("login", null, null);
                        db.close();
                        Intent intent = new Intent(LogoActivity.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
    public boolean isTableEmpty() {
        SQLiteDatabase db = openOrCreateDatabase("statelogin", MODE_PRIVATE, null);
        String countQuery = "SELECT COUNT(*) FROM login";
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count == 0;
    }

}