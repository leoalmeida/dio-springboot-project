package space.lasf.springboot_project.basicos;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.text.MessageFormat;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import space.lasf.springboot_project.base.TestFactory;
import space.lasf.springboot_project.domain.model.Cliente;

@DataMongoTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SimpleTest extends TestFactory{

	@Autowired 
    private MongoTemplate mongoTemplate;

	Cliente cliente1;
    Cliente cliente2;
    Cliente cliente3;

	@BeforeEach
	void setUp() throws Exception {
		System.out.println("Preparando dados que serão utilizados nos testes.");
		cliente1 = gerarCliente("João Silva", "(11) 99999-1111");
        cliente2 = gerarCliente("Maria Santos", "(21) 99999-2222");
        cliente3 = gerarCliente("Pedro Oliveira", "(31) 99999-3333");
        mongoTemplate.insertAll(Arrays.asList(cliente1,cliente2,cliente3));
		long entitySize = mongoTemplate.count(new Query(), Cliente.class);
		System.out.println(MessageFormat.format("Colection Cliente possui {0} registros.", entitySize));
	}

	@AfterEach
	void tearDown() throws Exception {
		System.out.println("Removendo dados utilizados no teste.");
		mongoTemplate.remove(cliente1);
        mongoTemplate.remove(cliente2);
        mongoTemplate.remove(cliente3);
		long entitySize = mongoTemplate.count(new Query(), Cliente.class);
		System.out.println(MessageFormat.format("Colection Cliente possui {0} registros.", entitySize));
	}


	@Test
	void contextLoads() throws Exception {
		assumeTrue(cliente1!=null);
		System.out.println("Executando SimpleTest.");
	}
}