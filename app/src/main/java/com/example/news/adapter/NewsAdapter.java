package com.example.news.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.news.MainActivity;
import com.example.news.R;
import com.example.news.dao.NewsDAO;
import com.example.news.enity.News;

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<News> {
    Context context;
    TextView tv_name;
    ArrayList<News> list;
    NewsDAO dao;
    View v_del;
    MainActivity main;
    public NewsAdapter(@NonNull Context context,MainActivity activity, ArrayList<News> list) {
        super(context, 0, list);
        this.context = context;
        this.list = list;
        this.main = activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_title_main, null);
        }
        final News item = list.get(position);
        if (v != null){
            tv_name = v.findViewById(R.id.tv_name);
            v_del = v.findViewById(R.id.v_del);
            tv_name.setText(item.getName());
        }

        return v;
    }
}
