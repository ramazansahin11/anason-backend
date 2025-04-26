package ramazan.sahin.ecommerce.service.impl;

import jakarta.persistence.EntityNotFoundException;


import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ramazan.sahin.ecommerce.dto.OrderDTO;
import ramazan.sahin.ecommerce.dto.OrderItemDTO;
import ramazan.sahin.ecommerce.entity.*;
import ramazan.sahin.ecommerce.exception.BadRequestException;
import ramazan.sahin.ecommerce.repository.*;
import ramazan.sahin.ecommerce.service.OrderService;
import ramazan.sahin.ecommerce.service.StripeService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final StripeService stripeService;
    

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            UserRepository userRepository,
                            AddressRepository addressRepository,
                            ProductRepository productRepository,
                            StripeService stripeService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.stripeService = stripeService;
    }

    @Override
    public OrderDTO createOrder(OrderDTO orderDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
    
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    
        Address shippingAddress = addressRepository.findById(orderDto.getShippingAddressId())
                .orElseThrow(() -> new EntityNotFoundException("Shipping address not found"));
    
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setStatus(Order.Status.PENDING);
    
        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new java.util.ArrayList<>();
    
        // 🔥 Klasik for döngüsü ile topluyoruz
        for (OrderItemDTO itemDto : orderDto.getOrderItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found: " + itemDto.getProductId()));
    
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setUnitPrice(itemDto.getUnitPrice());
            orderItem.setOrder(order);
    
            // Toplam fiyatı hesaplıyoruz
            total = total.add(itemDto.getUnitPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
    
            orderItems.add(orderItem); // Listeye ekliyoruz
        }
    
        order.setTotalPrice(total);
        order.setOrderItems(orderItems);
    
        Order savedOrder = orderRepository.save(order);
    
        return mapToDto(savedOrder);
    }
    @Override
public List<OrderDTO> getMyOrders() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

    List<Order> orders = orderRepository.findByUserId(user.getId());

    return orders.stream()
            .map(this::mapToDto)
            .toList();
}


    

    @Override
    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !order.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not authorized to view this order.");
        }

        return mapToDto(order);
    }

    @Override
    public List<OrderDTO> getAllOrdersForUser(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
public OrderDTO cancelOrder(Long orderId) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName(); // Şu anda login olan kullanıcının email'ini alıyoruz

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new EntityNotFoundException("Order not found"));

    // 🔥 Kullanıcı sadece kendi siparişini iptal edebilir
    if (!order.getUser().getEmail().equals(email)) {
        throw new AccessDeniedException("You are not authorized to cancel this order.");
    }

    // 🔥 Sipariş kargoya verildiyse veya teslim edildiyse iptal edemez
    if (order.getStatus() == Order.Status.SHIPPED || order.getStatus() == Order.Status.DELIVERED) {
        throw new BadRequestException("Cannot cancel order after it is shipped or delivered.");
    }

    // 🔥 Sipariş durumunu CANCELLED yapıyoruz
    order.setStatus(Order.Status.CANCELLED);

    Order updatedOrder = orderRepository.save(order);

    return mapToDto(updatedOrder);
}


    

    @Override
    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !order.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not authorized to delete this order.");
        }

        orderRepository.delete(order);
    }
    @Override
public OrderDTO updateOrderStatus(Long orderId, String newStatus) {
    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new EntityNotFoundException("Order not found"));

    // 🔥 Enum kontrolü
    try {
        Order.Status statusEnum = Order.Status.valueOf(newStatus.toUpperCase());
        order.setStatus(statusEnum);
    } catch (IllegalArgumentException e) {
        throw new RuntimeException("Invalid order status: " + newStatus);
    }

    Order updatedOrder = orderRepository.save(order);

    return mapToDto(updatedOrder);
}


    // DTO-Entity Mapper
    private OrderDTO mapToDto(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUser().getId());
        dto.setShippingAddressId(order.getShippingAddress().getId());
        dto.setStatus(order.getStatus().name());
        dto.setTotalPrice(order.getTotalPrice());

        List<OrderItemDTO> itemDtos = order.getOrderItems().stream().map(item -> {
            OrderItemDTO itemDto = new OrderItemDTO();
            itemDto.setProductId(item.getProduct().getId());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setUnitPrice(item.getUnitPrice());
            return itemDto;
        }).collect(Collectors.toList());

        dto.setOrderItems(itemDtos);
        return dto;
    }

    @Override
    public void completeOrderIfPaymentSucceeded(Long orderId, String paymentIntentId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));

        // Ödeme başarılıysa sipariş durumunu güncelle
        if (stripeService.isPaymentSucceeded(paymentIntentId)) {
            order.setStatus(Order.Status.DELIVERED);
            orderRepository.save(order);
        } else {
            throw new BadRequestException("Payment not succeeded for order ID: " + orderId);
        }
    }
}
