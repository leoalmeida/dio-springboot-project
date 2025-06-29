package me.dio.dio_springboot_project.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.dio.dio_springboot_project.core.util.ObjectsValidator;
import me.dio.dio_springboot_project.domain.model.Cliente;
import me.dio.dio_springboot_project.domain.model.ItemPedido;
import me.dio.dio_springboot_project.domain.model.Pedido;
import me.dio.dio_springboot_project.domain.model.Produto;
import me.dio.dio_springboot_project.domain.model.Status;
import me.dio.dio_springboot_project.domain.repository.ClienteRepository;
import me.dio.dio_springboot_project.domain.repository.ItemPedidoRepository;
import me.dio.dio_springboot_project.domain.repository.PedidoRepository;
import me.dio.dio_springboot_project.domain.repository.ProdutoRepository;
import me.dio.dio_springboot_project.service.PedidoService;

/**
 * Implementação do serviço para gerenciamento de pedidos.
 */
@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private ProdutoRepository produtoRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private ItemPedidoRepository itemPedidoRepository;
    
    @Autowired
    private ObjectsValidator<ItemPedido> validadorDeItem;

    private static Integer proximoIdPedido = 1;

    @Override
    @Transactional
    public Pedido criarPedido(String idCliente, List<ItemPedido> items) {
        if (idCliente == null ) {
            throw new IllegalArgumentException("ID do cliente inválido: " + idCliente);
        }
        Cliente cliente = clienteRepository.findById(idCliente)
				.orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        Pedido pedido = new Pedido();
        pedido.setId(UUID.randomUUID().toString());
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setNumeroPedido(gerarNumeroPedido());
        pedido.setCliente(cliente);

        // Adiciona os itens ao pedido
        if (items != null && !items.isEmpty()) {
            items.forEach(item -> {
                // Valida os dados do item antes de salvar
                validadorDeItem.validate(item);
                // Associa o item ao pedido
                item.updateSubtotal();
                pedido.incluirItemPedido(item);

                itemPedidoRepository.save(item);
            });
        }
        
        BigDecimal pedidoTotal = pedido.calcularTotalPedido();
        pedido.setValorTotalPedido(pedidoTotal);
        
        Pedido newPedido = pedidoRepository.save(pedido);

        cliente.getPedidos().add(newPedido);
        clienteRepository.save(cliente);
        
        return newPedido;
    }

    @Override
    public Optional<Pedido> buscarPedidoPorId(String id) {
        return pedidoRepository.findById(id);
    }

    @Override
    public Optional<Pedido> buscarPedidoPorNumero(String pedidoNumber) {
        if (!validarNumeroPedido(pedidoNumber)) {
            throw new IllegalArgumentException("Número de pedido inválido: " + pedidoNumber);
        }
   
        return pedidoRepository.findByNumeroPedido(pedidoNumber);
            //.orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado com o número: " + pedidoNumber));
    }

    @Override
    public List<Pedido> buscarTodosPedidos() {
        return pedidoRepository.findAll();
    }

    @Override
    public List<Pedido> buscarPedidosPorIdCliente(String idCliente) {
        if (idCliente == null) {
            throw new IllegalArgumentException("ID do cliente inválido: " + idCliente);
        }
        return pedidoRepository.findOrdersByClienteId(idCliente);
    }

    @Override
    @Transactional
    public void incluirItemAoPedido(String idPedido, ItemPedido item) {
        Pedido pedidoAlterado = buscarPedidoPorId(idPedido)
            .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado com o id: " + idPedido));

        // Valida os dados do item antes de salvar
        validadorDeItem.validate(item);
        // Associa o item ao pedido
        item.updateSubtotal();
        
        // Atualiza o pedido com o novo item
        pedidoAlterado.incluirItemPedido(item);
        pedidoAlterado.setValorTotalPedido(pedidoAlterado.calcularTotalPedido());

        itemPedidoRepository.save(item);
        pedidoRepository.save(pedidoAlterado);
    }

    @Override
    @Transactional
    public void removerItemDoPedido(String idPedido, String itemId) {
        Pedido pedidoAlterado = buscarPedidoPorId(idPedido)
            .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado com o id: " + idPedido));

        ItemPedido removedItem = pedidoAlterado.getItemsPedido().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .map(changedItem -> {
                    return changedItem;
                })
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado no pedido"));
        pedidoAlterado.removerItemPedido(removedItem);
        pedidoAlterado.setValorTotalPedido(pedidoAlterado.calcularTotalPedido());
        
        itemPedidoRepository.deleteById(itemId);
        pedidoRepository.save(pedidoAlterado);
    }

    @Override
    @Transactional
    public void alterarItemPedido(String idPedido, ItemPedido item) {
        Pedido pedidoAlterado = buscarPedidoPorId(idPedido)
            .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado com o id: " + idPedido));

        // Valida os dados do item antes de salvar
        validadorDeItem.validate(item);
        item.updateSubtotal();
        Optional<ItemPedido> itemAlterado = pedidoAlterado.getItemsPedido().stream()
            .filter(existingItem -> existingItem.getId().compareTo(item.getId())==0)
            .findFirst()
            .map(existingItem -> existingItem.updateData(item));

        itemAlterado.ifPresent(existingItem -> {
                itemPedidoRepository.save(item);
                pedidoAlterado.setValorTotalPedido(pedidoAlterado.calcularTotalPedido());
                pedidoRepository.save(pedidoAlterado);
            });
    }

    @Override
    @Transactional
    public BigDecimal calcularValorTotalPedido(String idPedido) {
        Pedido pedidoAlterado = buscarPedidoPorId(idPedido)
            .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado com o id: " + idPedido));
          
        BigDecimal total = pedidoAlterado.calcularTotalPedido();
        pedidoAlterado.setValorTotalPedido(total);
        pedidoRepository.save(pedidoAlterado);
        return total;
    }

    @Override
    @Transactional
    public void finalizarPedido(String idPedido) {
        Pedido pedidoAlterado = buscarPedidoPorId(idPedido)
            .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado com o id: " + idPedido));

        pedidoAlterado.getItemsPedido().forEach(item -> {
            // Associa o item ao pedido
            Produto produtoAlterado = item.getProduto();
            produtoAlterado.setEstoque(produtoAlterado.getEstoque() - item.getQuantidade());
            produtoRepository.save(produtoAlterado);
        });

        // Atualiza o valor total do pedido
        pedidoAlterado.setValorTotalPedido(pedidoAlterado.calcularTotalPedido());
        pedidoAlterado.setStatus(Status.FINALIZADO);
        pedidoRepository.save(pedidoAlterado);
    }

    @Override
    @Transactional
    public void cancelarPedido(String idPedido) {
        Pedido pedidoAlterado = buscarPedidoPorId(idPedido)
            .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado com o id: " + idPedido));

        pedidoAlterado.cancelarPedido();
        pedidoRepository.save(pedidoAlterado);
    }

    private String gerarNumeroPedido() {
        return String.format("ORD-%02d%04d", LocalDateTime.now().getDayOfMonth(), proximoIdPedido++);
    }

    private boolean validarNumeroPedido(String pedidoNumber) {
        return pedidoNumber != null && !pedidoNumber.isEmpty() && pedidoNumber.matches("ORD-[0-9]{1,2}[0-9]{4}");
    }
}
