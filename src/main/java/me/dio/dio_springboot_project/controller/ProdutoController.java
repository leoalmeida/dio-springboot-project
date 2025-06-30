package me.dio.dio_springboot_project.controller;


import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.dio.dio_springboot_project.core.util.ObjectsValidator;
import me.dio.dio_springboot_project.domain.model.Produto;
import me.dio.dio_springboot_project.dto.ProdutoDto;
import me.dio.dio_springboot_project.dto.mapper.ProdutoMapper;
import me.dio.dio_springboot_project.service.ProdutoService;



/**
 * Controller para gerenciamento de produtos.
 */
@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;
    @Autowired
    private ObjectsValidator<ProdutoDto> produtoValidator;


    @GetMapping
    public ResponseEntity<List<ProdutoDto>> buscarTodosProdutos() {
        List<ProdutoDto> produtos = ProdutoMapper.toListProdutoDto(produtoService.buscarTodosProdutos());
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDto> buscarProdutoById(@PathVariable String id) {
        return produtoService.buscarProdutoPorId(id)
                .map(ProdutoMapper::toProdutoDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProdutoDto> buscarProdutoPorSku(@PathVariable String sku) {
        return produtoService.buscarProdutoPorSku(sku)
                .map(ProdutoMapper::toProdutoDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProdutoDto> criarProduto(@RequestBody ProdutoDto produtoDto) {
        produtoValidator.validate(produtoDto);
        ProdutoDto savedProduto = ProdutoMapper.toProdutoDto(
                            produtoService.criarProduto(ProdutoMapper.toProdutoEntity(produtoDto)));
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(savedProduto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoDto> alterarProduto(@PathVariable String id, @RequestBody ProdutoDto produto) {
        produtoValidator.validate(produto);
        Produto entity = ProdutoMapper.toProdutoEntity(produto);
        return produtoService.buscarProdutoPorId(id)
                .map(existingProduto -> produtoService.alterarProduto(existingProduto.updateData(entity)))
                .map(ProdutoMapper::toProdutoDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerProduto(@PathVariable String id) {
        produtoService.removerProduto(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/estoque")
    public ResponseEntity<Void> alterarEstoque(@PathVariable String id, @RequestParam Integer estoque) {
        if (estoque == null || estoque < 0) {
            throw new IllegalArgumentException("Estoque não deveria ser atualizado com valor negativo");
        }
        produtoService.alterarEstoqueProduto(id, estoque);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/preco")
    public ResponseEntity<Void> updatePreco(@PathVariable String id, @RequestParam BigDecimal preco) {
        if (preco == null || preco.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço não deveria ser atualizado com valor negativo");
        }
        produtoService.alterarPrecoProduto(id, preco);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/emfalta")
    public ResponseEntity<List<ProdutoDto>> buscarProdutosComEstoqueBaixo() {
        List<ProdutoDto> produtos = ProdutoMapper.toListProdutoDto(produtoService.buscarProdutosComEstoqueBaixo());
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/total-inventario")
    public ResponseEntity<BigDecimal> consultarValorInventario() {
        BigDecimal value = produtoService.calcularValorInventario();
        return ResponseEntity.ok(value);
    }

    @GetMapping("/preco")
    public ResponseEntity<List<ProdutoDto>> buscarProdutosPorFaixaDePreco(
            @RequestParam(required = false) BigDecimal minPreco,
            @RequestParam(required = false) BigDecimal maxPreco) {

        if (maxPreco == null) {
            List<ProdutoDto> produtos = ProdutoMapper.toListProdutoDto(produtoService.buscarProdutosComPrecoMaior(minPreco));
            return ResponseEntity.ok(produtos); 
        } else if (minPreco == null) {
            List<ProdutoDto> produtos = ProdutoMapper.toListProdutoDto(produtoService.buscarProdutosComPrecoMenor(maxPreco));
            return ResponseEntity.ok(produtos); 
        }
        
        if (minPreco.compareTo(maxPreco)>0) {
            throw new IllegalArgumentException("Range de preços inválido");
        }

        List<ProdutoDto> produtos = ProdutoMapper.toListProdutoDto(produtoService.buscarProdutosPorFaixaDePreco(minPreco, maxPreco));
        return ResponseEntity.ok(produtos);
    }
}
