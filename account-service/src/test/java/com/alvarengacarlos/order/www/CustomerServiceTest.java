package com.alvarengacarlos.order.www;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.github.javafaker.Faker;

public class CustomerServiceTest {

    private CustomerService customerService;
    private CustomerRepository customerRepository;
    private CustomerPreRegistrationRepository customerPreRegisterRepository;
    private final Faker faker = new Faker();
    private final String phoneNumber = faker.phoneNumber().phoneNumber();

    @BeforeEach
    void beforeEach() {
        customerRepository = Mockito.mock();
        customerPreRegisterRepository = Mockito.mock();
        customerService = new CustomerService(customerRepository, customerPreRegisterRepository);
    }

    @Nested
    class PreRegisterCustomer {

        @Test
        void shouldPreRegisterACustomer() {
            PreRegisterCustomerDto preRegisterCustomerDto = new PreRegisterCustomerDto(phoneNumber);

            Assertions.assertDoesNotThrow(()
                    -> customerService.preRegisterCustomer(preRegisterCustomerDto)
            );
            Mockito.verify(
                    customerPreRegisterRepository,
                    Mockito.times(1)
            ).saveCustomerPreRegistration(
                    Mockito.any(CustomerPreRegistration.class)
            );
            Mockito.verify(
                    customerPreRegisterRepository,
                    Mockito.times(1)
            ).sendSmsToCustomer(Mockito.eq(phoneNumber), Mockito.anyString());
        }
    }

    @Nested
    class RegisterCustomer {

        private final String name = faker.name().name();
        private final CustomerPreRegistration customerPreRegistration = CustomerPreRegistration.newCustomerPreRegister(phoneNumber);
        private final RegisterCustomerDto registerCustomerDto = new RegisterCustomerDto(phoneNumber, customerPreRegistration.validationCode, name);

        @Nested
        class ShouldThrowInvalidValidationCodeException {

            @Test
            void whenPhoneNumberDoesNotExist() {
                Mockito.when(
                        customerPreRegisterRepository.findCustomerPreRegistration(phoneNumber)
                ).thenReturn(null);

                Assertions.assertThrows(
                        InvalidValidationCodeException.class,
                        () -> customerService.registerCustomer(registerCustomerDto)
                );
                Mockito.verify(
                        customerPreRegisterRepository,
                        Mockito.times(1)
                ).findCustomerPreRegistration(phoneNumber);
            }

            @Test
            void whenValidationCodeIsInvalid() {
                Mockito.when(
                        customerPreRegisterRepository.findCustomerPreRegistration(phoneNumber)
                ).thenReturn(customerPreRegistration);
                RegisterCustomerDto registerCustomerDto = new RegisterCustomerDto(phoneNumber, "123456", name);

                Assertions.assertThrows(
                        InvalidValidationCodeException.class,
                        () -> customerService.registerCustomer(registerCustomerDto)
                );
                Mockito.verify(
                        customerPreRegisterRepository,
                        Mockito.times(1)
                ).findCustomerPreRegistration(phoneNumber);
            }
        }

        @Test
        void shouldRegisterACustomer() {
            Mockito.when(
                    customerPreRegisterRepository.findCustomerPreRegistration(phoneNumber)
            ).thenReturn(customerPreRegistration);

            Assertions.assertDoesNotThrow(() -> customerService.registerCustomer(registerCustomerDto));
            Mockito.verify(
                    customerPreRegisterRepository,
                    Mockito.times(1)
            ).findCustomerPreRegistration(phoneNumber);
            Mockito.verify(
                    customerRepository,
                    Mockito.times(1)
            ).saveCustomer(Mockito.any(Customer.class));
        }
    }
}
