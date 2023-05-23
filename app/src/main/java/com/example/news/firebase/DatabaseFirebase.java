package com.example.news.firebase;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.news.MainActivity;
import com.example.news.enity.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
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

    public void writeUser(String username, String pass) {
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("passwork", pass);
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void addRss(String name, String link) {
        Map<String, Object> rss = new HashMap<>();
        rss.put("name", name);
        rss.put("link", link);
        db.collection("rss").add(rss);
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
}
