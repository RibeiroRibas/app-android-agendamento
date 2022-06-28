package br.com.beautystyle.model.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;


@Entity
public class Client implements Serializable{

    @PrimaryKey(autoGenerate = true)
    private Long clientId = 0L;
    private String name;
    private String phone;
    private Long companyId;
    private Long apiId;
    private Long userId = 0L;

    @Ignore
    public Client(String name, String phone, Long companyId) {
        this.name = name;
        this.phone = phone;
        this.companyId = companyId;
    }

    public Client() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
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

    public boolean checkId() {
        return clientId > 0;
    }

    public boolean checkApiId() {
        return apiId != null;
    }

    public boolean isClientAnUser() {
        return userId > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(clientId, client.clientId)
                && Objects.equals(name, client.name)
                && Objects.equals(phone, client.phone)
                && Objects.equals(companyId, client.companyId)
                && Objects.equals(apiId, client.apiId)
                && Objects.equals(userId, client.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, name, phone, companyId, apiId, userId);
    }

    public boolean isNewContact(List<Client> clients) {
        return clients.stream()
                .noneMatch(client ->
                        client.getName().equals(this.name) &&
                                client.getPhone().equals(this.phone)
                );
    }

    @Override
    public String toString() {
        return "Client{" +
                "clientId=" + clientId +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", companyId=" + companyId +
                ", apiId=" + apiId +
                ", userId=" + userId +
                '}';
    }
}


