package net.greeta.order.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserRequest {

    @Schema(example = "testUser")
    @NotBlank
    private String username;

    @Schema(example = "Test User")
    @NotBlank
    private String name;

    @Schema(example = "testuser@exammple.com")
    @NotBlank
    private String email;
}
