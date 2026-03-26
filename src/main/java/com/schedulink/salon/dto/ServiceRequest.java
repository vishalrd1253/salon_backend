package com.schedulink.salon.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRequest {

    private Long providerId;
    private String name;
    private int duration;
    private double price;
    private String description;
}