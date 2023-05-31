package com.example.news.firebase;

import static android.content.ContentValues.TAG;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.news.model.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiresApi(api = Build.VERSION_CODES.N)
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

    public CompletableFuture<Boolean> checkExistFavorite(String idUser,Item item) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        db.collection("favorite").whereEqualTo("idUser", idUser).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        Item newItem = new Item(document.getId(),
                                document.getData().get("title").toString(),
                                document.getData().get("link").toString(),
                                document.getData().get("date").toString(),
                                document.getData().get("linkImg").toString());
                        if (newItem.getTitle().equals(item.getTitle()) && newItem.getLink().equals(item.getLink()) && newItem.getDate().equals(item.getDate()) && newItem.getLinkImg().equals(item.getLinkImg())) {
                            future.complete(true);
                            return;
                        }
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
                future.complete(false);
            }
        });
        return future;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<Boolean> checkExistHistory(String idUser, Item item) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        db.collection("history").whereEqualTo("idUser", idUser).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Item newItem = new Item(document.getId(),
                                document.getData().get("title").toString(),
                                document.getData().get("link").toString(),
                                document.getData().get("date").toString(),
                                document.getData().get("linkImg").toString());
                        if (newItem.getTitle().equals(item.getTitle()) && newItem.getLink().equals(item.getLink()) && newItem.getDate().equals(item.getDate()) && newItem.getLinkImg().equals(item.getLinkImg())) {
                            future.complete(true);
                            return;
                        }
                    }
                } else {

                }
                future.complete(false);
            }
        });
        return future;
    }

    public void addHistory(String idUser, Item item) {
        Map<String, Object> rss = new HashMap<>();
        rss.put("idUser", idUser);
        rss.put("title", item.getTitle());
        rss.put("link", item.getLink());
        rss.put("date", item.getDate());
        rss.put("linkImg", item.getLinkImg());
        db.collection("history").add(rss);

    }
    public void deleteAllHistory(String idUser) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("history").whereEqualTo("idUser", idUser).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Item item;
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        deleteHistory(document.getId());
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
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

    public void saveAccount(String username, String password, String email) {
        Map<String, Object> save = new HashMap<>();
        save.put("username", username);
        save.put("password", password);
        save.put("email", email);
        save.put("name", "");
        save.put("role", "user");
        save.put("typeAccount", "0");
        db.collection("users").add(save);
    }
    public void saveAccountGG(String username, String password, String email) {
        Map<String, Object> save = new HashMap<>();
        save.put("username", username);
        save.put("password", password);
        save.put("email", email);
        save.put("name", "");
        save.put("role", "user");
        save.put("typeAccount", "1");
        db.collection("users").add(save);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<Boolean> checkExistFavorite(Item item, String idUser) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        db.collection("favorite").whereEqualTo("idUser", idUser).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Item newItem = new Item(document.getId(),
                                document.getData().get("title").toString(),
                                document.getData().get("link").toString(),
                                document.getData().get("date").toString(),
                                document.getData().get("linkImg").toString());
                        if (newItem.getTitle().equals(item.getTitle()) && newItem.getLink().equals(item.getLink()) && newItem.getDate().equals(item.getDate()) && newItem.getLinkImg().equals(item.getLinkImg())) {
                            future.complete(true);
                            return;
                        }
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
                future.complete(false);
            }
        });
        return future;
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

    public void deleteAllFavorite(String idUser) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("favorite").whereEqualTo("idUser", idUser).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Item item;
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        deleteFavorite(document.getId());
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }
    public void addComment(String idUser, String link, String content){
        Map<String, Object> rss = new HashMap<>();
        rss.put("idUser", idUser);
        rss.put("link", link);
        rss.put("content", content);
        db.collection("comments").add(rss);
    }
    public void deleteComment(String document) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("comments").document(document);
        docRef.delete();
    }
}
