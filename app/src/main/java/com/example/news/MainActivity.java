package com.example.news;

import static android.content.ContentValues.TAG;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.news.adapter.NewsAdapter;
import com.example.news.model.News;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView lv_main;
    NewsAdapter adapter;
    ArrayList<News> list;
    TextView tv_home;
    View user;
    String idUser;
    View search;
    EditText text_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv_main = findViewById(R.id.lv_main);

        search = findViewById(R.id.search);
        text_search = findViewById(R.id.text_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this,MainNewsSearch.class);
                Bundle bundle = new Bundle();
                bundle.putString("text",text_search.getText().toString());
                bundle.putString("idUser",idUser);
                myIntent.putExtra("search",bundle);
                startActivity(myIntent);
            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data");
        idUser = bundle.getString("idUser");

        UpdateLV();
        lv_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (checkNetwork()) {
                    String link = list.get(i).getLink();
                    String title = list.get(i).getName();
                    if (!link.isEmpty()) {
                        Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("idUser", idUser);
                        bundle.putString("link", link);
                        bundle.putString("title", title);
                        intent.putExtra("data" ,bundle);
                        startActivity(intent);
                    }
                } else {
                    NoInternetToast();
                }
            }
        });

        tv_home = findViewById(R.id.tv_home);
        tv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        user = findViewById(R.id.user);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("idUser", idUser);
                intent.putExtra("data" ,bundle);
                startActivity(intent);
            }
        });
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