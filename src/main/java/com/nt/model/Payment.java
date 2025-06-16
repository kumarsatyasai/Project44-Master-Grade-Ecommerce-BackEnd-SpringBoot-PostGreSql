package com.nt.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne(mappedBy = "payment", cascade = {jakarta.persistence.CascadeType.PERSIST, jakarta.persistence.CascadeType.MERGE})
    private Order order;

    @NotBlank
    @Size(max = 4, message = "Payment Method Must Contains Atleast 4 Characters.")
    private String paymentMethod;

    public Payment(String paymentMethod, String pgPaymentId, String pgResponseMessage, String pgName, String pgStatus) {
        this.paymentMethod = paymentMethod;
        this.pgPaymentId = pgPaymentId;
        this.pgResponseMessage = pgResponseMessage;
        this.pgName = pgName;
        this.pgStatus = pgStatus;
    }

    private String pgPaymentId;

    private String pgStatus;

    private String pgResponseMessage;

    private String pgName;
}
