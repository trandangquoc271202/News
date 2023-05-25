package com.example.news.model;

public class News {
    private String id;
    private String name;
    private String link;

    public News() {
    }

    public News(String name, String link, String id) {
        this.name = name;
        this.link = link;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
