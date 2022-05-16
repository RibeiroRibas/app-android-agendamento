package br.com.beautystyle.model.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;


@Entity
public class Client implements Serializable {

    @PrimaryKey
    private Long clientId;
    private String name;
    private String phone;

    @Ignore
    public Client(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public Client() {
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean checkNameAndPhone(List<Client> clientList) {
        return clientList.stream()
                .anyMatch(c->c.getName().equals(getName()
                )&&c.getPhone().equals(getPhone()));
    }
}
