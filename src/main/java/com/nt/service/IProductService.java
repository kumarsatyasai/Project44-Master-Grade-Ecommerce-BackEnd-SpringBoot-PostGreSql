package com.nt.service;

import com.nt.payload.ProductDTO;
import com.nt.payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IProductService {

    public ProductDTO createProduct(Long categoryId, ProductDTO productDTO);

    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortDirection, String keyword, String category);

    public ProductResponse getAllProductsByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortDirection);

    public ProductResponse getAllProductsByProductName(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortDirection);

    public ProductDTO updateProduct(ProductDTO productDTO, Long productId);

    public ProductDTO deleteProduct(Long productId);

    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;
}
