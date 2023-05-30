package com.example.news;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.news.model.DetailNew;
import com.example.news.model.Item;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    TextView title;
    ArrayList<DetailNew> list;
    ListView lv_news;
    View back, detail, comment;
    String link, idUser;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        link = intent.getStringExtra("linknews");
        idUser = intent.getStringExtra("idUser");

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        comment = findViewById(R.id.comment);
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, CommentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("linknews", link);
                bundle.putString("idUser", idUser);
                intent.putExtra("data", bundle);
                startActivity(intent);
            }
        });

        detail = findViewById(R.id.detail);
        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLink();
            }
        });
        lv_news = findViewById(R.id.lv_news);
        list = new ArrayList<DetailNew>();
        DetailActivity.ListDatabaseAdapter listdata = new DetailActivity.ListDatabaseAdapter(list);
        lv_news.setAdapter(listdata);
        title = findViewById(R.id.title);
        Handler handlerImageAvatar = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String result = (String) msg.obj;

            }
        };
        Handler handlerDetail = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                ArrayList<DetailNew> result = (ArrayList<DetailNew>) msg.obj;
                DetailActivity.ListDatabaseAdapter listdata = new DetailActivity.ListDatabaseAdapter(result);
                lv_news.setAdapter(listdata);
            }
        };
        Handler handlerTitle = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String result = (String) msg.obj;
                title.setText(result);
            }
        };

        List<DetailNew> des = new ArrayList<DetailNew>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = link;
                    Document doc = Jsoup.connect(url).get();

                    Element firstImage = doc.select("img.news-avatar").first();

                    try{
                        String src = firstImage.attr("src");

                    }catch (Exception e){

                    }

                    Element h1 = doc.select("h1").first();
                    String title = h1.text();

                    Elements p = doc.select("div#entry-body p");
                    for (Element link : p) {
                        DetailNew detailNew = new DetailNew();
                        detailNew.setDes(link.text());
                        des.add(detailNew);
                    }

                    ArrayList<String> imgs = new ArrayList<String>();
                    Elements linkImage = doc.select("div#entry-body img[src]");
                    for (Element link : linkImage) {
                        imgs.add(link.attr("src"));
                    }

                    if(imgs.size()<=des.size()){
                        for(int i = 0; i<imgs.size(); i++){
                            des.get(i).setLinkImage(imgs.get(i));
                        }
                    }
                    if(imgs.size()>des.size()){
                        for(int i = 0; i<des.size(); i++){
                            des.get(i).setLinkImage(imgs.get(i));
                        }
                    }

                    Message message = handlerDetail.obtainMessage();
                    message.obj = des;
                    handlerDetail.sendMessage(message);

                    Message message1 = handlerTitle.obtainMessage();
                    message1.obj = title;
                    handlerTitle.sendMessage(message1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void openLink() {
        Intent intent = new Intent(DetailActivity.this, WebViewActivity.class);
        intent.putExtra("linknews", link);
        startActivity(intent);
    }
    public class ListDatabaseAdapter extends BaseAdapter {
        final ArrayList<DetailNew> list;

        public ListDatabaseAdapter(ArrayList<DetailNew> list) {
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
                viewString = View.inflate(viewGroup.getContext(), R.layout.item_detail, null);
            } else viewString = view;
            DetailNew detail = (DetailNew) getItem(i);
            TextView display = viewString.findViewById(R.id.display);
            display.setText(detail.getDes());

            ImageView image = viewString.findViewById(R.id.iv_news);
            if(!(detail.getLinkImage()==null)){
                image.setVisibility(View.VISIBLE);
            Picasso.with(viewGroup.getContext()).load(detail.getLinkImage())
                    .placeholder(R.mipmap.placeholder)
                    .error(R.mipmap.placeholder)
                    .into(image);
            }else{
                image.setVisibility(View.GONE);
            }
            return viewString;
        }
    }
}