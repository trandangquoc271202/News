package com.example.news;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.news.adapter.News_Adapter;
import com.example.news.enity.Item;
import com.example.news.enity.News;
import com.example.news.enity.User;
import com.example.news.firebase.DatabaseFirebase;
import com.example.news.xmlpullparser.XmlPullParserHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    ListView lv;
    public
    ArrayList<Item> ItemLists = new ArrayList<Item>();
    String link;
    DatabaseFirebase db;
    ImageView iv_news;
    TextView tv_title, tv_link, tv_date;
    Button btn_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        lv = findViewById(R.id.lv_history);
        Intent intent = getIntent();
        link = intent.getStringExtra("link");
        Toast.makeText(getApplicationContext(), "" + link, Toast.LENGTH_SHORT).show();
        db = new DatabaseFirebase();
        loadAllItems();
        UpdateLV();
        ListDatabaseAdapter listdata = new ListDatabaseAdapter(ItemLists);
        listdata.updateAdapterData(ItemLists);
        lv.setAdapter(listdata);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                openLink(i);
            }
        });
    }
    private void recreateActivity() {
        // Gọi phương thức recreate() để tải lại layout activity_history
        loadAllItems();
    }
    private void loadAllItems() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("history")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        User username;
                        Item item;
                        if (task.isSuccessful()) {
                            ArrayList<News> list = new ArrayList<News>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                username = new User(document.getData().get("username").toString());
                                item = new Item(document.getData().get("title").toString(), document.getData().get("link").toString(), document.getData().get("date").toString(), document.getData().get("linkImg").toString(), document.getId());
                                ItemLists.add(item);
                                // Sau khi tải danh sách mới, cập nhật lại ListView
                                UpdateLV(ItemLists);

                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public boolean checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnected() || mobile.isConnected()) {
            return true;
        }
        return false;
    }

    public void openLink(int i) {

        Toast.makeText(getApplicationContext(), "click", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(HistoryActivity.this, WebViewActivity.class);
        intent.putExtra("linknews", ItemLists.get(i).getLink());
        startActivity(intent);

    }
    public void UpdateLV() {
        ItemLists = new ArrayList<Item>();
        loadAllItems();
    }
    public void UpdateLV(ArrayList<Item> listRss) {
        ItemLists = (ArrayList<Item>) listRss;
        ListDatabaseAdapter listdata = new ListDatabaseAdapter(ItemLists);
        lv.setAdapter(listdata);
    }

    public class ListDatabaseAdapter extends BaseAdapter {
        final ArrayList<Item> list;

        public ListDatabaseAdapter(ArrayList<Item> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Item getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public String getId(int i) {
            return list.get(i).getId();
        }

        public void deleteDB(int i) {
            this.list.remove(i);
        }

        public void deleteItem(int position) {
            Item item = list.get(position);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("history")
                    .document(item.getId())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            list.remove(position);
                            notifyDataSetChanged();
                            recreateActivity();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Xử lý lỗi khi xóa không thành công (nếu cần)
                            Log.e(TAG, "Error deleting document", e);
                        }
                    });
        }



        private void recreate() {
        }

        public void updateAdapterData(ArrayList<Item> newData) {
            ItemLists = newData;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View viewString;
            if (view == null) {
                viewString = View.inflate(viewGroup.getContext(), R.layout.item_history_new, null);
            } else {
                viewString = view;
            }
            iv_news = viewString.findViewById(R.id.iv_news);
            tv_title = viewString.findViewById(R.id.tv_title_item_news);
            tv_link = viewString.findViewById(R.id.tv_link_news);
            tv_date = viewString.findViewById(R.id.tv_date_news);
            btn_delete = viewString.findViewById(R.id.btn_delete);

            Item currentItem = getItem(i);
            Picasso.with(viewGroup.getContext()).load(getItem(i).getLinkImg())
                    .placeholder(R.mipmap.placeholder)
                    .error(R.mipmap.placeholder)
                    .into(iv_news);

            tv_title.setText(currentItem.getTitle());
            tv_link.setText(currentItem.getLink());
            tv_date.setText(currentItem.getDate());

            //  lắng nghe sự kiện click vào nút xóa
            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItem(i);
                }
            });

            return viewString;
        }

    }
}
