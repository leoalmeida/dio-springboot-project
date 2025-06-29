package me.dio.dio_springboot_project.integration.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;
import me.dio.dio_springboot_project.domain.repository.ClienteRepository;
import me.dio.dio_springboot_project.dto.ClienteDto;
import me.dio.dio_springboot_project.integration.base.AbstractIntegrationTest;


public class ClienteControllerIntegrationTest extends AbstractIntegrationTest{

  
    @Autowired
    private ClienteRepository clienteRepository;
    private ClienteDto clienteDto;

    @PostConstruct
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
        assertEquals("joao.silva@example.com", savedClienteDto.getEmail());
        assertEquals("João Silva", savedClienteDto.getNome());
        assertEquals("(11) 99999-1111", savedClienteDto.getTelefone());
    }
    
}
