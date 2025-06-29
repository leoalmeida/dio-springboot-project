package me.dio.dio_springboot_project.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import me.dio.dio_springboot_project.domain.model.ItemPedido;

/**
 * Repositório para a entidade Product.
 */
public interface ItemPedidoRepository extends MongoRepository<ItemPedido, String> {


}
