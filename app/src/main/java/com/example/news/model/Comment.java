package com.example.news.model;

public class Comment {
    private String id;
    private String content;
    private String idUser;
    private String link;

    public Comment(String content, String idUser, String link) {
        this.content = content;
        this.idUser = idUser;
        this.link = link;
    }
    public Comment(String content, String idUser, String link, String id) {
        this.content = content;
        this.idUser = idUser;
        this.link = link;
        this.id = id;
    }

    public Comment() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
