package me.dio.dio_springboot_project.integration.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import me.dio.dio_springboot_project.domain.model.Cliente;
import me.dio.dio_springboot_project.domain.repository.ClienteRepository;
import me.dio.dio_springboot_project.integration.base.TestFactory;

//@ExtendWith(SpringExtension.class)
@DataMongoTest
@ActiveProfiles("test")
public class ClienteRepositoryIntegrationTest extends TestFactory{

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired 
    private MongoTemplate mongoTemplate;
    
    Cliente cliente1;
    Cliente cliente2;
    Cliente cliente3;

    @BeforeEach
    public void setUp() {
        // Criar pedidos
        cliente1 = gerarCliente("João Silva", "(11) 99999-1111");
        cliente2 = gerarCliente("Maria Santos", "(21) 99999-2222");
        cliente3 = gerarCliente("Pedro Oliveira", "(31) 99999-3333");

        //Pedido pedido3 = 

        mongoTemplate.insertAll(Arrays.asList(cliente1,cliente2,cliente3));
    }

    @AfterEach
    void clean() {
        mongoTemplate.remove(cliente1);
        mongoTemplate.remove(cliente2);
        mongoTemplate.remove(cliente3);
        //mongoTemplate.remove(pedido);
    }

    @Test
    public void shouldBeNotEmpty() {
        assertTrue(clienteRepository.findAll().size()>0);
    }

    @Test
    void dadoCliente_quandoCriarCliente_entaoClientePersistido() {
        // given
        Cliente cliente4 = gerarCliente("Joaquim Nabuco", "(41) 99999-4444");

        // when
        clienteRepository.save(cliente4);

        // then
        Optional<Cliente> retrievedCliente = clienteRepository.findById(cliente4.getId());
        assertTrue(retrievedCliente.isPresent());
        assertEquals(cliente4.getId(), retrievedCliente.get().getId());
        assertEquals("Joaquim Nabuco", retrievedCliente.get().getNome());
    }
}
