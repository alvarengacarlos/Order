package com.alvarengacarlos.order.www;

import java.util.List;
import java.util.UUID;

public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;

    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository) {
        this.shoppingCartRepository = shoppingCartRepository;
    }

    public void addProductToShoppingCart(UUID customerId, AddProductToShoppingCartDto addProductToShoppingCartDto) {
        this.shoppingCartRepository.addProduct(customerId, addProductToShoppingCartDto.productId());
    }

    public void removeProductFromShoppingCart(UUID customerId, RemoveProductFromShoppingCartDto removeProductFromShoppingCartDto) {
        this.shoppingCartRepository.removeProduct(customerId, removeProductFromShoppingCartDto.productId());
    }

    public List<UUID> showShoppingCartProducts(UUID customerId) {
        return this.shoppingCartRepository.findProducts(customerId);
    }
}
