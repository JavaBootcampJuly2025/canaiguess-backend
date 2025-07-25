package com.canaiguess.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequestDTO {
    private String currentPassword;
    private String newPassword;
    private String email; // Optional: if you allow email updates
}
