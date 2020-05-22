package gateway;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class MultiGetBodyFilter1Test {

	@Test
	public void name() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
		map.add("test", "asldfjklsadfjlksajflasdjflkasjflkasjflkasjflkasjflkajlkf");

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<String> postForEntity = restTemplate.postForEntity("http://local-test.navercorp.com:8080/", request, String.class);
		String body = postForEntity.getBody();
		System.out.println(body);
	}

	@Test
	public void test1() {
		while (true) {
			try {
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.postForLocation("http://127.0.0.1:8080/bong", "test=1");
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (Exception e) {

			}
		}
	}


	@Test
	public void test2() {
		while (true) {
			try {
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.postForLocation("http://127.0.0.1:8080/bong", "test=2");
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (Exception e) {

			}
		}
	}

	@Test
	public void test3() {
		while (true) {
			try {
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.postForLocation("http://127.0.0.1:8080/bong", "test=3");
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (Exception e) {

			}
		}
	}

	@Test
	public void test4() {
		while (true) {
			try {
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.postForLocation("http://127.0.0.1:8080/bong", "test=4");
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (Exception e) {

			}
		}
	}

	@Test
	public void test5() {
		while (true) {
			try {
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.postForLocation("http://127.0.0.1:8080/bong", "test=5");
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (Exception e) {

			}
		}
	}

	@Test
	public void test6() {
		while (true) {
			try {
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.postForLocation("http://127.0.0.1:8080/bong", "test=6");
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (Exception e) {

			}
		}
	}

	@Test
	public void test7() {
		while (true) {
			try {
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.postForLocation("http://127.0.0.1:8080/bong", "test=7");
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (Exception e) {

			}
		}
	}

	@Test
	public void test8() {
		while (true) {
			try {
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.postForLocation("http://127.0.0.1:8080/bong", "test=8");
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (Exception e) {

			}
		}
	}

	@Test
	public void test9() {
		while (true) {
			try {
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.postForLocation("http://127.0.0.1:8080/bong", "test=9");
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (Exception e) {

			}
		}
	}

	@Test
	public void test10() {
		while (true) {
			try {
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.postForLocation("http://127.0.0.1:8080/bong", "test=10");
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (Exception e) {

			}
		}
	}

	@Test
	public void test11() {
		while (true) {
			try {
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.postForLocation("http://127.0.0.1:8080/bong", "test=11");
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (Exception e) {

			}
		}
	}

	@Test
	public void test12() {
		while (true) {
			try {
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.postForLocation("http://127.0.0.1:8080/bong", "test=12");
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (Exception e) {

			}
		}
	}

	@Test
	public void test13() {
		while (true) {
			try {
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.postForLocation("http://127.0.0.1:8080/bong", "test=13");
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (Exception e) {

			}
		}
	}

}