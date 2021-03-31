package com.movenet.movenet.model;

public class User
{
    private String id;
    private String username;
    private String email;
    private String number;
    private String pwd;

    public User() {}

    public User(String id, String username, String email, String number, String pwd)
    {
        this.id = id;
        this.username = username;
        this.email = email;
        this.number = number;
        this.pwd = pwd;
    }

    public void setId(String id) { this.id = id; }
    public String getId() { return id; }
    public void setUsername(String username) { this.username = username;  }
    public String getUsername() { return username; }
    public void setEmail(String email) { this.email = email; }
    public String getEmail() { return email; }
    public void setNumber(String number) { this.number = number; }
    public String getNumber() { return number; }
    public void setPwd(String pwd) { this.pwd = pwd; }
    public String getPwd() { return pwd; }

    @Override
    public String toString() { return id + " " + username + " " + email + " " + number; }
}
