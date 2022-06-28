package br.com.beautystyle.model.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

import br.com.beautystyle.model.UserLogin;

@Entity
public class User {

    @NonNull
    @PrimaryKey
    private String email="";
    private String password;
    private String profile;

    @Ignore
    public User(UserLogin userLogin, List<String> profiles) {
        this.email = userLogin.getEmail();
        this.password = userLogin.getPassword();
        this.profile = profiles.get(0);
    }

    public User() {
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
