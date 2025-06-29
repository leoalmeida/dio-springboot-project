package me.dio.dio_springboot_project.service;


import java.util.List;
import java.util.Optional;

import me.dio.dio_springboot_project.domain.model.Cliente;

/**
 * Serviço para gerenciamento de clientes.
 */
public interface ClienteService {
    
    Cliente criarCliente(Cliente Cliente);
    
    Optional<Cliente> buscarClientePorId(String idCliente);
    
    Optional<Cliente> buscarClientePorEmail(String email);
    
    List<Cliente> buscarTodosClientes();
    
    List<Cliente> buscarClientesPorNome(String nome);
    
    Cliente alterarCliente(Cliente Cliente);
    
    void removerCliente(String idCliente);
    
    List<Cliente> buscarClientesComPedidos();
    
    boolean validarEmailCliente(String email);
}