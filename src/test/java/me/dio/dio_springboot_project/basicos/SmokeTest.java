package me.dio.dio_springboot_project.basicos;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import me.dio.dio_springboot_project.controller.ClienteController;
import me.dio.dio_springboot_project.controller.PedidoController;
import me.dio.dio_springboot_project.controller.ProdutoController;
import me.dio.dio_springboot_project.controller.UsuarioController;

@SpringBootTest
@ActiveProfiles("test")
public class SmokeTest {

	@Autowired
	private ClienteController clienteController;
	@Autowired
	private ProdutoController produtoController;
	@Autowired
	private PedidoController pedidoController;
	@Autowired
	private UsuarioController usuarioController;

	@Test
	void contextLoads() throws Exception {
		assertThat(clienteController).isNotNull();
		assertThat(produtoController).isNotNull();
		assertThat(pedidoController).isNotNull();
		assertThat(usuarioController).isNotNull();
	}
}