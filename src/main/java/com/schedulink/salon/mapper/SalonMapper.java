package com.schedulink.salon.mapper;

import com.schedulink.salon.dto.*;
import com.schedulink.salon.entity.*;

public class SalonMapper {

    public static SalonAppointmentResponse toResponse(SalonAppointment entity) {
        return SalonAppointmentResponse.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .providerId(entity.getProviderId())
                .serviceId(entity.getServiceId())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .status(entity.getStatus())
                .build();
    }

    public static SalonProviderResponse toProviderResponse(SalonProvider entity) {
        return SalonProviderResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .experience(entity.getExperience())
                .skills(entity.getSkills())
                .approved(entity.isApproved())
                .build();
    }

    public static SalonServiceResponse toServiceResponse(SalonServiceEntity entity) {
        return SalonServiceResponse.builder()
                .id(entity.getId())
                .providerId(entity.getProviderId())
                .name(entity.getName())
                .duration(entity.getDuration())
                .price(entity.getPrice())
                .description(entity.getDescription())
                .build();
    }
}