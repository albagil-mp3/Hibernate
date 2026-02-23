package com.billing.model.party;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

import com.billing.model.SpanishProvince;

/**
 * Base party entity (shared identity for all third parties).
 */
@Entity
@Table(name = "party")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Party {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(length = 200)
    protected String name;

    @Column(length = 255)
    protected String address;

    @Column(length = 128)
    protected String email;

    @Column(name = "city", length = 128)
    protected String city;

    @Enumerated(EnumType.STRING)
    @Column(length = 64)
    protected SpanishProvince province;

    @Column(name = "postal_code", length = 8)
    protected String postalCode;

    @Column(name = "fixed_phone", length = 32)
    protected String fixedPhone;

    @Column(name = "mobile_phone", length = 32)
    protected String mobilePhone;

    @Column(length = 255)
    protected String website;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public SpanishProvince getProvince() { return province; }
    public void setProvince(SpanishProvince province) { this.province = province; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getFixedPhone() { return fixedPhone; }
    public void setFixedPhone(String fixedPhone) { this.fixedPhone = fixedPhone; }
    public String getMobilePhone() { return mobilePhone; }
    public void setMobilePhone(String mobilePhone) { this.mobilePhone = mobilePhone; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
}
