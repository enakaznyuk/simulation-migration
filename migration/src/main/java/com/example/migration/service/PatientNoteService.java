package com.example.migration.service;

import com.example.migration.dto.ClientsPost;
import com.example.migration.dto.CustomRequest;
import com.example.migration.dto.NotesPostResponse;
import com.example.migration.model.CompanyUser;
import com.example.migration.model.PatientNote;
import com.example.migration.model.PatientProfile;
import com.example.migration.repository.CompanyUserRepository;
import com.example.migration.repository.PatientNoteRepository;
import com.example.migration.repository.PatientProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientNoteService {

    @Autowired
    private PatientNoteRepository patientNoteRepository;

    @Autowired
    private PatientProfileRepository patientProfileRepository;

    @Autowired
    private CompanyUserRepository companyUserRepository;

    private final ModelMapper mapper;

    private static final Logger logger = LoggerFactory.getLogger(PatientNoteService.class);

    public void createPatientNote(NotesPostResponse notesPostResponse, ClientsPost clientsPost, Long patientId, Long userId) {
        logger.info("Создание новой заметки для пациента с ID={}", patientId);
        PatientNote notesForUpdate = new PatientNote();
        CompanyUser companyUser = new CompanyUser();
        PatientProfile patientProfile = new PatientProfile();

        companyUser.setLogin(notesPostResponse.getLoggedUser());
        if (userId == null) {
            logger.debug("Пользователь с userId=null, сохраняем нового пользователя");
            companyUserRepository.save(companyUser);
            logger.info("Создан новый пользователь с ID={}", companyUser.getId());
        } else {
            companyUser.setId(userId);
            logger.debug("Используем существующего пользователя с ID={}", userId);
        }

        patientProfile.setFirstName(clientsPost.getFirstName());
        patientProfile.setLastName(clientsPost.getLastName());
        patientProfile.setOldClientGuid(clientsPost.getGuid());
        patientProfile.setStatusId(Short.parseShort(clientsPost.getStatus()));
        patientProfile.setId(patientId);

        notesForUpdate.setLastModifiedDateTime(notesPostResponse.getModifiedDateTime());
        notesForUpdate.setNote(notesPostResponse.getComments());
        notesForUpdate.setCreatedDateTime(notesPostResponse.getCreatedDateTime());
        notesForUpdate.setNoteGuid(notesPostResponse.getGuid());
        notesForUpdate.setPatient(patientProfile);

        notesForUpdate.setLastModifiedByUser(companyUser);
        notesForUpdate.setCreatedByUser(companyUser);
        logger.info("Сохраняем новую заметку для пациента с ID={}", patientId);
        patientNoteRepository.save(notesForUpdate);
    }

    @Transactional
    public void deleteByNoteGuid(String noteGuid){
        patientNoteRepository.deleteByNoteGuid(noteGuid);
    }

    public List<CustomRequest> getActivePatients() {

        logger.info("Получение активных пациентов");
        List<Object[]> results = patientNoteRepository.findNotesWithPatientInfo();
        logger.info("Найдено {} записей активных пациентов", results.size());
        return results.stream()
                .map(row -> new CustomRequest(
                        row[0] != null ? ((Timestamp) row[0]).toLocalDateTime() : null,
                        row[1] != null ? ((Number) row[1]).longValue() : null,

                        (String) row[2],
                        (Long) row[3],
                        (String) row[4],
                        (Short) row[5]

                ))
                .collect(Collectors.toList());
    }

    public void updatePatientNote(NotesPostResponse notesPostResponse) {

        String guid = notesPostResponse.getGuid();
        logger.info("Обновление заметки с GUID={}", guid);

        PatientNote notesForUpdate = patientNoteRepository.findByNoteGuid(guid)
                .orElseThrow(() -> {
                    logger.error("Заметка с GUID={} не найдена", guid);
                    return new EntityNotFoundException("Заметка не найдена");
                });

        notesForUpdate.setLastModifiedDateTime(notesPostResponse.getModifiedDateTime());
        notesForUpdate.setNote(notesPostResponse.getComments());
        notesForUpdate.setNoteGuid(notesPostResponse.getGuid());

        logger.info("Сохраняем обновленную заметку с GUID={}", guid);
        patientNoteRepository.save(notesForUpdate);
    }
}
