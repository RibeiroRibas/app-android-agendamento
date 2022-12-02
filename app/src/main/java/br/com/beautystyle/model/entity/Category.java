package br.com.beautystyle.model.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

@Entity
public class Category implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String name;
    private Long apiId;
    private Long tenant;

    @Ignore
    public Category(String name) {
        this.name = name;
    }

    public Category() {
    }

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTenant() {
        return tenant;
    }

    public void setTenant(Long tenant) {
        this.tenant = tenant;
    }

    public boolean isApiIdEquals(Category categoryFromApi) {
        if(this.apiId != null){
            return this.apiId.equals(categoryFromApi.getApiId());
        }
        return false;
    }

    public boolean isNotExistOnApi(List<Category> categoriesFromApi) {
        if(this.apiId != null){
            for (Category categoryFromApi : categoriesFromApi) {
                if (this.apiId.equals(categoryFromApi.getApiId())) {
                    return false;
                }
            }
        }
        return true;
    }
}
