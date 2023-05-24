package com.example.news;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.news.adapter.News_Adapter;
import com.example.news.enity.Item;
import com.example.news.firebase.DatabaseFirebase;
import com.example.news.xmlpullparser.XmlPullParserHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    String idUser;
    View user;
    TextView tv_category;

    ListView lv;
    public List<Item> ItemLists = new ArrayList<>();
    DatabaseFirebase db;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data");
        idUser = bundle.getString("idUser");

        user = findViewById(R.id.user);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("idUser", idUser);
                intent.putExtra("data" ,bundle);
                startActivity(intent);
            }
        });
        tv_category = findViewById(R.id.tv_category);
        tv_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("idUser", idUser);
                intent.putExtra("data" ,bundle);
                startActivity(intent);
            }
        });
        downloadNew();
        db = new DatabaseFirebase();
        lv = findViewById(R.id.lv_news);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                db.addHistory(idUser,ItemLists.get(i));
                openLink(i);
            }
        });

        lv.setOnItemLongClickListener((adapterView, view, i, l) -> {
            openDialogFavorite(ItemLists.get(i));
            return true;
        });
    }
    public void openDialogFavorite(Item item){
        dialog = new Dialog(HomeActivity.this);
        dialog.setContentView(R.layout.dialog_add_favorite);
        Button button = dialog.findViewById(R.id.btn_add_favorite);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.addFavorite(item, idUser);
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Thêm vào danh sách yêu thích thành công!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    public void openLink(int i){
        Intent intent = new Intent(HomeActivity.this, WebViewActivity.class);
        intent.putExtra("linknews", ItemLists.get(i).getLink());
        startActivity(intent);
    }

    public void downloadNew(){
        new HomeActivity.downloadXML(HomeActivity.this, lv).execute("https://vtv.vn/trong-nuoc.rss");
    }

    public class downloadXML extends AsyncTask<String, Void, List<Item>> {

        News_Adapter adapter;
        private ListView listView;
        private Context context;
        public downloadXML(Context context, ListView lv) {
            this.context = context;
            this.listView = findViewById(R.id.lv_news);
        }

        @Override
        protected List<Item> doInBackground(String... strings) {
            try {
                ItemLists  =  loadURLfromNetWork(strings[0]);
                return ItemLists;
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
            ItemLists = null;
            try {
                stream = downloadURL(strUrl);
                Log.i("00000", stream.toString());
                ItemLists = handler.Pasers(stream);
            }finally {
                if (stream != null){
                    stream.close();
                }
            }
            Log.i("00000", String.valueOf(ItemLists.size()));
            return ItemLists;
        }
    }
}