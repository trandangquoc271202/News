package com.example.news;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ManagerUser extends AppCompatActivity implements View.OnClickListener {

    Button view_user;
    Button permission_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_user);

        view_user = findViewById(R.id.view_user);
        view_user.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.view_user:
                toListUser();
                break;
        }
    }

    private void toListUser() {
        Intent myintent = new Intent(ManagerUser.this,ListUser.class);
        Bundle bundle = new Bundle();
        myintent.putExtra("package",bundle);
        startActivity(myintent);
    }
}