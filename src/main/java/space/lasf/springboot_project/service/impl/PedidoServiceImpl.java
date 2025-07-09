package space.lasf.springboot_project.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import space.lasf.springboot_project.core.util.ObjectsValidator;
import space.lasf.springboot_project.domain.model.Cliente;
import space.lasf.springboot_project.domain.model.ItemPedido;
import space.lasf.springboot_project.domain.model.Pedido;
import space.lasf.springboot_project.domain.model.Produto;
import space.lasf.springboot_project.domain.model.Status;
import space.lasf.springboot_project.domain.repository.ClienteRepository;
import space.lasf.springboot_project.domain.repository.ItemPedidoRepository;
import space.lasf.springboot_project.domain.repository.PedidoRepository;
import space.lasf.springboot_project.domain.repository.ProdutoRepository;
import space.lasf.springboot_project.service.PedidoService;

/**
 * Implementação do serviço para gerenciamento de pedidos.
 */
@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final ProdutoRepository produtoRepository;
    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final ObjectsValidator<ItemPedido> validadorDeItem;

    @Override
    @Transactional
    public Pedido criarPedido(String idCliente, List<ItemPedido> items) {
        if (idCliente == null ) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo.");
        }
        Cliente cliente = clienteRepository.findById(idCliente)
				.orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        Pedido pedido = new Pedido();
        pedido.setId(UUID.randomUUID().toString());
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setNumeroPedido(gerarNumeroPedido());
        pedido.setCliente(cliente);

        List<ItemPedido> itemsToSave = new ArrayList<>();
        // Adiciona os itens ao pedido
        if (items != null && !items.isEmpty()) {
            items.forEach(item -> {
                // Valida os dados do item antes de salvar
                validadorDeItem.validate(item);
                // Associa o item ao pedido
                item.updateSubtotal();
                pedido.incluirItemPedido(item);
                itemsToSave.add(item);
            });
            itemPedidoRepository.saveAll(itemsToSave);
        }
        
        BigDecimal pedidoTotal = pedido.calcularTotalPedido();
        pedido.setValorTotalPedido(pedidoTotal);
        cliente.incluirPedido(pedido);
        
        clienteRepository.save(cliente);
        
        Pedido newPedido = pedidoRepository.save(pedido);
        
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
        return pedidoRepository.findPedidosByIdCliente(idCliente);
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

        if (pedidoAlterado.getStatus() != Status.PENDENTE) {
            throw new IllegalStateException("Apenas pedidos com status PENDENTE podem ser finalizados.");
        }

        Map<String, Integer> quantidadePorProdutoId = getQuantidadeVendida(pedidoAlterado.getItemsPedido());
        List<String> produtoIds = new ArrayList<>(quantidadePorProdutoId.keySet());
        List<Produto> produtosDoPedido = produtoRepository.findAllById(produtoIds);

        if (produtoIds.size() != produtosDoPedido.size()) {
            throw new IllegalStateException("Um ou mais produtos do pedido não foram encontrados.");
        }

        for (Produto produto : produtosDoPedido) {
            int quantidadePedida = quantidadePorProdutoId.get(produto.getId());
            if (produto.getEstoque() < quantidadePedida) {
                throw new IllegalStateException("Estoque insuficiente para o produto: " + produto.getNome());
            }
            produto.setEstoque(produto.getEstoque() - quantidadePedida);
        }

        produtoRepository.saveAll(produtosDoPedido);

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
        // Example: ORD-20240724-9F3B
        String datePart = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return String.format("ORD-%s-%s", datePart, randomPart);
    }

    private boolean validarNumeroPedido(String pedidoNumber) {
        return pedidoNumber != null && pedidoNumber.matches("ORD-\\d{8}-[A-Z0-9]{4}");
    }

    private Map<String, Integer> getQuantidadeVendida(List<ItemPedido> items) {
        Map<String, Integer> statistics = 
            items.stream().collect(Collectors.groupingBy(
                item -> item.getProduto().getId(),
                Collectors.summingInt(item -> item.getQuantidade())
            ));

        return statistics;
    }
}
