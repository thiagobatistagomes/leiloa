package com.thiago.leiloa_api.dto.device;

public record UserDeviceCreateDTO(
    String deviceType,   // "desktop" | "mobile" | "tablet"
    String userAgent,
    String deviceToken     
) {}
