package com.nt.repository;

import com.nt.model.Category;
import com.nt.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface IProductRepository extends JpaRepository<Product, Long> {


    List<Product> findByCategoryOrderByPriceAsc(Category category);

    Page<Product> findByProductNameLikeIgnoreCase(String keyword, PageRequest pageable);

    Optional<Product> findByProductNameIgnoreCase(String productName);

    Page<Product> findByCategory(Category category, PageRequest pageable);

    Page<Product> findByCategory_CategoryNameAndProductNameLikeIgnoreCase(String category, String s, PageRequest pageable);

    Page<Product> findByCategory_CategoryNameIgnoreCase(String category, PageRequest pageable);
}
