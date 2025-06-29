package me.dio.dio_springboot_project.component.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import me.dio.dio_springboot_project.core.util.ObjectsValidator;
import me.dio.dio_springboot_project.domain.model.Cliente;
import me.dio.dio_springboot_project.domain.model.ItemPedido;
import me.dio.dio_springboot_project.domain.model.Pedido;
import me.dio.dio_springboot_project.domain.model.Produto;
import me.dio.dio_springboot_project.service.PedidoService;
import me.dio.dio_springboot_project.service.ProdutoService;
import me.dio.dio_springboot_project.service.impl.PedidoServiceImpl;
import me.dio.dio_springboot_project.service.impl.ProdutoServiceImpl;

@DataMongoTest
@ActiveProfiles("test")
public class PedidoServiceImplIntegrationTest {

    @TestConfiguration
    static class PedidoServiceImplTestContextConfiguration {
        @Bean
        public PedidoService pedidoService() {
            return new PedidoServiceImpl();
        }
        @Bean
        public ProdutoService produtoService() {
            return new ProdutoServiceImpl();
        }
        @Bean 
        public ObjectsValidator<ItemPedido> validadorDeItem(){
            return new ObjectsValidator<ItemPedido>();
        };

        @Bean 
        public ObjectsValidator<Produto> validadorDeProduto(){
            return new ObjectsValidator<Produto>();
        };
    }

    @Autowired
    private PedidoService pedidoService;
    
    @Autowired
    private ProdutoService produtoService;
    
    @Autowired 
    private MongoTemplate mongoTemplate;

    Cliente cliente;
    Pedido pedido;
    Produto produto;
    ItemPedido itemPedido;

    @BeforeEach
    public void setUp() {
        mongoTemplate.dropCollection(Cliente.class);
        mongoTemplate.dropCollection(Produto.class);
        mongoTemplate.dropCollection(Pedido.class);
        mongoTemplate.dropCollection(ItemPedido.class);
        // Cria cliente para testes
        cliente = new Cliente();
        cliente.setId(UUID.randomUUID().toString());
        cliente.setNome("João Silva");
        cliente.setEmail("joao.silva@example.com");
        cliente.setTelefone("(11) 99999-1111");
        cliente.setPedidos(new ArrayList<>());
        
        // Cria produto para testes
        produto = new Produto();
        produto.setId(UUID.randomUUID().toString());
        produto.setNome("Produto 1");
        produto.setDescricao("Descrição do Produto 1");
        produto.setPreco(new BigDecimal("10.00"));
        produto.setEstoque(100);
        produto.setSku("SKU001");
        
        // Cria pedido para testes
        pedido = new Pedido();
        pedido.setId(UUID.randomUUID().toString());
        pedido.setNumeroPedido("ORD-150001");
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setCliente(cliente);
        pedido.setItemsPedido(new ArrayList<>());
        pedido.setValorTotalPedido(BigDecimal.ZERO);


        // Cria item de pedido para testes
        itemPedido = new ItemPedido();
        itemPedido.setId(UUID.randomUUID().toString());
        itemPedido.setProduto(produto);
        itemPedido.setQuantidade(2);
        itemPedido.setPrecoUnitario(new BigDecimal("10.00"));
        itemPedido.setSubtotal(new BigDecimal("20.00"));
        
        pedido.incluirItemPedido(itemPedido); // Adiciona item ao pedido do cliente
        cliente.incluirPedido(pedido);// Adiciona pedido à lista do cliente
        
        mongoTemplate.save(cliente);
        mongoTemplate.save(pedido);
        mongoTemplate.save(itemPedido);
        mongoTemplate.save(produto);
    }

    @AfterEach
    public void coolDown() {
        mongoTemplate.remove(itemPedido);
        mongoTemplate.remove(pedido);
        mongoTemplate.remove(cliente);
        mongoTemplate.remove(produto);
    }

