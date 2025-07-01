package space.lasf.springboot_project.dto.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import space.lasf.springboot_project.domain.model.ItemPedido;
import space.lasf.springboot_project.dto.ItemPedidoDto;




@Component
public class ItemPedidoMapper {
    
    public static ItemPedidoDto toItemPedidoDto(ItemPedido itemPedido) {

		if (itemPedido == null)
			return null;

		return ItemPedidoDto.builder()
				.id(itemPedido.getId())
				.idPedido((null!=itemPedido.getPedido())?itemPedido.getPedido().getId():"")
				.produto(ProdutoMapper.toProdutoDto(itemPedido.getProduto()))
				.quantidade(itemPedido.getQuantidade())
				.precoUnitario(itemPedido.getPrecoUnitario())
				.valorTotalItem(itemPedido.getSubtotal())
				.build();
	}
	
	public static ItemPedido toItemPedidoEntity(ItemPedidoDto itemPedidoDto) {

		if (itemPedidoDto == null)
			return null;
		
		ItemPedido itemPedidoEntity = new ItemPedido();
		
		itemPedidoEntity.setId(itemPedidoDto.getId());
        itemPedidoEntity.setProduto(ProdutoMapper.toProdutoEntity(itemPedidoDto.getProduto()));
        itemPedidoEntity.setQuantidade(itemPedidoDto.getQuantidade());
        itemPedidoEntity.setSubtotal(itemPedidoDto.getValorTotalItem());
        itemPedidoEntity.setPrecoUnitario(itemPedidoDto.getPrecoUnitario());


		return itemPedidoEntity;
	}
	
	public static List<ItemPedidoDto> toListItemPedidoDto(List<ItemPedido> itemPedidoEntities) {

		List<ItemPedidoDto> list = new ArrayList<ItemPedidoDto>();

		if(itemPedidoEntities != null) {
			itemPedidoEntities.stream().forEach(itemPedido -> {
				list.add(ItemPedidoMapper.toItemPedidoDto(itemPedido));
			});
		}

		return list;
	}

	public static List<ItemPedido> toListItemPedidoEntity(List<ItemPedidoDto> itemPedidoDtos) {

		List<ItemPedido> list = new ArrayList<ItemPedido>();

		if(itemPedidoDtos != null) {
			itemPedidoDtos.stream().forEach(itemPedido -> {
				list.add(ItemPedidoMapper.toItemPedidoEntity(itemPedido));
			});
		}

		return list;
	}
}