package com.education.platform.facade;

/** Simulated external payment system. */
public class PaymentSubSystem {
    public boolean charge(String studentName, double amount) {
        System.out.printf("[Payment] Charging %s: $%.2f%n", studentName, amount);
        return amount >= 0;
    }
}
