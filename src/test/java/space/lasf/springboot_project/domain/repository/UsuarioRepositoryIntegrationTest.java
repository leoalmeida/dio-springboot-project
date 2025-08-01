package space.lasf.springboot_project.domain.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import space.lasf.springboot_project.base.TestFactory;
import space.lasf.springboot_project.domain.model.Usuario;
import space.lasf.springboot_project.domain.repository.UsuarioRepository;

//@ExtendWith(SpringExtension.class)
@DataMongoTest
@ActiveProfiles("test")
public class UsuarioRepositoryIntegrationTest extends TestFactory{
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired 
    private MongoTemplate mongoTemplate;

    Usuario usuario1;
    Usuario usuario2;

    @BeforeEach
    public void setUp() {
        // Cria usuarios para testes básicos
        usuario1 = gerarUsuario();
        usuario2 = gerarUsuario();
        mongoTemplate.insertAll(Arrays.asList(usuario1,usuario2));
    }

    
    @AfterEach
    void clean() {
        mongoTemplate.remove(usuario1);
        mongoTemplate.remove(usuario2);
    }

    @Test
    void givenUserEntity_whenSaveUser_thenUserIsPersisted() {
        // given
        Usuario usuario = gerarUsuario();

        // when
        usuarioRepository.save(usuario);

        // then
        Optional<Usuario> retrievedUser = usuarioRepository.findById(usuario.getId());
        assertTrue(retrievedUser.isPresent());
        assertEquals("userName", retrievedUser.get().getLogin());
    }

}
