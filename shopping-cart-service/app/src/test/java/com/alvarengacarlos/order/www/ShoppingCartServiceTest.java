package com.alvarengacarlos.order.www;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ShoppingCartServiceTest {

    private final ShoppingCartRepository shoppingCartRepository = Mockito.mock(ShoppingCartRepository.class);
    private final ShoppingCartService shoppingCartService = new ShoppingCartService(shoppingCartRepository);
    private final UUID productId = UUID.randomUUID();
    private final UUID customerId = UUID.randomUUID();

    @Nested
    class AddProductToShoppingCart {

        @Test
        void shouldAddAProductToShoppingCart() {
            Assertions.assertDoesNotThrow(() -> shoppingCartService.addProductToShoppingCart(customerId, productId));

            Mockito.verify(
                    shoppingCartRepository,
                    Mockito.times(1)
            ).addProduct(Mockito.any(UUID.class), Mockito.any(UUID.class));
        }
    }

    @Nested
    class RemoveProductFromShoppingCart {

        @Test
        void shouldRemoveAProductFromShoppingCart() {
            Assertions.assertDoesNotThrow(() -> shoppingCartService.removeProductFromShoppingCart(customerId, productId));

            Mockito.verify(
                    shoppingCartRepository,
                    Mockito.times(1)
            ).removeProduct(Mockito.any(UUID.class), Mockito.any(UUID.class));
        }
    }

    @Nested
    class ShowShoppingCartProducts {

        @Test
        void shouldShowShoppingCartProducts() {
            Assertions.assertDoesNotThrow(() -> shoppingCartService.showShoppingCartProducts(customerId));

            Mockito.verify(
                    shoppingCartRepository,
                    Mockito.times(1)
            ).findProducts(Mockito.any(UUID.class));
        }
    }
}
