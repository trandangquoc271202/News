package com.example.news;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
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

import java.io.File;

public class LogoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase database = openOrCreateDatabase("statelogin", MODE_PRIVATE, null);
                String sql = "CREATE TABLE login (idUser TEXT,username TEXT,password TEXT)";
                try {
                    database.execSQL(sql);
                } catch (Exception e) {
                }
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
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
            Toast.makeText(context.getApplicationContext(), "clear cache success", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context.getApplicationContext(), "clear cache failed", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}