package com.nt.service;

import com.nt.payload.OrderDTO;
import jakarta.transaction.Transactional;

public interface IOrderService {

    @Transactional
    OrderDTO placeOrder(String email, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage);
}
