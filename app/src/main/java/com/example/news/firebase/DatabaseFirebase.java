package com.example.news.firebase;

import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

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

//    public CompletableFuture<Boolean> checkExistHisroty(Item item) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        CompletableFuture<Boolean> future = new CompletableFuture<>();
//        DocumentReference docRef = FirebaseFirestore.getInstance().collection("history").document(item.getTitle());
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
//                        addHistory(username,item);
//                    }
//                } else {
//                    System.out.println("lỗi truy vấn history");
//                    System.out.println(task.getException());
//                    // Xảy ra lỗi khi truy vấn tài liệu
//                }
//            }
//        });
//    }

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
/*xoá toàn bộ lịch sử
 */
    public void deleteAllHistory(String idUser) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("history")
                .whereEqualTo("userId", idUser)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Lặp qua tất cả các tài liệu và xoá chúng
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            documentSnapshot.getReference().delete();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xảy ra lỗi trong quá trình lấy dữ liệu
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
