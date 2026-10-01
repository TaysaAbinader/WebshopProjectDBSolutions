package com.webshop.model;

import com.webshop.repository.CustomerProfileRepository;
import com.webshop.repository.CustomerRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/** 1:1 between Customer and CustomerProfile (FK customer_id is unique, stored in the profile table). */
@DataJpaTest
class CustomerProfileAssociationTest {

    @Autowired CustomerRepository customerRepository;
    @Autowired CustomerProfileRepository profileRepository;
    @Autowired EntityManager em;

    private Customer saveCustomerWithProfile() {
        Customer customer = new Customer(null, "Anna", "Virtanen", "anna@example.com", null);
        CustomerProfile profile = new CustomerProfile();
        profile.setBirthDate(LocalDate.of(2000, 5, 17));
        profile.setNewsletterSubscribed(true);
        profile.setPreferredLanguage("fi");
        customer.setProfile(profile);
        Customer saved = customerRepository.saveAndFlush(customer);   // cascade saves the profile
        em.clear();
        return saved;
    }

    @Test
    void profileIsSavedAndRetrievedWithCustomer() {
        Customer saved = saveCustomerWithProfile();
        CustomerProfile profile = profileRepository.findByCustomer_Id(saved.getId()).orElseThrow();
        assertEquals("fi", profile.getPreferredLanguage());
        assertEquals(saved.getId(), profile.getCustomerId());
        assertEquals("fi", customerRepository.findById(saved.getId()).orElseThrow().getProfile().getPreferredLanguage());
    }

    @Test
    void deletingCustomerDeletesProfile() {
        Customer saved = saveCustomerWithProfile();
        customerRepository.delete(customerRepository.findById(saved.getId()).orElseThrow());
        customerRepository.flush();
        assertEquals(0, profileRepository.count());
    }

    @Test
    void removingProfileFromCustomerDeletesProfileRow() {
        Customer saved = saveCustomerWithProfile();
        Customer loaded = customerRepository.findById(saved.getId()).orElseThrow();
        loaded.setProfile(null);   // orphanRemoval
        customerRepository.flush();
        assertEquals(0, profileRepository.count());
        assertTrue(customerRepository.existsById(saved.getId()));
    }

    @Test
    void customerCannotHaveTwoProfiles() {
        Customer saved = saveCustomerWithProfile();
        CustomerProfile second = new CustomerProfile();
        second.setCustomer(customerRepository.findById(saved.getId()).orElseThrow());
        assertThrows(DataIntegrityViolationException.class, () -> profileRepository.saveAndFlush(second));
    }
}
