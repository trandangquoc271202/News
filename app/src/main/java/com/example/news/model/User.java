package com.example.news.model;

public class User {
    private String id;
    private String name;
    private String email;
    private String username;
    private String password;
    private String role;
    private String typeAccount;

    public User(String name, String username, String password, String role, String typeAccount, String id, String email) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
        this.typeAccount = typeAccount;
        this.id = id;
        this.email = email;
    }
    public User(){

    }
    public User(String name){
        this.name = name;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTypeAccount() {
        return typeAccount;
    }

    public void setTypeAccount(String typeAccount) {
        this.typeAccount = typeAccount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
