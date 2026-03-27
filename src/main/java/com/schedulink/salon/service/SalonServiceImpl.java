package com.schedulink.salon.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.schedulink.salon.dto.ProviderApplyRequest;
import com.schedulink.salon.dto.ServiceRequest;
import com.schedulink.salon.entity.Role;
import com.schedulink.salon.entity.SalonAppointment;
import com.schedulink.salon.entity.SalonProvider;
import com.schedulink.salon.entity.SalonServiceEntity;
import com.schedulink.salon.entity.Status;
import com.schedulink.salon.repository.SalonAppointmentRepository;
import com.schedulink.salon.repository.SalonProviderRepository;
import com.schedulink.salon.repository.SalonServiceRepository;

import jakarta.transaction.Transactional;

@Service
public class SalonServiceImpl implements SalonService {

    @Autowired
    private SalonAppointmentRepository appointmentRepo;

    @Autowired
    private SalonServiceRepository serviceRepo;

    @Autowired
    private SalonProviderRepository providerRepo;

    @Transactional
    @Override
    public SalonAppointment book(Long customerId, Long providerId, Long serviceId, LocalDateTime startTime) {

        // 1️⃣ Check provider exists
        SalonProvider provider = providerRepo.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        // 2️⃣ Check provider approved
        if (!provider.isApproved()) {
            throw new RuntimeException("Provider not approved");
        }

        // 3️⃣ Get service
        SalonServiceEntity service = serviceRepo.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        // 4️⃣ Validate service belongs to provider
        if (!service.getProviderId().equals(providerId)) {
            throw new RuntimeException("Service does not belong to provider");
        }

        // 5️⃣ Calculate end time
        LocalDateTime endTime = startTime.plusMinutes(service.getDuration());

        // 6️⃣ Check conflict
        List<SalonAppointment> conflicts =
                appointmentRepo.findConflictingAppointments(providerId, startTime, endTime);

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Slot already booked!");
        }

        // 7️⃣ Create appointment
        SalonAppointment appointment = SalonAppointment.builder()
                .customerId(customerId)
                .providerId(providerId)
                .serviceId(serviceId)
                .startTime(startTime)
                .endTime(endTime)
                .status(Status.PENDING)
                .build();

        return appointmentRepo.save(appointment);
    }

    @Override
    public SalonProvider apply(ProviderApplyRequest request) {

        SalonProvider provider = SalonProvider.builder()
                .userId(request.getUserId())
                .experience(request.getExperience())
                .skills(request.getSkills())
                .approved(false) // default
                .build();

        return providerRepo.save(provider);
    }
    
    public SalonProvider approveProvider(Long providerId) {

        SalonProvider provider = providerRepo.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        provider.setApproved(true);

        return providerRepo.save(provider);
    }
    
    @Override
    public SalonServiceEntity createService(ServiceRequest request) {

        // 1️⃣ Check provider exists
        SalonProvider provider = providerRepo.findById(request.getProviderId())
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        // 2️⃣ Check provider approved
        if (!provider.isApproved()) {
            throw new RuntimeException("Provider not approved");
        }

        // 3️⃣ Create service
        SalonServiceEntity service = SalonServiceEntity.builder()
                .providerId(request.getProviderId())
                .name(request.getName())
                .duration(request.getDuration())
                .price(request.getPrice())
                .description(request.getDescription())
                .build();

        return serviceRepo.save(service);
    }

    @Override
    public List<SalonProvider> getApprovedProviders() {
        return providerRepo.findByApprovedTrue();
    }

    @Override
    public List<SalonServiceEntity> getServicesByProvider(Long providerId) {

        // optional validation
        providerRepo.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        return serviceRepo.findByProviderId(providerId);
    }
    

    


    @Override
    public SalonAppointment cancelAppointment(Long appointmentId, Long userId, Role role) {

        SalonAppointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // 🔒 Ownership check
        if (!appointment.getCustomerId().equals(userId) && role != Role.ADMIN) {
            throw new RuntimeException("Not allowed to cancel this appointment");
        }

        if (appointment.getStatus() == Status.CANCELLED) {
            throw new RuntimeException("Already cancelled");
        }

        if (appointment.getStatus() == Status.COMPLETED) {
            throw new RuntimeException("Cannot cancel completed appointment");
        }

        appointment.setStatus(Status.CANCELLED);

        return appointmentRepo.save(appointment);
    }

    @Override
    public SalonAppointment updateStatus(Long appointmentId, Status status, Long userId, Role role) {

        SalonAppointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // 🔒 Only provider or admin
        if (role != Role.PROVIDER && role != Role.ADMIN) {
            throw new RuntimeException("Not allowed to update status");
        }

        Status current = appointment.getStatus();

        if (current == Status.CANCELLED || current == Status.COMPLETED) {
            throw new RuntimeException("Cannot update this appointment");
        }

        appointment.setStatus(status);

        return appointmentRepo.save(appointment);
    }

	@Override
	public Page<SalonProvider> getProviders(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
 
	    return providerRepo.findByApprovedTrue(pageable);
	}

	
	@Override
	public List<SalonAppointment> getMyBookings(Long userId, Role role) {

	    if (role == Role.USER) {
	        return appointmentRepo.findByCustomerId(userId);
	    }

	    if (role == Role.PROVIDER) {
	        return appointmentRepo.findByProviderId(userId);
	    }

	    if (role == Role.ADMIN) {
	        return appointmentRepo.findAll();
	    }

	    throw new RuntimeException("Invalid role");
	}

	@Override
	public List<SalonProvider> getAllProviders() {
		return providerRepo.findAll();
	}

	@Override
	public SalonAppointment reschedule(Long appointmentId, Long userId, LocalDateTime newTime) {

	    SalonAppointment old = appointmentRepo.findById(appointmentId)
	            .orElseThrow(() -> new RuntimeException("Appointment not found"));

	    // ✅ Only owner can reschedule
	    if (!old.getCustomerId().equals(userId)) {
	        throw new RuntimeException("Unauthorized");
	    }

	    // ❌ Cannot reschedule completed/cancelled
	    if (old.getStatus() != Status.PENDING && old.getStatus() != Status.CONFIRMED) {
	        throw new RuntimeException("Cannot reschedule this appointment");
	    }

	    // 🔁 Cancel old
	    old.setStatus(Status.CANCELLED);
	    appointmentRepo.save(old);

	    // 📦 Get service for duration
	    SalonServiceEntity service = serviceRepo.findById(old.getServiceId())
	            .orElseThrow(() -> new RuntimeException("Service not found"));

	    LocalDateTime endTime = newTime.plusMinutes(service.getDuration());

	    // ⚠️ Conflict check
	    boolean conflict = appointmentRepo.existsConflict(
	            old.getProviderId(), newTime, endTime
	    );

	    if (conflict) {
	        throw new RuntimeException("New slot not available");
	    }

	    // 🆕 Create new booking
	    SalonAppointment newBooking = SalonAppointment.builder()
	            .customerId(old.getCustomerId())
	            .providerId(old.getProviderId())
	            .serviceId(old.getServiceId())
	            .startTime(newTime)
	            .endTime(endTime)
	            .status(Status.PENDING)
	            .build();

	    return appointmentRepo.save(newBooking);
	}
    
    
    
    
}