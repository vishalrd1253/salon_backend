package com.schedulink.salon.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.schedulink.salon.dto.ApiResponse;
import com.schedulink.salon.dto.BookingRequest;
import com.schedulink.salon.dto.ProviderApplyRequest;
import com.schedulink.salon.dto.SalonAppointmentResponse;
import com.schedulink.salon.dto.SalonProviderResponse;
import com.schedulink.salon.dto.SalonServiceResponse;
import com.schedulink.salon.dto.ServiceRequest;
import com.schedulink.salon.entity.Role;
import com.schedulink.salon.entity.SalonAppointment;
import com.schedulink.salon.entity.SalonProvider;
import com.schedulink.salon.entity.SalonServiceEntity;
import com.schedulink.salon.entity.Status;
import com.schedulink.salon.mapper.SalonMapper;
import com.schedulink.salon.service.SalonService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/salon")
public class SalonController {

    @Autowired
    private SalonService service;

//    @Parameter(hidden = true)
//    HttpServletRequest request
    
    // ✅ BOOK
    @PostMapping("/book")
    public ResponseEntity<ApiResponse<SalonAppointmentResponse>> book(@RequestBody BookingRequest request) {

        SalonAppointment appointment = service.book(
                request.getCustomerId(),
                request.getProviderId(),
                request.getServiceId(),
                request.getStartTime()
        );

        return ResponseEntity.ok(
                ApiResponse.<SalonAppointmentResponse>builder()
                        .success(true)
                        .message("Appointment booked successfully")
                        .data(SalonMapper.toResponse(appointment))
                        .build()
        );
    }

    // ✅ APPLY PROVIDER
    @PostMapping("/provider/apply")
    public ApiResponse<SalonProviderResponse> apply(@RequestBody ProviderApplyRequest request) {

        SalonProvider provider = service.apply(request);

        return ApiResponse.<SalonProviderResponse>builder()
                .success(true)
                .message("Application submitted")
                .data(SalonMapper.toProviderResponse(provider))
                .build();
    }

    // ✅ APPROVE PROVIDER
    @PutMapping("/provider/{id}/approve")
    public ApiResponse<SalonProviderResponse> approve(@PathVariable Long id) {

        SalonProvider provider = service.approveProvider(id);

        return ApiResponse.<SalonProviderResponse>builder()
                .success(true)
                .message("Provider approved")
                .data(SalonMapper.toProviderResponse(provider))
                .build();
    }

    // ✅ CREATE SERVICE
    @PostMapping("/service")
    public ApiResponse<SalonServiceResponse> createService(@RequestBody ServiceRequest request) {

        SalonServiceEntity serviceEntity = service.createService(request);

        return ApiResponse.<SalonServiceResponse>builder()
                .success(true)
                .message("Service created successfully")
                .data(SalonMapper.toServiceResponse(serviceEntity))
                .build();
    }

    // ✅ GET PROVIDERS
    @GetMapping("/providers")
    public ApiResponse<Page<SalonProviderResponse>> getProviders(
            @RequestParam int page,
            @RequestParam int size) {

        Page<SalonProvider> providers = service.getProviders(page, size);

        Page<SalonProviderResponse> response =
                providers.map(SalonMapper::toProviderResponse);

        return ApiResponse.<Page<SalonProviderResponse>>builder()
                .success(true)
                .message("Providers fetched")
                .data(response)
                .build();
    }

    // ✅ GET SERVICES
    @GetMapping("/services/{providerId}")
    public ApiResponse<List<SalonServiceResponse>> getServices(@PathVariable Long providerId) {

        List<SalonServiceResponse> services = service.getServicesByProvider(providerId)
                .stream()
                .map(SalonMapper::toServiceResponse)
                .collect(Collectors.toList());

        return ApiResponse.<List<SalonServiceResponse>>builder()
                .success(true)
                .message("Services fetched")
                .data(services)
                .build();
    }

    // ✅ CANCEL (JWT BASED)
    @PutMapping("/appointment/{id}/cancel")
    public ApiResponse<SalonAppointmentResponse> cancel(
            @PathVariable Long id,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        Role role = Role.valueOf((String) request.getAttribute("role"));

        SalonAppointment appointment = service.cancelAppointment(id, userId, role);

        return ApiResponse.<SalonAppointmentResponse>builder()
                .success(true)
                .message("Appointment cancelled")
                .data(SalonMapper.toResponse(appointment))
                .build();
    }

    // ✅ UPDATE STATUS (JWT BASED)
    @PutMapping("/appointment/{id}/status")
    public ApiResponse<SalonAppointmentResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam Status status,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        Role role = Role.valueOf((String) request.getAttribute("role"));

        SalonAppointment appointment = service.updateStatus(id, status, userId, role);

        return ApiResponse.<SalonAppointmentResponse>builder()
                .success(true)
                .message("Status updated")
                .data(SalonMapper.toResponse(appointment))
                .build();
    }
}