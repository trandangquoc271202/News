package com.example.news;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.news.adapter.ManageUserAdapter;
import com.example.news.model.User;
import com.example.news.firebase.DatabaseFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListUser extends AppCompatActivity implements View.OnClickListener {
    ListView lv_main;
    Button btn_back, btn_search, btn_all;
    DatabaseFirebase db;
    ArrayList<User> list;
    ManageUserAdapter adapter;
    EditText text_search;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);

        lv_main = findViewById(R.id.lv_main);
        btn_back = (Button) findViewById(R.id.bn_back);
        db = new DatabaseFirebase();
        UpdateLV();
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        lv_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Xử lý sự kiện tại đây
                long itemID = parent.getItemIdAtPosition(position);
                User user = (User)parent.getItemAtPosition((int)itemID);
//                Toast.makeText(ListUser.this, ""+user.getId(), Toast.LENGTH_SHORT).show();
                Detail_Customer(user);
            }
        });
        btn_search = findViewById(R.id.search);
        btn_search.setOnClickListener(this);
        btn_all = findViewById(R.id.all);
        btn_all.setOnClickListener(this);

    }

    private void Detail_Customer(User user) {
        String id = user.getId();
        String name = user.getName();
        String username = user.getUsername();
        String role = user.getRole();
        String type_acc = user.getTypeAccount();
        String pass = user.getPassword();
        String email = user.getEmail();

        Intent myintent = new Intent(ListUser.this,DetailUser.class);
        Bundle bundle = new Bundle();
        bundle.putString("id",id);
        bundle.putString("name",name);
        bundle.putString("username",username);
        bundle.putString("role",role);
        bundle.putString("type_acc",type_acc);
        bundle.putString("pass",pass);
        bundle.putString("email",email);
        myintent.putExtra("package",bundle);
        startActivity(myintent);
    }

    public void UpdateLV() {
        list = new ArrayList<User>();
        loadAllRss();
        adapter = new ManageUserAdapter(getApplicationContext(), ListUser.this, list);
        lv_main.setAdapter(adapter);
    }
    public void UpdateLV(ArrayList<User> listUser) {
        list = (ArrayList<User>) listUser;
        adapter = new ManageUserAdapter(getApplicationContext(), ListUser.this, list);
        lv_main.setAdapter(adapter);
    }

    private void loadAllRss() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        User user;
                        if (task.isSuccessful()) {
                            ArrayList<User> list = new ArrayList<User>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Toast.makeText(ListUser.this, ""+document.getData().get("name"), Toast.LENGTH_SHORT).show();
                                user = new User((String) document.getData().get("name"),(String) document.getData().get("username"),(String) document.getData().get("password"),(String) document.getData().get("role"),(String) document.getData().get("typeAccount"),document.getId(),(String)document.getData().get("email"));
                                list.add(user);
                                UpdateLV(list);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.search:
                search();
                break;
            case R.id.all:
                Intent myintent = new Intent(ListUser.this,ListUser.class);
                startActivity(myintent);
                finish();
                break;
        }
    }

    private void search() {
        ArrayList<User> list = new ArrayList<User>();
        text_search = findViewById(R.id.text_search);
        String text = text_search.getText().toString();
        int size = lv_main.getAdapter().getCount();
        for(int i=0;i<size;i++){
            User user = (User)lv_main.getAdapter().getItem(i);
            if(user.getUsername().toLowerCase().contains(text)){
                list.add(user);
            }
        }
        if(list.isEmpty()){
            Toast.makeText(this, "Không có thông tin cần tìm", Toast.LENGTH_SHORT).show();
        }else{
            UpdateLV(list);
        }
    }
}