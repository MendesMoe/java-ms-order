package com.postech.msorders.usecase;

import com.postech.msorders.entity.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class OrderUseCaseTest {

    private RestTemplate restTemplate;
    private OrderUseCase orderUseCase;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        orderUseCase = new OrderUseCase();
    }

    @Test
    void testFindCustomer_CustomerExists_ReturnsTrue() {
        String idCustomer = "af80d930-495c-483d-a78b-536bf414762c";
        String url = "http://localhost:8081/customers/" + idCustomer;
        ResponseEntity<String> response = new ResponseEntity<>("", HttpStatus.OK);

        when(restTemplate.getForEntity(url, String.class)).thenReturn(response);

        boolean result = orderUseCase.findCustomer(idCustomer);

        assertThat(result).isTrue();
    }

    @Test
    void testFindCustomer_CustomerNotFound_ReturnsFalse() {
        // Arrange
        Order order = new Order();
        OrderUseCase orderUseCase = new OrderUseCase();

        // Act & Assert
        assertThrows(HttpClientErrorException.class, () -> orderUseCase.validateInsertOrder(order));
    }
}
