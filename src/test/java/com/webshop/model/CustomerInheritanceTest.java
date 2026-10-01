package com.webshop.model;

import com.webshop.repository.CompanyCustomerRepository;
import com.webshop.repository.CustomerRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Customer (parent) and CompanyCustomer (child), mapped with InheritanceType.JOINED. */
@DataJpaTest
class CustomerInheritanceTest {

    @Autowired CustomerRepository customerRepository;
    @Autowired CompanyCustomerRepository companyRepository;
    @Autowired EntityManager em;

    private CompanyCustomer company() {
        CompanyCustomer c = new CompanyCustomer();
        c.setFirstName("Mia");
        c.setLastName("Korhonen");
        c.setEmail("mia@acme.fi");
        c.setCompanyName("Acme Oy");
        c.setVatNumber("FI12345678");
        return c;
    }

    @Test
    void companyCustomerIsSavedAndLoadedThroughParentRepository() {
        Long id = companyRepository.saveAndFlush(company()).getId();
        em.clear();
        Customer loaded = customerRepository.findById(id).orElseThrow();   // polymorphic query
        assertInstanceOf(CompanyCustomer.class, loaded);
        assertEquals("Acme Oy", ((CompanyCustomer) loaded).getCompanyName());
        assertEquals("mia@acme.fi", loaded.getEmail());   // shared field from Customers
        assertEquals("company", loaded.getCustomerType());
    }

    @Test
    void findAllReturnsPrivateAndCompanyCustomers() {
        customerRepository.save(new Customer(null, "Anna", "Virtanen", "anna@example.com", null));
        companyRepository.save(company());
        em.flush();
        em.clear();
        List<Customer> all = customerRepository.findAll();
        assertEquals(2, all.size());
        assertEquals(1, all.stream().filter(c -> c instanceof CompanyCustomer).count());
        assertEquals(1, companyRepository.findAll().size());   // only the subclass
    }

    @Test
    void deletingCompanyCustomerDeletesBothTableRows() {
        Long id = companyRepository.saveAndFlush(company()).getId();
        customerRepository.deleteById(id);
        customerRepository.flush();
        assertEquals(0, companyRepository.count());
        assertEquals(0, customerRepository.count());
    }

    @Test
    void companyCustomerCanAlsoHaveAProfile() {
        CompanyCustomer c = company();
        CustomerProfile profile = new CustomerProfile();
        profile.setPreferredLanguage("en");
        c.setProfile(profile);
        Long id = companyRepository.saveAndFlush(c).getId();
        em.clear();
        assertEquals("en", customerRepository.findById(id).orElseThrow().getProfile().getPreferredLanguage());
    }
}
