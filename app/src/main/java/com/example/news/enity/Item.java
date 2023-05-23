package com.example.news.enity;

public class Item {
    private String title, link, date, linkImg,id;

    public Item() {
    }

    public Item(String title, String link, String sumary) {
        this.title = title;
        this.link = link;
        this.date = sumary;
    }

    public Item(String title, String link, String date, String linkImg) {
        this.title = title;
        this.link = link;
        this.date = date;
        this.linkImg = linkImg;
    }
    public Item(String title, String link, String date, String linkImg,String id) {
        this.title = title;
        this.link = link;
        this.date = date;
        this.linkImg = linkImg;
        this.id=id;
    }
    public String getLinkImg() {
        return linkImg;
    }

    public void setLinkImg(String linkImg) {
        this.linkImg = linkImg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }
    public String getId() {
        return id;
    }
    public void setLink(String link) {
        this.link = link;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
