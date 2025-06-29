package me.dio.dio_springboot_project.domain.model;

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
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Entidade que representa um produto.
 */
@Document(collection  = "produtos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produto {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Indexed
    private String nome;

    private String descricao;

    @NotNull
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que 0.00")
    private BigDecimal preco;

    @NotNull
    @Min(value = 0, message = "Estoque não pode ser negativo")
    private Integer estoque;

    @NotNull
    private String sku;

    public BigDecimal calcularTotalEstoque() {
        if (estoque == null || preco == null) {
            return BigDecimal.ZERO;
        }
        return preco.multiply(new BigDecimal(estoque));
    }

    public Produto updateData(Produto product) {
        this.nome = product.getNome();
        this.descricao = product.getDescricao();
        this.preco = product.getPreco();
        this.estoque = product.getEstoque();
        this.sku = product.getSku();
        return this;
    }
}
