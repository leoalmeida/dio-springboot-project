package space.lasf.springboot_project.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


/**
 * Entidade que representa um item de pedido.
 */
@Document(collection  = "items")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemPedido {
    
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    @DBRef
    private Pedido pedido;

    @DBRef
    private Produto produto;

    @NotNull
    @Min(value = 1, message = "Quantidade deve ser pelo menos 1")
    private Integer quantidade;

    @Field(name = "preco_unitario")
    @DecimalMin(value = "0.01", message = "Preço dever maior que 0.00")
    private BigDecimal precoUnitario;

    @DecimalMin(value = "0.00", message = "Preço dever maior ou igual a 0.00")
    private BigDecimal subtotal;

    public BigDecimal getSubtotal() {
        return this.updateSubtotal();
    }

    public BigDecimal updateSubtotal() {
        if (quantidade == null || precoUnitario == null) {
            this.subtotal = BigDecimal.ZERO;
        }else{
            this.subtotal = precoUnitario.multiply(new BigDecimal(quantidade));
        }
        return this.subtotal;
    }

    public ItemPedido updateData( ItemPedido orderItem ) {
        this.quantidade = orderItem.getQuantidade();
        this.precoUnitario = orderItem.getPrecoUnitario();
        this.updateSubtotal();
        this.pedido = orderItem.getPedido();
        this.produto = orderItem.getProduto();
        return this;
    }

   

}
