package com.schedulink.salon.repository;

import com.schedulink.salon.entity.SalonProvider;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalonProviderRepository extends JpaRepository<SalonProvider, Long> {
	List<SalonProvider> findByApprovedTrue();
	Page<SalonProvider> findByApprovedTrue(Pageable pageable);
	
	
}