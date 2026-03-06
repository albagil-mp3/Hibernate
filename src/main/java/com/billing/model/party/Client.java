package com.billing.model.party;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import com.billing.model.PaymentMethod;
/**
 * Client model (inherits Party).
 */
@Entity
@Table(name = "client")
@PrimaryKeyJoinColumn(name = "id")
public class Client extends Party {
    @Column(name = "dni", length = 64)
    protected String dni;

    @Column(length = 32, unique = true)
    protected String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 64)
    protected PaymentMethod paymentMethod;

    @Column(name = "credit_limit")
    protected BigDecimal creditLimit;

    @Column(name = "bank_account_number", length = 64)
    protected String bankAccountNumber;

    @Column
    protected Boolean active;

    @Column(length = 1000)
    protected String observations;

    public Client() {}
    public Client(String name) { this.name = name; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public BigDecimal getCreditLimit() { return creditLimit; }
    public void setCreditLimit(BigDecimal creditLimit) { this.creditLimit = creditLimit; }

    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    @Override public String toString() { return "Client{" + "id=" + id + ", name='" + name + '\'' + '}'; }
}
