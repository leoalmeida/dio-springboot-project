package space.lasf.springboot_project.integration.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import space.lasf.springboot_project.base.AbstractIntegrationTest;
import space.lasf.springboot_project.domain.repository.UsuarioRepository;
import space.lasf.springboot_project.dto.UsuarioDto;


public class UsuarioControllerIntegrationTest extends AbstractIntegrationTest {


    @Autowired
    private UsuarioRepository repository;
    private UsuarioDto usuarioDto;
    
    @BeforeEach
    public void init() {
        usuarioDto = gerarUsuarioDTO();
    }

    
    @Test
    @DisplayName("Usuario Path Test: salvar usuario dto e retornar")
    public void dadoUsuarioDtoCorreto_entaoSalvaUsuario_eRetornaUsuarioDto()
      throws Exception {

        // when
        UsuarioDto savedUsuarioDto = performPostRequestExpectedSuccess(
                                    USUARIOS_API_ENDPOINT, usuarioDto, UsuarioDto.class);

        //then
        assertNotNull(savedUsuarioDto);
        assertEquals(usuarioDto.getLogin(), savedUsuarioDto.getLogin());
        assertEquals(usuarioDto.getPassword(), savedUsuarioDto.getPassword());
    }
}
