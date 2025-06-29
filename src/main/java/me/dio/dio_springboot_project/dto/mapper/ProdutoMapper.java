package me.dio.dio_springboot_project.dto.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import me.dio.dio_springboot_project.domain.model.Produto;
import me.dio.dio_springboot_project.dto.ProdutoDto;

@Component
public class ProdutoMapper {
    
    public static ProdutoDto toProdutoDto(Produto produto) {

		if (produto == null)
			return null;

		return ProdutoDto.builder()
				.id(produto.getId())
				.nome(produto.getNome())
				.descricao(produto.getDescricao())
				.preco(produto.getPreco())
				.estoque(produto.getEstoque())
				.sku(produto.getSku())
				.build();
	}
	
	public static Produto toProdutoEntity(ProdutoDto produtoDto) {

		if (produtoDto == null)
			return null;
		
		Produto produtoEntity = new Produto();
		
        produtoEntity.setId(produtoDto.getId());
        produtoEntity.setNome(produtoDto.getNome());
        produtoEntity.setDescricao(produtoDto.getDescricao());
        produtoEntity.setPreco(produtoDto.getPreco());
        produtoEntity.setEstoque(produtoDto.getEstoque());
        produtoEntity.setSku(produtoDto.getSku());

		return produtoEntity;
	}
	
	public static List<ProdutoDto> toListProdutoDto(List<Produto> produtoEntities) {

		List<ProdutoDto> list = new ArrayList<ProdutoDto>();

		if(produtoEntities != null) {
			produtoEntities.stream().forEach(produto -> {
				list.add(ProdutoMapper.toProdutoDto(produto));
			});
		}

		return list;
	}

	public static List<Produto> toListProdutoEntity(List<ProdutoDto> produtoDtos) {

		List<Produto> list = new ArrayList<Produto>();

		if(produtoDtos != null) {
			produtoDtos.stream().forEach(produto -> {
				list.add(ProdutoMapper.toProdutoEntity(produto));
			});
		}

		return list;
	}
}