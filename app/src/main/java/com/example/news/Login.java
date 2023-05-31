package com.example.news;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.news.firebase.DatabaseFirebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class Login extends AppCompatActivity {
    private EditText Username;
    private EditText Password;
    private Button LoginButton;
    private Button RegisterButton;
    private Button loginGG;
    private TextView Display;
    private int RC_SIGN_IN = 9001;
    private DatabaseFirebase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Username = findViewById(R.id.edit_text_username);
        Password = findViewById(R.id.edit_text_password);
        LoginButton = findViewById(R.id.button_login);
        RegisterButton = findViewById(R.id.button_register);
        Display = findViewById(R.id.display);
        loginGG = findViewById(R.id.google);
        database = new DatabaseFirebase();
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(Login.this, options);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = Username.getText().toString();
                String password = Password.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Display.setText("Điền đầy đủ thông tin về tên đăng nhập hoặc mật khẩu!");
                } else {
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

//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        if (account == null) {
//            Display.setText("Dang nhap k thanh cong");
//        } else {
//            Display.setText("Dang nhap thanh cong");
//        }
        loginGG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
//                googleSignInClient.signOut();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                String username = account.getId();
                String password = account.getId();
                String email = account.getEmail();
                AccountExistGG(username, password, email);

            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void AccountExistGG(String username, String password, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").whereEqualTo("typeAccount", "1")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            boolean result = false;
                            String id = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if ( document.getData().get("password").toString().equals(password)) {
                                    result = true;
                                    id = document.getId();
                                    break;
                                }
                            }
                            if (result) {
                                writeInfoLogin(id, username, password);
                                Intent intent = new Intent(Login.this, HomeActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("idUser", id);
                                intent.putExtra("data", bundle);
                                startActivity(intent);
                                finish();
                            } else {
                                getDocumentIdGG(username, password, email);
                            }
                        }
                    }
                });
    }
    private void getDocumentIdGG(String username, String password, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> save = new HashMap<>();
        save.put("username", username);
        save.put("password", password);
        save.put("email", email);
        save.put("name", "");
        save.put("role", "user");
        save.put("typeAccount", "1");
        db.collection("users")
                .add(save)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Intent intent = new Intent(Login.this, HomeActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("idUser", documentReference.getId());
                        intent.putExtra("data", bundle);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
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