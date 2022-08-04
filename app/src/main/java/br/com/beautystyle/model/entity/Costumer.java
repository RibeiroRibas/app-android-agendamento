package br.com.beautystyle.model.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
public class Costumer implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private Long clientId = 0L;
    private String name;
    private String phone;
    private Long companyId;
    private Long apiId = 0L;
    private Long userId = 0L;

    @Ignore
    public Costumer(String name, String phone) {
        this.apiId = null;
        this.name = name;
        this.phone = phone;
    }

    public Costumer() {
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
        Costumer costumer = (Costumer) o;
        return Objects.equals(clientId, costumer.clientId)
                && Objects.equals(name, costumer.name)
                && Objects.equals(phone, costumer.phone)
                && Objects.equals(companyId, costumer.companyId)
                && Objects.equals(apiId, costumer.apiId)
                && Objects.equals(userId, costumer.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, name, phone, companyId, apiId, userId);
    }

    public boolean isNewContact(List<Costumer> costumers) {
        return costumers.stream()
                .noneMatch(client ->
                        client.getName().equals(this.name) &&
                                client.getPhone().equals(this.phone)
                );
    }

    public boolean isNotExistOnApi(List<Costumer> costumerFromApi) {
        for (Costumer fromApi : costumerFromApi) {
            if (this.apiId.equals(fromApi.getApiId())) {
                return false;
            }
        }
        return true;
    }

}


