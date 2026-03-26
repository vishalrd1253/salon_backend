package com.schedulink.salon.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalonProviderResponse {

    private Long id;
    private Long userId;
    private String experience;
    private String skills;
    private boolean approved;
}