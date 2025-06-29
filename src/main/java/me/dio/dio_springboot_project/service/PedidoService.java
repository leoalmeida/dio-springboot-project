package me.dio.dio_springboot_project.service;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import me.dio.dio_springboot_project.domain.model.ItemPedido;
import me.dio.dio_springboot_project.domain.model.Pedido;

/**
 * Serviço para gerenciamento de pedidos.
 */
public interface PedidoService {
    
    Pedido criarPedido(String customerId, List<ItemPedido> items);
    
    Optional<Pedido> buscarPedidoPorId(String id);
    
    Optional<Pedido> buscarPedidoPorNumero(String PedidoNumber);
    
    List<Pedido> buscarTodosPedidos();
    
    List<Pedido> buscarPedidosPorIdCliente(String customerId);
    
    void incluirItemAoPedido(String idPedido, ItemPedido item);
    
    void removerItemDoPedido(String idPedido, String idItemPedido);
    
    void alterarItemPedido(String idPedido, ItemPedido item);
    
    BigDecimal calcularValorTotalPedido(String idPedido);
    
    void finalizarPedido(String idPedido);
    
    void cancelarPedido(String idPedido);
}