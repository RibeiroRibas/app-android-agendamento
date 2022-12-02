package br.com.beautystyle.retrofit.model.form;

import br.com.beautystyle.model.entity.User;

public class UserLoginForm {

    private String email;
    private String password;

    public UserLoginForm(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserLoginForm(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
