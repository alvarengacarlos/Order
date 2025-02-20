package com.alvarengacarlos.order.www;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CustomerServiceTest {

    private CustomerService customerService;
    private CustomerRepository customerRepository;

    @BeforeEach
    void beforeEach() {
        customerRepository = Mockito.mock();
        customerService = new CustomerService(customerRepository);
    }

    @Nested
    class PreRegisterCustomer {

        @Test
        void shouldPreRegisterACustomer() {
            String phoneNumber = "+55 (00)00000-0000";

            Assertions.assertDoesNotThrow(() -> customerService.preRegisterCustomer(phoneNumber));
            Mockito.verify(customerRepository, Mockito.times(1)).saveCustomerPreRegister(Mockito.eq(phoneNumber), Mockito.anyString());
            Mockito.verify(customerRepository, Mockito.times(1)).sendSmsToCustomer(Mockito.eq(phoneNumber), Mockito.anyString());
        }
    }

    @Nested
    class RegisterCustomer {

        @Nested
        class ShouldThrowInvalidValidationCodeException {

            @Test
            void whenPhoneNumberDoesNotExist() {
                String phoneNumber = "+55 (00)00000-0000";
                String validationCode = "123456";
                String name = "John Doe";
                Mockito.when(customerRepository.findCustomerPreRegister(phoneNumber)).thenReturn(null);

                Assertions.assertThrows(InvalidValidationCodeException.class, () -> customerService.registerCustomer(phoneNumber, validationCode, name));
                Mockito.verify(customerRepository, Mockito.times(1)).findCustomerPreRegister(phoneNumber);
            }

            @Test
            void whenValidationCodeIsInvalid() {
                String phoneNumber = "+55 (00)00000-0000";
                String validationCode = "123456";
                String name = "John Doe";
                Mockito.when(customerRepository.findCustomerPreRegister(phoneNumber)).thenReturn("654321");

                Assertions.assertThrows(InvalidValidationCodeException.class, () -> customerService.registerCustomer(phoneNumber, validationCode, name));
                Mockito.verify(customerRepository, Mockito.times(1)).findCustomerPreRegister(phoneNumber);
            }
        }

        @Test
        void shouldRegisterACustomer() {
            String phoneNumber = "+55 (00)00000-0000";
            String validationCode = "123456";
            String name = "John Doe";
            Mockito.when(customerRepository.findCustomerPreRegister(phoneNumber)).thenReturn(validationCode);

            Assertions.assertDoesNotThrow(() -> customerService.registerCustomer(phoneNumber, validationCode, name));
            Mockito.verify(customerRepository, Mockito.times(1)).findCustomerPreRegister(phoneNumber);
            Mockito.verify(customerRepository, Mockito.times(1)).saveCustomerRegister(name, phoneNumber);
        }
    }
}
