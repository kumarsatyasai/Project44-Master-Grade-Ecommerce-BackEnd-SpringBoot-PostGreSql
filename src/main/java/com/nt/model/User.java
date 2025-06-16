package com.nt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users",
        uniqueConstraints = {
        @UniqueConstraint(columnNames = "userName"),
        @UniqueConstraint(columnNames = "email")
        })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

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

    public User(String userName, String password, String email) {
        this.userName = userName;
        this.password = password;
        this.email = email;
    }

    /*
           How to Decide [user - Role] Uni - Directional.
           ===============
            In a unidirectional many-to-many relationship, the decision is straightforward:
            -------------------------------------------------------------------------------

            The owning side is the entity where you define the @ManyToMany relationship and the @JoinTable.
            The inverse side is the other entity, which does not have any annotations or awareness of the relationship.

            The choice of which entity should be the owning side depends on:
            ----------------------------------------------------------------
            Business Logic:
            ---------------
            Choose the entity that logically controls the relationship.
            For example, in a Student and Course relationship, you might make Student the owning side if the application primarily manages course enrollment from the student's perspective.
            Usage Patterns:
            ---------------
            If one entity is more frequently used to manage the relationship (e.g., adding/removing entries in the join table), make it the owning side.
            Simplicity:
            -----------
            Choose the side that aligns with the application's primary use case to keep the code intuitive.

            [User{Owning}- Role{Inverse}]
            ==============================
            Owning Side -- Use inside @ManyToMany() use cascade, fetch.
            Owning Side -- use @joinTable.
            Inverse Side -- No Awereness.
        */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<Role>();





    // One To many [Bi-Directional]         [User-Product]
    //================================
    // Always Owning Side Establish RelationShip & Inverse Side Establish mappedBy use cascade, fetch on both sides.
    // But Use cascade, fetch at Parent Side means Inverse Side it Is Mandatory.
    // But Not Manditory At Owning Side.
    // Many To One & One To Many [Bi-Directional] [Product(manySide) - User(oneSide)][Many(OwningSide)-One(InverseSide)]
    // Always Many[Owning] Side use  @ManyToOne inside it cascade, fetch use.
    // Always Many[Owning] Side Establish Relation Ship, use @joinColumn.
    // Always Many[Owning] Side inside @JoinColumn bellow joinColumn Property Primary Key[category-id].
    // Always One[inverse] Side inside @OneToMany use mappedBy, cascade, fetch.
    // Always One[Inverse] Side mappedBy use manySide relationship under property name bellow @JoinColumn, [private User {user}]
    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private Set<Product> products = new HashSet<>();


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
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Address> addresses = new ArrayList<>();

    @ToString.Exclude
    @OneToOne(mappedBy = "user", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    private Cart cart;

}
