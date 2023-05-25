package com.example.news;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class Login extends AppCompatActivity {
    private EditText Username;
    private EditText Password;
    private Button LoginButton;
    private Button RegisterButton;
    private TextView Display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Username = findViewById(R.id.edit_text_username);
        Password = findViewById(R.id.edit_text_password);
        LoginButton = findViewById(R.id.button_login);
        RegisterButton = findViewById(R.id.button_register);
        Display = findViewById(R.id.display);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = Username.getText().toString();
                String password = Password.getText().toString();

                if(username.isEmpty() || password.isEmpty()){
                        Display.setText("Điền đầy đủ thông tin về tên đăng nhập hoặc mật khẩu!");
                }else{
                    AccountExist(username, password);
                }
            }
        });
//        Chuyển sang đăng kí
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, RegisterAccount.class);
                startActivity(intent);
            }
        });

    }

    private void AccountExist(String username, String pass) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            boolean result = false;
                            String id = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getData().get("username").toString().equals(username) && document.getData().get("password").toString().equals(pass)) {
                                    result = true;
                                    id = document.getId();
                                    break;
                                }
                            }
                            if (result) {
                                writeInfoLogin(id, username, pass);
                                Intent intent = new Intent(Login.this, HomeActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("idUser", id);
                                intent.putExtra("data", bundle);
                                startActivity(intent);
                                finish();
                            }
                            if (!result) {
                                Display.setText("Sai tên đăng nhập hoặc mật khẩu!");
                            }
                        }
                    }
                });
    }
    public void writeInfoLogin(String id, String username, String password) {
        SQLiteDatabase database = openOrCreateDatabase("statelogin", MODE_PRIVATE, null);
        String sql = "CREATE TABLE login (idUser TEXT,username TEXT,password TEXT)";
        try {
            database.execSQL(sql);
        } catch (Exception e) {
        }
        ContentValues values = new ContentValues();
        values.put("idUser", id);
        values.put("username", username);
        values.put("password", password);
        database.insert("login", null, values);
        database.close();
    }
}