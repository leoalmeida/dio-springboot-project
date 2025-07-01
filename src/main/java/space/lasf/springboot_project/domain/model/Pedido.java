package space.lasf.springboot_project.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.DecimalMin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Entidade que representa um pedido.
 */
@Document(collection  = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Serial
    private static final long serialVersionUID = 1L;
 
    @Id
    private String id;

    @Field(name = "numero_pedido")
    private String numeroPedido;

    @Field(name = "data_pedido")
    private LocalDateTime dataPedido;

    @DBRef
    //@JsonBackReference
    @JsonIgnoreProperties("pedidos")
    private Cliente cliente;

    @Builder.Default
    private Status status = Status.PENDENTE;

    @DBRef
    @Builder.Default
    @JsonIgnoreProperties("pedido")
    //@JsonManagedReference
    private List<ItemPedido> itemsPedido = new ArrayList<>();

    @Field(name = "valor_total")
    @DecimalMin(value = "0.00", message = "Preço dever maior ou igual a 0.00")
    private BigDecimal valorTotalPedido;

    public BigDecimal calcularTotalPedido() {
        // Não considera descontos ou impostos
        BigDecimal total = BigDecimal.ZERO;
        for (ItemPedido item : itemsPedido) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

    public void atualizaValorTotalPedido() {
        this.valorTotalPedido = this.calcularTotalPedido();
    }

    public void cancelarPedido() {
        this.status = Status.CANCELADO;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public Pedido updateData(Pedido order) {
        this.numeroPedido = order.getNumeroPedido();
        this.dataPedido = order.getDataPedido();
        this.cliente = order.getCliente();
        this.status = order.getStatus();
        this.itemsPedido = order.getItemsPedido() != null ? order.getItemsPedido() : new ArrayList<>();
        this.calcularTotalPedido();
        return this;
    }

    
    public void incluirItemPedido(ItemPedido item) {
        itemsPedido.add(item);
        item.setPedido(this);
    }

    public void removerItemPedido(ItemPedido item) {
        itemsPedido.remove(item);
        item.setPedido(null);
    }

}