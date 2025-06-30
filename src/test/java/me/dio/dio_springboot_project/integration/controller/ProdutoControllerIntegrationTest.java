package me.dio.dio_springboot_project.integration.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import me.dio.dio_springboot_project.base.AbstractIntegrationTest;
import me.dio.dio_springboot_project.domain.repository.ProdutoRepository;
import me.dio.dio_springboot_project.dto.ProdutoDto;


public class ProdutoControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProdutoRepository repository;
    private ProdutoDto produtoDto;

    @BeforeEach
    public void init() {
        produtoDto = gerarProdutoDto();
    }

    @Test
    @DisplayName("Produto Path Test: salvar produto dto e retornar")
    public void dadoProdutoDtoCorreto_entaoSalvaProduto_eRetornaProdutoDto()
      throws Exception {

        // when
        ProdutoDto savedProdutoDto = performPostRequestExpectedSuccess(
                                    PRODUTOS_API_ENDPOINT, produtoDto, ProdutoDto.class);

        //then
        assertNotNull(savedProdutoDto);
        assertEquals(produtoDto.getPreco(), savedProdutoDto.getPreco());
        assertEquals(produtoDto.getNome(), savedProdutoDto.getNome());
        assertEquals(produtoDto.getSku(), savedProdutoDto.getSku());
    }
}
