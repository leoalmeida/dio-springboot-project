package me.dio.dio_springboot_project.domain.repository;

import me.dio.dio_springboot_project.domain.model.Usuario;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsuarioRepository extends MongoRepository<Usuario, String>{
    
}
