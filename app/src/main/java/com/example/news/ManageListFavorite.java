package com.example.news;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.news.adapter.ManageListFavoriteAdapter;
import com.example.news.adapter.News_Adapter;
import com.example.news.enity.Item;
import com.example.news.firebase.DatabaseFirebase;
import com.example.news.xmlpullparser.XmlPullParserHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ManageListFavorite extends AppCompatActivity {
    ListView lv_favourite;
    public List<Item> listFavorite;
    String link;
    Button btn_back;
    Dialog dialog;
    ManageListFavoriteAdapter adapter;
    DatabaseFirebase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_list_favorite);
        lv_favourite = findViewById(R.id.lv_favorite);
        btn_back = (Button) findViewById(R.id.btn_back);
        db = new DatabaseFirebase();
        loadAllFavourite();
        updateLV();
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        link = intent.getStringExtra("link");
        if (checkInternet()) {
            downloadNew();
        }

        lv_favourite.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                openLink(i);
            }
        });
        lv_favourite.setOnItemLongClickListener((adapterView, view, i, l) -> {
            deleteDialogFavorite(listFavorite.get(i).getId());
            return true;
        });
    }

    public void loadAllFavourite() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("favorite").whereEqualTo("idUser", "123123").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Item item;
                if (task.isSuccessful()) {
                    List<Item> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document: task.getResult()
                         ) {
                        item = new Item(document.getId() ,document.getData().get("title").toString(),document.getData().get("link").toString(),
                                document.getData().get("date").toString(),document.getData().get("linkImg").toString());
                        list.add(item);
                        updateLV(list);
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    public void updateLV() {
        listFavorite = new ArrayList<>();
        loadAllFavourite();
        adapter = new ManageListFavoriteAdapter(getApplicationContext(), ManageListFavorite.this, listFavorite);
        lv_favourite.setAdapter(adapter);
    }
    public void updateLV(List<Item> list) {
        listFavorite = list;
        adapter = new ManageListFavoriteAdapter(getApplicationContext(), ManageListFavorite.this, listFavorite);
        lv_favourite.setAdapter(adapter);
    }
    private void deleteDialogFavorite(String item) {
        dialog = new Dialog(ManageListFavorite.this);
        dialog.setContentView(R.layout.dialog_del_favorite);
        Button button = dialog.findViewById(R.id.btn_del_favorite);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.deleteFavorite(item);
                updateLV();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void openLink(int i){
        Intent intent = new Intent(ManageListFavorite.this, WebViewActivity.class);
        intent.putExtra("linknews", listFavorite.get(i).getLink());
        startActivity(intent);
    }

    public boolean checkInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnected() || mobile.isConnected()){
            return true;
        }
        return false;
    }

    public void downloadNew(){
        new ManageListFavorite.downloadXML(ManageListFavorite.this, lv_favourite).execute(link);
    }
    public class downloadXML extends AsyncTask<String, Void, List<Item>> {

        News_Adapter adapter;
        private ListView listView;
        private Context context;
        public downloadXML(Context context, ListView lv) {
            this.context = context;
            this.listView = lv;
        }

        @Override
        protected List<Item> doInBackground(String... strings) {
            try {
                listFavorite  =  loadURLfromNetWork(strings[0]);
                return listFavorite;
            }catch (Exception e){
            }
            return null;
        }
        @Override
        protected void onPostExecute(List<Item> list) {
            super.onPostExecute(list);
            adapter = new News_Adapter(context, (ArrayList<Item>) list);
            if (list != null){
                listView.setAdapter(adapter);
            }
        }
        private InputStream downloadURL(String url)throws IOException {
            java.net.URL url1 = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            InputStream in = conn.getInputStream();
            //Log.i("00000", in.toString());
            return in;
        }
        public List<Item> loadURLfromNetWork(String strUrl)throws Exception{
            InputStream stream = null;
            XmlPullParserHandler handler = new XmlPullParserHandler();
            listFavorite = null;
            try {
                stream = downloadURL(strUrl);
                Log.i("00000", stream.toString());
                listFavorite = handler.Pasers(stream);
            }finally {
                if (stream != null){
                    stream.close();
                }
            }
            Log.i("00000", String.valueOf(listFavorite.size()));
            return listFavorite;
        }
    }
}
