package space.lasf.springboot_project.integration.repository;

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

import space.lasf.springboot_project.base.TestFactory;
import space.lasf.springboot_project.domain.model.Produto;
import space.lasf.springboot_project.domain.repository.ProdutoRepository;


//@ExtendWith(SpringExtension.class)
@DataMongoTest
@ActiveProfiles("test")
public class ProdutoRepositoryIntegrationTest extends TestFactory{

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired 
    private MongoTemplate mongoTemplate;

    Produto produto1;
    Produto produto2;

    @BeforeEach
    public void setUp() {
        // Cria produtos para testes básicos
        produto1 = gerarProduto();
        produto2 = gerarProduto();
        mongoTemplate.insertAll(Arrays.asList(produto1,produto2));
    }

    
    @AfterEach
    void clean() {
        mongoTemplate.remove(produto1);
        mongoTemplate.remove(produto2);
    }

    @Test
    public void shouldBeNotEmpty() {
        assertTrue(produtoRepository.findAll().size()>0);
    }

    @Test
    void dadoProduto_quandoCriarProduto_entaoProdutoPersistido() {
        // given
        Produto produto1 = gerarProduto();

        // when
        produtoRepository.save(produto1);

        // then
        Optional<Produto> retrievedProduto = produtoRepository.findById(produto1.getId());
        assertTrue(retrievedProduto.isPresent());
        assertEquals(produto1.getId(), retrievedProduto.get().getId());
        assertEquals(produto1.getNome(), retrievedProduto.get().getNome());
    }
}