    @Test
    public void testCreatePedido() {
        // Cria lista de itens para o pedido
        List<ItemPedido> items = new ArrayList<>();

        ItemPedido newItem = new ItemPedido();
        newItem.setProduto(produto);
        newItem.setQuantidade(3);
        newItem.setPrecoUnitario(new BigDecimal("10.00"));
        items.add(newItem);

        // Executa o método
        Pedido createdPedido = pedidoService.criarPedido(cliente.getId(), items);
        
        // Verifica o resultado
        assertNotNull(createdPedido,"Pedido criado não deveria ser nulo");
        assertNotNull(createdPedido.getId(),"Pedido criado deveria ter um ID");
        assertEquals(cliente.getId(), createdPedido.getCliente().getId(),"Pedido deveria ter o cliente correto");
        assertEquals( 1, createdPedido.getItemsPedido().size(),"Pedido deveria ter 1 item");

        // Verifica se o pedido foi realmente salvo no banco de dados
        Optional<Pedido> foundPedido = pedidoService.buscarPedidoPorId(createdPedido.getId());
        assertTrue(foundPedido.isPresent(),"Pedido deveria ser encontrado no banco de dados");
    }

    @Test
    public void testFindPedidoById() {
        // Executa o método
        Optional<Pedido> foundPedido = pedidoService.buscarPedidoPorId(pedido.getId());

        // Verifica o resultado
        assertTrue(foundPedido.isPresent(), "Pedido deveria ser encontrado");
        assertEquals(pedido.getId(), foundPedido.get().getId(),
                "Pedido encontrado deveria ter o ID correto" );
        assertEquals("ORD-150001", foundPedido.get().getNumeroPedido(),
                    "Pedido encontrado deveria ter o número correto");
    }

    @Test
    public void testFindPedidoByNumber() {
        // Executa o método
        Optional<Pedido> foundPedido = pedidoService.buscarPedidoPorNumero("ORD-150001");

        // Verifica o resultado
        assertTrue(foundPedido.isPresent(),"Pedido deveria ser encontrado");
        assertEquals("ORD-150001", foundPedido.get().getNumeroPedido(),
                    "Pedido encontrado deveria ter o número correto");
    }

    @Test
    public void testFindAllPedidos() {
        // Executa o método
        List<Pedido> pedidos = pedidoService.buscarTodosPedidos();

        // Verifica o resultado
        assertEquals(1, pedidos.size(),"Deveria encontrar 1 pedido");
        assertEquals("ORD-150001", pedidos.get(0).getNumeroPedido(),
                    "Pedido encontrado deveria ter o número correto");
    }

    @Test
    public void testFindPedidosByCustomerId() {
        // Executa o método
        List<Pedido> pedidos = pedidoService.buscarPedidosPorIdCliente(cliente.getId());

        // Verifica o resultado
        assertEquals(1, pedidos.size(),"Deveria encontrar 1 pedido");
        assertEquals( "ORD-150001", pedidos.get(0).getNumeroPedido(), "Pedido encontrado deveria ter o número correto");
    }

    @Test
    public void testAddItemToPedido() {
        // Cria um novo item para adicionar
        ItemPedido newItem = new ItemPedido();
        newItem.setProduto(produto);
        newItem.setQuantidade(1);
        newItem.setPrecoUnitario(new BigDecimal("10.00"));

        // Executa o método
        pedidoService.incluirItemAoPedido(pedido.getId(), newItem);

        // Verifica se o item foi adicionado ao pedido
        Optional<Pedido> pedidoAtualizado = pedidoService.buscarPedidoPorId(pedido.getId());
        assertTrue(pedidoAtualizado.isPresent(), "Pedido deveria ser encontrado");
        assertEquals(2, pedidoAtualizado.get().getItemsPedido().size(), "Pedido deveria ter 2 itens");
    }

    @Test
    public void testRemoveItemFromPedido() {
        // Executa o método
        pedidoService.removerItemDoPedido(pedido.getId(), itemPedido.getId());

        // Verifica se o item foi removido do pedido
        Optional<Pedido> pedidoAtualizado = pedidoService.buscarPedidoPorId(pedido.getId());
        assertTrue(pedidoAtualizado.isPresent(), "Pedido deveria ser encontrado");
        assertEquals(0,pedidoAtualizado.get().getItemsPedido().size(),"Pedido não deveria ter itens");
    }

