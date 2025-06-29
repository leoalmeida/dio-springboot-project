package me.dio.dio_springboot_project.dto.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import me.dio.dio_springboot_project.domain.model.Cliente;
import me.dio.dio_springboot_project.dto.ClienteDto;




@Component
public class ClienteMapper {
    public static ClienteDto toClienteDto(Cliente cliente) {

		if (cliente == null)
			return null;

		return ClienteDto.builder()
                .id(cliente.getId())
				.email(cliente.getEmail())
				.nome(cliente.getNome())
				.telefone(cliente.getTelefone())
				.pedidos(PedidoMapper.toListPedidoDto(cliente.getPedidos()))
                .build();
	}

	public static Cliente toClienteEntity(ClienteDto clienteDto) {

		if (clienteDto == null)
			return null;

		Cliente clienteEntity = new Cliente();
		
		clienteEntity.setId(clienteDto.getId());
		clienteEntity.setEmail(clienteDto.getEmail());
		clienteEntity.setNome(clienteDto.getNome());
		clienteEntity.setTelefone(clienteDto.getTelefone());
		clienteEntity.setPedidos(PedidoMapper.toListPedidoEntity(clienteDto.getPedidos()));

		return clienteEntity;
	}

	public static List<ClienteDto> toListClienteDto(List<Cliente> clienteEntities) {

		List<ClienteDto> list = new ArrayList<ClienteDto>();

		if(clienteEntities != null) {
			clienteEntities.stream().forEach(cliente -> {
				list.add(ClienteMapper.toClienteDto(cliente));
			});
		}

		return list;
	}

	public static List<Cliente> toListClienteEntity(List<ClienteDto> clienteDtos) {

		List<Cliente> list = new ArrayList<Cliente>();

		if(clienteDtos != null) {
			clienteDtos.stream().forEach(cliente -> {
				list.add(ClienteMapper.toClienteEntity(cliente));
			});
		}

		return list;
	}
}