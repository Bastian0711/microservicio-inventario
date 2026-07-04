package cl.duoc.backend_inventario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class BackendInventarioApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendInventarioApplication.class, args);
	}

}
