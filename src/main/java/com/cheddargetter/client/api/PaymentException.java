package com.cheddargetter.client.api;

public class PaymentException extends Exception {

    public PaymentException() {
    }

    public PaymentException(String s) {
        super(s);
    }

    public PaymentException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public PaymentException(Throwable throwable) {
        super(throwable);
    }
}
