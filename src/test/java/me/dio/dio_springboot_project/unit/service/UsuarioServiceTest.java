package me.dio.dio_springboot_project.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.UUID;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import me.dio.dio_springboot_project.core.util.ObjectsValidator;
import me.dio.dio_springboot_project.domain.model.Usuario;
import me.dio.dio_springboot_project.domain.repository.UsuarioRepository;
import me.dio.dio_springboot_project.dto.UsuarioDto;
import me.dio.dio_springboot_project.service.impl.UsuarioServiceImpl;


@ExtendWith(SpringExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private ObjectsValidator<UsuarioDto> validator;
    
    @InjectMocks
    private UsuarioServiceImpl service;

    private Usuario usuario1;
    private Usuario usuario2;

    @BeforeEach
    public void setUp() {
        usuario1 = new Usuario(UUID.randomUUID().toString(),"email@test.com","user","pass");
        usuario2 = new Usuario(UUID.randomUUID().toString(),"email2@test.com","user","pass");

    }
}