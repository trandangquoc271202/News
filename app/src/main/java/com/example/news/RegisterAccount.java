package com.example.news;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.news.enity.News;
import com.example.news.firebase.DatabaseFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RegisterAccount extends AppCompatActivity {
    private EditText Username;
    private EditText Email;
    private EditText Password;
    private EditText ConfirmPassword;
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
                if(validateRegister(username, email, password, confirmPassword)) {
                    // Thực hiện đăng kí
                    AccountExist(username,email, password );
                }else{
                    // Hiển thị thông báo lỗi nếu nhập sai thông tin đăng kí
                    Toast.makeText(RegisterAccount.this, "Thông tin nhập vào không hợp lệ", Toast.LENGTH_SHORT).show();
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


// Kiểm tra rỗng
    private boolean validateRegister(String username, String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(username)) {
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            return false;
        }
        if (!password.equals(confirmPassword)) {
            return false;
        }
        return true;
    }
//    Kiểm tra tồn tại
    private void AccountExist(String username, String email, String pass){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                  .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                      @Override
                      public void onComplete(@NonNull Task<QuerySnapshot> task) {

                          if (task.isSuccessful()) {
                              boolean result =false;
                              for (QueryDocumentSnapshot document : task.getResult()) {
                                  if(document.getData().get("username").toString().equals(username) ||document.getData().get("email").toString().equals(email) ){
                                      result = true;
                                      break;
                                  }
                              }
                              if(result){
                                  result(true);
                              }
                              if(!result){
                                  database.saveAccount(username, pass, email);
                                  Intent intent = new Intent(RegisterAccount.this, Login.class);
                                  startActivity(intent);
                                  finish();
                              }
                          }
                      }
                  });

    }
    public void result(boolean result){

    }


}
