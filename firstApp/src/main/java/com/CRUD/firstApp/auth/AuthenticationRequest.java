package com.CRUD.firstApp.auth;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;



@Builder

public record AuthenticationRequest(    @Email
                                        String email,

                                        @NotBlank
                                        String password) {

}
