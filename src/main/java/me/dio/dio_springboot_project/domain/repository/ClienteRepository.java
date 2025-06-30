package me.dio.dio_springboot_project.domain.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import me.dio.dio_springboot_project.domain.model.Cliente;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para a entidade de Cliente.
 */
public interface ClienteRepository extends MongoRepository<Cliente, String> {

    @Query(" { 'email': ?0 }")
    Optional<Cliente> findByEmail(String email);

    @Query(" { 'nome': /?0/ }")
    List<Cliente> findByNomeContaining(String nome);
    
    @Query("{ 'pedidos': {$exists: true, $ne: [] }}")
    List<Cliente> findClientesWithPedidos();
}