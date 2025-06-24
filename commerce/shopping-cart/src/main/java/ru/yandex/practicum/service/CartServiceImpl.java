package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.mapper.CartMapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.repository.CartRepository;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartServiceImpl implements CartService {
    final CartRepository cartRepository;
    final CartMapper cartMapper;

    @Override
    @Transactional(readOnly = true)
    public ShoppingCartDto getCart(String username) {
        checkUser(username);
        return cartRepository.findByUsername(username)
                .map(cartMapper::toDto)
                .orElseGet(() -> createNewCartDto(username));
    }

    @Override
    @Transactional
    public ShoppingCartDto addProductToCart(String username, Map<UUID, Long> products) {
        checkUser(username);
        Optional<ShoppingCart> cartOpt = cartRepository.findByUsername(username);
        ShoppingCart cart;
        if (cartOpt.isPresent()) {
            cart = cartOpt.get();
            products.forEach((productId, quantity) ->
                    cart.getProducts().merge(productId, quantity, Long::sum));
        } else {
            cart = ShoppingCart.builder().username(username).products(products).isActive(true).build();
        }
        return cartMapper.toDto(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public void deleteUserCart(String username) {
        checkUser(username);
        cartRepository.findByUsername(username)
                .ifPresent(cart -> {
                    cart.setIsActive(false);
                    cartRepository.save(cart);
                });
    }

    @Override
    @Transactional
    public ShoppingCartDto removeFromCart(String username, Set<UUID> productIds) {
        checkUser(username);
        ShoppingCart cart = findCart(username);
        for (UUID productId : productIds) {
            if (!cart.getProducts().containsKey(productId)) {
                throw new NoProductsInShoppingCartException("Товар = " + productId + " не найден в корзине");
            }
            cart.getProducts().remove(productId);
        }
        return cartMapper.toDto(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        checkUser(username);
        ShoppingCart cart = findCart(username);
        if (!cart.getProducts().containsKey(request.getProductId())) {
            throw new NoProductsInShoppingCartException("Товар = " + request.getProductId() + " не найден в корзине");
        }
        cart.getProducts().put(request.getProductId(), request.getNewQuantity());
        return cartMapper.toDto(cartRepository.save(cart));
    }

    private void checkUser(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым или null");
        }
    }

    private ShoppingCartDto createNewCartDto(String username) {
        ShoppingCart newCart = createNewCart(username);
        return cartMapper.toDto(newCart);
    }

    private ShoppingCart createNewCart(String username) {
        ShoppingCart newCart = ShoppingCart.builder()
                .username(username)
                .isActive(true)
                .build();
        return cartRepository.save(newCart);
    }

    private ShoppingCart findCart(String username) {
        return cartRepository.findByUsername(username)
                .orElseThrow(() -> new NoProductsInShoppingCartException("Корзина не найдена для пользователя = " +
                        username));
    }
}