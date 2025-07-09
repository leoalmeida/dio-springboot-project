package space.lasf.springboot_project.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import space.lasf.springboot_project.core.util.ObjectsValidator;
import space.lasf.springboot_project.domain.model.Produto;
import space.lasf.springboot_project.domain.repository.ProdutoRepository;
import space.lasf.springboot_project.service.impl.ProdutoServiceImpl;

/**
 * Testes para o serviço de produtos.
 */
@ExtendWith(SpringExtension.class)
//@SpringBootTest
public class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repository;

    @Mock
    private ObjectsValidator<Produto> validator;
    
    @InjectMocks
    private ProdutoServiceImpl service;

    private Produto produto1;
    private Produto produto2;
    private Produto produto3;

  
    @BeforeEach
    public void setUp() {
        
        // Cria produtos para testes
        produto1 = new Produto();
        produto1.setId(UUID.randomUUID().toString());
        produto1.setNome("Produto 1");
        produto1.setDescricao("Descrição do Produto 1");
        produto1.setPreco(new BigDecimal("10.00"));
        produto1.setEstoque(100);
        produto1.setSku("SKU001");

        produto2 = new Produto();
        produto2.setId(UUID.randomUUID().toString());
        produto2.setNome("Produto 2");
        produto2.setDescricao("Descrição do Produto 2");
        produto2.setPreco(new BigDecimal("20.00"));
        produto2.setEstoque(5);
        produto2.setSku("SKU002");

        produto3 = new Produto();
        produto3.setId(UUID.randomUUID().toString());
        produto3.setNome("Produto 3");
        produto3.setDescricao("Descrição do Produto 3");
        produto3.setPreco(new BigDecimal("30.00"));
        produto3.setEstoque(0);
        produto3.setSku("SKU003");
        
    }

    @Test
    public void testeCriarProduto() {
        Produto mockUpdatedProduto = Produto.builder().id(produto1.getId()).build().updateData(produto1);
        // Configura o mock
        doReturn(mockUpdatedProduto).when(repository).save(mockUpdatedProduto);
        //doNothing().when(modelMapper).map(mockPostRequestDto, post);
        //doReturn(mockProdutoDto).when(modelMapper).map(mockUpdatedProduto, ProdutoDto.class);
        doReturn(mockUpdatedProduto).when(validator).validate(mockUpdatedProduto);

        // Executa o método
        Produto savedProduto = service.criarProduto(mockUpdatedProduto);

        // Verifica o resultado
        assertNotNull(savedProduto, "Produto salvo não deveria ser nulo");
        assertEquals(mockUpdatedProduto.getId(), savedProduto.getId(),"Produto salvo deveria ter o mesmo ID");

        // Verifica se o método do repositório foi chamado
        verify(repository, times(1)).save(mockUpdatedProduto);
        verify(validator, times(1)).validate(mockUpdatedProduto);
    }

    @Test
    public void testeCriarProdutoComPrecoNegativo() {
        // Cria um produto com preço negativo
        Produto mockInvalidProduto = Produto.builder()
                        .nome("Produto Inválido")
                        .preco(new BigDecimal("-10.00"))
                        .build();

        // Configura o mock
        doThrow(new IllegalArgumentException("Preço não pode ser negativo"))
                        .when(validator).validate(mockInvalidProduto);
        doReturn(mockInvalidProduto).when(repository).save(mockInvalidProduto);
        
        // Executa o método
        Throwable  throwable  = 
                assertThrows(IllegalArgumentException.class, () ->{
                    Produto savedProduto = service.criarProduto(mockInvalidProduto);
                    assertNull(savedProduto, "Produto com preço negativo foi salvo");
                });
        
        assertEquals(IllegalArgumentException.class, throwable.getClass());
        assertEquals("Preço não pode ser negativo", throwable.getMessage());

        // Verifica se o método do repositório foi chamado
        verify(validator, times(1)).validate(mockInvalidProduto);
        verify(repository, times(0)).save(mockInvalidProduto);
    }

    @Test
    public void testeAlterarProduto() {
        Produto mockUpdatedProduto = Produto.builder().id(produto1.getId()).build().updateData(produto1);
        mockUpdatedProduto.setNome("ProdutoAlterado");
        // Configura o mock
        doReturn(mockUpdatedProduto).when(validator).validate(mockUpdatedProduto);
        //doNothing().when(modelMapper).map(mockPostRequestDto, post);
        doReturn(mockUpdatedProduto).when(repository).save(mockUpdatedProduto);
        //doReturn(mockProdutoDto).when(modelMapper).map(mockUpdatedProduto, ProdutoDto.class);
        
        
        // Executa o método
        Produto produtoAlterado = service.alterarProduto(mockUpdatedProduto);

        // Verifica o resultado
        assertNotNull(produtoAlterado, "Produto salvo não deveria ser nulo");
        assertEquals(mockUpdatedProduto.getId(), produtoAlterado.getId(),"Produto salvo deveria ter o mesmo ID");
        assertEquals(mockUpdatedProduto.getNome(), produtoAlterado.getNome(),"Produto deveria ter o nome modificado");

        // Verifica se o método do repositório foi chamado
        verify(repository, times(1)).save(mockUpdatedProduto);
        verify(validator, times(1)).validate(mockUpdatedProduto);
    }

    @Test
    public void testeBuscarProdutoById() {
        // Configura o mock
        doReturn(Optional.of(produto1)).when(repository).findById(produto1.getId());

        // Executa o método
        Optional<Produto> foundProduto = service.buscarProdutoPorId(produto1.getId());

        // Verifica o resultado
        assertTrue(foundProduto.isPresent(), "Produto deveria ser encontrado");
        assertEquals(produto1.getId(), 
                    foundProduto.get().getId(),
                    "Produto encontrado deveria ter o ID correto");

        // Verifica se o método do repositório foi chamado
        verify(repository, times(1)).findById(produto1.getId());
    }

    @Test
    public void testeBuscarProdutoBySku() {
        // Configura o mock
        doReturn(Optional.of(produto1)).when(repository).findBySku("SKU001");

        // Executa o método
        Optional<Produto> foundProduto = service.buscarProdutoPorSku("SKU001");

        // Verifica o resultado
        assertTrue(foundProduto.isPresent(), "Produto deveria ser encontrado");
        assertEquals("SKU001", 
                    foundProduto.get().getSku(),
                    "Produto encontrado deveria ter o SKU correto");

        // Verifica se o método do repositório foi chamado
        verify(repository, times(1)).findBySku("SKU001");
    }

    @Test
    public void testeBuscarAllProdutos() {
        // Configura o mock
        doReturn(Arrays.asList(produto1, produto2, produto3))
            .when(repository).findAll();

        // Executa o método
        List<Produto> produtos = service.buscarTodosProdutos();

        // Verifica o resultado
        assertEquals(3, produtos.size(),"Deveria encontrar 3 produtos");

        // Verifica se o método do repositório foi chamado
        verify(repository, times(1)).findAll();
    }

    @Test
    public void testeBuscarProdutosPorFaixaDePreco() {
        // Configura o mock
        doReturn(Arrays.asList(produto2, produto3))
            .when(repository).findByFaixaPreco(new BigDecimal("15.00"),new BigDecimal("25.00"));

        // Executa o método
        List<Produto> produtos = service.buscarProdutosPorFaixaDePreco(
            new BigDecimal("15.00"), new BigDecimal("25.00"));

        // Verifica o resultado
        assertEquals(2, produtos.size(),"Deveria encontrar 2 produtos");

        // Verifica se o método do repositório foi chamado
        verify(repository, times(1))
            .findByFaixaPreco(new BigDecimal("15.00"),new BigDecimal("25.00"));
    }

    @Test
    public void testeBuscarProdutosComPrecoMenorQueMaximo() {
        // Configura o mock
        doReturn(Arrays.asList(produto2, produto3))
            .when(repository).findByPrecoLowerThan(new BigDecimal("25.00"));

        // Executa o método
        List<Produto> produtos = service.buscarProdutosComPrecoMenor(
            new BigDecimal("25.00"));

        // Verifica o resultado
        assertEquals(2, produtos.size(),"Deveria encontrar 2 produtos");

        // Verifica se o método do repositório foi chamado
        verify(repository, times(1))
            .findByPrecoLowerThan(new BigDecimal("25.00"));
    }

    @Test
    public void testeBuscarProdutosComPrecoMaiorQueMinimo() {
        // Configura o mock
        doReturn(Arrays.asList(produto2, produto3))
            .when(repository).findByPrecoGreaterThan(new BigDecimal("15.00"));

        // Executa o método
        List<Produto> produtos = service.buscarProdutosComPrecoMaior(
            new BigDecimal("15.00"));

        // Verifica o resultado
        assertEquals(2, produtos.size(),"Deveria encontrar 2 produtos");

        // Verifica se o método do repositório foi chamado
        verify(repository, times(1))
                .findByPrecoGreaterThan(new BigDecimal("15.00"));
    }

    @Test
    public void testeBuscarProdutosByInvalidPrecoRange() {
        // Executa o método
        Throwable  throwable  = 
                assertThrows(IllegalArgumentException.class, () ->{
                    service.buscarProdutosPorFaixaDePreco(
                        new BigDecimal("25.00"),new BigDecimal("15.00"));
                });
        assertEquals(IllegalArgumentException.class, throwable.getClass());
        assertEquals("Faixa de preços inválido", throwable.getMessage());

        // Verifica se o método do repositório foi chamado
        verify(repository, times(0))
            .findByFaixaPreco(new BigDecimal("25.00"),new BigDecimal("15.00"));
    }

    @Test
    public void testeAlterarEstoqueDeProduto() {
        Integer novoEstoque = 50;
        Produto mockUpdatedProduto = Produto.builder().id(produto1.getId()).build().updateData(produto1);
        mockUpdatedProduto.setEstoque(novoEstoque);
        // Configura o mock
        doReturn(Optional.of(produto1)).when(repository).findById(produto1.getId());
        doReturn(mockUpdatedProduto).when(repository).save(mockUpdatedProduto);

        // Executa o método
        service.alterarEstoqueProduto(produto1.getId(), novoEstoque);

        // Verifica se os métodos do repositório foram chamados
        verify(repository, times(1)).findById(produto1.getId());
        verify(repository, times(1)).save(mockUpdatedProduto);
    }

    @Test
    public void testeAlterarPrecoDeProduto() {
        BigDecimal novoPreco = new BigDecimal("15.00");
        Produto mockUpdatedProduto = Produto.builder().id(produto1.getId()).build().updateData(produto1);
        mockUpdatedProduto.setPreco(novoPreco);
        // Configura o mock
        doReturn(Optional.of(produto1)).when(repository).findById(produto1.getId());
        doReturn(mockUpdatedProduto).when(repository).save(produto1);

        // Executa o método
        service.alterarPrecoProduto(produto1.getId(), new BigDecimal("15.00"));

        // Verifica se os métodos do repositório foram chamados
        verify(repository, times(1)).findById(produto1.getId());
        verify(repository, times(1)).save(mockUpdatedProduto);
    }

    @Test
    public void testeRemoverProdutoPorIdProduto() {
        // Configura o mock
        doNothing().when(repository).deleteById(produto1.getId());

        // Executa o método
        service.removerProduto(produto1.getId());

        // Verifica se o método do repositório foi chamado
        verify(repository, times(1)).deleteById(produto1.getId());
    }

    @Test
    public void testeRemoverProdutoComIdInvalido() {
        String idInvalido = UUID.randomUUID().toString();
        // Configura o mock
        doThrow(NoSuchElementException.class)
                .when(repository).deleteById(idInvalido);
    
        // Executa o método
        Throwable  throwable  = 
                assertThrows(NoSuchElementException.class, () ->{
                    service.removerProduto(idInvalido);
                });
        assertEquals(NoSuchElementException.class, throwable.getClass());

        // Verifica se o método do repositório foi chamado
        verify(repository, times(1)).deleteById(idInvalido);
    }

    @Test
    public void testeCalcularValorTotalDeInventario() {
        // Configura o mock
        doReturn(Arrays.asList(produto1, produto2, produto3))
                .when(repository).findAll();

        // Executa o método
        BigDecimal totalValue = service.calcularValorInventario();

        // Verifica o resultado
        // produto1: 10.00 * 100 = 1000.00
        // produto2: 20.00 * 5 = 100.00
        // produto3: 30.00 * 0 = 0.00
        // Total esperado: 1100.00
        assertEquals(new BigDecimal("1100.00"), totalValue,
                    "Valor total do inventário deveria ser 1100.00");

        // Verifica se o método do repositório foi chamado
        verify(repository, times(1)).findAll();
    }

    @Test
    public void testeBuscarProdutosComNivelDeEstoqueBaixo() {
        // Configura o mock
        doReturn(Arrays.asList(produto2, produto3)).when(repository)
                    .findProdutosComEstoqueBaixo(10);

        // Executa o método
        List<Produto> produtos = service.buscarProdutosComEstoqueBaixo();

        // Verifica o resultado
        assertEquals(2, produtos.size(), "Deveria encontrar 2 produtos com estoque baixo");

        // Verifica se o método do repositório foi chamado
        verify(repository, times(1)).findProdutosComEstoqueBaixo(10);
    }

    @Test
    public void testeCalcularValorTotalDeInventarioComEstoqueNulo() {
        // Cria um produto com estoque nulo
        Produto produtoWithNullEstoque = new Produto();
        produtoWithNullEstoque.setId(UUID.randomUUID().toString());
        produtoWithNullEstoque.setNome("Produto 4");
        produtoWithNullEstoque.setPreco(new BigDecimal("40.00"));
        produtoWithNullEstoque.setEstoque(null);

        // Configura o mock
        doReturn(Arrays.asList(produto1, produto2, produto3, produtoWithNullEstoque))
                .when(repository).findAll();

        // Executa o método
        BigDecimal totalValue = service.calcularValorInventario();

        // Verifica o resultado
        assertEquals(new BigDecimal("1100.00"), totalValue, 
                    "Valor total do inventário deveria ser 1100.00");

        // Verifica se o método do repositório foi chamado
        verify(repository, times(1)).findAll();
    }

    @Test
    public void testeCriarProdutoComPrecoZerado() {
        // Cria um produto com preço zero
        Produto mockInvalidProduto = Produto.builder().id(produto1.getId()).build().updateData(produto1);
        mockInvalidProduto.setPreco(BigDecimal.ZERO);
        mockInvalidProduto.setNome("Produto Inválido");
        // Configura o mock
        doThrow(new IllegalArgumentException("Preço não pode ser negativo"))
                .when(validator).validate(mockInvalidProduto);
        doReturn(mockInvalidProduto).when(repository).save(mockInvalidProduto);
        
        // Executa o método
        Throwable  throwable  = 
                assertThrows(IllegalArgumentException.class, () ->{
                    Produto savedProduto = service.criarProduto(mockInvalidProduto);
                    assertNull(savedProduto, "Produto com preço zero foi salvo");
                });
        assertEquals(IllegalArgumentException.class, throwable.getClass());
        assertEquals("Preço não pode ser negativo", throwable.getMessage());

        // Verifica se o método do repositório foi chamado
        verify(validator, times(1)).validate(mockInvalidProduto);
        verify(repository, never()).save(mockInvalidProduto);
    }

    @Test
    public void testeAlterarEstoqueDeProdutoComValorNegativo() {
        // Configura o mock
        Produto mockInvalidProduto = Produto.builder().id(produto1.getId()).build().updateData(produto1);
        mockInvalidProduto.setEstoque(-10);
        doReturn(Optional.of(produto1)).when(repository).findById(produto1.getId());
        doReturn(mockInvalidProduto).when(repository).save(any(Produto.class));
        
        // Executa o método        
        Throwable  throwable  = 
                assertThrows(IllegalArgumentException.class, () ->{
                    service.alterarEstoqueProduto(produto1.getId(), -10);
                });
        assertEquals(IllegalArgumentException.class, throwable.getClass());
        assertEquals("O estoque não pode ser negativo", throwable.getMessage());

        // Verifica se os métodos do repositório foram chamados
        verify(repository, never()).findById(produto1.getId());
        verify(repository, never()).save(any(Produto.class));
    }
}