    @Test
    public void testUpdateItemPedido() {
        // Modifica o Item do pedido
        ItemPedido itemPedidoAutualizado = new ItemPedido();
        itemPedidoAutualizado.updateData(itemPedido);
        itemPedidoAutualizado.setId(itemPedido.getId());
        itemPedidoAutualizado.setQuantidade(5);
        itemPedidoAutualizado.setPrecoUnitario(new BigDecimal("12.00"));

        // Executa o método
        pedidoService.alterarItemPedido(pedido.getId(), itemPedidoAutualizado);

        // Verifica se o item foi atualizado
        Optional<Pedido> pedidoAtualizado = pedidoService.buscarPedidoPorId(itemPedidoAutualizado.getPedido().getId());
        assertTrue(pedidoAtualizado.isPresent(), "Pedido deveria ser encontrado");
        assertEquals(pedido.getId(), pedidoAtualizado.get().getId(),"Item deveria ter o mesmo ID");
        assertEquals(pedido.getItemsPedido().size(), pedidoAtualizado.get().getItemsPedido().size(),"Item deveria ter o mesmo ID");

        assertEquals(5, (int) pedidoAtualizado.get().getItemsPedido().get(0).getQuantidade(),"Item deveria ter a quantidade atualizada");
        assertEquals(new BigDecimal("12.00"), pedidoAtualizado.get().getItemsPedido().get(0).getPrecoUnitario(),"Item deveria ter o preço unitário atualizado");
    }

    @Test
    public void testCalculatePedidoTotal() {
        // Executa o método
        BigDecimal total = pedidoService.calcularValorTotalPedido(pedido.getId());

        // Verifica o resultado
        assertEquals(new BigDecimal("20.00"), total,"Valor total deveria ser 20.00");
    }

    @Test
    public void testFinalizePedido() {
        // Executa o método
        pedidoService.finalizarPedido(pedido.getId());

        // Verifica se o pedido foi finalizado
        Optional<Pedido> finalizedPedido = pedidoService.buscarPedidoPorId(pedido.getId());
        assertTrue(finalizedPedido.isPresent(),"Pedido deveria ser encontrado");
        assertEquals( new BigDecimal("20.00"), finalizedPedido.get().getValorTotalPedido(),"Valor total do pedido deveria ser 20.00");

        // Verifica se o estoque do produto foi atualizado
        Optional<Produto> produtoAtualizado = produtoService.buscarProdutoPorId(produto.getId());
        assertTrue(produtoAtualizado.isPresent(), "Produto deveria ser encontrado");
        assertEquals(98, produtoAtualizado.get().getEstoque().intValue(),
                        "Estoque do produto deveria ser atualizado");
    }

    @Test
    public void testCancelPedido() {
        // Executa o método
        pedidoService.cancelarPedido(pedido.getId());

        // Verifica se o pedido foi cancelado
        Optional<Pedido> pedidoCancelado = pedidoService.buscarPedidoPorId(pedido.getId());
        assertTrue(pedidoCancelado.isPresent(), "Pedido deveria ser encontrado");

        assertEquals("CANCELADO", pedidoCancelado.get().getStatus().name(), "Status do pedido deveria ser CANCELADO");

        Optional<Produto> produtoAtualizado = produtoService.buscarProdutoPorId(produto.getId());
        assertTrue(produtoAtualizado.isPresent(), "Produto deveria ser encontrado");
        assertEquals(100, (int) produtoAtualizado.get().getEstoque(), 
                    "Estoque do produto deveria ser restaurado");
    }

    @Test
    public void testCreatePedidoWithInvalidCustomerId() {
        // Executa o método - deve lançar IllegalArgumentException
        
        Throwable  throwable  = 
                assertThrows(IllegalArgumentException.class, () ->{
                    pedidoService.criarPedido(UUID.randomUUID().toString(), new ArrayList<>());
                });
        
        assertEquals(IllegalArgumentException.class, throwable.getClass());
    }

    @Test
    public void testPedidoNumberUniqueness() {
        // Cria dois pedidos no mesmo dia
        Pedido pedido1 = pedidoService.criarPedido(cliente.getId(), new ArrayList<>());
        Pedido pedido2 = pedidoService.criarPedido(cliente.getId(), new ArrayList<>());

        // Verifica se os números dos pedidos são diferentes
        assertNotEquals(pedido1.getNumeroPedido(), pedido2.getNumeroPedido(),
                        "Os números dos pedidos deveriam ser diferentes");
    }

    @Test
    public void testNegativeQuantityInItemPedido() {
        // Cria um item com quantidade negativa
        ItemPedido negativeItem = new ItemPedido();
        negativeItem.setProduto(produto);
        negativeItem.setQuantidade(-5); // Quantidade negativa
        negativeItem.setPrecoUnitario(new BigDecimal("10.00"));

        // Tenta adicionar o item ao pedido
        Throwable  throwable  = 
                assertThrows(IllegalArgumentException.class, () ->{
                    pedidoService.incluirItemAoPedido(pedido.getId(), negativeItem);
                });
        assertEquals(IllegalArgumentException.class, throwable.getClass());
    }

}