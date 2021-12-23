package br.com.beautystyle.dao;

import br.com.beautystyle.model.Client;

import java.util.ArrayList;
import java.util.List;

public class ClienteDao {
    private static int countId= 1;

    private final static List<Client> listClients = new ArrayList<>();

    public void save(Client client){
        client.setId(countId);
        listClients.add(client);
        countId++;
    }

    public List<Client> listAll(){
        return new ArrayList<>(listClients);
    }

    public void remove(Client client) {
        Client findedClient = findClientById(client);
        if (findedClient != null) {
            listClients.remove(findedClient);
        }
    }
        public Client findClientById(Client findClient){
            for (Client client : listClients) {
                if (findClient.getId() == client.getId()) {
                    return client;
                }
            }
            return null;
        }

    public void edit(Client newClient) {
        Client client = findClientById(newClient);
        if (client != null) {
            int clientAtPosition = listClients.indexOf(client);
            listClients.set(clientAtPosition, newClient);
        }
    }

    public boolean checkClient(Client client) {
        return listAll().stream()
                .anyMatch(c->c.getName().equals(client.getName()
                )&&c.getPhone().equals(client.getPhone()));
    }

    public void saveAllImportedClients(List<Client> contactList) {
        for (Client c : contactList) {
            save(c);
        }
    }
}
