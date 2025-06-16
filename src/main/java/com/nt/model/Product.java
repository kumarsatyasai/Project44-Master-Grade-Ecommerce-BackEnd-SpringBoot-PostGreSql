package com.nt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    @NotBlank(message = "Dear User Product Name Is Required.")
    @Size(min = 3, message = "Dear User Product Name Must Contains Atleast 3 Characters.")
    private String productName;
    private String image;
    @NotBlank(message = "Dear User Product Description Is Required.")
    @Size(min = 6, message = "Dear User Product Description Must Contains Atleast 6 Characters.")
    private String description;
    private Integer quantity;
    private double price;
    private double discount;
    private double specialPrice;

    // One To many [Bi-Directional]      [Product - Category]
    //================================
    // Always Owning Side Establish RelationShip & Inverse Side Establish mappedBy use cascade, fetch on both sides.
    // But Use cascade, fetch at Parent Side means Inverse Side it Is Mandatory.
    // But Not Manditory At Owning Side.
    // Many To One & One To Many [Bi-Directional] [Product(manySide) - Category(oneSide)][Many(OwningSide)-One(InverseSide)]
    // Always Many[Owning] Side use  @ManyToOne inside it cascade, fetch use.
    // Always Many[Owning] Side Establish Relation Ship, use @joinColumn.
    // Always Many[Owning] Side inside @JoinColumn bellow joinColumn Property Primary Key[category-id].
    // Always One[inverse] Side inside @OneToMany use mappedBy, cascade, fetch.
    // Always One[Inverse] Side mappedBy use manySide relationship under property name bellow @JoinColumn, [private Category {category}]
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;



    // One To many [Bi-Directional]        [Product - User]
    //=============================
    // Always Owning Side Establish RelationShip & Inverse Side Establish mappedBy use cascade, fetch on both sides.
    // But Use cascade, fetch at Parent Side means Inverse Side it Is Mandatory.
    // But Not Manditory At Owning Side.
    // Many To One & One To Many [Bi-Directional] [Product(manySide) - User(oneSide)][Many(OwningSide)-One(InverseSide)]
    // Always Many[Owning] Side use  @ManyToOne inside it cascade, fetch use.
    // Always Many[Owning] Side Establish Relation Ship, use @joinColumn.
    // Always Many[Owning] Side inside @JoinColumn bellow joinColumn Property Primary Key[category-id].
    // Always One[inverse] Side inside @OneToMany use mappedBy, cascade, fetch.
    // Always One[Inverse] Side mappedBy use manySide relationship under property name bellow @JoinColumn, [private User {user}]
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")// "user_id" have to use but understandable we changed to "seller_id".
    private User user;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

}
