package me.dio.dio_springboot_project.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClienteDto {

    private String id;
    private String nome;
    private String email;
    private String telefone;
    @Singular(value = "pedidos")
    private List<PedidoDto> pedidos;

}