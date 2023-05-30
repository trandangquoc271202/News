package com.example.news;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
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

import com.example.news.model.Item;
import com.example.news.firebase.DatabaseFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class History extends AppCompatActivity {
    ListView listView;
    String idUser;
    Dialog dialog;
    View back;
    View deleteALl;
    DatabaseFirebase database;
    public ArrayList<Item> ItemLists = new ArrayList<Item>();
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history2);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data");
        idUser = bundle.getString("idUser");
        database = new DatabaseFirebase();
        loadAllHistory();
        UpdateLV();

        listView = (ListView) findViewById(R.id.lv_news);

        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            deleteDialogHistory(ItemLists.get(i).getId());
            return true;
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                openLink(i);
            }
        });
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        deleteALl = findViewById(R.id.delete_all);
        deleteALl.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAllDialogHistory();
            }
        }));
    }

    private void deleteDialogHistory(String item) {
        dialog = new Dialog(History.this);
        dialog.setContentView(R.layout.dialog_del_favorite);
        Button button = dialog.findViewById(R.id.btn_del_favorite);

        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                database.deleteHistory(item);
                UpdateLV();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void deleteAllDialogHistory() {
        dialog = new Dialog(History.this);
        dialog.setContentView(R.layout.delete_all_favorite);
        Button button = dialog.findViewById(R.id.cancel);
        Button button2 = dialog.findViewById(R.id.confirm);
        TextView text = dialog.findViewById(R.id.text);
        text.setText("Bạn có chắc muốn xóa toàn bộ lịch sử không?");
        button2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                database.deleteAllHistory(idUser);
                ItemLists = new ArrayList<Item>();
                ListDatabaseAdapter listdata = new ListDatabaseAdapter(ItemLists);
                listView.setAdapter(listdata);
//                UpdateLV();
                dialog.dismiss();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void openLink(int i){
        Intent intent = new Intent(History.this, DetailActivity.class);
        intent.putExtra("linknews", ItemLists.get(i).getLink());
        intent.putExtra("idUser", idUser);
        startActivity(intent);
    }
    public void loadAllHistory() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("history").whereEqualTo("idUser", idUser)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Item item;
                        if (task.isSuccessful()) {
                            ArrayList<Item> list = new ArrayList<Item>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                item = new Item(document.getId(),document.getData().get("title").toString(), document.getData().get("link").toString(), document.getData().get("date").toString(), document.getData().get("linkImg").toString());
                                list.add(item);
                            }
                            UpdateLV(list);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void UpdateLV() {
        ItemLists = new ArrayList<Item>();
        loadAllHistory();
    }

    public void UpdateLV(ArrayList<Item> listRss) {
        ItemLists = (ArrayList<Item>) listRss;
        ListDatabaseAdapter listdata = new ListDatabaseAdapter(ItemLists);
        listView.setAdapter(listdata);
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
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View viewString;
            if (view == null) {
                viewString = View.inflate(viewGroup.getContext(), R.layout.item_newss, null);
            } else viewString = view;
            Item item = (Item) getItem(i);

            TextView textView = viewString.findViewById(R.id.tv_title_item_news);
            textView.setText(item.getTitle());
            TextView t = viewString.findViewById(R.id.tv_date_news);
            t.setText(item.getDate());
            ImageView iv_news = viewString.findViewById(R.id.iv_news);
            Picasso.with(viewGroup.getContext()).load(item.getLinkImg())
                    .placeholder(R.mipmap.placeholder)
                    .error(R.mipmap.placeholder)
                    .into(iv_news);
            return viewString;
        }
    }
}