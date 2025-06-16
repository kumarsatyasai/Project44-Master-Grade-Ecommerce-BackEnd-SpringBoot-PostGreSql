package com.nt.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;


    @NotBlank(message = "Dear User Category Name Is Required.")
    @Size(min = 5, message = "Dear User Category Name Must Contains Atleast 5 Characters.")
    private String categoryName;

    // One To many [Bi-Directional]            [Category - Product]
    //=============================
    // Always Owning Side Establish RelationShip & Inverse Side Establish mappedBy use cascade, fetch on both sides.
    // But Use cascade, fetch at Parent Side means Inverse Side it Is Mandatory.
    // But Not Manditory At Owning Side.
    // If we use Cascade, fetch At Owning Side it is undiserable means No Menaing.
    // Many To One & One To Many [Bi-Directional] [Product(manySide) - Category(oneSide)][Many(OwningSide)-One(InverseSide)]
    // Always Many[Owning] Side use  @ManyToOne inside it cascade, fetch use.
    // Always Many[Owning] Side Establish Relation Ship, use @joinColumn.
    // Always Many[Owning] Side inside @JoinColumn bellow joinColumn Property Primary Key[category-id].
    // Always One[inverse] Side inside @OneToMany use mappedBy, cascade, fetch.
    // Always One[Inverse] Side mappedBy use manySide relationship under property name bellow @JoinColumn, [private Category {category}]
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products;





    /*

    How to Decide
In a unidirectional one-to-many relationship, the decision is clear:

The owning side is always the entity with the @OneToMany annotation (the "one" side).
The inverse side is the entity on the "many" side, which has no mapping or reference to the relationship.
The choice of which entity is the "one" side depends on:
Business Logic: Choose the entity that logically owns or controls the relationship. For example, in a Customer and Order relationship, Customer is typically the "one" side because a customer can have multiple orders.
Usage Patterns: Make the entity that is more frequently used to manage the relationship (e.g., adding/removing orders) the owning side.
Database Design: The "many" side typically has a foreign key column referencing the "one" side, which is managed by the owning side.
     */
}
