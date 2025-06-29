package me.dio.dio_springboot_project.component.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


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
import me.dio.dio_springboot_project.domain.model.Pedido;
import me.dio.dio_springboot_project.service.ClienteService;
import me.dio.dio_springboot_project.service.impl.ClienteServiceImpl;

//@ExtendWith(SpringExtension.class)
@DataMongoTest
@ActiveProfiles("test")
public class ClienteServiceImplIntegrationTest {

   @TestConfiguration
    static class ClienteServiceImplTestContextConfiguration {
        @Bean
        public ClienteService clienteService() {
            return new ClienteServiceImpl();
        }
        @Bean 
        public ObjectsValidator<Cliente> validadorDeCliente(){
            return new ObjectsValidator<Cliente>();
        };
    }

    @Autowired
    private ClienteService clienteService;

    @Autowired 
    private MongoTemplate mongoTemplate;

    Cliente cliente1;
    Cliente cliente2;
    Cliente cliente3;
    Pedido pedido;

    @BeforeEach
    public void setUp() {
        mongoTemplate.dropCollection(Cliente.class);
        mongoTemplate.dropCollection(Pedido.class);
        // Prepara Mock cliente1
        cliente1 = new Cliente();
        cliente1.setId(UUID.randomUUID().toString());
        cliente1.setNome("João Silva");
        cliente1.setEmail("joao.silva@example.com");
        cliente1.setTelefone("(11) 99999-1111");
                            
        // Prepara Mock cliente2
        cliente2 = new Cliente();
        cliente2.setId(UUID.randomUUID().toString());
        cliente2.setNome("Maria Santos");
        cliente2.setEmail("maria.santos@example.com");
        cliente2.setTelefone("(11) 99999-2222");
        
        pedido = new Pedido();
        pedido.setId(UUID.randomUUID().toString());
        pedido.setNumeroPedido("ORD-150001");
        cliente2.incluirPedido(pedido);

        // Prepara Mock cliente3
        cliente3 = new Cliente();
        cliente3.setId(UUID.randomUUID().toString());
        cliente3.setNome("Pedro Oliveira");
        cliente3.setEmail("pedro.oliveira@example.com");
        cliente3.setTelefone("(11) 99999-3333");

        mongoTemplate.save(cliente1);
        mongoTemplate.save(cliente2);
        mongoTemplate.save(pedido);
        mongoTemplate.save(cliente3);
                                
    }

    @AfterEach
    public void coolDown() {
        mongoTemplate.remove(pedido);
        mongoTemplate.remove(cliente1);
        mongoTemplate.remove(cliente2);
        mongoTemplate.remove(cliente3);
    }

    @Test
    public void quandoClienteValido_entaoClienteDeveSerSalvo() {
        // Cria novo cliente
        Cliente newCliente = new Cliente();
        newCliente.setNome("Novo Cliente");
        newCliente.setEmail("novo.cliente@example.com");
        newCliente.setTelefone("(11) 99999-4444");
        newCliente.setPedidos(new ArrayList<>());

        // Executa o serviço testado
        Cliente clienteSalvo = clienteService.criarCliente(newCliente);

        // Verifica o resultado
        assertNotNull(clienteSalvo, "Novo cliente não deve retornar nulo");
        assertNotNull(clienteSalvo.getId(), "Novo cliente deve ter um ID válido");
        assertEquals("Novo Cliente", clienteSalvo.getNome(), "Novo cliente deve ter o nome correto");
        assertEquals("novo.cliente@example.com", clienteSalvo.getEmail(), "Novo cliente deve ter o e-mail correto");

        // Verifica se o cliente foi realmente salvo no banco de dados
        Optional<Cliente> foundCliente = clienteService.buscarClientePorId(clienteSalvo.getId());
        assertTrue(foundCliente.isPresent(), "Cliente deveria ser encontrado no banco de dados");
    }

    @Test
    public void quandoClienteComEmailInvalido_entaoErroDeveSerRetornado() {
        // Novo cliente com e-mail inválido
        Cliente mockEmailInvalido = new Cliente();
        mockEmailInvalido.setNome("Cliente Inválido");
        mockEmailInvalido.setEmail("email-invalido");
        mockEmailInvalido.setTelefone("(11) 99999-5555");
        mockEmailInvalido.setPedidos(new ArrayList<>());

        // Executa o método
        Throwable  throwable  = 
            assertThrows(IllegalArgumentException.class, () ->{
                // Executa o método - deveria falhar para emails inválidos
                clienteService.criarCliente(mockEmailInvalido);
        
                // Uma exceção deveria ter sido lançada ao salvar um cliente com email inválido
                assertTrue(true, "Exception não lançada ao salvar cliente com email inválido");
            });

        
        // O teste deve falhar caso não seja gerada uma exception
        assertEquals(IllegalArgumentException.class, throwable.getClass(), 
                            "Exception gerada não é relacionada ao erro de email:" + throwable.getMessage());
        assertEquals("Email inválido: " + mockEmailInvalido.getEmail(), throwable.getMessage());
    }

    @Test
    public void fornecidoIdCliente_aoConsultarClientes_entaoRetornaClienteComSucesso() {
        // Executa o método
        Optional<Cliente> foundCliente = clienteService.buscarClientePorId(cliente1.getId());

        // Verifica o resultado
        assertTrue(foundCliente.isPresent(), "Cliente deveria ser encontrado");
        assertEquals(cliente1.getId(), foundCliente.get().getId(), "Cliente encontrado deveria ter o ID correto");
        assertEquals("João Silva", foundCliente.get().getNome(), "Cliente encontrado deveria ter o nome correto");
    }

