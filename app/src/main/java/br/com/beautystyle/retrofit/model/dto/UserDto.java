package br.com.beautystyle.retrofit.model.dto;

import java.util.List;

import br.com.beautystyle.model.entity.OpeningHours;

public class UserDto {

    private String token;
    private String type;
    private Long tenant;
    private List<String> profiles;
    private List<OpeningHours> openingHours;

    public String getTypeToken(){
        return this.type + " " + this.token;
    }

    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }

    public Long getTenant() {
        return tenant;
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

    public void setTenant(Long tenant) {
        this.tenant = tenant;
    }

    public void setProfiles(List<String> profiles) {
        this.profiles = profiles;
    }

    public List<OpeningHours> getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(List<OpeningHours> openingHours) {
        this.openingHours = openingHours;
    }

    public String getProfile() {
        if(!profiles.isEmpty()){
            return profiles.get(0);
        }
        return "";
    }
}
