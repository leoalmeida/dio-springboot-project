package space.lasf.springboot_project.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import space.lasf.springboot_project.domain.model.ItemPedido;
import space.lasf.springboot_project.domain.model.Pedido;
import space.lasf.springboot_project.domain.model.Produto;
import space.lasf.springboot_project.domain.repository.ClienteRepository;
import space.lasf.springboot_project.domain.repository.ItemPedidoRepository;
import space.lasf.springboot_project.domain.repository.PedidoRepository;
import space.lasf.springboot_project.domain.repository.ProdutoRepository;
import space.lasf.springboot_project.service.impl.PedidoServiceImpl;

@ExtendWith(SpringExtension.class)
public class PedidoServiceTest  extends TestFactory{

    @Mock
    private PedidoRepository pedidosRepository;

    @Mock
    private ClienteRepository clientesRepository;

    @Mock
    private ItemPedidoRepository itemPedidoRepository;

    @Mock
    private ProdutoRepository produtoRepository;
    
    @Mock
    private ObjectsValidator<Pedido> validadorDePedido;

    @Mock
    private ObjectsValidator<ItemPedido> validadorDeItem;
    
    @InjectMocks
    private PedidoServiceImpl service;

    private List<Pedido> pedidosAssets;
    private List<Cliente> clientesAssets;
    Cliente clienteEntity;
    Pedido pedidoEntity;
    ItemPedido itemPedidoEntity;
    Produto produtoEntity;

    @BeforeEach
    public void setUp() {

        clienteEntity = gerarCliente("João Silva","(11) 99999-1111");
        pedidoEntity = gerarPedido(clienteEntity);
        itemPedidoEntity = gerarItemPedido();
        produtoEntity = gerarProduto();

        // Criar clientes para testes        
        Cliente clienteEntity1 = gerarCliente("Manoel Nobrega","(91) 99999-8888");
            
        // Cria pedido para testes
        Pedido pedidoEntity1 = gerarPedido(clienteEntity);
        Pedido pedidoEntity2 = gerarPedido(clienteEntity);

        
        gerarItemPedido(pedidoEntity,produtoEntity);
    
        clientesAssets = Arrays.asList(clienteEntity,clienteEntity1);
        pedidosAssets = Arrays.asList(pedidoEntity,pedidoEntity1,pedidoEntity2);
    }


    @Test
    public void testeCriacaoDePedidoParaClienteValido() {
        // Cria um mock de Order para poder mockar os métodos de calculo 
        // Configura os mocks
        doReturn(Optional.of(clienteEntity)).when(clientesRepository)
            .findById(clienteEntity.getId());
        doReturn(itemPedidoEntity).when(validadorDeItem).validate(itemPedidoEntity);
        doReturn(pedidoEntity).when(pedidosRepository).save(any(Pedido.class));
        doReturn(clienteEntity).when(clientesRepository).save(any(Cliente.class));
        doReturn(itemPedidoEntity).when(itemPedidoRepository).save(any(ItemPedido.class));
        
        // Cria lista de itens para o pedido
        List<ItemPedido> items = new ArrayList<>();
        items.add(itemPedidoEntity);

        // Executa o método
        Pedido createdPedido = service.criarPedido(clienteEntity.getId(), items);

        // Verifica o resultado
        assertNotNull(createdPedido, "Pedido criado não deveria ser nulo");
        assertEquals(clienteEntity, createdPedido.getCliente(), "Pedido deveria ter o cliente correto");

        // Verifica se os métodos foram chamados
        verify(clientesRepository, times(1))
            .findById(clienteEntity.getId());
        // Verifica que persist foi chamado pelo menos uma vez para Pedido e uma vez para ItemPedido
        // Não verificamos o número exato de chamadas porque isso pode variar dependendo da implementação
        verify(pedidosRepository, atLeastOnce()).save(any(Pedido.class));
        verify(clientesRepository, atLeastOnce()).save(any(Cliente.class));
        verify(itemPedidoRepository, atLeastOnce()).save(any(ItemPedido.class));
        
    }

