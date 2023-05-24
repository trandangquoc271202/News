package com.example.news;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {
    Button b1, b2, b3;
    String idUser;
    View user;
    TextView tv_category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data");
        idUser = bundle.getString("idUser");

        user = findViewById(R.id.user);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("idUser", idUser);
                intent.putExtra("data" ,bundle);
                startActivity(intent);
            }
        });
        tv_category = findViewById(R.id.tv_category);
        tv_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("idUser", idUser);
                intent.putExtra("data" ,bundle);
                startActivity(intent);
            }
        });

//        Intent intent = getIntent();
//        Bundle bundle = intent.getBundleExtra("data");
//        idUser = bundle.getString("idUser");
//        b1 = findViewById(R.id.button);
//
//        b1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("idUser", idUser);
//                intent.putExtra("data" ,bundle);
//                startActivity(intent);
//            }
//        });
//
//        b2 = findViewById(R.id.button2);
//
//        b2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("idUser", idUser);
//                intent.putExtra("data" ,bundle);
//                startActivity(intent);
//            }
//        });
//
//        b3 = findViewById(R.id.button3);
//
//        b3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("idUser", idUser);
//                intent.putExtra("data" ,bundle);
//                startActivity(intent);
//            }
//        });

    }
}