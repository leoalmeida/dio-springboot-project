package space.lasf.springboot_project.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import space.lasf.springboot_project.core.util.ObjectsValidator;
import space.lasf.springboot_project.domain.model.Cliente;
import space.lasf.springboot_project.domain.repository.ClienteRepository;
import space.lasf.springboot_project.service.ClienteService;


/**
 * Implementação do serviço para gerenciamento de clientes.
 */
@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {
 
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private ObjectsValidator<Cliente> validadorDeCliente;
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9+_.-]+\\.[a-z]{2,4}$");

    @Override
    @Transactional
    public Cliente criarCliente(Cliente cliente) {
        // Valida os dados do cliente antes de salvar
        if (!validarEmailCliente(cliente.getEmail())) {
            throw new IllegalArgumentException("Email inválido: " + cliente.getEmail());
        }
        validadorDeCliente.validate(cliente);

        return clienteRepository.save(cliente);

    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    public Optional<Cliente> buscarClientePorId(String id) {
        return clienteRepository.findById(id);
    }

    @Override
    public Optional<Cliente> buscarClientePorEmail(String email) {
        if (!validarEmailCliente(email)) {
            throw new IllegalArgumentException("false ");
        }
        
        return clienteRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> buscarTodosClientes() {
        return clienteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> buscarClientesPorNome(String nome) {
        return clienteRepository.findByNomeContaining(nome);
    }

    @Override
    @Transactional
    public Cliente alterarCliente(Cliente cliente) {
        // Valida os dados do cliente antes de salvar
        if (!validarEmailCliente(cliente.getEmail())) {
            throw new IllegalArgumentException("Email inválido: " + cliente.getEmail());
        }
        validadorDeCliente.validate(cliente);
        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional
    public void removerCliente(String idCliente) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(idCliente);
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            clienteRepository.delete(cliente);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> buscarClientesComPedidos() {
        return clienteRepository.findClientesWithPedidos();
    }

    @Override
    public boolean validarEmailCliente(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        
        return EMAIL_PATTERN.matcher(email).matches();
    }
}