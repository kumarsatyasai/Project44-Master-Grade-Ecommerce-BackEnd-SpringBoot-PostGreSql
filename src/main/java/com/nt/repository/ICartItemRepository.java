package com.nt.repository;

import com.nt.model.CartItem;
import com.nt.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ICartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByProduct(Product product);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.product.productId = :productId")
    void deleteByProductId(@Param("productId") Long productId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.cartId = ?1")
    void deleteAllByCartId(Long cartId);
}
