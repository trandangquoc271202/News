package com.example.news.xmlpullparser;

import android.util.Log;
import android.util.Xml;

import com.example.news.enity.Item;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XmlPullParserHandler {
    public static final String ns = null;
    public List<Item> Pasers(InputStream in) throws XmlPullParserException , IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readRss(parser);
        }finally {
            in.close();
        }
    }
    private List<Item> readRss(XmlPullParser parser) throws XmlPullParserException , IOException {
        Log.i("-----", "read rss");
        List<Item> list = new ArrayList<Item>();
        parser.require(XmlPullParser.START_TAG, ns, "rss");
        while (parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            String name1 = parser.getName();
            if (name1.equals("channel")) {
                parser.require(XmlPullParser.START_TAG, ns, "channel");
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    String name = parser.getName();
                    if (name.equals("item")){
                        list.add(readItem(parser));
                    }else {
                        skip(parser);
                    }
                }
            }
        }
        return list;
    }


    private Item readItem(XmlPullParser parser) throws XmlPullParserException , IOException {
        Log.i("-----", "read Item");
        parser.require(XmlPullParser.START_TAG, ns, "item");
        String title = null;
        String link = null;
        String pubDate = null;
        String img = null;
        while (parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")){
                title = readTitle(parser);
                Log.i("-------title ",title);
            }else if (name.equals("link")){
                link = readLink(parser);
                Log.i("-------link ",link);
            }else if (name.equals("description")){
                img = readImgLink(parser);
                Log.i("-------link img",img);
            }else if (name.equals("pubDate")){
                pubDate = readDate(parser);
                Log.i("-------pubDate ",pubDate);
            }else {
                skip(parser);
            }

        }
        return new Item(title, link, pubDate, img);
    }


    private String getLinkFromText(String a){
        String link = "";
        if (a.contains("img")){
            int posImg = a.indexOf("img");// lấy vị trí chuỗi "img" trong chỗi
            int posDong = a.indexOf("/>");
            a = a.substring(posImg, posDong);
            a = a.substring(a.indexOf("\"")+1, a.length() - 2);
        }
        return a;
    }
    private String readImgLink(XmlPullParser parser)throws XmlPullParserException , IOException{
        Log.i("-----", "read description"+parser.getText());
        String imageLink = "";
        parser.require(XmlPullParser.START_TAG, ns, "description");
       while (parser.next() != XmlPullParser.END_TAG){
           String name = parser.getText();
           imageLink = getLinkFromText(name);
           Log.i("-----", "read description IMG SRC: "+imageLink);
       }
        return imageLink;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException , IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG){
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0){
            switch (parser.next()){
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
    private String readText(XmlPullParser parser) throws XmlPullParserException , IOException {
        //Log.i("-----", "read text");
        String rs = "";
        if (parser.next() == XmlPullParser.TEXT){
            rs = parser.getText();
            parser.nextTag();
        }
        return rs;
    }
    private String readTitle(XmlPullParser parser) throws XmlPullParserException , IOException {
        Log.i("-----", "read title");
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }
    private String readLink(XmlPullParser parser) throws XmlPullParserException , IOException {
        Log.i("-----", "read link");
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link;
    }
    private String readDate(XmlPullParser parser) throws XmlPullParserException , IOException {
        Log.i("-----", "read date");
        parser.require(XmlPullParser.START_TAG, ns, "pubDate");
        String pubDate = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "pubDate");
        return pubDate;
    }
}
