package com.example.news;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.news.adapter.News_Adapter;
import com.example.news.model.Item;
import com.example.news.firebase.DatabaseFirebase;
import com.example.news.xmlpullparser.XmlPullParserHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NewsActivity extends AppCompatActivity {
    ListView lv;
    public List<Item> ItemLists = new ArrayList<>();
    String link, title;
    String idUser;
    Dialog dialog;
    DatabaseFirebase db;
    View back;
    TextView tv_title;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        lv = findViewById(R.id.lv_news);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data");
        idUser = bundle.getString("idUser");
        link = bundle.getString("link");
        title = bundle.getString("title");

        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(title);
        db = new DatabaseFirebase();

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        downloadNew();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CompletableFuture<Boolean> isExistfuture = db.checkExistHistory(idUser, ItemLists.get(i));
                isExistfuture.thenAccept(isExist -> {
                    if (!isExist) {
                        db.addHistory(idUser, ItemLists.get(i));
                        dialog.dismiss();
                    }
                });
                openLink(i);
            }
        });

        lv.setOnItemLongClickListener((adapterView, view, i, l) -> {
            openDialogFavorite(ItemLists.get(i));
            return true;
        });
    }

    public void openDialogFavorite(Item item) {
        dialog = new Dialog(NewsActivity.this);
        dialog.setContentView(R.layout.dialog_add_favorite);
        Button button = dialog.findViewById(R.id.btn_add_favorite);
        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                CompletableFuture<Boolean> isExistFuture = db.checkExistFavorite(item, idUser);
                isExistFuture.thenAccept(isExist -> {
                    if (isExist) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Đã tồn tại trong danh sách yêu thích!", Toast.LENGTH_SHORT).show();
                    } else {
                        db.addFavorite(item, idUser);
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Thêm vào danh sách yêu thích thành công!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        dialog.show();
    }

    public void openLink(int i) {
        Intent intent = new Intent(NewsActivity.this, DetailActivity.class);
        intent.putExtra("linknews", ItemLists.get(i).getLink());
        intent.putExtra("idUser", idUser);
        startActivity(intent);
    }

    public void downloadNew() {
        new downloadXML(NewsActivity.this, lv).execute(link);
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
                ItemLists = loadURLfromNetWork(strings[0]);
                return ItemLists;
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Item> list) {
            super.onPostExecute(list);
            adapter = new News_Adapter(context, (ArrayList<Item>) list);
            if (list != null) {
                listView.setAdapter(adapter);
            }
        }

        private InputStream downloadURL(String url) throws IOException {
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

        public List<Item> loadURLfromNetWork(String strUrl) throws Exception {
            InputStream stream = null;
            XmlPullParserHandler handler = new XmlPullParserHandler();
            ItemLists = null;
            try {
                stream = downloadURL(strUrl);
                Log.i("00000", stream.toString());
                ItemLists = handler.Pasers(stream);
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
            Log.i("00000", String.valueOf(ItemLists.size()));
            return ItemLists;
        }
    }
}