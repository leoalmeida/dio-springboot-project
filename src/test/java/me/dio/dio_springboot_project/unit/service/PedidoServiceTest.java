package me.dio.dio_springboot_project.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import me.dio.dio_springboot_project.core.util.ObjectsValidator;
import me.dio.dio_springboot_project.domain.model.Cliente;
import me.dio.dio_springboot_project.domain.model.Pedido;
import me.dio.dio_springboot_project.domain.repository.PedidoRepository;
import me.dio.dio_springboot_project.service.impl.PedidoServiceImpl;

@ExtendWith(SpringExtension.class)
public class PedidoServiceTest {

    @Mock
    private PedidoRepository repository;

    @Mock
    private ObjectsValidator<Pedido> validator;
    
    @InjectMocks
    private PedidoServiceImpl service;

    Cliente cliente1;
    Cliente cliente2;
    Cliente cliente3;
    Pedido pedido;

    @BeforeEach
    public void setUp() {
    }
}