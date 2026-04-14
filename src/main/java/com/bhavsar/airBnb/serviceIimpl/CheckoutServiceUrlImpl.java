package com.bhavsar.airBnb.serviceIimpl;

import com.bhavsar.airBnb.model.Booking;
import com.bhavsar.airBnb.model.User;
import com.bhavsar.airBnb.repository.BookingRepository;
import com.bhavsar.airBnb.service.CheckoutService;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class CheckoutServiceUrlImpl implements CheckoutService {

    private final BookingRepository bookingRepository;

    @Override
    public String getCheckoutSession(Booking booking, String successUrl, String failureUrl) {
        log.info("Creating Session for booking with id:{}",booking.getId());
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            CustomerCreateParams customerParams  = CustomerCreateParams.builder()
                    .setEmail(user.getEmail())
                    .setName(user.getName())
                    .build();
          Customer customer = Customer.create(customerParams);

            SessionCreateParams sessionParams = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.REQUIRED)
                    .setCustomer(customer.getId())
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(failureUrl)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("INR")
                                        .setUnitAmount(booking.getAmount().multiply(BigDecimal.valueOf(100)).longValue())
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName(booking.getHotel().getName() + " : "+ booking.getRoom().getType())
                                                .setDescription("Booking ID : "+booking.getId())
                                            .build()
                                        )
                                    .build()
                            )
                      .build()
                    )
            .build();

            Session session = Session.create(sessionParams);
            booking.setPaymentSessionId(session.getId());
            bookingRepository.save(booking);
            log.info("Session created successfully for booking with id:{}",booking.getId());

            return session.getUrl();

        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
        //create a sessio params from checkout only

    }
}
