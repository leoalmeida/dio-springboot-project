package me.dio.dio_springboot_project.integration.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import me.dio.dio_springboot_project.domain.model.Cliente;
import me.dio.dio_springboot_project.domain.model.ItemPedido;
import me.dio.dio_springboot_project.domain.model.Pedido;
import me.dio.dio_springboot_project.domain.model.Produto;
import me.dio.dio_springboot_project.domain.repository.PedidoRepository;
import me.dio.dio_springboot_project.integration.base.TestFactory;


//@ExtendWith(SpringExtension.class)
@DataMongoTest
@ActiveProfiles("test")
public class PedidoRepositoryIntegrationTest extends TestFactory{

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired 
    private MongoTemplate mongoTemplate;

    Pedido pedido1;
    Pedido pedido2;
    Pedido pedido3;
    Produto produto1;

    
    @BeforeEach
    public void setUp() {
        
        // Criar pedidos
        pedido1 = gerarPedido();
        pedido2 = gerarPedido();
        pedido3 = gerarPedido();

        mongoTemplate.insertAll(Arrays.asList(pedido1,pedido2,pedido3));
    }

    @AfterEach
    void clean() {
        mongoTemplate.remove(pedido1);
        mongoTemplate.remove(pedido2);
        mongoTemplate.remove(pedido3);
    }

    @Test
    public void shouldBeNotEmpty() {
        assertTrue(pedidoRepository.findAll().size()>0);
    }

    @Test
    void dadoPedido_quandoCriarPedido_entaoPedidoPersistido() {
        // given
        Produto produto1 = new Produto();
        produto1.setId(UUID.randomUUID().toString());
        produto1.setNome("Produto 1");
        produto1.setDescricao("Descrição do Produto 1");
        produto1.setPreco(new BigDecimal("10.00"));
        produto1.setEstoque(100);
        produto1.setSku("SKU001");

        ItemPedido itemPedido1 = new ItemPedido();
        itemPedido1.setId(UUID.randomUUID().toString());
        itemPedido1.setProduto(produto1);
        itemPedido1.setQuantidade(3);
        itemPedido1.setPrecoUnitario(new BigDecimal("10.00"));
        
        Pedido pedido1 = new Pedido();
        pedido1.setId(UUID.randomUUID().toString());
        pedido1.setNumeroPedido("ORD-160001");
        pedido1.setDataPedido(LocalDateTime.now());
        pedido1.setCliente(Cliente.builder()
                .id(UUID.randomUUID().toString())
                .nome("João Silva")
                .build());
        pedido1.incluirItemPedido(itemPedido1);
        
        // when
        pedidoRepository.save(pedido1);

        // then
        Optional<Pedido> retrievedPedido = pedidoRepository.findById(pedido1.getId());
        assertTrue(retrievedPedido.isPresent());
        assertEquals("ORD-160001", retrievedPedido.get().getNumeroPedido());
    }
}
