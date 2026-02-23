package com.billing.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO / View model for item details prepared by service layer
 */
public class ItemDetailsView {
    public String code;
    public String name;
    public String description;
    public String family;
    public String category;
    public String supplier;
    public BigDecimal salePrice;
    public BigDecimal costPrice;
    public Integer vatPercent;
    public BigDecimal priceWithVat;
    public BigDecimal marginPercent;
    public String marginStatus;
    public Integer stock;
    public String stockStatus;
    public String barcode;
    public Boolean active;
    public String observations;
    public LocalDateTime dateAdded;
    public String imagePath;

    public ItemDetailsView() {}

    public static ItemDetailsView of(String code, String name) {
        ItemDetailsView v = new ItemDetailsView();
        v.code = code;
        v.name = name;
        return v;
    }
}
