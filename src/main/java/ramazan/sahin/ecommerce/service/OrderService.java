package ramazan.sahin.ecommerce.service;

import ramazan.sahin.ecommerce.dto.OrderDTO;
import java.util.List;


public interface OrderService {

    // Yeni sipariş oluşturma
    OrderDTO createOrder(OrderDTO orderDto);

    // Sipariş ID ile sipariş getirme
    OrderDTO getOrderById(Long orderId);

    // Kullanıcının kendi siparişlerini listeleme
    List<OrderDTO> getAllOrdersForUser(Long userId);

    // Sipariş durumunu güncelleme (örn: PENDING → SHIPPED)
    OrderDTO updateOrderStatus(Long orderId, String newStatus);

    // Siparişi silme etme
    void deleteOrder(Long orderId);

    //siparisi iptal etme
    OrderDTO cancelOrder(Long orderId);

    //siparis listeleme
    List<OrderDTO> getMyOrders();

    public void completeOrderIfPaymentSucceeded(Long orderId, String paymentIntentId);

    List<OrderDTO> getAllOrders(Long userId); // İSMİ de mantıklı olsun diye ufak sadeleştirelim

    List<OrderDTO> getOrdersByStatus(String status);

    
}
