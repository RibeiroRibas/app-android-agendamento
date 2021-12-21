package com.example.beautystyle.dao;

import com.example.beautystyle.model.Services;

import java.util.ArrayList;
import java.util.List;

public class ServiceDao {
    private final static List<Services> listServices = new ArrayList<>();
    private static int countId = 1;

    public void save(Services service){
        service.setId(countId);
        listServices.add(service);
        countId++;
    }

    public List<Services> listAll(){
        return new ArrayList<>(listServices);
    }

    public void remove(Services selectedService) {
        Services service = findServiceById(selectedService);
        if (service != null) {
            listServices.remove(service);
        }
    }

    private Services findServiceById(Services selectedService) {
        for (Services service : listServices) {
            if (selectedService.getId() == service.getId()) {
                return service;
            }
        }
        return null;
    }

    public void edit(Services newService) {
        Services service = findServiceById(newService);
        if (service != null) {
            int eventAtPosition = listServices.indexOf(service);
            listServices.set(eventAtPosition, newService);
        }
    }
}
