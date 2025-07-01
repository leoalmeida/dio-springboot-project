package space.lasf.springboot_project.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.validation.constraints.NotNull;


/**
 * Entidade que representa um cliente.
 */
@Document(collection  = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    @Id
    private String id;
 
    @NotNull
    @Field(name = "nome")
    @TextIndexed
    private String nome;

    @Field(name = "email")
    private String email;

    @Field(name = "telefone")
    private String telefone;

    @DBRef
    @Builder.Default
    @JsonIgnoreProperties("cliente")
    @JsonManagedReference
    private List<Pedido> pedidos = new ArrayList<>();

    public Cliente updateData(Cliente cliente) {
        this.nome = cliente.getNome();
        this.email = cliente.getEmail();
        this.telefone = cliente.getTelefone();
        this.pedidos = cliente.getPedidos() != null ? cliente.getPedidos() : new ArrayList<>();
        return this;
    }

    public void incluirPedido(Pedido pedido) {
        pedidos.add(pedido);
        pedido.setCliente(this);
    }

    public void removerPedido(Pedido pedido) {
        pedidos.remove(pedido);
        pedido.setCliente(null);
    }
}