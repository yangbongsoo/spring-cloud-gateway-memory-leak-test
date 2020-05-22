package gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

import gateway.multygetbody.type1.MultiGetBodyFilter1;
import gateway.multygetbody.type1.MultiGetBodyFilter2;

@SpringBootApplication
@RestController
public class MyApplication {
	public static void main(String[] args) {
		SpringApplication.run(MyApplication.class, args);
	}

	@Bean
	public RouteLocator myRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(r -> r.path("/")
						.filters(
//								f -> f.filter(multyGetBodyFilter1()).filter(multyGetBodyFilter2())
//								f -> f.filter(multiGetBodyFilter1()).filter(multiGetBodyFilter2())
								f -> f.filter(noFilter())
						)
						.uri("http://local-test.navercorp.com:8080"))
//						.uri("http://localhost:8000"))
				.build();
	}

//	@Bean
//	public RouteLocator myRouteLocator(RouteLocatorBuilder builder) {
//		return builder.routes()
//				.route(r -> r.path("/bong")
//						.filters(f -> f.filter(memoryLeakFilter()))
//						.uri("http://localhost:8000"))
//				.build();
//	}

	@Bean
	public MemoryLeakFilter memoryLeakFilter() {
		return new MemoryLeakFilter();
	}

	@Bean
	public NoMemoryLeakFilter noMemoryLeakFilter() {
		return new NoMemoryLeakFilter();
	}

	@Bean
	public NoFilter noFilter() {
		return new NoFilter();
	}

	@Bean
	public NoMemoryLeakFilter2 noMemoryLeakFilter2() {
		return new NoMemoryLeakFilter2();
	}

	@Bean
	public MultiGetBodyFilter1 multyGetBodyFilter1() {
		return new MultiGetBodyFilter1();
	}

	@Bean
	public MultiGetBodyFilter2 multyGetBodyFilter2() {
		return new MultiGetBodyFilter2();
	}

	@Bean
	public gateway.multygetbody.type2.MultiGetBodyFilter1 multiGetBodyFilter1() {
		return new gateway.multygetbody.type2.MultiGetBodyFilter1();
	}

	@Bean
	public gateway.multygetbody.type2.MultiGetBodyFilter2 multiGetBodyFilter2() {
		return new gateway.multygetbody.type2.MultiGetBodyFilter2();
	}


}
