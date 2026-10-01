package com.webshop.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

/**
 * A customer that is a company. Inherits id, name, email, phone, addresses and profile from Customer
 * and adds the company specific fields (stored in the CompanyCustomers table, joined by id).
 */
@Entity
@Table(name = "CompanyCustomers")
@PrimaryKeyJoinColumn(name = "id")
public class CompanyCustomer extends Customer {

    @Column(name = "company_name", nullable = false, length = 255)
    @JsonProperty("company_name")
    private String companyName;

    @Column(name = "vat_number", length = 50)
    @JsonProperty("vat_number")
    private String vatNumber;

    public CompanyCustomer() {
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }

    @Override
    public String getCustomerType() {
        return "company";
    }
}
