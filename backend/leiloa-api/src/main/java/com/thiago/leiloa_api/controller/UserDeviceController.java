package com.thiago.leiloa_api.controller;

import java.util.UUID;
import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.thiago.leiloa_api.dto.device.UserDeviceAdminDetailDTO;
import com.thiago.leiloa_api.dto.device.UserDeviceAdminListDTO;
import com.thiago.leiloa_api.domain.user.UserDevice;
import com.thiago.leiloa_api.service.UserDeviceService;
import lombok.RequiredArgsConstructor;

@Tag(name = "Admin - User Devices", description = "Endpoints para gerenciamento de dispositivos dos usuários (Admin)")
@RestController
@RequestMapping("/admin/devices")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class UserDeviceController {

    private final UserDeviceService userDeviceService;

    @GetMapping("/all")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Listar todos os dispositivos", description = "Retorna uma página de dispositivos registrados por todos os usuários")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Página de dispositivos retornada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - apenas administradores podem acessar este endpoint")
    })
    public ResponseEntity<Page<UserDeviceAdminListDTO>> listAll(Pageable pageable) {

        Page<UserDevice> page = userDeviceService.findAll(pageable);

        Page<UserDeviceAdminListDTO> dtoPage = page.map(device ->
            new UserDeviceAdminListDTO(
                device.getId(),
                device.getUser().getId(),
                device.getUser().getEmail(),
                device.getDeviceType(),
                device.getDeviceToken(),
                device.getUserAgent(),
                device.getCreatedAt(),
                device.getLastUsedAt()
            )
        );

        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/user/{userId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Listar dispositivos por usuário", description = "Retorna uma lista de dispositivos registrados por um usuário específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de dispositivos retornada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - apenas administradores podem acessar este endpoint"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<List<UserDeviceAdminListDTO>> listByUser(@PathVariable UUID userId) {

        List<UserDeviceAdminListDTO> list =
                userDeviceService.findByUserId(userId).stream()
                        .map(device -> new UserDeviceAdminListDTO(
                                device.getId(),
                                device.getUser().getId(),
                                device.getUser().getEmail(),
                                device.getDeviceType(),
                                device.getDeviceToken(),
                                device.getUserAgent(),
                                device.getCreatedAt(),
                                device.getLastUsedAt()
                        )).toList();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{deviceId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Detalhes do dispositivo", description = "Retorna os detalhes de um dispositivo específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Detalhes do dispositivo retornados com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - apenas administradores podem acessar este endpoint"),
        @ApiResponse(responseCode = "404", description = "Dispositivo não encontrado")
    })
    public ResponseEntity<UserDeviceAdminDetailDTO> findById(@PathVariable UUID deviceId) {

        UserDevice device = userDeviceService.findById(deviceId);

        UserDeviceAdminDetailDTO dto = new UserDeviceAdminDetailDTO(
                device.getId(),
                device.getUser().getId(),
                device.getUser().getEmail(),
                device.getDeviceType(),
                device.getDeviceToken(),
                device.getUserAgent(),
                device.getCreatedAt(),
                device.getLastUsedAt()
        );

        return ResponseEntity.ok(dto);
    }
}
