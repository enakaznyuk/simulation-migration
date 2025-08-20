package com.example.migration.service;

import com.example.migration.dto.CompanyUserDto;
import com.example.migration.model.CompanyUser;
import com.example.migration.repository.CompanyUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class CompanyUserService {

    @Autowired
    private CompanyUserRepository companyUserRepository;

    private static final Logger logger = LoggerFactory.getLogger(CompanyUserService.class);

    private final ModelMapper mapper;

    public void createCompanyUser(CompanyUserDto companyUserDto) {
        CompanyUser companyUser = mapper.map(companyUserDto, CompanyUser.class);
        companyUserRepository.save(companyUser);
    }

    public List<CompanyUserDto> getAllCompanyUsers() {
        logger.info("Получение всех пользователей компании");
        List<CompanyUser> companyUsers = StreamSupport.stream(companyUserRepository.findAll().spliterator(), false).toList();
        logger.info("Найдено {} пользователей", companyUsers.size());
        return companyUsers.stream()
                .peek(user -> logger.debug("Обработка пользователя с ID={}", user.getId()))
                .map(companyUser -> mapper.map(companyUser, CompanyUserDto.class))
                .collect(Collectors.toList());
    }

    public void deleteCompanyUser(Long id) {
        companyUserRepository.deleteById(id);
    }

    public CompanyUserDto findById(Long id){
        CompanyUser companyUser = companyUserRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return mapper.map(companyUser, CompanyUserDto.class);
    }
}
