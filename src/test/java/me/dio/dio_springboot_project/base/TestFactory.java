package me.dio.dio_springboot_project.base;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

import me.dio.dio_springboot_project.domain.model.Cliente;
import me.dio.dio_springboot_project.domain.model.ItemPedido;
import me.dio.dio_springboot_project.domain.model.Pedido;
import me.dio.dio_springboot_project.domain.model.Produto;
import me.dio.dio_springboot_project.domain.model.Usuario;
import me.dio.dio_springboot_project.dto.ClienteDto;
import me.dio.dio_springboot_project.dto.ItemPedidoDto;
import me.dio.dio_springboot_project.dto.PedidoDto;
import me.dio.dio_springboot_project.dto.ProdutoDto;
import me.dio.dio_springboot_project.dto.UsuarioDto;
import me.dio.dio_springboot_project.dto.mapper.ClienteMapper;
import me.dio.dio_springboot_project.dto.mapper.ItemPedidoMapper;
import me.dio.dio_springboot_project.dto.mapper.PedidoMapper;
import me.dio.dio_springboot_project.dto.mapper.ProdutoMapper;
import me.dio.dio_springboot_project.dto.mapper.UsuarioMapper;

public class TestFactory {

    private static Integer proximoIdPedido = 1;
    public static final String USUARIOS_API_ENDPOINT = "/api/usuarios";
    public static final String PEDIDOS_API_ENDPOINT = "/api/pedidos";
    public static final String CLIENTES_API_ENDPOINT = "/api/clientes";
    public static final String PRODUTOS_API_ENDPOINT = "/api/produtos";

    public UsuarioDto gerarUsuarioDTO() {
        return UsuarioMapper.toUsuarioDto(gerarUsuario());
    }

    public Usuario gerarUsuario() {
        return Usuario.builder()
                .id(UUID.randomUUID().toString())
                .email("mail@teste.com")
                .login("userName")
                .password("userPassword")
                .build();
    }
    
    public PedidoDto gerarPedidoDto(Cliente cliente) {
        return PedidoMapper.toPedidoDto(gerarPedido(cliente));
    }

    public Pedido gerarPedido(Cliente cliente) {        
        if (null==cliente) throw new IllegalArgumentException("Cliente não pode ser nulo");
        Pedido pedido = Pedido.builder()
                .id(UUID.randomUUID().toString())
                .numeroPedido(gerarNumeroPedido())
                .dataPedido(LocalDateTime.now())
                .valorTotalPedido(BigDecimal.ZERO)
                .cliente(cliente)
                .build();
        pedido.atualizaValorTotalPedido();
        return pedido;
    }

    public ItemPedidoDto gerarItemPedidoDto() {
        return ItemPedidoMapper.toItemPedidoDto(gerarItemPedido());
    }

    public ItemPedidoDto gerarItemPedidoDto(Cliente cliente) {
        Pedido pedido = gerarPedido(cliente);
        Produto produto = gerarProduto();
        return gerarItemPedidoDto(pedido,produto);
    }

    public ItemPedidoDto gerarItemPedidoDto(Pedido pedido, Produto produto) {
        return ItemPedidoMapper.toItemPedidoDto(gerarItemPedido(pedido,produto));
    }

    public ItemPedido gerarItemPedido() {
        Produto regProduto = gerarProduto();
        ItemPedido itemPedido = ItemPedido.builder()
                .id(UUID.randomUUID().toString())
                .quantidade((int)(Math.random() * 10) + 1)
                .precoUnitario(regProduto.getPreco())
                .produto(regProduto)
                .build();

        itemPedido.updateSubtotal();
        return itemPedido;
    }

    public ItemPedido gerarItemPedido(Pedido pedido, Produto produto) {
        if (null==pedido) throw new IllegalArgumentException("Pedido não pode ser nulo");
        Produto regProduto = (null==produto)?gerarProduto():produto;
        ItemPedido itemPedido = ItemPedido.builder()
                .id(UUID.randomUUID().toString())
                .quantidade((int)(Math.random() * 10) + 1)
                .precoUnitario(regProduto.getPreco())
                .produto(regProduto)
                .build();
        
        pedido.incluirItemPedido(itemPedido);
        pedido.atualizaValorTotalPedido();
    
        itemPedido.updateSubtotal();
        return itemPedido;
    }

    public ClienteDto gerarClienteDto(String nome, String telefone) {
        return ClienteMapper.toClienteDto(gerarCliente(nome,telefone));
    }

    public Cliente gerarCliente(String nome, String telefone) {
        String regNome = (null==nome||nome.isBlank())?"Jonas Morgan":nome;
        String email = regNome.replace(" ", ".").concat("@example.com");
        Cliente cliente1 = Cliente.builder()
                .id(UUID.randomUUID().toString())
                .nome(regNome)
                .email(email)
                .telefone((null==telefone||telefone.isBlank())?"(11) 99999-1111":telefone)
                .build();
        return cliente1;
    }

    public ProdutoDto gerarProdutoDto() {
        return ProdutoMapper.toProdutoDto(gerarProduto());
    }

    public Produto gerarProduto() {
        Integer regProduto = (int)(Math.random() * 999) + 1;
        Produto produto = Produto.builder()
                .id(UUID.randomUUID().toString())
                .nome("Produto "+regProduto)
                .descricao("Descrição do Produto " + regProduto)
                .preco(new BigDecimal(Math.random()*100).round(new MathContext(2, RoundingMode.FLOOR)))
                .estoque((int)(Math.random() * 1000) + 1)
                .sku(String.format("SKU%03d",regProduto))
                .build();
        return produto;
    }

    
    public String gerarNumeroPedido() {
        return String.format("ORD-%02d%04d", LocalDateTime.now().getDayOfMonth(), proximoIdPedido++);
    }
}