    @Test
    public void testeCriacaoDePedidoParaUmIdClienteInvalido() {
        String idClienteInvalido = "999";
        // Configura o mock para retornar um cliente vazio
        doReturn(Optional.empty()).when(pedidosRepository).findById(idClienteInvalido);

        // Chamada ao serviço que deve lançar uma IllegalArgumentException
        Throwable  throwable  = 
            assertThrows(IllegalArgumentException.class, () ->{
                // Executa o método - deveria falhar para id de cliente inválidos
                service.criarPedido(idClienteInvalido, new ArrayList<>());
        
                // Uma exceção deveria ter sido lançada ao salvar um pedido com cliente inválido
                assertTrue(true, "Exception não lançada ao salvar pedido com id de cliente inválido");
            });
        // O teste deve falhar caso não seja gerada uma exception
        assertEquals(IllegalArgumentException.class, throwable.getClass(), 
                            "Exception gerada sem relação ao Id do cliente Inválido:" + throwable.getMessage());
        assertEquals("Cliente não encontrado", throwable.getMessage());
    }

    @Test
    public void testeCriacaoDePedidoParaUmIdClienteNulo() {
        // Configura o mock para retornar um cliente vazio
        doReturn(Optional.empty()).when(pedidosRepository).findById(null);

        // Chamada ao serviço que deve lançar uma IllegalArgumentException
        Throwable  throwable  = 
            assertThrows(IllegalArgumentException.class, () ->{
                // Executa o método - deveria falhar para id de cliente inválidos
                service.criarPedido(null, new ArrayList<>());
        
                // Uma exceção deveria ter sido lançada ao salvar um pedido com cliente inválido
                assertTrue(true, "Exception não lançada ao salvar pedido com id de cliente inválido");
            });
        // O teste deve falhar caso não seja gerada uma exception
        assertEquals(IllegalArgumentException.class, throwable.getClass(), 
                            "Exception gerada sem relação ao Id do cliente nulo:" + throwable.getMessage());
        assertEquals("ID do cliente não pode ser nulo.", throwable.getMessage());
    }

    @Test
    public void testeBuscarPedidoPorIdPedidos() {
         // Configura os mocks
        doReturn(Optional.of(pedidoEntity)).when(pedidosRepository).findById("1");

        // Executa o método
        Optional<Pedido> foundPedido = service.buscarPedidoPorId("1");

        // Verifica o resultado
        assertTrue(foundPedido.isPresent(),"Pedido deveria ser encontrado");
        assertEquals(pedidoEntity.getId(), foundPedido.get().getId(),
                "Pedido encontrado deveria ter o ID correto");

        // Verifica se o método foi chamado
        verify(pedidosRepository, times(1)).findById("1");
    }

    @Test
    public void testeBuscarPedidoByNumber() {
        // Configura o mock
        doReturn(Optional.of(pedidoEntity)).when(pedidosRepository).findByNumeroPedido(pedidoEntity.getNumeroPedido());

        // Executa o método
        Optional<Pedido> foundPedido = service.buscarPedidoPorNumero(pedidoEntity.getNumeroPedido());

        // Verifica o resultado
        assertTrue(foundPedido.isPresent(),"Pedido deveria ser encontrado");
        assertEquals(pedidoEntity.getNumeroPedido(), foundPedido.get().getNumeroPedido(),"Pedido encontrado deveria ter o número correto");

        // Verifica se os métodos foram chamados
        verify(pedidosRepository, times(1)).findByNumeroPedido(pedidoEntity.getNumeroPedido());
    }

    @Test
    public void testeBucarTodosOsPedidos() {
        // Configura o mock
        doReturn(pedidosAssets).when(pedidosRepository).findAll();

        // Executa o método
        List<Pedido> pedidos = service.buscarTodosPedidos();

        // Verifica o resultado
        assertEquals( pedidosAssets.size(), pedidos.size(), "Deveria encontrar 1 pedido");

        // Verifica se os métodos foram chamados
        verify(pedidosRepository, times(1)).findAll();
    }

