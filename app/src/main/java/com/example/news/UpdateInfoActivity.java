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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateInfoActivity extends AppCompatActivity {
    EditText et_name, pass1, pass2, pass3;
    View back;
    Button btn_update, btn_updatePass;
    String idUser;
    TextView nofi, nofiname;
    DatabaseFirebase databse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upadte_info);
        databse = new DatabaseFirebase();
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data");
        idUser = bundle.getString("idUser");
        et_name = findViewById(R.id.et_name);
        btn_update = findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                databse.updateNameUser(et_name.getText().toString(), idUser);
                if(!et_name.getText().toString().equals("")){
                    databse.updateNameUser(et_name.getText().toString(), idUser);
                    nofiname.setText("Cập nhật thành công");
                }else{
                    nofiname.setText("Yêu cầu không để trống");
                }
            }
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        pass1 = findViewById(R.id.pass1);
        pass2 = findViewById(R.id.pass2);
        pass3 = findViewById(R.id.pass3);
        nofi = findViewById(R.id.nofi);
        nofiname = findViewById(R.id.nofiname);
        btn_updatePass = findViewById(R.id.btn_updatePass);
        btn_updatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkEqual()) {
                    checkPass();
                }
            }
        });
        displayUser();
    }

    public void displayUser() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(idUser);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    et_name.setText(documentSnapshot.getString("name") + "");
                } else {
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    public boolean checkEqual() {
        String p1 = pass1.getText().toString();
        String p2 = pass2.getText().toString();
        String p3 = pass3.getText().toString();
        if (p3.equals("") && p2.equals("") && p1.equals("")) {
            nofi.setText("Yêu cầu nhập đủ thông tin");
            return false;
        }
        if (p1.equals(p2) && !p3.equals("") && !p2.equals("") && !p1.equals("")) {
            return true;
        }
        nofi.setText("Yêu cầu nhập mới giống nhau");
        return false;
    }

    public void checkPass() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(idUser);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.getString("password").equals(pass3.getText().toString())) {
                        databse.updatePassUser(pass1.getText().toString(), idUser);
                        pass1.setText("");
                        pass2.setText("");
                        pass3.setText("");
                        nofi.setText("Đổi thành công");
                    } else {
                        nofi.setText("Sai mật khẩu");
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
}