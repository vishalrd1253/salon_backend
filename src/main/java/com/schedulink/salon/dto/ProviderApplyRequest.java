package com.schedulink.salon.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderApplyRequest {

    private Long userId;
    private String experience;
    private String skills;
}