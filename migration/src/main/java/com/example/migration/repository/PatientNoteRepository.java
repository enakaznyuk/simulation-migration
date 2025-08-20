package com.example.migration.repository;

import com.example.migration.model.PatientNote;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PatientNoteRepository extends CrudRepository<PatientNote, Long> {

    @Query(value = "SELECT pn.last_modified_date_time AS lastModifiedDateTime, " +
            "pn.last_modified_by_user_id AS lastModifiedByUserId, " +
            "pn.note_guid AS noteGuid," +
            "pn.patient_id AS patientId, " +
            "pp.old_client_guid AS oldClientGuid, " +
            "pp.status_id AS statusId " +
            "FROM patient_note pn " +
            "RIGHT JOIN patient_profile pp ON pn.patient_id = pp.id " +
            "WHERE pp.status_id IN (200, 210, 230)", nativeQuery = true)
    List<Object[]> findNotesWithPatientInfo();

    void deleteByNoteGuid(String noteGuid);

    Optional<PatientNote> findByNoteGuid(String noteGuid);
}
