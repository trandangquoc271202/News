package com.example.news.firebase;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.news.MainActivity;
import com.example.news.enity.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseFirebase {
    FirebaseFirestore db;

    public DatabaseFirebase() {
        this.db = FirebaseFirestore.getInstance();
    }


    public void addRss(String name, String link) {
        Map<String, Object> rss = new HashMap<>();
        rss.put("name", name);
        rss.put("link", link);
        db.collection("rss").add(rss);
    }

    public void addHistory(String username, Item item) {
//        DocumentReference docRef = FirebaseFirestore.getInstance().collection("history").document(item.getId());
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        // Tài liệu tồn tại
//                        System.out.println("history tồn tại");
//                    } else {
//                        // Tài liệu không tồn tại
                        Map<String, Object> rss = new HashMap<>();
                        rss.put("idUser", username);
                        rss.put("title", item.getTitle());
                        rss.put("link", item.getLink());
                        rss.put("date", item.getDate());
                        rss.put("linkImg", item.getLinkImg());
                        db.collection("history").add(rss);
//                    }
//                } else {
//                    System.out.println("lỗi truy vấn history");
//                    System.out.println(task.getException());
//                    // Xảy ra lỗi khi truy vấn tài liệu
//                }
//            }
//        });

    }


    public void deleteRss(String document) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("rss").document(document);
        docRef.delete();
    }

    public void updateRss(String document, String name, String link) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("rss").document(document);
        Map<String, Object> newData = new HashMap<>();
        newData.put("name", name);
        newData.put("link", link);
        docRef.set(newData);
    }

    public void deleteUser(String document) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(document);
        docRef.delete();
    }

    public void updateUser(String document, String name, String username, String password, String role, String typeAccount, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(document);
        Map<String, Object> newData = new HashMap<>();
        newData.put("name", name);
        newData.put("username", username);
        newData.put("password", password);
        newData.put("role", role);
        newData.put("typeAccount", typeAccount);
        newData.put("email", email);

        docRef.set(newData);
    }

public void saveAccount(String username, String password, String email){
    Map<String, Object> save = new HashMap<>();
    save.put("username", username);
    save.put("password", password);
    save.put("email", email);
    save.put("name", "");
    save.put("role", "user");
    save.put("typeAccount","0");
    db.collection("users").add(save);
}
    public void addFavorite(Item item, String id) {
        Map<String, Object> favorite = new HashMap<>();
        favorite.put("idUser", id);
        favorite.put("title", item.getTitle());
        favorite.put("link", item.getLink());
        favorite.put("date", item.getDate());
        favorite.put("linkImg", item.getLinkImg());
        db.collection("favorite").add(favorite);
    }

    public void deleteFavorite(String document) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("favorite").document(document);
        docRef.delete();
    }
    public void updateNameUser(String name, String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(id);
        Map<String, Object> newData = new HashMap<>();
        newData.put("name", name);
        docRef.update(newData);
    }
    public void updatePassUser(String pass, String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(id);
        Map<String, Object> newData = new HashMap<>();
        newData.put("password", pass);
        docRef.update(newData);
    }
    public void deleteHistory(String document) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("history").document(document);
        docRef.delete();
    }
}
