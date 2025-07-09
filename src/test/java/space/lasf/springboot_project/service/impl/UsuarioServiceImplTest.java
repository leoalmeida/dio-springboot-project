package space.lasf.springboot_project.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import space.lasf.springboot_project.core.util.ObjectsValidator;
import space.lasf.springboot_project.domain.model.Usuario;
import space.lasf.springboot_project.domain.repository.UsuarioRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ObjectsValidator<Usuario> validator;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(UUID.randomUUID().toString())
                .login("testuser")
                .email("test@example.com")
                .password("password")
                .build();
    }

    @Test
    void criarUsuario_shouldSaveAndReturnUsuario() {
        // Arrange
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        Usuario createdUsuario = usuarioService.criarUsuario(UsuarioMapper.toDto(usuario));

        // Assert
        assertNotNull(createdUsuario);
        assertEquals("testuser", createdUsuario.getLogin());
        verify(validator).validate(usuario);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void buscarUsuarioPorId_shouldReturnUsuario_whenFound() {
        // Arrange
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));

        // Act
        Optional<Usuario> foundUsuario = usuarioService.buscarUsuarioPorId(usuario.getId());

        // Assert
        assertTrue(foundUsuario.isPresent());
        assertEquals(usuario.getId(), foundUsuario.get().getId());
    }

    @Test
    void buscarUsuarioPorId_shouldReturnEmpty_whenNotFound() {
        // Arrange
        when(usuarioRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        // Act
        Optional<Usuario> foundUsuario = usuarioService.buscarUsuarioPorId("non-existent-id");

        // Assert
        assertFalse(foundUsuario.isPresent());
    }

    @Test
    void buscarTodosUsuarios_shouldReturnListOfUsuarios() {
        // Arrange
        when(usuarioRepository.findAll()).thenReturn(Collections.singletonList(usuario));

        // Act
        List<Usuario> usuarios = usuarioService.buscarTodosUsuarios();

        // Assert
        assertFalse(usuarios.isEmpty());
        assertEquals(1, usuarios.size());
    }

    @Test
    void removerUsuario_shouldCallDeleteById() {
        // Arrange
        String userId = usuario.getId();
        // Mock the repository to do nothing when deleteById is called
        doNothing().when(usuarioRepository).deleteById(userId);

        // Act
        usuarioService.removerUsuario(userId);

        // Assert
        verify(usuarioRepository, times(1)).deleteById(userId);
    }
}