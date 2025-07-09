package space.lasf.springboot_project.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import space.lasf.springboot_project.base.AbstractIntegrationTest;
import space.lasf.springboot_project.domain.repository.ClienteRepository;
import space.lasf.springboot_project.dto.ClienteDto;


public class ClienteControllerIntegrationTest extends AbstractIntegrationTest{

  
    @Autowired
    private ClienteRepository clienteRepository;
    private ClienteDto clienteDto;

    @BeforeEach
    public void init() {
        clienteDto = gerarClienteDto("Marta Rocha", "(51) 99999-5555");
    }

    @Test
    @DisplayName("Cliente Path Test: salvar cliente dto e retornar")
    public void dadoClienteDtoCorreto_entaoSalvaCliente_eRetornaClienteDto()
      throws Exception {

        // when
        ClienteDto savedClienteDto = performPostRequestExpectedSuccess(
                                    CLIENTES_API_ENDPOINT, clienteDto, ClienteDto.class);

        //then
        assertNotNull(savedClienteDto);
        assertEquals(clienteDto.getEmail(), savedClienteDto.getEmail());
        assertEquals(clienteDto.getNome(), savedClienteDto.getNome());
        assertEquals(clienteDto.getTelefone(), savedClienteDto.getTelefone());
    }
    
}
