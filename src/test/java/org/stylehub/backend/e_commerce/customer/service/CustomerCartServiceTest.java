package org.stylehub.backend.e_commerce.customer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.stylehub.backend.e_commerce.brand.entity.Brand;
import org.stylehub.backend.e_commerce.brand.service.BrandService;
import org.stylehub.backend.e_commerce.cart.entity.Cart;
import org.stylehub.backend.e_commerce.cart.entity.CartStatus;
import org.stylehub.backend.e_commerce.cart.item.entity.CartItem;
import org.stylehub.backend.e_commerce.cart.item.repository.CartItemRepository;
import org.stylehub.backend.e_commerce.cart.repository.CartRepository;
import org.stylehub.backend.e_commerce.customer.dto.cart.UpdateCartItemQuantityRequest;
import org.stylehub.backend.e_commerce.customer.profile.entity.CustomerProfile;
import org.stylehub.backend.e_commerce.customer.profile.service.CustomerProfileService;
import org.stylehub.backend.e_commerce.platform.mail.publisher.EmailEventPublisher;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.product.color.entity.ProductColor;
import org.stylehub.backend.e_commerce.product.color.variant.entity.ProductVariant;
import org.stylehub.backend.e_commerce.product.color.variant.repository.ProductVariantRepository;
import org.stylehub.backend.e_commerce.product.entity.Product;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerCartServiceTest {

    @Mock
    private CurrentUserProvider currentUserProvider;
    @Mock
    private CustomerProfileService customerProfileService;
    @Mock
    private BrandService brandService;
    @Mock
    private ProductVariantRepository productVariantRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private EmailEventPublisher emailEventPublisher;

    @InjectMocks
    private CustomerCartService customerCartService;

    @Test
    void changeCartItemQuantityDeletesItemWhenQuantityBecomesZero() {
        String brandExternalId = "brand-1";
        UUID customerId = UUID.randomUUID();
        UUID brandId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        UUID cartItemId = UUID.randomUUID();

        CustomerProfile customer = new CustomerProfile();
        customer.setId(customerId);
        customer.setUsername("moustafa");

        Brand brand = new Brand();
        brand.setId(brandId);
        brand.setBrandName("Style Hub");
        brand.setBrandEmail("brand@test.com");

        Cart cart = new Cart();
        cart.setId(cartId);
        cart.setCartStatus(CartStatus.ACTIVE);
        cart.setCustomer(customer);
        cart.setBrand(brand);

        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cartItem.setCart(cart);
        cartItem.setQuantity(1);
        cartItem.setPrice(BigDecimal.TEN);

        when(this.currentUserProvider.externalId()).thenReturn("customer-external-id");
        when(this.customerProfileService.findCustomerProfileByExternalUserId("customer-external-id")).thenReturn(customer);
        when(this.brandService.findBrandByExternalId(brandExternalId)).thenReturn(brand);
        when(this.cartRepository.findCartByCartStatusAndCustomer_IdAndBrand_Id(CartStatus.ACTIVE, customerId, brandId))
                .thenReturn(Optional.of(cart));
        when(this.cartItemRepository.findByIdAndCart_Id(cartItemId, cartId)).thenReturn(Optional.of(cartItem));

        Map<String, Integer> response = this.customerCartService.changeCartItemQuantity(
                brandExternalId,
                cartId,
                cartItemId,
                new UpdateCartItemQuantityRequest(0)
        );

        assertEquals(0, response.get("quantity"));
        verify(this.cartItemRepository).delete(cartItem);
        verify(this.cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void changeCartItemQuantityUpdatesItemWhenStockIsAvailable() {
        String brandExternalId = "brand-1";
        UUID customerId = UUID.randomUUID();
        UUID brandId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        UUID cartItemId = UUID.randomUUID();

        CustomerProfile customer = new CustomerProfile();
        customer.setId(customerId);
        customer.setUsername("moustafa");

        Brand brand = new Brand();
        brand.setId(brandId);
        brand.setBrandName("Style Hub");
        brand.setBrandEmail("brand@test.com");

        Product product = new Product();
        product.setProductNameEn("Sneaker");

        ProductColor productColor = new ProductColor();
        productColor.setProduct(product);

        ProductVariant variant = new ProductVariant();
        variant.setProductColor(productColor);
        variant.setSku("SKU-1");
        variant.setStock(5);

        Cart cart = new Cart();
        cart.setId(cartId);
        cart.setCartStatus(CartStatus.ACTIVE);
        cart.setCustomer(customer);
        cart.setBrand(brand);

        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cartItem.setCart(cart);
        cartItem.setProductVariant(variant);
        cartItem.setQuantity(1);
        cartItem.setPrice(BigDecimal.TEN);

        when(this.currentUserProvider.externalId()).thenReturn("customer-external-id");
        when(this.customerProfileService.findCustomerProfileByExternalUserId("customer-external-id")).thenReturn(customer);
        when(this.brandService.findBrandByExternalId(brandExternalId)).thenReturn(brand);
        when(this.cartRepository.findCartByCartStatusAndCustomer_IdAndBrand_Id(CartStatus.ACTIVE, customerId, brandId))
                .thenReturn(Optional.of(cart));
        when(this.cartItemRepository.findByIdAndCart_Id(cartItemId, cartId)).thenReturn(Optional.of(cartItem));
        when(this.cartItemRepository.save(cartItem)).thenReturn(cartItem);

        Map<String, Integer> response = this.customerCartService.changeCartItemQuantity(
                brandExternalId,
                cartId,
                cartItemId,
                new UpdateCartItemQuantityRequest(4)
        );

        assertEquals(4, response.get("quantity"));
        assertEquals(4, cartItem.getQuantity());
        verify(this.cartItemRepository).save(cartItem);
    }
}
