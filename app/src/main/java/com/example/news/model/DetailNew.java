package com.example.news.model;

public class DetailNew {
    private String des;
    private String linkImage;

    public DetailNew(String des, String linkImage) {
        this.des = des;
        this.linkImage = linkImage;
    }
    public DetailNew(){

    }
    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getLinkImage() {
        return linkImage;
    }

    public void setLinkImage(String linkImage) {
        this.linkImage = linkImage;
    }
}
