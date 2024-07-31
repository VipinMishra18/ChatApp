package common;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String password; // Add more fields as needed

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
