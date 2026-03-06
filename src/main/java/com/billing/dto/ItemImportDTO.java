package com.billing.dto;

import java.math.BigDecimal;

public class ItemImportDTO {
    public String name;
    public String description;
    public String familyCode;
    public String categoryCode;
    public String unitSymbol;
    public String supplierCode;
    public BigDecimal costPrice;
    public BigDecimal salePrice;
    public Integer ivaPercent;
    public Integer currentStock;
    public Integer minimumStock;
    public String barcode;
    public String observations;
    public Boolean active;
}
