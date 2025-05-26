package com.CRUD.firstApp.admin;

import com.CRUD.firstApp.auth.Role;

public record AdminRequest(
         String email ,
         String password,
         Role role
) {
}
