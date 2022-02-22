package br.com.beautystyle.domain.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;


@Entity
public class Client implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id = 0;
    private String name;
    private String phone;

    @Ignore
    public Client(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public Client() {
    }

    public int getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setId(int id) {
        this.id = id;
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
