package com.example.migration.scheduler;

import com.example.migration.client.FirstRequestClient;
import com.example.migration.client.SecondRequestClient;
import com.example.migration.dto.ClientsPost;
import com.example.migration.dto.CustomRequest;
import com.example.migration.dto.NotesPostRequest;
import com.example.migration.dto.NotesPostResponse;
import com.example.migration.service.PatientNoteService;
import com.example.migration.service.ProcessingCollectionsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ScheduledRequest {

    private final FirstRequestClient firstRequestClient;

    private final SecondRequestClient secondRequestClient;

    @Autowired
    private PatientNoteService patientNoteService;

    @Autowired
    private ProcessingCollectionsService processingCollectionsService;

    private static final Logger logger = LoggerFactory.getLogger(ScheduledRequest.class);

    public void migrationLogic() throws JsonProcessingException {

        List<ClientsPost> clientsPostList = firstRequestClient.getAllClients();
        logger.info("Получено клиентов из первого запроса апи. Найдено {} ", clientsPostList.size());

        Map<String, String> guidAgencyMapFromFirstRequest = processingCollectionsService
                .comparedGuidAndAgencyFromFirstRequest(clientsPostList);
        logger.info("Завершено сравнение GUID и Agency. Найдено {} пар", guidAgencyMapFromFirstRequest.size());

        List<CustomRequest> activePatientsFromDBList = patientNoteService.getActivePatients();
        logger.info("Получение активных пациентов. Найдено {}", activePatientsFromDBList.size());

        Map<String, Long> matchingGuidAndPatientId = processingCollectionsService.comparedGuidAndPatientIdFromDb(activePatientsFromDBList);
        logger.info("Завершено сравнение GUID и PatientId. Найдено {} пар", matchingGuidAndPatientId.size());

        Map<String, String> matchingGuidAndAgencyWithActivePatients = processingCollectionsService
                .comparedGuidAndAgencyWithActivePatients(activePatientsFromDBList,
                        guidAgencyMapFromFirstRequest);
        logger.info("Завершено сравнение GUID и Agency. Найдено {} ", matchingGuidAndAgencyWithActivePatients.size());

        List<NotesPostResponse> notesFromSecondRequest = new ArrayList<>();

        matchingGuidAndAgencyWithActivePatients.forEach((guid, agency) -> {
            try {
                NotesPostRequest notesPostRequest = new NotesPostRequest();
                notesPostRequest.setAgency(agency);
                notesPostRequest.setClientGuid(guid);
                List<NotesPostResponse> notes = secondRequestClient.sendRequestWithBody(notesPostRequest);
                logger.info("Получено записей из второго запроса апи. Найдено {} ", notes.size());
                notesFromSecondRequest.addAll(notes);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });

        Map<String, List<CustomRequest>> matchingGuidToDateTime = processingCollectionsService
                .matchingGuidToDateTimeFromDB(activePatientsFromDBList, guidAgencyMapFromFirstRequest);
        logger.info("Завершено сопоставление GUID с датой/временем. Найдено {} ", matchingGuidToDateTime.size());

        processingCollectionsService.separateNotesAndSaveChangesIntoDb(notesFromSecondRequest,
                matchingGuidToDateTime,
                clientsPostList,
                matchingGuidAndPatientId);
    }
}
