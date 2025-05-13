package app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("test")
    public void pushUserTestPaymentNotification() {

    }

    @PostMapping
    public void pushUserPaymentNotification() {

    }
}
