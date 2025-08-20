package com.example.migration.service;

import com.example.migration.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProcessingCollectionsService {

    @Autowired
    private PatientNoteService patientNoteService;

    @Autowired
    private CompanyUserService companyUserService;

    @Autowired
    private PatientProfileService patientProfileService;

    private static final Logger logger = LoggerFactory.getLogger(ProcessingCollectionsService.class);

    public Map<String, String> comparedGuidAndAgencyFromFirstRequest(List<ClientsPost> clientsPostList){

        return clientsPostList.stream()
                .filter(client -> {
                    boolean valid = client.getGuid() != null && client.getAgency() != null;
                    if (!valid) {
                        logger.debug("Пропущен клиент без guid или agency: {}", client);
                    }
                    return valid;
                })
                .collect(Collectors.toMap(
                        ClientsPost::getGuid,
                        ClientsPost::getAgency,
                        (existingValue, newValue) -> existingValue
                ));
    }

    public Map<String, Long> comparedGuidAndPatientIdFromDb( List<CustomRequest> activePatientsFromDBList){

        return activePatientsFromDBList.stream()
                .filter(obj -> {
                    boolean valid = obj.getOldClientGuid() != null && obj.getPatientId() != null;
                    if (!valid) {
                        logger.debug("Объект пропущен из-за null: {}", obj);
                    }
                    return valid;
                })
                .flatMap(obj -> Arrays.stream(obj.getOldClientGuid().split(","))
                        .map(String::trim)
                        .filter(guid -> !guid.isEmpty())
                        .map(guid -> new AbstractMap.SimpleEntry<>(guid, obj.getPatientId()))
                )
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> existing
                ));
    }

    public Map<String, String> comparedGuidAndAgencyWithActivePatients(List<CustomRequest> activePatientsFromDBList,
                                                                       Map<String, String> guidAgencyMapFromFirstRequest){

        return activePatientsFromDBList.stream()
                .flatMap(request -> {
                    String oldGuids = request.getOldClientGuid();
                    if (oldGuids == null || oldGuids.trim().isEmpty()) {
                        return Stream.empty();
                    }
                    return Arrays.stream(oldGuids.split(","))
                            .map(String::trim)
                            .filter(guidAgencyMapFromFirstRequest::containsKey);
                })
                .distinct()
                .collect(Collectors.toMap(
                        guid -> guid,
                        guidAgencyMapFromFirstRequest::get
                ));
    }

    public Map<String, List<CustomRequest>> matchingGuidToDateTimeFromDB(List<CustomRequest> activePatientsFromDBList,
                                                                        Map<String, String> guidAgencyMapFromFirstRequest){
        Map<String, Set<CustomRequest>> matchingGuidToDateTime =
        activePatientsFromDBList.stream()
                .flatMap(request -> {
                    String oldGuids = request.getOldClientGuid();
                    if (oldGuids == null || oldGuids.trim().isEmpty()) {
                        return Stream.empty();
                    }
                    return Arrays.stream(oldGuids.split(","))
                            .map(String::trim)
                            .filter(guidAgencyMapFromFirstRequest::containsKey);
                })
                .collect(Collectors.groupingBy(
                        guid -> guid,
                        Collectors.flatMapping(guid -> {
                            return activePatientsFromDBList.stream()
                                    .filter(req -> {
                                        String guidsStr = req.getOldClientGuid();
                                        if (guidsStr == null || guidsStr.trim().isEmpty()) {
                                            return false;
                                        }
                                        List<String> guids = Arrays.stream(guidsStr.split(","))
                                                .map(String::trim).toList();
                                        return guids.contains(guid);
                                    });
                        }, Collectors.toSet())
                ));

        return matchingGuidToDateTime.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new ArrayList<>(entry.getValue())
                ));
    }

    public void separateNotesAndSaveChangesIntoDb(List<NotesPostResponse> guidFromDBNotesFromAPIMappingList,
                                                  Map<String, List<CustomRequest>> matchingGuidToDateTime,
                                                  List<ClientsPost> clientsPostList,
                                                  Map<String, Long> matchingGuidAndPatientId){

        Set<CustomRequest> toRemoveSet = new HashSet<>();

        for (NotesPostResponse note : guidFromDBNotesFromAPIMappingList) {
            String clientGuid = note.getClientGuid();
            boolean existsInMatchingMap = false;

            if (matchingGuidToDateTime.containsKey(clientGuid)) {

                List<CustomRequest> customRequests = matchingGuidToDateTime.get(clientGuid);
                Optional<CustomRequest> matchingRequestOpt = customRequests.stream()
                        .filter(cr -> {
                            String noteGuid = cr.getNoteGuid();
                            if (noteGuid == null) {
                                return false;
                            }
                            return noteGuid.equals(note.getGuid());
                        })
                        .findFirst();

                if (matchingRequestOpt.isPresent()) {
                    existsInMatchingMap = true;
                    CustomRequest matchingRequest = matchingRequestOpt.get();

                    logger.info("Обработка совпадения для GUID: {}. LastModifiedDateTime из базы: {}, из заметки: {}",
                            clientGuid, matchingRequest.getLastModifiedDateTime(), note.getModifiedDateTime());

                    if(matchingRequest.getLastModifiedDateTime().equals(note.getModifiedDateTime()) || matchingRequest.getLastModifiedDateTime().isAfter(note.getModifiedDateTime())){
                        logger.info("Даты совпадают или дата из базы позже. GUID: {}", clientGuid);
                        toRemoveSet.add(matchingRequest);
                    }
                    if(matchingRequest.getLastModifiedDateTime().isBefore(note.getModifiedDateTime())){
                        logger.info("Дата из базы раньше. Обновляем заметку для GUID: {}", clientGuid);
                        patientNoteService.updatePatientNote(note);
                        toRemoveSet.add(matchingRequest);
                    }
                }
            }
            if (!existsInMatchingMap) {
                logger.info("Заметка с датой {} не найдена в matchingGuidToDateTime для клиента GUID: {}", note.getModifiedDateTime(), clientGuid);

                ClientsPost clientsPost = clientsPostList.stream()
                        .filter(p -> p.getGuid().equals(clientGuid))
                        .findFirst()
                        .orElse(null);

                List<CompanyUserDto> companyUserDtoList = companyUserService.getAllCompanyUsers();
                Long patientId = matchingGuidAndPatientId.get(note.getClientGuid());
                Long userId = null;
                for(CompanyUserDto companyUserDto : companyUserDtoList){
                    if (companyUserDto.getLogin().equals(note.getLoggedUser())) {
                        userId = companyUserDto.getId();
                        break;
                    }
                }
                logger.info("Создание новой заметки для клиента GUID: {}, PatientID: {}, UserID: {}", clientGuid, patientId, userId);
                assert clientsPost != null;
                patientNoteService.createPatientNote(note, clientsPost, patientId, userId);
            }
        }

        List<CustomRequest> nullNoteGuidRemove;
        for(Map.Entry<String, List<CustomRequest>> a : matchingGuidToDateTime.entrySet()){
            nullNoteGuidRemove = a.getValue().stream()
                    .filter(cr -> cr.getNoteGuid() == null)
                    .toList();
            logger.info("Изменения для GUID {}. Удаляем записи с guidNote null {}",
                    a.getKey(), nullNoteGuidRemove);
            a.getValue().removeAll(nullNoteGuidRemove);
            a.getValue().removeAll(toRemoveSet);
        }

        for (Map.Entry<String, List<CustomRequest>> a : matchingGuidToDateTime.entrySet()){
            for (CustomRequest customRequest : a.getValue()){
                logger.info("Удаление записи по NoteGUID: {}", customRequest.getNoteGuid());
                patientNoteService.deleteByNoteGuid(customRequest.getNoteGuid());
            }
        }
    }
}
