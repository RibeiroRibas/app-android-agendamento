package br.com.beautystyle.model.entity;

import androidx.room.PrimaryKey;

public class Company {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String cnpj;
    private String name;
    private Long userCreatorId;
    private Long addressCreatorId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getUserCreatorId() {
        return userCreatorId;
    }

    public void setUserCreatorId(Long userCreatorId) {
        this.userCreatorId = userCreatorId;
    }

    public Long getAddressCreatorId() {
        return addressCreatorId;
    }

    public void setAddressCreatorId(Long addressCreatorId) {
        this.addressCreatorId = addressCreatorId;
    }
}
