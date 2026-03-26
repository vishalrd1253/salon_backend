package com.schedulink.salon.repository;

import com.schedulink.salon.entity.SalonServiceEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SalonServiceRepository extends JpaRepository<SalonServiceEntity, Long> {

	List<SalonServiceEntity> findByProviderId(Long providerId);
}