package com.example.news.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.news.R;
import com.example.news.enity.Item;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class News_Adapter extends ArrayAdapter<Item> {
    private Context context;
    private List<Item> lists;
    TextView tv_title, tv_link, tv_date;
    ImageView iv_news;
    public News_Adapter(@NonNull Context context, ArrayList<Item> list) {
        super(context, 0, list);
        this.context = context;
        this.lists = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_newss, null);
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            v = inflater.inflate(R.layout.item_news, null);
        }
        final Item item = lists.get(position);
        if (item != null){
            tv_title = v.findViewById(R.id.tv_title_item_news);
            tv_link = v.findViewById(R.id.tv_link_news);
            tv_date = v.findViewById(R.id.tv_date_news);
            iv_news = v.findViewById(R.id.iv_news);
            tv_title.setText(item.getTitle());
            tv_link.setText(item.getLink());
            Picasso.with(getContext()).load(item.getLinkImg())
                    .placeholder(R.mipmap.placeholder)
                    .error(R.mipmap.placeholder)
                    .into(iv_news);
            try {
                String date = item.getDate().substring(4, 16);
                tv_date.setText(date);
            }catch (Exception e){

            }
        }
        return v;
    }


    //    public String getItemLink(int position){
//        Item entry = lists.get(position);
//        String link = entry.getTitle();
//        return link;
//    }
}
