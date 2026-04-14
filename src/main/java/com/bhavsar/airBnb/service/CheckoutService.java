package com.bhavsar.airBnb.service;

import com.bhavsar.airBnb.model.Booking;

public interface CheckoutService {
    String getCheckoutSession(Booking booking, String successUrl , String failureUrl);
}
