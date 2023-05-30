package com.example.news;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.news.firebase.DatabaseFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class RegisterAccount extends AppCompatActivity {
    private EditText Username;
    private EditText Email;
    private EditText Password;
    private EditText ConfirmPassword;

    private TextView Display;
    private Button RegisterButton, button_login;

    DatabaseFirebase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);
        database = new DatabaseFirebase();
        Username = findViewById(R.id.edit_text_username);
        Email = findViewById(R.id.edit_text_email);
        Password = findViewById(R.id.edit_text_password);
        ConfirmPassword = findViewById(R.id.edit_text_confirm_password);
        Display = findViewById(R.id.display);
        button_login = findViewById(R.id.button_login);
        RegisterButton = findViewById(R.id.button_register);
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = Username.getText().toString();
                String email = Email.getText().toString();
                String password = Password.getText().toString();
                String confirmPassword = ConfirmPassword.getText().toString();

//                kiểm tra thông tin đăng kí
//                if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()) {
//                    // Thực hiện đăng kí
//                    AccountExist(username, email, password);
//                } else {
//                    if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
//                        Display.setText("Yêu cầu nhập đầy đủ thông tin!");
//                    }
//
//                }
//                if (!password.equals(confirmPassword)) {
//                    Display.setText("Mật khẩu không trùng khớp!");
//                }
                if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()) {
                    if (!password.equals(confirmPassword)) {
                        Display.setText("Mật khẩu không trùng khớp!");
                    } else {
                        AccountExist(username, email, password);
                    }

                } else {
                    Display.setText("Yêu cầu nhập đầy đủ thông tin!");
                }


            }
        });
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //    Kiểm tra tồn tại
    private void AccountExist(String username, String email, String pass) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            boolean result = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getData().get("username").toString().equals(username) || document.getData().get("email").toString().equals(email)) {
                                    result = true;
                                    break;
                                }
                            }
                            if (result) {
                                Display.setText("Tên đăng nhập hoặc email đã tồn tại!");
                            }else{
                                if (!result) {
                                    database.saveAccount(username, pass, email);
                                    Intent intent = new Intent(RegisterAccount.this, Login.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }
                    }
                });

    }
}
