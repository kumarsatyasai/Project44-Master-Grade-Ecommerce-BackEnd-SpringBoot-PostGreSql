package com.nt.service;

import com.nt.payload.StripePaymentDto;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface IStripeService {


    PaymentIntent paymentIntent(StripePaymentDto stripePaymentDto) throws StripeException;
}