    @Test
    public void testeBuscarPedidosByCustomerId() {
        // Configura o mock
        doReturn(Arrays.asList(pedidoEntity)).when(pedidosRepository)
            .findPedidosByIdCliente("1");

        // Executa o método
        List<Pedido> pedidos = service.buscarPedidosPorIdCliente("1");

        // Verifica o resultado
        assertEquals( 1, pedidos.size(),"Deveria encontrar 1 pedido");

        // Verifica se os métodos foram chamados
        verify(pedidosRepository, times(1)).findPedidosByIdCliente("1");
    }

    @Test
    public void testeIncluirNovoItemAoPedido() {
        // Configura os mocks
        doReturn(Optional.of(pedidoEntity)).when(pedidosRepository).findById(pedidoEntity.getId());
        doReturn(itemPedidoEntity).when(validadorDeItem).validate(any(ItemPedido.class));
        doReturn(pedidoEntity).when(pedidosRepository).save(any(Pedido.class));

        // Cria um novo item para adicionar
        ItemPedido newItem = gerarItemPedido();
        newItem.setQuantidade(1);
        newItem.setPrecoUnitario(new BigDecimal("10.00"));

        // Executa o método
        service.incluirItemAoPedido(pedidoEntity.getId(), newItem);

        // Verifica o resultado
        assertEquals(2, pedidoEntity.getItemsPedido().size(), 
        "Deveria encontrar 2 itens no pedido");

        // Verifica se os métodos foram chamados
        verify(pedidosRepository, times(1)).findById(pedidoEntity.getId());
        verify(pedidosRepository, times(1)).save(any(Pedido.class));

    }

    @Test
    public void testeRemoverItemDoPedido() {
        // Configura os mocks
        String idItemRemovido = pedidoEntity.getItemsPedido().get(0).getId();
        doReturn(Optional.of(pedidoEntity)).when(pedidosRepository).findById(pedidoEntity.getId());
        doReturn(pedidoEntity).when(pedidosRepository).save(any(Pedido.class));
        doNothing().when(itemPedidoRepository).deleteById(idItemRemovido);
        
        // Executa o método
        service.removerItemDoPedido(pedidoEntity.getId(), idItemRemovido);

        // Verifica o resultado
        assertEquals( 0, pedidoEntity.getItemsPedido().size(),
            "Deveria encontrar 0 itens no pedido");

        // Verifica se os métodos foram chamados
        verify(pedidosRepository, times(1)).findById(pedidoEntity.getId());
        verify(pedidosRepository, times(1)).save(any(Pedido.class));
        verify(itemPedidoRepository, times(1)).deleteById(idItemRemovido);

    }

    @Test
    public void testeAlterarItemDoPedido() {
        ItemPedido itemAlterado = pedidoEntity.getItemsPedido().get(0);
        // Configura os mocks
        doReturn(Optional.of(pedidoEntity)).when(pedidosRepository).findById(pedidoEntity.getId());
        doReturn(itemAlterado).when(validadorDeItem).validate(any(ItemPedido.class));
        doReturn(pedidoEntity).when(pedidosRepository).save(any(Pedido.class));
        doReturn(itemAlterado).when(itemPedidoRepository).save(any(ItemPedido.class));

        itemAlterado.setQuantidade(3);
        // Executa o método
        service.alterarItemPedido(pedidoEntity.getId(), itemAlterado);

        // Verifica o resultado
        assertEquals(1, pedidoEntity.getItemsPedido().size(),"Deveria encontrar 1 item no pedido");
        assertTrue(pedidoEntity.getItemsPedido().get(0).getQuantidade().compareTo(3) == 0,
            "Deveria encontrar a quantidade 3 no item do pedido");

        // Verifica se os métodos foram chamados
        verify(pedidosRepository, times(1)).findById(pedidoEntity.getId());
        verify(pedidosRepository, times(1)).save(any(Pedido.class));
        verify(itemPedidoRepository, times(1)).save(any(ItemPedido.class));

    }

