package me.dio.dio_springboot_project.domain.repository;

import me.dio.dio_springboot_project.domain.model.Produto;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositório para a entidade Product.
 */
public interface ProdutoRepository extends MongoRepository<Produto, String> {

    @Query("{ 'sku':  ?0 }")
    Optional<Produto> findBySku(String sku);

    @Query("{ 'preco': { $gte: ?0 } }")
    List<Produto> findByPrecoGreaterThan(BigDecimal minpreco);
    
    @Query("{ 'preco': { $lte: ?0 } }")
    List<Produto> findByPrecoLowerThan(BigDecimal maxpreco);

    @Query("{ 'preco': { $gte: ?0 , $lte: ?1 } }")
    List<Produto> findByFaixaPreco(BigDecimal minpreco, BigDecimal maxpreco);

    @Query("{ 'nome':  /?0/ }")
    List<Produto> searchByNome(String nome);

    @Query("{ 'estoque': { $lte: ?0 } }")
    List<Produto> findProdutosComEstoqueBaixo(Integer quantidadeMinima);

}
