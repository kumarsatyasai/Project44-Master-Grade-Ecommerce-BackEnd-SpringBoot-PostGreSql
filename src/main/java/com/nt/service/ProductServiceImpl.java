package com.nt.service;

import com.nt.exceptions.APIException;
import com.nt.exceptions.ResourceNotFoundException;
import com.nt.model.Cart;
import com.nt.model.CartItem;
import com.nt.model.Category;
import com.nt.model.Product;
import com.nt.payload.ProductDTO;
import com.nt.payload.ProductResponse;
import com.nt.repository.ICartItemRepository;
import com.nt.repository.ICartRepository;
import com.nt.repository.ICategoryRepository;
import com.nt.repository.IProductRepository;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private ICategoryRepository categoryRepository;

    @Value("${check.duplicate.product:true}")
    private boolean checkDuplicateProduct;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IFileService fileService;

    @Autowired
    private ICartItemRepository cartItemRepository;

    @Autowired
    private ICartRepository cartRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ICartService cartService;



    @Value("${image.base.url}")
    private String imageBaseUrl;

    @Value("${project.image}")
    private String pathName;
    @Autowired
    private CartServiceImpl cartServiceImpl;

    @Override
    public ProductDTO createProduct(Long categoryId, ProductDTO productDTO) {


        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            throw new ResourceNotFoundException("Category", "categoryId", categoryId);
        }

        boolean productExists = category.get().getProducts().stream()
                .anyMatch(p -> p.getProductName().equalsIgnoreCase(productDTO.getProductName()));
        if (productExists) {
            throw new APIException("Product with name '" + productDTO.getProductName() + "' already exists in this category");
        }

        Product product = modelMapper.map(productDTO, Product.class);
        product.setCategory(category.get());
        product.setImage("default.png");
        Double specialPrice = product.getPrice() - (product.getDiscount() * 0.01 * product.getPrice());
        product.setSpecialPrice(specialPrice);
        Product savedProduct = productRepository.save(product);


        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortDirection, String keyword, String category) {

        Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();


        PageRequest pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> productPage;

        if (category != null && !category.isEmpty() && keyword != null && !keyword.isEmpty()) {
            productPage = productRepository.findByCategory_CategoryNameAndProductNameLikeIgnoreCase(category, "%" + keyword + "%", pageable);
        } else if (category != null && !category.isEmpty()) {
            productPage = productRepository.findByCategory_CategoryNameIgnoreCase(category, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            productPage = productRepository.findByProductNameLikeIgnoreCase("%" + keyword + "%", pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        if (productPage.isEmpty()) {
            throw new APIException("No Products Found");
        }

        List<ProductDTO> productsDTOS = productPage.getContent().stream()
                .map(p ->{
                    ProductDTO productDTO = modelMapper.map(p, ProductDTO.class);
                    productDTO.setImage( constructImageUrl( p.getImage() ) );
                    return productDTO;


                }).toList();


        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productsDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages((long) productPage.getTotalPages());
        productResponse.setLast(productPage.isLast());

        return productResponse;
    }

    private String constructImageUrl(String imageName){
        return imageBaseUrl.endsWith("/") ? imageBaseUrl + imageName : imageBaseUrl + "/" + imageName;
    }

    @Override
    public ProductResponse getAllProductsByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortDirection) {


        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            throw new ResourceNotFoundException("Category", "categoryId", categoryId);
        }

        Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        PageRequest pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> productPage = productRepository.findByCategory(category.get(), pageable);

        if (productPage.isEmpty()) {
            throw new APIException("No Products Found for category with id: " + categoryId);
        }

        List<ProductDTO> productsDTOS = productPage.getContent().stream()
                .map(p -> modelMapper.map(p, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productsDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages((long) productPage.getTotalPages());
        productResponse.setLast(productPage.isLast());

        return productResponse;
    }

    @Override
    public ProductResponse getAllProductsByProductName(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        PageRequest pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> productPage =  productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%', pageable);

        if (productPage.isEmpty()) {
            throw new APIException("No Products Found with keyword: " + keyword);
        }

        List<ProductDTO> productsDTOS = productPage.getContent().stream()
                .map(p ->{
                    ProductDTO productDTO = modelMapper.map(p, ProductDTO.class);
                    productDTO.setImage( constructImageUrl( p.getImage() ) );
                    return productDTO;


                })
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productsDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages((long) productPage.getTotalPages());
        productResponse.setLast(productPage.isLast());

        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            throw new ResourceNotFoundException("Product", "productId", productId);
        }

        Product product = modelMapper.map(productDTO, Product.class);
        Product productDB = productOptional.get();
        productDB.setPrice(product.getPrice());
        productDB.setDiscount(product.getDiscount());
        productDB.setQuantity(product.getQuantity());
        productDB.setProductName(product.getProductName());
        productDB.setDescription(product.getDescription());
        Double specialPrice = product.getPrice() - (product.getDiscount() * 0.01 * product.getPrice());
        productDB.setSpecialPrice(specialPrice);
        Product savedProduct = productRepository.save(productDB);

        // Update all cart items containing this product
        List<CartItem> cartItems = cartItemRepository.findAll();
        cartItems.stream()
                .filter(item -> item.getProduct().getProductId().equals(productId))
                .forEach(item -> {
                    item.setProductPrice(savedProduct.getSpecialPrice() * item.getQuantity());
                    CartItem savedCartItem = cartItemRepository.save(item);

                    Double cartTotal = savedCartItem.getCart().getCartItems().stream()
                            .mapToDouble(cartItem -> cartItem.getProductPrice())
                            .sum();
                    savedCartItem.getCart().setTotalPrice(cartTotal);
                    cartRepository.save(savedCartItem.getCart());
                });




        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
          List<Cart> carts = cartRepository.findCartsByProductId(productId);
          carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), productId));
          //productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        //Get The Product From DB.
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            throw new ResourceNotFoundException("Product", "productId", productId);
        }
        Product productFromDB = productOptional.get();
        //Upload Image To Server.
        //Get The File Name Of Uploded Image.
        String path = pathName;
        String fileName = fileService.uploadImage(path, image);
        //Updating The New File Name To Product
        productFromDB.setImage(fileName);
        //Save Updated Product.
        Product savedProduct = productRepository.save(productFromDB);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }
}
