package com.example.news;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.news.adapter.NewsAdapter;
import com.example.news.dao.NewsDAO;
import com.example.news.enity.News;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView lv_main;
    View view_add, view_add_favorite;
    Dialog dialog;
    TextInputEditText ed_name, ed_link;
    Button btn_add, btn_del, btn_cancel;
    NewsAdapter adapter;
    NewsDAO dao;
    News news;
    ArrayList<News> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv_main = findViewById(R.id.lv_main);
        view_add = findViewById(R.id.view_add);
        view_add_favorite = findViewById(R.id.view_list_favorite);

        dao = new NewsDAO(MainActivity.this);
        UpdateLV();

        deleteCache(getApplicationContext()); //xóa cache
        view_add_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ManageListFavorite.class);
                startActivity(intent);
            }
        });
        view_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "Click", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, ManageRss.class);
                startActivity(intent);
            }
        });
        lv_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (checkNetwork()) {
                    String link = list.get(i).getLink();
                    if (!link.isEmpty()) {
                        Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                        intent.putExtra("link", link);
                        startActivity(intent);
                    }
                } else {
                    NoInternetToast();
                }
            }
        });

    }

    public void openDialog() {
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_add);
        ed_name = dialog.findViewById(R.id.ed_name);
        ed_link = dialog.findViewById(R.id.ed_link);
        btn_add = dialog.findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = ed_name.getText().toString().trim();
                String link = ed_link.getText().toString().trim();
                if (name.isEmpty() || link.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else {
                    News news = new News();
                    news.setName(name);
                    news.setLink(link);
                    if (dao.insert(news)) {
                        Toast.makeText(getApplicationContext(), "thêm thành công", Toast.LENGTH_SHORT).show();
                        UpdateLV();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "lỗi", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        dialog.show();
    }

    public void NoInternetToast() {
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.no_internet_toast, null);
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(v);
        toast.show();
    }

    private boolean checkNetwork() {
        boolean wifiAvailable = false;
        boolean mobileAvailable = false;
        ConnectivityManager conManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = conManager.getAllNetworkInfo();
        for (NetworkInfo netInfo : networkInfo) {
            if (netInfo.getTypeName().equalsIgnoreCase("WIFI"))
                if (netInfo.isConnected()) wifiAvailable = true;
            if (netInfo.getTypeName().equalsIgnoreCase("MOBILE"))
                if (netInfo.isConnected()) mobileAvailable = true;
        }
        return wifiAvailable || mobileAvailable;
    }

    public void delete(String id) {
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_del);
        btn_cancel = dialog.findViewById(R.id.btn_cancel);
        btn_del = dialog.findViewById(R.id.btn_del);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dao.delete(id)) {
                    Toast.makeText(getApplicationContext(), "xóa thành công", Toast.LENGTH_SHORT).show();
                    UpdateLV();
                } else {
                    Toast.makeText(getApplicationContext(), "lỗi", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
        UpdateLV();
    }
    public void loadAllRss(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("rss")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        News news;
                        if (task.isSuccessful()) {
                            ArrayList<News> list = new ArrayList<News>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                news = new News(document.getData().get("name").toString(), document.getData().get("link").toString(), document.getId());
                                list.add(news);
                                UpdateLV(list);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
    public void UpdateLV() {
        list = new ArrayList<News>();
        loadAllRss();
        adapter = new NewsAdapter(getApplicationContext(), MainActivity.this, list);
        lv_main.setAdapter(adapter);
    }
    public void UpdateLV(ArrayList<News> listRss) {
        list = (ArrayList<News>) listRss;
        adapter = new NewsAdapter(getApplicationContext(), MainActivity.this, list);
        lv_main.setAdapter(adapter);
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