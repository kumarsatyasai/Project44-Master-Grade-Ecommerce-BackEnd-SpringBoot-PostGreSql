package com.nt.controller;

import com.nt.payload.CartDTO;
import com.nt.payload.CartItemDTO;
import com.nt.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private ICartService cartService;

    // Adding Products To cart with Respective To user.
    @PostMapping("/cart/create")
    public ResponseEntity<String> createOrUpdateCart(@RequestBody List<CartItemDTO> cartItems){

        String response = cartService.createOrUpdateCartWithItems(cartItems);

        return new ResponseEntity<>(response,  HttpStatus.CREATED);



    }

    // Adding Products To cart with Respective To user.
    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId,
                                                   @PathVariable Integer quantity){

        CartDTO cartDto = cartService.addProductToCart(productId, quantity);
        return new ResponseEntity<>(cartDto, org.springframework.http.HttpStatus.CREATED);

    }

    // Getting All Carts Related TO Entire E-commerce Project Irrespective Of users.
    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getAllCarts(){
        List<CartDTO> cartDTOS = cartService.getAllCarts();
        return new ResponseEntity<>(cartDTOS, HttpStatus.FOUND);
    }

    //Fetching Users Cart.

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getUserCart(){
        CartDTO cartDTO = cartService.getUsersCart();
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    @PutMapping("/carts/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProduct(@PathVariable Long productId, @PathVariable String operation){
        CartDTO cartDTO = cartService.updatedCartProduct(productId, operation.equalsIgnoreCase("delete")?-1:1);
       return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId, @PathVariable Long productId){
        String status = cartService.deleteProductFromCart(cartId, productId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }


}
