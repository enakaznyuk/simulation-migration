package com.example.migration.service;

import com.example.migration.dto.PatientProfileDto;
import com.example.migration.model.PatientProfile;
import com.example.migration.repository.PatientProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class PatientProfileService {

    @Autowired
    private PatientProfileRepository patientProfileRepository;

    private final ModelMapper mapper;

    private static final Logger logger = LoggerFactory.getLogger(PatientProfileService.class);

    public void createPatientProfile(PatientProfileDto patientProfileDto) {
        logger.info("Создание нового профиля пациента: {}", patientProfileDto);
        PatientProfile patientProfile = mapper.map(patientProfileDto, PatientProfile.class);
        logger.debug("Профиль после маппинга: {}", patientProfile);
        patientProfileRepository.save(patientProfile);
        logger.info("Профиль пациента успешно сохранен с ID={}", patientProfile.getId());
    }

    public List<PatientProfileDto> getAllPatientProfiles() {
        logger.info("Получение всех профилей пациентов");
        List<PatientProfile> patientProfiles = StreamSupport.stream(patientProfileRepository.findAll().spliterator(), false).toList();
        logger.info("Найдено {} профилей", patientProfiles.size());
        return patientProfiles.stream()
                .map(patientProfile -> {
                    logger.debug("Обработка профиля с ID={}", patientProfile.getId());
                    return mapper.map(patientProfile, PatientProfileDto.class);
                })
                .toList();
    }

    public void deletePatientProfile(Long id) {
        logger.info("Удаление профиля пациента с ID={}", id);
        try {
            patientProfileRepository.deleteById(id);
            logger.info("Профиль с ID={} успешно удален", id);
        } catch (Exception e) {
            logger.error("Ошибка при удалении профиля с ID={}", id, e);
            throw e;
        }
    }

    public PatientProfileDto findById(Long id){
        logger.info("Поиск профиля пациента по ID={}", id);
        PatientProfile patientProfile = patientProfileRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Профиль с ID={} не найден", id);
                    return new EntityNotFoundException("User not found");
                });
        logger.info("Профиль найден: {}", patientProfile);
        return mapper.map(patientProfile, PatientProfileDto.class);
    }

    public List<PatientProfileDto> findAllActivePatients() {
        logger.info("Получение всех активных пациентов");
        List<PatientProfile> patientProfiles = patientProfileRepository.findProfilesWithSpecificStatuses().stream().toList();
        logger.info("Найдено {} активных пациентов", patientProfiles.size());
        return patientProfiles.stream()
                .map(patientProfile -> {
                    logger.debug("Обработка активного пациента с ID={}", patientProfile.getId());
                    return mapper.map(patientProfile, PatientProfileDto.class);
                })
                .toList();
    }
}
