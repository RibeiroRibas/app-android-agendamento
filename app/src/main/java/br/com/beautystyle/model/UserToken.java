package br.com.beautystyle.model;

import java.util.List;

public class UserToken {

    private String token;
    private String type;
    private Long companyId;
    private List<String> profiles;

    public String getTypeToken(){
        return this.type + " " + this.token;
    }

    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public List<String> getProfiles() {
        return profiles;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public void setProfiles(List<String> profiles) {
        this.profiles = profiles;
    }

    public String getProfile() {
        if(!profiles.isEmpty()){
            return profiles.get(0);
        }
        return "";
    }
}
