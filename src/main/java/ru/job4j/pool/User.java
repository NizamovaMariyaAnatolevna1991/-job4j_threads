package ru.job4j.pool;

public class User {
    String username;
    String email;
    String body;

    public User(String username, String email, String masharu) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