    @Test
    public void fornecidoEmailCliente_aoConsultarClientes_entaoRetornaClienteComSucesso() {
        // Executa o método
        Optional<Cliente> foundCliente = clienteService.buscarClientePorEmail("joao.silva@example.com");

        // Verifica o resultado
        assertTrue(foundCliente.isPresent(), "Cliente deveria ser encontrado");
        assertEquals("joao.silva@example.com", foundCliente.get().getEmail(), "Cliente encontrado deveria ter o e-mail correto");
        assertEquals("João Silva", foundCliente.get().getNome(), "Cliente encontrado deveria ter o nome correto");
    }

    @Test
    public void aoConsultarTodosClientes_entaoRetornaListaClientesComSucesso() {
        // Executa o método
        List<Cliente> clientes = clienteService.buscarTodosClientes();

        // Verifica o resultado
        assertEquals(3, clientes.size(), "Deveria encontrar 3 clientes");
    }

    @Test
    public void fornecidoNomeCliente_aoConsultarClientes_entaoRetornaClienteComSucesso() {
        // Executa o método
        List<Cliente> clientes = clienteService.buscarClientesPorNome("Silva");

        // Verifica o resultado
        assertEquals(1, clientes.size(), "Deveria encontrar 1 cliente");
        assertEquals("João Silva", clientes.get(0).getNome(), "Cliente encontrado deveria ser João Silva");
    }

    @Test
    public void fornecidoInjecaoSQLNoNomeCliente_aoConsultarClientes_entaoRetornaException() {
        // Executa o método com uma tentativa de injeção SQL
    
        // Se ocorrer uma exceção, o teste deve falhar com uma mensagem clara
        // em vez de deixar a exceção se propagar
        
        assertDoesNotThrow(() ->{
            // Caso a consulta seja vulnerável, retornará todos os clientes
            List<Cliente> clientes = clienteService.buscarClientesPorNome("' OR '1'='1");
            // Verificamos se a consulta retornou uma lista vazia senão é uma falha
            assertTrue(clientes.isEmpty(), "A consulta não deveria ser vulnerável a injeção de SQL");       
        },"O teste deveria falhar com uma asserção e não com uma exceção.:");
    
    }

    @Test
    public void fornecidoClienteAtualizado_aoAlterarCliente_entaoRetornaSucesso() {
        Cliente clienteAutualizado = new Cliente();
        clienteAutualizado.updateData(cliente1);
        clienteAutualizado.setId(cliente1.getId());
        // Modifica o cliente
        clienteAutualizado.setNome("João Silva Atualizado");
        clienteAutualizado.setEmail("joao.atualizado@example.com");


        // Executa o método
        clienteService.alterarCliente(clienteAutualizado);

        // Verifica se o cliente foi atualizado no banco de dados
        Optional<Cliente> updatedCliente = clienteService.buscarClientePorId(cliente1.getId());
        assertTrue(updatedCliente.isPresent(), "Cliente deveria ser encontrado");
        assertEquals("João Silva Atualizado", updatedCliente.get().getNome(), "Nome do cliente deveria ser atualizado");
        assertEquals("joao.atualizado@example.com", updatedCliente.get().getEmail(), "E-mail do cliente deveria ser atualizado");
    }

    @Test
    public void testDeleteCliente() {
        // Executa o método
        clienteService.removerCliente(cliente1.getId());

        // Verifica se o cliente foi removido do banco de dados
        Optional<Cliente> deletedCliente = clienteService.buscarClientePorId(cliente1.getId());
        assertFalse(deletedCliente.isPresent(), "Cliente deveria ser removido");
    }

    @Test
    public void testDeleteClienteWithOrders() {
        // Executa o método
        clienteService.removerCliente(cliente2.getId());

        // Verifica se o cliente foi removido do banco de dados
        Optional<Cliente> deletedCliente = clienteService.buscarClientePorId(cliente2.getId());
        assertFalse(deletedCliente.isPresent(), "Cliente com pedidos deveria ser removido");
    }

    @Test
    public void testFindClientesWithOrders() {
        // Executa o método
        List<Cliente> clientes = clienteService.buscarClientesComPedidos();

        // Verifica o resultado
        assertEquals(1, clientes.size(), "Deveria encontrar 1 cliente com pedidos");
        assertEquals( "Maria Santos", clientes.get(0).getNome(), "Cliente encontrado deveria ser Maria Santos");
    }

    @Test
    public void testValidateClienteEmail() {
        // Testa e-mail válido
        boolean isValid = clienteService.validarEmailCliente("joao.silva@example.com");
        assertTrue(isValid, "E-mail deveria ser considerado válido");

        // Testa e-mail inválido
        isValid = clienteService.validarEmailCliente("email-invalido");
        assertFalse(isValid,"E-mail deveria ser considerado inválido");

        // Testa e-mail nulo
        isValid = clienteService.validarEmailCliente(null);
        assertFalse(isValid,"E-mail nulo deveria ser considerado inválido");

        // Testa e-mail vazio
        isValid = clienteService.validarEmailCliente("");
        assertFalse(isValid, "E-mail vazio deveria ser considerado inválido");
    }

    @Test
    public void testValidateClienteEmailWithSimplePattern() {
        // Testa e-mail com formato válido mas domínio inválido
        boolean isValid = clienteService.validarEmailCliente("joao.silva@dominio-invalido");
        assertFalse(isValid, "E-mail com domínio inválido não deveria ser aceito");

        // Testa e-mail com formato válido mas sem TLD
        isValid = clienteService.validarEmailCliente("joao.silva@dominio");
        assertFalse(isValid, "E-mail sem TLD não deveria ser aceito");
    }
}