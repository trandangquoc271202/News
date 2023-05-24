package com.example.news;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.news.enity.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {
    TextView tv_history, tv_update, tv_favourite, tv_name, tv_email;
    View back;
    String idUser;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

//        lay thong tin nguoi dung
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data");
        idUser = bundle.getString("idUser");

//        Xu ly su kien
        tv_history = findViewById(R.id.tv_history);
        tv_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, HistoryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("idUser", idUser);
                intent.putExtra("data" ,bundle);
                startActivity(intent);
            }
        });

        tv_favourite = findViewById(R.id.tv_favourite);
        tv_favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, ManageListFavorite.class);
                Bundle bundle = new Bundle();
                bundle.putString("idUser", idUser);
                intent.putExtra("data" ,bundle);
                startActivity(intent);
            }
        });

        tv_update = findViewById(R.id.tv_update);
        tv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                tv_update.setText("ok");
            }
        });

        tv_name = findViewById(R.id.tv_name);
        tv_email = findViewById(R.id.tv_email);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        displayUser(idUser);
    }

    public void displayUser(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    tv_name.setText(documentSnapshot.getString("name")+"");
                    tv_email.setText(documentSnapshot.getString("email")+"");
                } else {

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

    }

}