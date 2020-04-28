package gateway;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import org.bouncycastle.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import io.netty.util.internal.PlatformDependent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MemoryLeakFilter implements GatewayFilter {

	private static final Logger logger = LoggerFactory.getLogger(MemoryLeakFilter.class);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		ServerHttpResponse response = exchange.getResponse();

		logger.info("[MemoryLeakFilter] filter");
		// Netty DirectMemory 정보 확인
		boolean directBufferPreferred = PlatformDependent.directBufferPreferred();
		long usedDirectMemory = PlatformDependent.usedDirectMemory();
		long maxDirectMemory = PlatformDependent.maxDirectMemory();

		logger.warn("[MemoryLeakFilter] directBufferPreferred : {}", directBufferPreferred);
		logger.warn("[MemoryLeakFilter] usedDirectMemory : {}", usedDirectMemory);
		logger.warn("[MemoryLeakFilter] maxDirectMemory : {}", maxDirectMemory);
		//Dio.netty.allocator.chunkSize

		// Join all the DataBuffers so we have a single DataBuffer for the body
		return DataBufferUtils.join(request.getBody())
				.flatMap(dataBuffer -> {
					//Update the retain counts so we can read the body twice, once to parse into an object
					//that we can test the predicate against and a second time when the HTTP client sends
					//the request downstream
					//Note: if we end up reading the body twice we will run into a problem, but as of right
					//now there is no good use case for doing this
					DataBufferUtils.retain(dataBuffer);

					//Make a slice for each read so each read has its own read/write indexes
					Flux<DataBuffer> cachedFlux = Flux.defer(() -> Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));

					ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
						@Override
						public Flux<DataBuffer> getBody() {
							return cachedFlux;
						}
					};

					MultiValueMap<String, String> requestBodyMap = parseFormData(StandardCharsets.UTF_8, toRawString(cachedFlux));
					logger.info("[MemoryLeakFilter] requestBodyMap : {}", requestBodyMap);

//					DataBufferUtils.release(dataBuffer);


//					return chain.filter(exchange.mutate().request(mutatedRequest).build());
					return response.writeWith(Flux.just(response.bufferFactory().wrap("YBS".getBytes())));
				});
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
