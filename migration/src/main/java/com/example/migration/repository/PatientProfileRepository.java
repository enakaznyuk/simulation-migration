package com.example.migration.repository;

import com.example.migration.model.PatientProfile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PatientProfileRepository extends CrudRepository<PatientProfile, Long> {

    @Query("SELECT p FROM PatientProfile p " +
            "JOIN PatientNote n ON n.patient = p " +
            "JOIN n.lastModifiedByUser u " +
            "WHERE p.statusId IN (200, 210, 230)")
    List<PatientProfile> findProfilesWithSpecificStatuses();
}
