package com.webshop.service;

import com.webshop.dto.AddressInputDto;
import com.webshop.dto.CustomerInputDto;
import com.webshop.exception.ResourceNotFoundException;
import com.webshop.model.Customer;
import com.webshop.model.CustomerAddress;
import com.webshop.repository.CustomerAddressRepository;
import com.webshop.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerAddressRepository customerAddressRepository;

    public CustomerService(CustomerRepository customerRepository, CustomerAddressRepository customerAddressRepository) {
        this.customerRepository = customerRepository;
        this.customerAddressRepository = customerAddressRepository;
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
