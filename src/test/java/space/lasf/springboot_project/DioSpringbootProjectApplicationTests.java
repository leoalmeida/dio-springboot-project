package space.lasf.springboot_project;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import space.lasf.springboot_project.DioSpringbootProjectApplication;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DioSpringbootProjectApplication.class)
@ActiveProfiles("test")
@TestPropertySource(properties = { "logging.level.org.springframework=DEBUG",
    "logging.level.org.hibernate=DEBUG" })
class DioSpringbootProjectApplicationTests {

	@Test
	void contextLoads() {
		assertTrue(true);
	}

}
