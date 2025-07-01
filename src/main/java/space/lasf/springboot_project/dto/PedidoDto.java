package space.lasf.springboot_project.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class PedidoDto {

    private String id;
    private String idClient;
    private String numeroPedido;
    private LocalDateTime dataPedido;
    private String status; 
    @Singular(value = "itemsPedido")
    private List<ItemPedidoDto> itemsPedido;
    private BigDecimal valorTotalPedido;

}
