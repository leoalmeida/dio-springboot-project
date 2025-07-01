package space.lasf.springboot_project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import space.lasf.springboot_project.core.util.ObjectsValidator;
import space.lasf.springboot_project.domain.model.Cliente;
import space.lasf.springboot_project.dto.ClienteDto;
import space.lasf.springboot_project.dto.mapper.ClienteMapper;
import space.lasf.springboot_project.service.ClienteService;


/**
 * Controller para gerenciamento de clientes.
 */
@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ObjectsValidator<ClienteDto> clienteValidator;


    @GetMapping
    public ResponseEntity<List<ClienteDto>> buscarClientes() {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
            .body(ClienteMapper.toListClienteDto(clienteService.buscarTodosClientes()));
    }

    @GetMapping("/com-pedidos")
    public ResponseEntity<List<ClienteDto>> buscarClientesComPedidos() {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body(ClienteMapper.toListClienteDto(clienteService.buscarClientesComPedidos()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDto> buscarClientePorId(@PathVariable String id) {
        return clienteService.buscarClientePorId(id)
                .map(ClienteMapper::toClienteDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ClienteDto> buscarClientePorEmail(@PathVariable String email) {
        if (!clienteService.validarEmailCliente(email)) {
            return ResponseEntity.notFound().build();
        }
        return clienteService.buscarClientePorEmail(email)
                .map(ClienteMapper::toClienteDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pesquisar")
    public ResponseEntity<List<ClienteDto>> buscarClientesPorNome(@RequestParam String nome) {
        List<Cliente> listaClientes = clienteService.buscarClientesPorNome(nome);
        List<ClienteDto> clientes = ClienteMapper.toListClienteDto(listaClientes);
        return ResponseEntity.ok(clientes);
    }

    @PostMapping
    public ResponseEntity<ClienteDto> criarCliente(@RequestBody ClienteDto cliente) {
        clienteValidator.validate(cliente);
        ClienteDto novoCliente = ClienteMapper.toClienteDto(
                        clienteService.criarCliente(ClienteMapper.toClienteEntity(cliente)));
        return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(novoCliente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDto> alterarCliente(@PathVariable String id, @RequestBody ClienteDto cliente) {
        clienteValidator.validate(cliente);
        clienteService.alterarCliente(ClienteMapper.toClienteEntity(cliente));
        return ResponseEntity.ok(cliente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerCliente(@PathVariable String id) {
        clienteService.removerCliente(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/validar-email")
    public ResponseEntity<Boolean> validateEmail(@RequestParam String email) {
        if (clienteService.validarEmailCliente(email)) {
            return ResponseEntity.ok(true);
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }
}