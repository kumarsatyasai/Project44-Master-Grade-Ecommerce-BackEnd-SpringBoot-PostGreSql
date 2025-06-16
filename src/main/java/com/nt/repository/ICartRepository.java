package com.nt.repository;

import com.nt.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ICartRepository extends JpaRepository<Cart, Long> {

    /**
     * Find all carts containing a specific product
     * @param productId the ID of the product to search for
     * @return list of carts containing the specified product
     */
    @Query("SELECT DISTINCT c FROM Cart c JOIN c.cartItems ci WHERE ci.product.productId = :productId")
    List<Cart> findCartsByProductId(@Param("productId") Long productId);

    @Query("SELECT c FROM Cart c JOIN c.user u WHERE u.email = :email")
    Cart findByUserEmail(@Param("email") String email);
}
