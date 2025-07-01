package space.lasf.springboot_project.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import space.lasf.springboot_project.base.TestFactory;
import space.lasf.springboot_project.core.util.ObjectsValidator;
import space.lasf.springboot_project.domain.model.Cliente;
import space.lasf.springboot_project.domain.repository.ClienteRepository;
import space.lasf.springboot_project.service.impl.ClienteServiceImpl;

@ExtendWith(SpringExtension.class)
public class ClienteServiceTest extends TestFactory{

    @Mock
    private ClienteRepository repository;

    @Mock
    private ObjectsValidator<Cliente> validator;
    
    @InjectMocks
    private ClienteServiceImpl service;

    Cliente clienteEntity;
    Cliente clienteEntity2;
    Cliente clienteEntity3;
    Cliente pedido;
    List<Cliente> clientesAssets;

    @BeforeEach
    public void setUp() {
        clienteEntity = gerarCliente("João Silva","(11) 99999-1111");
        clienteEntity2 = gerarCliente("Maria Santos","(11) 99999-2222");
        clienteEntity3 = gerarCliente("Pedro Oliveira","(11) 99999-3333");
        clientesAssets = Arrays.asList(clienteEntity,clienteEntity2,clienteEntity3);
    }

    
    @Test
    public void testeCriacaoDeNovoCliente() {
        // Configura os mocks
        doReturn(clienteEntity).when(validator)
            .validate(clienteEntity);
        doReturn(clienteEntity).when(repository).save(any(Cliente.class));
        
        // Executa o método
        Cliente createdCliente = service.criarCliente(clienteEntity);

        // Verifica o resultado
        assertNotNull(createdCliente, "Cliente criado não deveria ser nulo");
        assertEquals(clienteEntity.getId(), createdCliente.getId(), "Cliente deveria ter o id correto");

        // Verifica se os métodos foram chamados
        verify(repository, atLeastOnce()).save(any(Cliente.class));
    }

    @Test
    public void testeCriacaoDeClienteComEmailInvalido() {
        clienteEntity.setEmail("email-invalido");

        // Chamada ao serviço que deve lançar uma IllegalArgumentException
        Throwable  throwable  = 
            assertThrows(IllegalArgumentException.class, () ->{
                // Executa o método - deveria falhar para clientes com email inválido
                service.criarCliente(clienteEntity);
        
                // Uma exceção deveria ter sido lançada ao salvar um cliente inválido
                assertTrue(true, "Exception não lançada ao salvar cliente com email inválido");
            });
        // O teste deve falhar caso não seja gerada uma exception
        assertEquals(IllegalArgumentException.class, throwable.getClass(), 
                            "Exception gerada sem relação ao email do cliente Inválido:" + throwable.getMessage());
        assertEquals("Email inválido: " + clienteEntity.getEmail(), throwable.getMessage());
    }

    @Test
    public void testeBuscarClientePorIdCliente() {
         // Configura os mocks
        doReturn(Optional.of(clienteEntity)).when(repository).findById(clienteEntity.getId());

        // Executa o método
        Optional<Cliente> foundCliente = service.buscarClientePorId(clienteEntity.getId());

        // Verifica o resultado
        assertTrue(foundCliente.isPresent(),"Cliente deveria ser encontrado");
        assertEquals(clienteEntity.getId(), foundCliente.get().getId(),
                "Cliente encontrado deveria ter o ID correto");

        // Verifica se o método foi chamado
        verify(repository, times(1)).findById(clienteEntity.getId());
    }

    
    @Test
    public void testeBuscarClientePorEmail() {
        // Configura o mock
        doReturn(Optional.of(clienteEntity)).when(repository).findByEmail(clienteEntity.getEmail());

        // Executa o método
        Optional<Cliente> foundCliente = service.buscarClientePorEmail(clienteEntity.getEmail());

        // Verifica o resultado
        assertTrue(foundCliente.isPresent(),"Cliente deveria ser encontrado");
        assertEquals(clienteEntity.getEmail(), foundCliente.get().getEmail(),"Cliente encontrado deveria ter o email correto");

        // Verifica se os métodos foram chamados
        verify(repository, times(1)).findByEmail(clienteEntity.getEmail());
    }

    @Test
    public void testeBuscarClientePorNome() {
        // Configura o mock
        doReturn(Arrays.asList(clienteEntity)).when(repository).findByNomeContaining(clienteEntity.getNome());

        // Executa o método
        List<Cliente> foundListClientes = service.buscarClientesPorNome(clienteEntity.getNome());

        // Verifica o resultado
        assertEquals(1,foundListClientes.size(),"Apenas 1 Cliente deveria ser encontrado");
        assertEquals(clienteEntity.getNome(), foundListClientes.get(0).getNome(),"Cliente encontrado deveria ter o nome correto");

        // Verifica se os métodos foram chamados
        verify(repository, times(1)).findByNomeContaining(clienteEntity.getNome());
    }

    @Test
    public void testeBucarTodosOsClientes() {
        // Configura o mock
        doReturn(clientesAssets).when(repository).findAll();

        // Executa o método
        List<Cliente> clientes = service.buscarTodosClientes();

        // Verifica o resultado
        assertEquals( clientesAssets.size(), clientes.size(), 
                "Deveria encontrar a quantidade correta de clientes");

        // Verifica se os métodos foram chamados
        verify(repository, times(1)).findAll();
    }

}