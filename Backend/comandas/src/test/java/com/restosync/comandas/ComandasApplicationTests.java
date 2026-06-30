package com.restosync.comandas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersMySqlConfig.class)
@RequiresDocker
class ComandasApplicationTests {

	@Test
	void contextLoads() {
	}

}
