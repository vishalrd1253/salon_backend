package com.schedulink.salon.dto;

import lombok.*;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequest {

    @NotNull
    private Long customerId; 

    @NotNull
    private Long providerId;

    @NotNull
    private Long serviceId;

    @NotNull
    private LocalDateTime startTime;
}