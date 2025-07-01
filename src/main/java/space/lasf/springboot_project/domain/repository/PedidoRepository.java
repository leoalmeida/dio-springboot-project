package space.lasf.springboot_project.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import space.lasf.springboot_project.domain.model.Pedido;

/**
 * Repositório para a entidade Product.
 */
public interface PedidoRepository extends MongoRepository<Pedido, String> {

    @Query("{ 'numero_pedido':  ?0 }")
    Optional<Pedido> findByNumeroPedido(String numeroPedido);

    @Query("{ 'cliente.id':  ?0 }")
    List<Pedido> findPedidosByIdCliente(String idCliente);
}
