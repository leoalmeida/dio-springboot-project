package space.lasf.springboot_project.controller;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import space.lasf.springboot_project.domain.model.ItemPedido;
import space.lasf.springboot_project.domain.model.Pedido;
import space.lasf.springboot_project.domain.model.Status;
import space.lasf.springboot_project.dto.ItemPedidoDto;
import space.lasf.springboot_project.dto.PedidoDto;
import space.lasf.springboot_project.dto.mapper.ItemPedidoMapper;
import space.lasf.springboot_project.dto.mapper.PedidoMapper;
import space.lasf.springboot_project.service.PedidoService;

/**
 * Controller para gerenciamento de pedidos.
 */
@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;
    @Autowired
    private ObjectsValidator<ItemPedidoDto> itemPedidoValidator;
    @Autowired
    private ObjectsValidator<PedidoDto> pedidoValidator;


    @GetMapping
    public ResponseEntity<List<PedidoDto>> buscarTodosPedidos() {
        List<PedidoDto> pedidos = PedidoMapper.toListPedidoDto(pedidoService.buscarTodosPedidos());
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDto> buscarPedidoPorId(@PathVariable String id) {
        return pedidoService.buscarPedidoPorId(id)
                .map(PedidoMapper::toPedidoDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/numero/{numeroPedido}")
    public ResponseEntity<PedidoDto> buscarPedidoPorNumeroPedido(@PathVariable String numeroPedido) {
        return pedidoService.buscarPedidoPorNumero(numeroPedido)
                .map(PedidoMapper::toPedidoDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<PedidoDto>> buscarPedidosPorIdCliente(@PathVariable String idCliente) {
        List<PedidoDto> pedidos = PedidoMapper.toListPedidoDto(pedidoService.buscarPedidosPorIdCliente(idCliente));
        return ResponseEntity.ok(pedidos);
    }

    @PostMapping
    public ResponseEntity<PedidoDto> criarPedido(@RequestParam String idCliente, @RequestBody List<ItemPedidoDto> items) {
        items.forEach(itemPedidoValidator::validate);
        List<ItemPedido> entities = ItemPedidoMapper.toListItemPedidoEntity(items);
        Pedido pedidoCriado = pedidoService.criarPedido(idCliente, entities);
        PedidoDto novoPedido = PedidoMapper.toPedidoDto(pedidoCriado);
        return ResponseEntity.status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(novoPedido);
    }

    @PostMapping("/{idPedido}/items")
    public ResponseEntity<Void> incluirItemAoPedido(@PathVariable String idPedido, @RequestBody ItemPedidoDto item) {

        itemPedidoValidator.validate(item);
        ItemPedido itemEntity = ItemPedidoMapper.toItemPedidoEntity(item);
        pedidoService.alterarItemPedido(idPedido, itemEntity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{idPedido}/items/{idItemPedido}")
    public ResponseEntity<Void> removerItemDoPedido(@PathVariable String idPedido, @PathVariable String idItemPedido) {
        pedidoService.removerItemDoPedido(idPedido, idItemPedido);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{idPedido}/items")
    public ResponseEntity<Void> alterarItemDoPedido(@PathVariable String idPedido, @RequestBody ItemPedidoDto item) {
        itemPedidoValidator.validate(item);
        ItemPedido itemEntity = ItemPedidoMapper.toItemPedidoEntity(item);
        pedidoService.alterarItemPedido(idPedido, itemEntity);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{idPedido}/total")
    public ResponseEntity<BigDecimal> calcularTotalDoPedido(@PathVariable String idPedido) {
        return ResponseEntity.ok(pedidoService.calcularValorTotalPedido(idPedido));
    }

    @PostMapping("/{idPedido}/finalizar")
    public ResponseEntity<Void> finalizarPedido(@PathVariable String idPedido) {
        pedidoService.finalizarPedido(idPedido);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{idPedido}/cancelar")
    public ResponseEntity<Void> cancelarPedido(@PathVariable String idPedido) {
        pedidoService.cancelarPedido(idPedido);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{idPedido}/validar")
    public ResponseEntity<Map<String, Boolean>> validarPedido(@PathVariable String idPedido) {
        Optional<PedidoDto> pedido = pedidoService.buscarPedidoPorId(idPedido)
                        .map(PedidoMapper::toPedidoDto)
                        .map(pedidoValidator::validate);
        return (pedido.isPresent() && Status.PENDENTE.toString().compareTo(pedido.get().getStatus())==0)
                            ?ResponseEntity.ok(Map.of(idPedido, true))
                            :ResponseEntity.ok(Map.of(idPedido, false));
    }
}