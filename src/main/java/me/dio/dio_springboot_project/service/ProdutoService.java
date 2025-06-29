package me.dio.dio_springboot_project.service;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import me.dio.dio_springboot_project.domain.model.Produto;

/**
 * Serviço para gerenciamento de produtos.
 */
public interface ProdutoService {
    
    Produto criarProduto(Produto Produto);

    Produto alterarProduto(Produto Produto);
    
    Optional<Produto> buscarProdutoPorId(String id);
    
    Optional<Produto> buscarProdutoPorSku(String sku);
    
    List<Produto> buscarTodosProdutos();
    
    List<Produto> buscarProdutosPorFaixaDePreco(BigDecimal minPreco, BigDecimal maxPreco);
    
    List<Produto> buscarProdutosComPrecoMenor(BigDecimal maxPreco);
        
    List<Produto> buscarProdutosComPrecoMaior(BigDecimal minPreco);
    
    void alterarEstoqueProduto(String ProdutoId, Integer novaQuantidade);
    
    void alterarPrecoProduto(String ProdutoId, BigDecimal novoPreco);
    
    void removerProduto(String ProdutoId);
    
    BigDecimal calcularValorInventario();
    
    List<Produto> buscarProdutosComEstoqueBaixo();
}