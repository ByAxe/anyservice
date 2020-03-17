package com.anyservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class DetailedWrapper<DETAILED> {
    private DETAILED detailed;
    private UUID uuid;
}
