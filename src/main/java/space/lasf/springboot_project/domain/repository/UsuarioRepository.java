package space.lasf.springboot_project.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import space.lasf.springboot_project.domain.model.Usuario;

public interface UsuarioRepository extends MongoRepository<Usuario, String>{
    
}
