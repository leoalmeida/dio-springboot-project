package space.lasf.springboot_project.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import space.lasf.springboot_project.core.util.ObjectsValidator;
import space.lasf.springboot_project.domain.model.Cliente;
import space.lasf.springboot_project.domain.model.ItemPedido;
import space.lasf.springboot_project.domain.model.Pedido;
import space.lasf.springboot_project.domain.model.Status;
import space.lasf.springboot_project.domain.model.Produto;
import space.lasf.springboot_project.domain.repository.ClienteRepository;
import space.lasf.springboot_project.domain.repository.ItemPedidoRepository;
import space.lasf.springboot_project.domain.repository.PedidoRepository;
import space.lasf.springboot_project.domain.repository.ProdutoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private ItemPedidoRepository itemPedidoRepository;
    @Mock
    private ObjectsValidator<ItemPedido> validadorDeItem;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    private Cliente cliente;
    private String clienteId;
    private Pedido pedido;
    private Produto produto1;
    private Produto produto2;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID().toString();
        cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setNome("Test Client");
        cliente.setEmail("test@client.com");

        // Setup for finalizarPedido tests
        String pedidoId = UUID.randomUUID().toString();
        pedido = new Pedido();
        pedido.setId(pedidoId);
        pedido.setCliente(cliente);

        produto1 = new Produto();
        produto1.setId(UUID.randomUUID().toString());
        produto1.setNome("Produto 1");
        produto1.setEstoque(100);
        produto1.setPreco(BigDecimal.TEN);

        produto2 = new Produto();
        produto2.setId(UUID.randomUUID().toString());
        produto2.setNome("Produto 2");
        produto2.setEstoque(50);
        produto2.setPreco(BigDecimal.valueOf(20));

        ItemPedido item1 = new ItemPedido();
        item1.setId(UUID.randomUUID().toString());
        item1.setProduto(produto1);
        item1.setQuantidade(2);
        item1.setPrecoUnitario(produto1.getPreco());
        item1.updateSubtotal();

        ItemPedido item2 = new ItemPedido();
        item2.setId(UUID.randomUUID().toString());
        item2.setProduto(produto2);
        item2.setQuantidade(3);
        item2.setPrecoUnitario(produto2.getPreco());
        item2.updateSubtotal();

        pedido.setItemsPedido(List.of(item1, item2));
        pedido.setStatus(Status.PENDENTE);
        pedido.setValorTotalPedido(pedido.calcularTotalPedido());
    }

    @Test
    void criarPedido_shouldThrowException_whenClientNotFound() {
        // Arrange
        when(clienteRepository.findById(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            pedidoService.criarPedido("non-existent-id", Collections.emptyList());
        });
    }

    @Test
    void criarPedido_shouldCreatePedido_whenClientExists() {
        // Arrange
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Pedido result = pedidoService.criarPedido(clienteId, Collections.emptyList());

        // Assert
        assertNotNull(result);
        verify(clienteRepository).save(cliente);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void finalizarPedido_shouldThrowException_whenPedidoNotFound() {
        // Arrange
        when(pedidoRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            pedidoService.finalizarPedido("non-existent-id");
        });
    }

    @Test
    void finalizarPedido_shouldUpdateProductStockAndPedidoStatus_whenSuccessful() {
        // Arrange
        when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));
        when(produtoRepository.findById(produto1.getId())).thenReturn(Optional.of(produto1));
        when(produtoRepository.findById(produto2.getId())).thenReturn(Optional.of(produto2));

        // Act
        pedidoService.finalizarPedido(pedido.getId());

        // Assert
        assertEquals(98, produto1.getEstoque()); // 100 - 2
        assertEquals(47, produto2.getEstoque()); // 50 - 3
        assertEquals(Status.FINALIZADO, pedido.getStatus());

        verify(produtoRepository, times(1)).saveAll(anyList());
        verify(pedidoRepository, times(1)).save(pedido);
    }

    @Test
    void finalizarPedido_shouldHandleMissingProductGracefully() {
        // Arrange
        // One product exists, the other one doesn't (e.g., deleted after order creation)
        when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));
        when(produtoRepository.findById(produto1.getId())).thenReturn(Optional.of(produto1));
        when(produtoRepository.findById(produto2.getId())).thenReturn(Optional.empty());

        // Act
        pedidoService.finalizarPedido(pedido.getId());

        // Assert
        // Stock is updated only for the existing product
        assertEquals(98, produto1.getEstoque());
        // Stock for the missing product is not touched (and no error is thrown)
        assertEquals(50, produto2.getEstoque());
        assertEquals(Status.FINALIZADO, pedido.getStatus());

        // saveAll should be called with a list containing only the updated product
        verify(produtoRepository, times(1)).saveAll(List.of(produto1));
        verify(pedidoRepository, times(1)).save(pedido);
    }

    @Test
    void cancelarPedido_shouldUpdateStatusToCancelado_whenPedidoExists() {
        // Arrange
        when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));

        // Act
        pedidoService.cancelarPedido(pedido.getId());

        // Assert
        assertEquals(Status.CANCELADO, pedido.getStatus());
        verify(pedidoRepository, times(1)).save(pedido);
    }

    @Test
    void cancelarPedido_shouldThrowException_whenPedidoNotFound() {
        // Arrange
        String nonExistentId = "non-existent-id";
        when(pedidoRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            pedidoService.cancelarPedido(nonExistentId);
        });
    }
}