    @Test
    public void testeCalcularValorTotalDoPedido() {
        // Cria um mock de Pedido para poder mockar o método calcularTotalPedido
        Pedido mockPedido = mock(Pedido.class);
        when(mockPedido.calcularTotalPedido()).thenReturn(new BigDecimal("20.00"));

        // Configura o Repository para retornar o mock de Pedido
        doReturn(Optional.of(mockPedido)).when(pedidosRepository).findById(eq("1"));
        doReturn(mockPedido).when(pedidosRepository).save(any(Pedido.class));

        // Executa o método
        BigDecimal total = service.calcularValorTotalPedido("1");

        // Verifica o resultado
        assertEquals(new BigDecimal("20.00"), total,"Valor total deveria ser 20.00");

        // Verifica se os métodos foram chamados
        verify(pedidosRepository, times(1)).findById("1");
        verify(mockPedido, times(1)).calcularTotalPedido();
        verify(mockPedido, times(1)).setValorTotalPedido(new BigDecimal("20.00"));
        verify(pedidosRepository, times(1)).save(any(Pedido.class));

    }

    @Test
    public void testeFinalizacaoDeUmPedido() {
        // Cria um mock de Pedido para poder mockar o método calcularTotalPedido
        Pedido mockPedido = mock(Pedido.class);
        ItemPedido mockItemPedido = mock(ItemPedido.class);
        Produto mockProduto = mock(Produto.class);

        //Pedido
        doReturn(pedidoEntity.getValorTotalPedido()).when(mockPedido).calcularTotalPedido();
        doNothing().when(mockPedido).setValorTotalPedido(pedidoEntity.getValorTotalPedido());
        
        //Item Pedido e Produto
        doReturn(Arrays.asList(mockItemPedido)).when(mockPedido).getItemsPedido();
        doReturn(3).when(mockItemPedido).getQuantidade();
        doReturn(mockProduto).when(mockItemPedido).getProduto();
        doReturn(produtoEntity.getId()).when(mockProduto).getId();
        
        // Configura o Repository para retornar o mock de Pedido
        doReturn(Optional.of(mockPedido)).when(pedidosRepository).findById(pedidoEntity.getId());
        doReturn(mockPedido).when(pedidosRepository).save(any(Pedido.class));
        
        doReturn(Optional.of(mockProduto)).when(produtoRepository).findById(produtoEntity.getId());
        doReturn(Arrays.asList(mockProduto)).when(produtoRepository).saveAll(any());
        
        // Executa o método
        service.finalizarPedido(pedidoEntity.getId());

        // Verifica se os métodos foram chamados
        verify(pedidosRepository, times(1)).findById(pedidoEntity.getId());
        verify(mockPedido, times(1)).calcularTotalPedido();
        verify(mockPedido, times(1)).setValorTotalPedido(pedidoEntity.getValorTotalPedido());
        verify(mockPedido, times(1)).getItemsPedido();
        verify(pedidosRepository, times(1)).save(any(Pedido.class));

        verify(mockItemPedido, atLeastOnce()).getProduto();
        verify(mockProduto, atLeastOnce()).getId();
        verify(produtoRepository, atLeastOnce()).findById(produtoEntity.getId());
        verify(produtoRepository, atLeastOnce()).saveAll(any());
    }

    @Test
    public void testeCancelamentoDeUmPedido() {
        // Cria um mock de Pedido para poder mockar o método calcularTotalPedido
        Pedido mockPedido = mock(Pedido.class);

        // Configura o Repository para retornar o mock de Pedido
        doReturn(Optional.of(mockPedido)).when(pedidosRepository).findById(eq("1"));
        doReturn(mockPedido).when(pedidosRepository).save(any(Pedido.class));

        // Executa o método
        service.cancelarPedido("1");

        // Verifica se os métodos foram chamados
        verify(pedidosRepository, times(1)).findById("1");
        verify(mockPedido, times(1)).cancelarPedido();
        verify(pedidosRepository, times(1)).save(any(Pedido.class));
       
    }
}