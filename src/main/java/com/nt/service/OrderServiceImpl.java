package com.nt.service;

import com.nt.exceptions.APIException;
import com.nt.exceptions.ResourceNotFoundException;
import com.nt.model.*;
import com.nt.payload.OrderDTO;
import com.nt.payload.OrderItemDTO;
import com.nt.payload.PaymentDTO;
import com.nt.repository.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private ICartRepository cartRepository;

    @Autowired
    private IPaymentRepository paymentRepository;

    @Autowired
    private IAddressRepository addressRepository;

    @Autowired
    private IOrderRepository orderRepository;

    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private ICartService cartService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderDTO placeOrder(String email, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {

        // Getting User Cart
        Cart cart = cartRepository.findByUserEmail(email);
        if(cart == null){
            throw new ResourceNotFoundException("Cart", "user email", email);
        }

        Optional<Address> optionalAddress = addressRepository.findById(addressId);
        if(optionalAddress.isEmpty()){
            throw new ResourceNotFoundException("Address", "addressId", addressId);
        }

        Address address = optionalAddress.get();


        // Create A New Order With payment information

        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setEmail(email);
        order.setOrderStatus("Order Acepted!");
        order.setShippingAddress(address);
        order.setTotalAmount(cart.getTotalPrice());

        Payment payment = new Payment(paymentMethod, pgPaymentId, pgResponseMessage, pgName, pgStatus) ;

        payment.setOrder(order);

        Payment savedPayment = paymentRepository.save(payment);

        order.setPayment(savedPayment);

        Order savedOrder = orderRepository.save(order);




        // Get Iems From The Cart InTo The Order Items

        List<CartItem> cartItems = cart.getCartItems();

        if(cartItems.isEmpty()){
            throw new APIException("Cart is Empty");
        }

        List<OrderItem> orderItems = new ArrayList<>();

        cart.getCartItems().forEach(
                cartItem -> {
                    OrderItem item = new OrderItem();
                    item.setProduct(cartItem.getProduct());
                    item.setDiscount(cartItem.getDiscount());
                    item.setQuantity(cartItem.getQuantity());
                    item.setOrderedProductPrice(cartItem.getProductPrice());
                    item.setOrder(savedOrder);
                    orderItems.add(item);

                }
        );
        order.setOrderItems(orderItems);
        Order savedOrder1 = orderRepository.save(order);

        // Update Product Stock
        cart.getCartItems().forEach(
                cartItem -> {
                    Product product = cartItem.getProduct();
                    product.setQuantity(cartItem.getProduct().getQuantity() - cartItem.getQuantity());
                    productRepository.save(product);
                    // Clear The Cart

                }
        );

        // Create a copy of cart items to avoid ConcurrentModificationException
        List<CartItem> cartItemsCopy = new ArrayList<>(cart.getCartItems());
        cartItemsCopy.forEach(cartItem ->
                cartService.deleteProductFromCart(cart.getCartId(), cartItem.getProduct().getProductId())
        );

        // Send Back The Order Summary.

        OrderDTO orderDTO = modelMapper.map(savedOrder1, OrderDTO.class);
        // Set the orderedDate field manually since the field names don't match
        orderDTO.setOrderedDate(savedOrder1.getOrderDate());

        PaymentDTO paymentDTO = modelMapper.map(savedPayment, PaymentDTO.class);

        orderDTO.setPaymentDTO(paymentDTO);

        List<OrderItemDTO> orderItemDTOS = orderItems.stream()
                .map(orderItem -> {
                    OrderItemDTO itemDTO = modelMapper.map(orderItem, OrderItemDTO.class);
                    itemDTO.setProductDTO(modelMapper.map(orderItem.getProduct(), com.nt.payload.ProductDTO.class));
                    return itemDTO;

                }).toList() ;
        orderDTO.setOrderItemDTO(orderItemDTOS);

        return orderDTO;
    }
}
