package com.webshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class CompanyCustomerInputDto extends CustomerInputDto {

    @NotBlank(message = "Company name is required")
    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("vat_number")
    private String vatNumber;

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
}
