package com.nt.controller;


import com.nt.model.User;
import com.nt.payload.OrderDTO;
import com.nt.payload.OrderRequestDTO;
import com.nt.payload.StripePaymentDto;
import com.nt.repository.IUserRepository;
import com.nt.service.IOrderService;
import com.nt.service.IStripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IStripeService stripeService;



    @PostMapping("/order/users/payment/{paymentMethod}")
    public ResponseEntity<OrderDTO> placeOrder(@PathVariable String paymentMethod, @RequestBody OrderRequestDTO orderRequestDTO){

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String email = user.getEmail();

        OrderDTO orderDTO = orderService.placeOrder(email,
                orderRequestDTO.getAddressId(),
                paymentMethod,
                orderRequestDTO.getPgName(),
                orderRequestDTO.getPgPaymentId(),
                orderRequestDTO.getPgStatus(),
                orderRequestDTO.getPgResponseMessage());


        return new ResponseEntity<>(orderDTO, org.springframework.http.HttpStatus.CREATED);
    }

    @PostMapping("/order/stripe-client-secret")
    public ResponseEntity<String> placeOrder(@RequestBody StripePaymentDto  stripePaymentDto) throws StripeException {

        PaymentIntent paymentIntent = stripeService.paymentIntent(stripePaymentDto);

        return new ResponseEntity<>(paymentIntent.getClientSecret(), org.springframework.http.HttpStatus.CREATED);


    }

}
