package br.com.beautystyle.model.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
public class Customer implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String name;
    private String phone;
    private Long tenant;
    private Long apiId;
    private boolean isUser = false;

    @Ignore
    public Customer(String name, String phone) {
        this.apiId = null;
        this.name = name;
        this.phone = phone;
    }

    public Customer() {
    }

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    public Long getTenant() {
        return tenant;
    }

    public void setTenant(Long tenant) {
        this.tenant = tenant;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isIdNotNull() {
        return id != null;
    }

    public boolean checkApiId() {
        return apiId != null;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id)
                && Objects.equals(name, customer.name)
                && Objects.equals(phone, customer.phone)
                && Objects.equals(tenant, customer.tenant)
                && Objects.equals(apiId, customer.apiId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, phone, tenant, apiId);
    }

    public boolean isNewContact(List<Customer> customers) {
        return customers.stream()
                .noneMatch(client ->
                        client.getName().equals(this.name) &&
                                client.getPhone().equals(this.phone)
                );
    }

    public boolean isNotExistOnApi(List<Customer> customerFromApi) {
        for (Customer fromApi : customerFromApi) {
            if (this.apiId.equals(fromApi.getApiId())) {
                return false;
            }
        }
        return true;
    }

    public boolean isApiIdEquals(Long apiId) {
        return this.apiId != null && this.apiId.equals(apiId);
    }
}


