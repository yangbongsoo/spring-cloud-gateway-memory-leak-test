package gateway.multygetbody.type1;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicReference;

import org.bouncycastle.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import io.netty.util.internal.PlatformDependent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MultiGetBodyFilter2 implements GatewayFilter {

	private static final Logger logger = LoggerFactory.getLogger(MultiGetBodyFilter2.class);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		ServerHttpResponse response = exchange.getResponse();

		logger.info("[MultyGetBodyFilter2] filter");
		// Netty DirectMemory 정보 확인
		boolean directBufferPreferred = PlatformDependent.directBufferPreferred();
		long usedDirectMemory = PlatformDependent.usedDirectMemory();
		long maxDirectMemory = PlatformDependent.maxDirectMemory();

		logger.warn("[MultyGetBodyFilter2] directBufferPreferred : {}", directBufferPreferred);
		logger.warn("[MultyGetBodyFilter2] usedDirectMemory : {}", usedDirectMemory);
		logger.warn("[MultyGetBodyFilter2] maxDirectMemory : {}", maxDirectMemory);
		//Dio.netty.allocator.chunkSize

//		Flux<DataBuffer> body = request.getBody();
//		body
//			.doOnNext(dataBuffer -> {
//				byte[] content = new byte[dataBuffer.readableByteCount()];
//				dataBuffer.read(content);
//				logger.info("[MultyGetBodyFilter2] content : {}", new String(content, StandardCharsets.UTF_8));
//			})
//			.subscribe();
		return chain.filter(exchange);
	}

	private String toRawString(Flux<DataBuffer> body) {
		if (body == null) {
			return "";
		}

		AtomicReference<String> rawRef = new AtomicReference<>();
		body.subscribe(buffer -> {
			byte[] bytes = new byte[buffer.readableByteCount()];
			buffer.read(bytes);
			DataBufferUtils.release(buffer);
			rawRef.set(Strings.fromUTF8ByteArray(bytes));
		});
		return rawRef.get();
	}

	private MultiValueMap<String, String> parseFormData(Charset charset, String body) {
		String[] pairs = StringUtils.tokenizeToStringArray(body, "&");
		MultiValueMap<String, String> result = new LinkedMultiValueMap<>(pairs.length);
		try {
			for (String pair : pairs) {
				int idx = pair.indexOf('=');
				if (idx == -1) {
					result.add(URLDecoder.decode(pair, charset.name()), null);
				}
				else {
					String name = URLDecoder.decode(pair.substring(0, idx),  charset.name());
					String value = URLDecoder.decode(pair.substring(idx + 1), charset.name());
					result.add(name, value);
				}
			}
		}
		catch (UnsupportedEncodingException ex) {
			throw new IllegalStateException(ex);
		}
		return result;
	}
}
