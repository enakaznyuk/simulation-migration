package com.example.migration.repository;

import com.example.migration.model.CompanyUser;
import org.springframework.data.repository.CrudRepository;

public interface CompanyUserRepository extends CrudRepository<CompanyUser, Long> {
}
