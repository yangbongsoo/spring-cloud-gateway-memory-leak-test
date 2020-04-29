package gateway;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.bouncycastle.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.server.ServerWebExchange;

import io.netty.buffer.ByteBufAllocator;
import io.netty.util.internal.PlatformDependent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class NoMemoryLeakFilter2 implements GatewayFilter {

	private static final Logger logger = LoggerFactory.getLogger(NoMemoryLeakFilter2.class);
	private static final List<HttpMessageReader<?>> messageReaders = HandlerStrategies.withDefaults().messageReaders();

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		ServerHttpResponse response = exchange.getResponse();

		logger.info("[NoMemoryLeakFilter2] filter");
		// Netty DirectMemory 정보 확인
		boolean directBufferPreferred = PlatformDependent.directBufferPreferred();
		long usedDirectMemory = PlatformDependent.usedDirectMemory();
		long maxDirectMemory = PlatformDependent.maxDirectMemory();

		logger.warn("[NoMemoryLeakFilter2] directBufferPreferred : {}", directBufferPreferred);
		logger.warn("[NoMemoryLeakFilter2] usedDirectMemory : {}", usedDirectMemory);
		logger.warn("[NoMemoryLeakFilter2] maxDirectMemory : {}", maxDirectMemory);
		//Dio.netty.allocator.chunkSize
		// Join all the DataBuffers so we have a single DataBuffer for the body
		return DataBufferUtils.join(request.getBody())
				.flatMap(dataBuffer -> {
					byte[] content = new byte[dataBuffer.readableByteCount()];
					dataBuffer.read(content);
					AtomicReference<String> rawRef = new AtomicReference<>();
					rawRef.set(Strings.fromUTF8ByteArray(content));

					DataBufferUtils.release(dataBuffer);

					MultiValueMap<String, String> requestBodyMap = parseFormData(StandardCharsets.UTF_8, rawRef.get());
					logger.info("[NoMemoryLeakFilter2] requestBodyMap : {}", requestBodyMap);

					ServerHttpRequest requestDecorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
						@Override
						public Flux<DataBuffer> getBody() {
							logger.info("[NoMemoryLeakFilter2] getBody call");
							return DataBufferUtils.read(new ByteArrayResource(content), new NettyDataBufferFactory(ByteBufAllocator.DEFAULT),
									content.length);
						}
					};
//					ServerWebExchange mutatedExchange = exchange.mutate().request(requestDecorator).build();
					return response.writeWith(Flux.just(response.bufferFactory().wrap("YBS".getBytes())));
				});
	}

	private MultiValueMap<String, String> parseFormData(Charset charset, String body) {
		String[] pairs = StringUtils.tokenizeToStringArray(body, "&");
		MultiValueMap<String, String> result = new LinkedMultiValueMap<>(pairs.length);
		try {
			for (String pair : pairs) {
				int idx = pair.indexOf('=');
				if (idx == -1) {
					result.add(URLDecoder.decode(pair, charset.name()), null);
				} else {
					String name = URLDecoder.decode(pair.substring(0, idx), charset.name());
					String value = URLDecoder.decode(pair.substring(idx + 1), charset.name());
					result.add(name, value);
				}
			}
		} catch (UnsupportedEncodingException ex) {
			throw new IllegalStateException(ex);
		}
		return result;
	}
}
