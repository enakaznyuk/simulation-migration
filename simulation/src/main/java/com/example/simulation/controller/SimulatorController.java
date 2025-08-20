package com.example.simulation.controller;

import com.example.simulation.dto.RequestBodyDto;
import com.example.simulation.model.Client;
import com.example.simulation.model.Note;
import com.example.simulation.service.ClientService;
import com.example.simulation.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SimulatorController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private NoteService noteService;

    @PostMapping(value = "/data", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Client> generatePersons() {


        return clientService.getAllClients();
    }

    @PostMapping(value = "/data/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Note> getDataWithParams(@RequestBody RequestBodyDto params) {

        String clientGuid = params.getClientGuid();
        return noteService.getAllNotes(clientGuid);
    }
}
