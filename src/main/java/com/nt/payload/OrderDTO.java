package com.nt.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    private Long orderId;//

    private String email;//

    private List<OrderItemDTO> orderItemDTO;

    private LocalDate orderedDate;//

    private PaymentDTO paymentDTO;//

    private Double totalAmount;//

    private String orderStatus;//



}
