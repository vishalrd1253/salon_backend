package com.schedulink.salon.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;

import com.schedulink.salon.dto.ProviderApplyRequest;
import com.schedulink.salon.dto.ServiceRequest;
import com.schedulink.salon.entity.Role;
import com.schedulink.salon.entity.SalonAppointment;
import com.schedulink.salon.entity.SalonProvider;
import com.schedulink.salon.entity.SalonServiceEntity;
import com.schedulink.salon.entity.Status;

public interface SalonService {

    SalonAppointment book( Long customerId,
            Long providerId,
            Long serviceId,
            LocalDateTime startTime );
    
    SalonProvider apply(ProviderApplyRequest request);

	SalonProvider approveProvider(Long id);
	
	SalonServiceEntity createService(ServiceRequest request);
	
	List<SalonProvider> getApprovedProviders();
	List<SalonServiceEntity> getServicesByProvider(Long providerId);
	
	SalonAppointment updateStatus(Long appointmentId, Status status, Long userId, Role role);
	
	SalonAppointment cancelAppointment(Long appointmentId, Long userId, Role role);

	Page<SalonProvider> getProviders(int page, int size);

	List<SalonAppointment> getMyBookings(Long userId, Role role);

	List<SalonProvider> getAllProviders();

	SalonAppointment reschedule(Long appointmentId, Long userId, LocalDateTime newTime);
} 