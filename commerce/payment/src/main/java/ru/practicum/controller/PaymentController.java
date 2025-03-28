package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.order.OrderDto;
import ru.practicum.dto.payment.PaymentDto;
import ru.practicum.service.PaymentService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public PaymentDto makingPaymentForOrder(@Valid @RequestBody OrderDto orderDto) {
        return paymentService.makingPaymentForOrder(orderDto);
    }

    @PostMapping("/productCost")
    public Double calculateProductsCost(@Valid @RequestBody OrderDto orderDto) {
        return paymentService.calculateProductsCost(orderDto);
    }

    @PostMapping("/totalCost")
    public Double calculateTotalCost(@Valid @RequestBody OrderDto orderDto) {
        return paymentService.calculateTotalCost(orderDto);
    }

    @PostMapping("/refund")
    public void changePaymentStateToSuccess(@RequestBody UUID paymentId) {
        paymentService.changePaymentStateToSuccess(paymentId);
    }

    @PostMapping("/failed")
    public void changePaymentStateToFailed(@RequestBody UUID paymentId) {
        paymentService.changePaymentStateToFailed(paymentId);
    }
}
