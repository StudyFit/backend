package com.farmers.studyfit.domain.connection.repository;

import com.farmers.studyfit.domain.connection.entity.Connection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {
}
