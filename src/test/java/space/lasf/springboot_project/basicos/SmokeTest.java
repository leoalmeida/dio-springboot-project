package space.lasf.springboot_project.basicos;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import space.lasf.springboot_project.DioSpringbootProjectApplication;
import space.lasf.springboot_project.controller.ClienteController;
import space.lasf.springboot_project.controller.PedidoController;
import space.lasf.springboot_project.controller.ProdutoController;
import space.lasf.springboot_project.controller.UsuarioController;

@SpringBootTest
@ContextConfiguration(classes = DioSpringbootProjectApplication.class)
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