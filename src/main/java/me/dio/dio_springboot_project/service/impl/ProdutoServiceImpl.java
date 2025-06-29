package me.dio.dio_springboot_project.service.impl;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import me.dio.dio_springboot_project.core.util.ObjectsValidator;
import me.dio.dio_springboot_project.domain.model.Produto;
import me.dio.dio_springboot_project.domain.repository.ProdutoRepository;
import me.dio.dio_springboot_project.service.ProdutoService;

/**
 * Implementação do serviço para gerenciamento de produtos.
 */
@Service
@RequiredArgsConstructor
public class ProdutoServiceImpl implements ProdutoService {

    @Autowired
    private ObjectsValidator<Produto> validadorDeProduto;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Override
    @Transactional
    public Produto criarProduto(Produto produto) {
        // Valida o produto antes de salvar
        validadorDeProduto.validate(produto);
        return  produtoRepository.save(produto);
    }

    @Override
    @Transactional
    public Produto alterarProduto(Produto produto) {
        // Valida o produto antes de realizar o merge
        validadorDeProduto.validate(produto);
        return  produtoRepository.save(produto);
    }

    @Override
    public Optional<Produto> buscarProdutoPorId(String id) {
        return produtoRepository.findById(id);
    }

    @Override
    public Optional<Produto> buscarProdutoPorSku(String sku) {
        return produtoRepository.findBySku(sku);
    }

    @Override
    public List<Produto> buscarTodosProdutos() {
        return produtoRepository.findAll();
    }

    @Override
    public List<Produto> buscarProdutosPorFaixaDePreco(BigDecimal minPreco, BigDecimal maxPreco) {
        if (null == minPreco || null == maxPreco || minPreco.compareTo(maxPreco)>=0){
            throw new IllegalArgumentException("Faixa de preços inválido");
        }
        return produtoRepository.findByFaixaPreco(minPreco,maxPreco);
    }

    @Override
    public List<Produto> buscarProdutosComPrecoMenor(BigDecimal maxPreco) {
        if (null == maxPreco || BigDecimal.ZERO.compareTo(maxPreco)>=0){
            throw new IllegalArgumentException("Preço máximo inválido");
        }
        return produtoRepository.findByPrecoLowerThan(maxPreco);
    }
    
    @Override
    public List<Produto> buscarProdutosComPrecoMaior(BigDecimal minPreco) {
        if (null == minPreco || BigDecimal.ZERO.compareTo(minPreco)>=0){
            minPreco = BigDecimal.ZERO;
        }
        return produtoRepository.findByPrecoGreaterThan(minPreco);
    }

    @Override
    @Transactional
    public void alterarEstoqueProduto(String produtoId, Integer novoEstoque) {
        if(null == novoEstoque || novoEstoque < 0) {
            throw new IllegalArgumentException("O estoque não pode ser negativo");
        }

        Produto produto = produtoRepository.findById(produtoId)
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
        
        produto.setEstoque(novoEstoque);
        produtoRepository.save(produto);

    }

    @Override
    @Transactional
    public void alterarPrecoProduto(String produtoId, BigDecimal novoPreco) {
        if(novoPreco.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O preço não pode ser negativo");
        }

        Produto produto = produtoRepository.findById(produtoId)
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

        produto.setPreco(novoPreco);
        produtoRepository.save(produto);
    
    }

    @Override
    @Transactional
    public void removerProduto(String produtoId) {
        produtoRepository.deleteById(produtoId);
    }

    @Override
    public BigDecimal calcularValorInventario() {
        List<Produto> produtos = produtoRepository.findAll();
        
        return produtos.stream()
                .map(Produto::calcularTotalEstoque)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<Produto> buscarProdutosComEstoqueBaixo() {
        return produtoRepository.findProdutosComEstoqueBaixo(10);
    }
    
}