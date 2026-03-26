package com.schedulink.salon.dto;

import com.schedulink.salon.entity.Status;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalonAppointmentResponse {

    private Long id;
    private Long customerId;
    private Long providerId;
    private Long serviceId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Status status;
}