package space.lasf.springboot_project.dto.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import space.lasf.springboot_project.domain.model.Pedido;
import space.lasf.springboot_project.domain.model.Status;
import space.lasf.springboot_project.dto.PedidoDto;




@Component
public class PedidoMapper {
    
    public static PedidoDto toPedidoDto(Pedido pedido) {

		if (pedido == null)
			return null;



		return PedidoDto.builder()
				.id(pedido.getId())
				.idClient((null!=pedido.getCliente())?pedido.getCliente().getId():"")
				.numeroPedido(pedido.getNumeroPedido())
				.dataPedido(pedido.getDataPedido())
				.status(pedido.getStatus().name())
				.valorTotalPedido(pedido.getValorTotalPedido())
				.itemsPedido(ItemPedidoMapper.toListItemPedidoDto(pedido.getItemsPedido()))
				.build();
	}
	
	public static Pedido toPedidoEntity(PedidoDto pedidoDto) {

		if (pedidoDto == null)
			return null;
		
		Pedido pedidoEntity = new Pedido();
		
        pedidoEntity.setId(pedidoDto.getId());
		pedidoEntity.setNumeroPedido(pedidoDto.getNumeroPedido());
        pedidoEntity.setDataPedido(pedidoDto.getDataPedido());
        pedidoEntity.setStatus(Status.valueOf(pedidoDto.getStatus()));
        pedidoEntity.setValorTotalPedido(pedidoDto.getValorTotalPedido());
        pedidoEntity.setItemsPedido(ItemPedidoMapper.toListItemPedidoEntity(pedidoDto.getItemsPedido()));


		return pedidoEntity;
	}
	
	public static List<PedidoDto> toListPedidoDto(List<Pedido> pedidos) {

		List<PedidoDto> list = new ArrayList<PedidoDto>();

		if(pedidos != null) {
			pedidos.stream().forEach(pedido -> {
				list.add(PedidoMapper.toPedidoDto(pedido));
			});
		}

		return list;
	}

	public static List<Pedido> toListPedidoEntity(List<PedidoDto> pedidoDtos) {

		List<Pedido> list = new ArrayList<Pedido>();

		if(pedidoDtos != null) {
			pedidoDtos.stream().forEach(pedido -> {
				list.add(PedidoMapper.toPedidoEntity(pedido));
			});
		}

		return list;
	}
}