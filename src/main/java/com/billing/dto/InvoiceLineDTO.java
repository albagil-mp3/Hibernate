package com.billing.dto;

import java.math.BigDecimal;

public class InvoiceLineDTO {
    public String itemCode;
    public String itemDescription;
    public Integer quantity;
    public BigDecimal unitPrice;
    public BigDecimal taxRate;
    public BigDecimal lineTotal;
}
