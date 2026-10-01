package com.webshop.service;

import com.webshop.dto.AddressInputDto;
import com.webshop.dto.CompanyCustomerInputDto;
import com.webshop.dto.CustomerInputDto;
import com.webshop.dto.CustomerProfileInputDto;
import com.webshop.exception.ResourceNotFoundException;
import com.webshop.model.CompanyCustomer;
import com.webshop.model.Customer;
import com.webshop.model.CustomerAddress;
import com.webshop.model.CustomerProfile;
import com.webshop.repository.CompanyCustomerRepository;
import com.webshop.repository.CustomerAddressRepository;
import com.webshop.repository.CustomerProfileRepository;
import com.webshop.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerAddressRepository customerAddressRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final CompanyCustomerRepository companyCustomerRepository;

    public CustomerService(CustomerRepository customerRepository, CustomerAddressRepository customerAddressRepository,
                           CustomerProfileRepository customerProfileRepository,
                           CompanyCustomerRepository companyCustomerRepository) {
        this.companyCustomerRepository = companyCustomerRepository;
        this.customerRepository = customerRepository;
        this.customerAddressRepository = customerAddressRepository;
        this.customerProfileRepository = customerProfileRepository;
    }

    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
    }

    public Customer registerCustomer(CustomerInputDto input) {
        if (customerRepository.existsByEmail(input.getEmail())) {
            throw new IllegalArgumentException("Customer with email " + input.getEmail() + " already exists");
        }

        Customer customer = new Customer();
        customer.setFirstName(input.getFirstName());
        customer.setLastName(input.getLastName());
        customer.setEmail(input.getEmail());
        customer.setPhone(input.getPhone());
        return customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public List<CompanyCustomer> getAllCompanyCustomers() {
        return companyCustomerRepository.findAll();
    }

    public CompanyCustomer registerCompanyCustomer(CompanyCustomerInputDto input) {
        if (customerRepository.existsByEmail(input.getEmail())) {
            throw new IllegalArgumentException("Customer with email " + input.getEmail() + " already exists");
        }
        CompanyCustomer customer = new CompanyCustomer();
        customer.setFirstName(input.getFirstName());
        customer.setLastName(input.getLastName());
        customer.setEmail(input.getEmail());
        customer.setPhone(input.getPhone());
        customer.setCompanyName(input.getCompanyName());
        customer.setVatNumber(input.getVatNumber());
        return companyCustomerRepository.save(customer);
    }

    public Customer updateCustomer(Long id, CustomerInputDto input) {
        Customer customer = getCustomerById(id);
        if (input.getEmail() != null && !input.getEmail().equalsIgnoreCase(customer.getEmail())) {
            if (customerRepository.existsByEmail(input.getEmail())) {
                throw new IllegalArgumentException("Customer with email " + input.getEmail() + " already exists");
            }
            customer.setEmail(input.getEmail());
        }
        if (input.getFirstName() != null) {
            customer.setFirstName(input.getFirstName());
        }
        if (input.getLastName() != null) {
            customer.setLastName(input.getLastName());
        }
        if (input.getPhone() != null) {
            customer.setPhone(input.getPhone());
        }
        return customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public CustomerProfile getCustomerProfile(Long customerId) {
        CustomerProfile profile = getCustomerById(customerId).getProfile();
        if (profile == null) {
            throw new ResourceNotFoundException("Profile not found for customer " + customerId);
        }
        return profile;
    }

    /** Creates the profile if the customer has none, otherwise updates it (1:1). */
    public CustomerProfile saveCustomerProfile(Long customerId, CustomerProfileInputDto input) {
        Customer customer = getCustomerById(customerId);
        CustomerProfile profile = customer.getProfile();
        if (profile == null) {
            profile = new CustomerProfile();
            customer.setProfile(profile);
        }
        if (input.getBirthDate() != null) {
            profile.setBirthDate(input.getBirthDate());
        }
        if (input.getNewsletterSubscribed() != null) {
            profile.setNewsletterSubscribed(input.getNewsletterSubscribed());
        }
        if (input.getPreferredLanguage() != null) {
            profile.setPreferredLanguage(input.getPreferredLanguage());
        }
        return customerProfileRepository.save(profile);
    }

    public void deleteCustomerProfile(Long customerId) {
        Customer customer = getCustomerById(customerId);
        if (customer.getProfile() == null) {
            throw new ResourceNotFoundException("Profile not found for customer " + customerId);
        }
        customer.setProfile(null); // orphanRemoval deletes the profile row
    }

    public void deleteCustomer(Long id) {
        Customer customer = getCustomerById(id);
        customerRepository.delete(customer);
    }

    @Transactional(readOnly = true)
    public List<CustomerAddress> getCustomerAddresses(Long customerId) {
        getCustomerById(customerId); // verify customer exists
        return customerAddressRepository.findByCustomerId(customerId);
    }

    public CustomerAddress addCustomerAddress(Long customerId, AddressInputDto input) {
        getCustomerById(customerId); // verify customer exists

        CustomerAddress address = new CustomerAddress();
        address.setCustomerId(customerId);
        address.setStreetAddress(input.getStreetAddress());
        address.setCity(input.getCity());
        address.setState(input.getState());
        address.setPostalCode(input.getPostalCode());
        address.setCountry(input.getCountry());
        address.setIsDefault(input.getIsDefault() != null ? input.getIsDefault() : false);

        return customerAddressRepository.save(address);
    }

    @Transactional(readOnly = true)
    public CustomerAddress getCustomerAddressById(Long customerId, Long addressId) {
        getCustomerById(customerId);
        return customerAddressRepository.findById(addressId)
                .filter(addr -> addr.getCustomerId().equals(customerId))
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with ID: " + addressId + " for customer " + customerId));
    }

    public void deleteCustomerAddress(Long customerId, Long addressId) {
        CustomerAddress address = getCustomerAddressById(customerId, addressId);
        customerAddressRepository.delete(address);
    }
}
