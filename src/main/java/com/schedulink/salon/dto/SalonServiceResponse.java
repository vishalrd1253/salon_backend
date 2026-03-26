package com.schedulink.salon.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalonServiceResponse {

    private Long id;
    private Long providerId;
    private String name;
    private int duration;
    private double price;
    private String description;
}