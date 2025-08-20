package com.example.simulation.service;

import com.example.simulation.model.Note;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {

    public List<Note> getAllNotes(String clientGuid){

        List<Note> noteList = Arrays.asList(
                new Note("Comment 1", "b16ee9dd-7bba-11f0-9768-00155d6a2540", "1991-12-02 08:55:30", "guid1", "2021-09-16 12:02:26 CDT", "user1", "2023-10-15 09:30:00"),
                new Note("Comment 2", "456", "1991-10-14 11:00:00", "guid2", "1991-09-16 12:02:26 CDT", "user2", "2023-10-14 10:30:00"),
                new Note("Comment 3", "b16efbb9-7bba-11f0-9768-00155d6a2540", "2025-01-10 17:00:00", "guid3", "2021-09-16 12:02:26 CDT", "user3", "2023-10-13 11:30:00"),
                new Note("Comment 4", "b16efdd6-7bba-11f0-9768-00155d6a2540", "2022-02-05 12:30:00", "guid4", "2021-09-16 12:02:26 CDT", "user4", "2023-10-12 12:30:00"),
                new Note("Comment 5", "000", "2010-10-11 14:00:00", "guid5", "1991-09-16 12:02:26 CDT", "user5", "2023-10-11 13:30:00"),
                new Note("Comment 6", "111", "2010-10-10 15:00:00", "guid6", "1991-09-16 12:02:26 CDT", "user6", "2023-10-10 14:30:00"),
                new Note("Comment 7", "b16f063b-7bba-11f0-9768-00155d6a2540", "2025-10-10 15:00:00", "guid2", "2021-09-16 12:02:26 CDT", "user7", "2023-10-10 14:30:00")
        );

        List<Note> noteListCompareWithClientGuid = new ArrayList<>();

        for (Note note : noteList) {
            if(note.getClientGuid().equals(clientGuid))
                noteListCompareWithClientGuid.add(note);
        }

       return noteListCompareWithClientGuid;

    }
}
