package com.billing.dto;

import java.math.BigDecimal;
import java.util.List;

public class InvoiceExportDTO {
    public Long id;
    public String code;
    public String date;
    public String clientCode;
    public String clientName;
    public BigDecimal subtotal;
    public BigDecimal taxes;
    public BigDecimal total;
    public List<InvoiceLineDTO> lines;
}
