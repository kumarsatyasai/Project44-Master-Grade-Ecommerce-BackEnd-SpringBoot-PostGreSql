package com.nt.service;

import com.nt.payload.CartDTO;

import java.util.List;

public interface ICartService {
    CartDTO addProductToCart(Long productId, Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getUsersCart();

    CartDTO updatedCartProduct(Long productId, Integer delete);

    String deleteProductFromCart(Long cartId, Long productId);
}
