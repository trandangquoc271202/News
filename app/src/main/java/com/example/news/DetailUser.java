package com.example.news;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.news.model.User;
import com.example.news.firebase.DatabaseFirebase;


public class DetailUser extends AppCompatActivity implements View.OnClickListener{

    EditText text_id, text_name, text_username, text_role, text_type_acc, text_email;
    Button btn_back;
    View  btn_edit, btn_save, btn_delete;
    String pass;
    DatabaseFirebase db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_user);

        db = new DatabaseFirebase();

        Intent callerIntent = getIntent();
        Bundle packetFromCaller = callerIntent.getBundleExtra("package");
        String id = packetFromCaller.getString("id");
        String name = packetFromCaller.getString("name");
        String username = packetFromCaller.getString("username");
        String role = packetFromCaller.getString("role");
        String type_acc = packetFromCaller.getString("type_acc");
        pass = packetFromCaller.getString("pass");
        String email = packetFromCaller.getString("email");

        text_id = findViewById(R.id.text_id);
        text_id.setEnabled(false);
        text_id.setClickable(false);
        text_id.setFocusable(false);

        text_name = findViewById(R.id.text_name);
        text_name.setEnabled(false);
        text_name.setClickable(false);

        text_username = findViewById(R.id.text_username);
        text_username.setEnabled(false);
        text_username.setClickable(false);
        text_username.setFocusable(false);

        text_role = findViewById(R.id.text_role);
        text_role.setEnabled(false);
        text_role.setClickable(false);

        text_type_acc = findViewById(R.id.text_type_acc);
        text_type_acc.setEnabled(false);
        text_type_acc.setClickable(false);
        text_type_acc.setFocusable(false);

        text_email = findViewById(R.id.text_email);
        text_email.setEnabled(false);
        text_email.setClickable(false);
        text_email.setFocusable(false);

        text_id.setText(id);
        text_name.setText(name);
        text_username.setText(username);
        text_role.setText(role);
        text_type_acc.setText(type_acc);
        text_email.setText(email);


        btn_back = findViewById(R.id.bn_back);
        btn_edit = findViewById(R.id.view_edit);
        btn_edit.setOnClickListener(this);
        btn_save = findViewById(R.id.view_save);
        btn_save.setOnClickListener(this);
        btn_delete = findViewById(R.id.view_delete);
        btn_delete.setOnClickListener(this);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.view_edit:
                active_edit();
                break;
            case R.id.view_save:
                save_edit();
                break;
            case R.id.view_delete:
                delete_user();
                break;
        }
    }

    private void delete_user() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete);

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        dialog.setCancelable(false);
        Button cancel = dialog.findViewById(R.id.cancel);
        Button delete = dialog.findViewById(R.id.delete);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = text_id.getText().toString();
                db.deleteUser(id);
                Intent myintent = new Intent(DetailUser.this,ListUser.class);
                startActivity(myintent);
            }
        });

        dialog.show();
    }

    private void save_edit() {
        String name = text_name.getText().toString();
        String username = text_username.getText().toString();
        String role = text_role.getText().toString();
        String roles = "";
        if(name.equalsIgnoreCase("")||username.equalsIgnoreCase("")||role.equalsIgnoreCase("")){
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        }else if(!(role.equalsIgnoreCase("user")||role.equalsIgnoreCase("admin"))){
            Toast.makeText(this, "Giá trị role chỉ có thể là user hoặc admin", Toast.LENGTH_SHORT).show();
        }else{
            if(role.equalsIgnoreCase("user")){
                roles+="user";
            }else{
                roles+="admin";
            }
            User user = new User();
            user.setId(text_id.getText().toString());
            user.setName(name);
            user.setUsername(username);
            user.setPassword(pass);
            user.setRole(roles);
            user.setTypeAccount(text_type_acc.getText().toString());
            user.setEmail(text_email.getText().toString());
            if (checkNetwork()) {
                Toast.makeText(getApplicationContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                db.updateUser(text_id.getText().toString(), name, username,pass,roles,text_type_acc.getText().toString(),text_email.getText().toString());
                Intent myintent = new Intent(DetailUser.this,ListUser.class);
                startActivity(myintent);
            } else {
                Toast.makeText(getApplicationContext(), "lỗi", Toast.LENGTH_SHORT).show();
                NoInternetToast();
            }
        }
    }

    private void NoInternetToast() {
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.no_internet_toast, null);
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(v);
        toast.show();
    }

    private boolean checkNetwork() {
        boolean wifiAvailable = false;
        boolean mobileAvailable = false;
        ConnectivityManager conManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = conManager.getAllNetworkInfo();
        for (NetworkInfo netInfo : networkInfo) {
            if (netInfo.getTypeName().equalsIgnoreCase("WIFI"))
                if (netInfo.isConnected()) wifiAvailable = true;
            if (netInfo.getTypeName().equalsIgnoreCase("MOBILE"))
                if (netInfo.isConnected()) mobileAvailable = true;
        }
        return wifiAvailable || mobileAvailable;
    }

    private void active_edit() {
        text_name.setEnabled(true);
        text_name.setClickable(true);

        text_role.setEnabled(true);
        text_role.setClickable(true);
    }
}