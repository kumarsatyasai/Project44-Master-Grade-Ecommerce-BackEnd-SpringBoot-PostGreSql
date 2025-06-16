package com.nt.service;
import com.nt.exceptions.APIException;
import com.nt.exceptions.ResourceNotFoundException;
import com.nt.model.Cart;
import com.nt.model.CartItem;
import com.nt.model.Product;
import com.nt.model.User;
import com.nt.payload.CartDTO;
import com.nt.payload.ProductDTO;
import com.nt.repository.ICartItemRepository;
import com.nt.repository.ICartRepository;
import com.nt.repository.IProductRepository;
import com.nt.repository.IUserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements ICartService {
    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ICartRepository cartRepository;

    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ICartItemRepository iCartItemRepository;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUserName(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", userDetails.getUsername()));
        Cart cart = user.getCart();
        if(cart == null){
            Optional<Product> optionalProduct = productRepository.findById(productId);
            if(optionalProduct.isEmpty()){
                throw new APIException("Product not found with id: " + productId);
            }
            Product productFromDb = optionalProduct.get();
            if(quantity > productFromDb.getQuantity()){
                throw new APIException("Requested quantity " + quantity + " not available for product with id: " + productId);
            }
            cart = new Cart();
            cart.setUser(user);
            CartItem cartItem = new CartItem();
            cartItem.setProduct(productFromDb);
            cartItem.setQuantity(quantity);
            cartItem.setDiscount(productFromDb.getDiscount());
            cartItem.setProductPrice(productFromDb.getSpecialPrice());
            cartItem.setCart(cart);
            cart.getCartItems().add(cartItem);
            cart.setTotalPrice(cart.getTotalPrice()+ cartItem.getProductPrice() * cartItem.getQuantity());
            cart = cartRepository.save(cart);
        }
        else{
            Optional<Product> optionalProduct = productRepository.findById(productId);
            if(optionalProduct.isEmpty()){
                throw new APIException("Product not found with id: " + productId);
            }
            Product productFromDb = optionalProduct.get();
            if(quantity > productFromDb.getQuantity()){
                throw new APIException("Requested quantity " + quantity + " not available for product with id: " + productId);
            }

            Optional<CartItem> optionalCartItem = cart.getCartItems().stream()
                    .filter(item -> item.getProduct().getProductId().equals(productId))
                    .findFirst();

            if (optionalCartItem.isPresent()) {
                CartItem existingItem = optionalCartItem.get();
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
                cart.setTotalPrice(cart.getTotalPrice() + existingItem.getProductPrice() * quantity);
            } else {
                CartItem cartItem = new CartItem();
                cartItem.setProduct(productFromDb);
                cartItem.setQuantity(quantity);
                cartItem.setDiscount(productFromDb.getDiscount());
                cartItem.setProductPrice(productFromDb.getSpecialPrice());
                cartItem.setCart(cart);
                cart.getCartItems().add(cartItem);
                cart.setTotalPrice(cart.getTotalPrice() + cartItem.getProductPrice() * cartItem.getQuantity());
            }
            cart = cartRepository.save(cart);

        }

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();
        List<ProductDTO> productDTOs = cartItems.stream()
                .map(cartItem -> {
                    cartItem.getProduct().setQuantity(cartItem.getQuantity());
                    return modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                })
                .collect(Collectors.toList());
        cartDTO.setProducts(productDTOs);
        return cartDTO;
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        if(carts.isEmpty()){
            throw new APIException("No Carts Found");
        }
        List<CartDTO> cartDTOS = carts.stream()
                .map(cart -> {
                     CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
                    List<ProductDTO> productDTOS = cart.getCartItems().stream()
                            .map(cartItem -> {
                                cartItem.getProduct().setQuantity(cartItem.getQuantity());
                                return modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                            })
                            .toList();
                    cartDTO.setProducts(productDTOS);
                    return cartDTO;
                }).toList();
        return cartDTOS;
    }

    @Override
    public CartDTO getUsersCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
         Optional<User> user = userRepository.findByUserName(userDetails.getUsername());

         Cart cart = user.get().getCart();
         if(cart == null){
             throw new APIException("No Cart Found for User: " + userDetails.getUsername());
         }
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<ProductDTO> productDTOS = cart.getCartItems().stream()
                .map(cartItem -> {
                    cartItem.getProduct().setQuantity(cartItem.getQuantity());
                    return modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                })
                .collect(Collectors.toList());
        cartDTO.setProducts(productDTOS);
        return cartDTO;
    }


    @Override
    public CartDTO updatedCartProduct(Long productId, Integer quantity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> optionalUser = userRepository.findByUserName(userDetails.getUsername());
        User userFromDb = optionalUser.get();
        Cart cart = userFromDb.getCart();
        if(cart == null){
            throw new APIException("No Cart Found for User: " + userDetails.getUsername());
        }

        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            throw new APIException("Product not found with id: " + productId);
        }
        Product productFromDb = optionalProduct.get();

        Optional<CartItem> optionalCartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getProductId().equals(productId))
                .findFirst();
        if (optionalCartItem.isEmpty()) {
            throw new APIException("Product not found in cart: " + productId);
        }

        CartItem cartItem = optionalCartItem.get();
        int updatedQuantity = cartItem.getQuantity() + quantity;

        if (updatedQuantity > productFromDb.getQuantity()) {
            throw new APIException("Requested quantity " + updatedQuantity + " not available for product with id: " + productId);
        }

        if (updatedQuantity <= 0) {
            cart.getCartItems().remove(cartItem);
        } else {
            cartItem.setQuantity(updatedQuantity);
            iCartItemRepository.save(cartItem);

        }
        cart.setTotalPrice(cart.getTotalPrice() + cartItem.getProductPrice() * quantity);
        cart = cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<ProductDTO> productDTOS = cart.getCartItems().stream()
                .map(item -> {
                    item.getProduct().setQuantity(item.getQuantity());
                    return modelMapper.map(item.getProduct(), ProductDTO.class);
                })
                .collect(Collectors.toList());
        cartDTO.setProducts(productDTOS);
        return cartDTO;
    }

    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Optional<Cart> optionalCart = cartRepository.findById(cartId);
        if (optionalCart.isEmpty()) {
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        }

        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            throw new ResourceNotFoundException("Product", "productId", productId);
        }


        Cart cart = optionalCart.get();
        Optional<CartItem> optionalCartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getProductId().equals(productId))
                .findFirst();

        if (optionalCartItem.isEmpty()) {
            throw new ResourceNotFoundException("Product", "productId", productId);
        }

        CartItem cartItem = optionalCartItem.get();
        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity()));

        // Remove the specific cart item from the cart's collection
        cart.getCartItems().remove(cartItem);

        // Save the cart first to update the total price
        cart = cartRepository.save(cart);

        // Delete the specific cart item from the database
        iCartItemRepository.deleteById(cartItem.getCartItemId());

        return "Product removed from cart successfully";
    }
}
