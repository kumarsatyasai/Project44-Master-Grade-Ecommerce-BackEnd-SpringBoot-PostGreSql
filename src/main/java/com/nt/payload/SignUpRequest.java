package com.nt.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

    @NotBlank(message = "Dear User UserName Is Required.")
    @Size(max = 20, message = "Dear User UserName Must Contains Less Than 20 Characters.")
    private String userName;

    @NotBlank(message = "Dear User Password Is Required.")
    @Size(max = 120, message = "Dear User Password Must Contains Less Than 120 Characters.")
    private String password;

    @NotBlank(message = "Dear User Email Is Required.")
    @Size(max = 50, message = "Dear User Email Must Contains Less Than 50 Characters.")
    @Email(message = "Dear User Email Must Be Valid Email.")
    private  String email;


    private Set<String> roles;

}
