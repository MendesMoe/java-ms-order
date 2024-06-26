package com.postech.msorders.controller;

import com.postech.msorders.dto.OrderDTO;
import com.postech.msorders.entity.Order;
import com.postech.msorders.gateway.OrderGateway;
import com.postech.msorders.usecase.OrderUseCase;
import com.postech.msorders.utils.OutOfStockException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderGateway orderGateway;

    @PostMapping("")
    @Operation(summary = "Request for create a order", responses = {
            @ApiResponse(description = "Order created", responseCode = "201"),
            @ApiResponse(description = "Bad request", responseCode = "400"),
            @ApiResponse(description = "Customer not found", responseCode = "404"),
            @ApiResponse(description = "Product out of stock", responseCode = "412"),
    })
    public ResponseEntity<?> createOrder(@Validated @RequestBody OrderDTO orderDTO) {
        log.info("PostMapping - createOrder for customer [{}]", orderDTO.getIdCustomer());
        try {
            Order orderNew = new Order(orderDTO);
            OrderUseCase orderUseCase = new OrderUseCase();

            orderUseCase.validateInsertOrder(orderNew);
            orderUseCase.validateProductAvailability(orderNew);

            Order orderCreated = orderGateway.createOrder(orderNew);
            return new ResponseEntity<>(orderCreated, HttpStatus.CREATED);
        } catch (HttpClientErrorException enf) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(enf.getMessage());
        } catch (OutOfStockException ie ){
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(ie.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Request for list all orders", responses = {
            @ApiResponse(description = "Order's list", responseCode = "200"),
    })
    public ResponseEntity<List<Order>> listAllOrders() {
        log.info("GetAllOrders");
        List<Order> orders = orderGateway.listAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get only Order by ID", responses = {
            @ApiResponse(description = "The order by ID", responseCode = "200", content = @Content(schema = @Schema(implementation = Order.class))),
            @ApiResponse(description = "Order Not Found", responseCode = "404", content = @Content(schema = @Schema(type = "string", example = "Pedido não encontrado.")))
    })
    public ResponseEntity<?> findOrder(@PathVariable String id) {
        log.info("GetMapping - FindOrder");
        Order order = orderGateway.findOrder(id);
        if (order != null) {
            return new ResponseEntity<>( order , HttpStatus.OK);
        }
        return new ResponseEntity<>("Pedido não encontrado.", HttpStatus.NOT_FOUND);
    }
}
