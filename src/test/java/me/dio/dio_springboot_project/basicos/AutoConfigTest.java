package me.dio.dio_springboot_project.basicos;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import me.dio.dio_springboot_project.DioSpringbootProjectApplication;
import me.dio.dio_springboot_project.domain.model.Cliente;
import me.dio.dio_springboot_project.domain.model.ItemPedido;
import me.dio.dio_springboot_project.domain.model.Pedido;
import me.dio.dio_springboot_project.domain.model.Produto;
import me.dio.dio_springboot_project.domain.model.Usuario;


@DataMongoTest
@ContextConfiguration(classes = DioSpringbootProjectApplication.class)
@ActiveProfiles("test")
public class AutoConfigTest {
  @Test
  void example(@Autowired final MongoTemplate mongoTemplate) {
    assertThat(mongoTemplate.getDb()).isNotNull();
      mongoTemplate.dropCollection(Cliente.class);
      mongoTemplate.dropCollection(Pedido.class);
      mongoTemplate.dropCollection(ItemPedido.class);
      mongoTemplate.dropCollection(Produto.class);
      mongoTemplate.dropCollection(Usuario.class);
  }
}