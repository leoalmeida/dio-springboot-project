package me.dio.dio_springboot_project.integration.base;

import java.math.BigDecimal;
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
    
    public PedidoDto gerarPedidoDto() {
        return PedidoMapper.toPedidoDto(gerarPedido());
    }

    public PedidoDto gerarPedidoDto(Cliente cliente, ItemPedido itemPedido) {
        return PedidoMapper.toPedidoDto(gerarPedido(cliente,itemPedido));
    }

    public Pedido gerarPedido() {
        return gerarPedido(null,null);
    }

    public Pedido gerarPedido(Cliente cliente, ItemPedido itemPedido) {
        Cliente regCliente = (null==cliente)?gerarCliente(null,null):cliente;
        ItemPedido regItemPedido = (null==itemPedido)?gerarItemPedido():itemPedido;
        Pedido pedido = Pedido.builder()
                .id(UUID.randomUUID().toString())
                .numeroPedido(gerarNumeroPedido())
                .dataPedido(LocalDateTime.now())
                .valorTotalPedido(BigDecimal.ZERO)
                .cliente(regCliente)
                .build();
        pedido.incluirItemPedido(regItemPedido);
        pedido.atualizaValorTotalPedido();
        return pedido;
    }

    public ItemPedidoDto gerarItemPedidoDto() {
        return gerarItemPedidoDto(null,null);
    }

    public ItemPedidoDto gerarItemPedidoDto(Pedido pedido, Produto produto) {
        return ItemPedidoMapper.toItemPedidoDto(gerarItemPedido(pedido,produto));
    }

    public ItemPedido gerarItemPedido() {
        return gerarItemPedido(null,null);
    }

    public ItemPedido gerarItemPedido(Pedido pedido, Produto produto) {
        Produto regProduto = (null==produto)?gerarProduto():produto;
        Pedido regPedido = (null==pedido)?gerarPedido():pedido;
        ItemPedido itemPedido = ItemPedido.builder()
                .id(UUID.randomUUID().toString())
                .pedido(regPedido)
                .quantidade((int)(Math.random() * 10) + 1)
                .precoUnitario(regProduto.getPreco())
                .produto(regProduto)
                .build();
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
        cliente1.incluirPedido(gerarPedido());
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
                .preco(new BigDecimal(Math.random()*100))
                .estoque((int)(Math.random() * 1000) + 1)
                .sku(String.format("SKU%03d",regProduto))
                .build();
        return produto;
    }

    
    public String gerarNumeroPedido() {
        return String.format("ORD-%02d%04d", LocalDateTime.now().getDayOfMonth(), proximoIdPedido++);
    }
}
