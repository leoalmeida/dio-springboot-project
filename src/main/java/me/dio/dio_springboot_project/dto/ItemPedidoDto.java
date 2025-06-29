package me.dio.dio_springboot_project.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemPedidoDto {

    private String id;
    private String idPedido;
    private ProdutoDto produto;
    private Integer quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal valorTotalItem;


}
