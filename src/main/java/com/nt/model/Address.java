package com.nt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotNull
    @Size(min = 5, message = "Dear User Street Name Must Contains Atleast 5 Characters.")
    private String street;

    @NotNull
    @Size(min = 5, message = "Dear User building Name Must Contains Atleast 5 Characters.")
    private String buildingName;

    @NotBlank(message = "Dear User City Name Is Required.")
    @Size(min = 4, message = "Dear User City Name Must Contains Atleast 4 Characters.")
    private String city;

    @NotBlank(message = "Dear User State Name Is Required.")
    @Size(min = 2, message = "Dear User State Name Must Contains Atleast 2 Characters.")
    private String state;

    @NotBlank(message = "Dear User Country Name Is Required.")
    @Size(min = 2, message = "Dear User Country Name Must Contains Atleast 2 Characters.")
    private String country;

    @NotBlank(message = "Dear User Zip Code Is Required.")
    @Size(min = 6, message = "Dear User Zip Code Must Contains Atleast 6 Characters.")
    private String zipCode;

    // Many To Many{Bi-Directional} ==> [User{Owning-Side} - Address{Inverse-Side}]
    /*How to Decide
    ================
    There is no strict rule dictating which entity should be the owning side, but you can decide based on:
    ------------------------------------------------------------------------------------------------------
    Business Logic:
    ----------------
    Choose the side that makes more sense to control the relationship based on the domain model.
    For example, in a Student and Course relationship, you might decide that Student owns the relationship if students enroll in courses.
    Usage Patterns:
    ---------------
    If one side is more frequently used to manage the relationship (e.g., adding/removing entries in the join table), make that the owning side.
    Ex: User is More Frequently used for adding, removing entries in join table so user is Owing Side In this Context.
    Convention:
    -----------
    If there’s no clear preference, you can arbitrarily choose one side, but be consistent across your application.*/

    // Owning Side : Relation Ship Use @JoinTable, {cascade, Fetch at @MantToMany annotation}
    // Inverse Side : use mappedBy and cascade and fetch at @ManyToMany.

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
