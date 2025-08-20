package com.example.simulation.service;

import com.example.simulation.model.Client;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    public List<Client> getAllClients(){
        Client client1 = new Client();
        Client client2 = new Client();
        Client client3 = new Client();
        Client client4 = new Client();
        Client client5 = new Client();
        Client client6 = new Client();
//        Client client7 = new Client();
//        Client client8 = new Client();
//        Client client9 = new Client();
//        Client client0 = new Client();

        client1.setAgency("vhh4");
        client1.setFirstName("First");
        client1.setLastName("First");
        client1.setStatus("111");
        client1.setDob("10-15-1999");
        client1.setGuid("guid1");
        client1.setCreatedDateTime("2021-12-02 08:55:30");

        client2.setAgency("vhh4");
        client2.setFirstName("Second");
        client2.setLastName("Second");
        client2.setStatus("111");
        client2.setDob("10-15-1999");
        client2.setGuid("guid2");
        client2.setCreatedDateTime("2022-12-22 12:12:12");

        client3.setAgency("vhh4");
        client3.setFirstName("Third");
        client3.setLastName("Third");
        client3.setStatus("111");
        client3.setDob("10-15-1999");
        client3.setGuid("guid3");
        client3.setCreatedDateTime("2022-01-10 17:00:00");

        client4.setAgency("vhh4");
        client4.setFirstName("Forth");
        client4.setLastName("Forth");
        client4.setStatus("111");
        client4.setDob("10-15-1999");
        client4.setGuid("guid4");
        client4.setCreatedDateTime("2022-02-05 12:30:00");

        client5.setAgency("vhh4");
        client5.setFirstName("Fifth");
        client5.setLastName("Fifth");
        client5.setStatus("111");
        client5.setDob("10-15-1999");
        client5.setGuid("guid5");
        client5.setCreatedDateTime("1990-12-02 08:55:30");

        client6.setAgency("vhh4");
        client6.setFirstName("Sixth");
        client6.setLastName("Sixth");
        client6.setStatus("111");
        client6.setDob("10-15-1999");
        client6.setGuid("guid6");
        client6.setCreatedDateTime("1990-12-02 08:55:30");

        List<Client> clientList = new ArrayList<>();
        clientList.add(client1);
        clientList.add(client2);
        clientList.add(client3);
        clientList.add(client4);
        clientList.add(client5);
        clientList.add(client6);
//        clientList.add(client7);
//        clientList.add(client8);
//        clientList.add(client9);
//        clientList.add(client0);
        return clientList;
    }
}
