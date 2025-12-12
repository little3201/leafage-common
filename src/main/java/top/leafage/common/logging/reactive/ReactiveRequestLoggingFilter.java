/*
 * Copyright (c) 2025.  little3201.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.leafage.common.logging.reactive;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.Collectors;

public class ReactiveRequestLoggingFilter implements WebFilter {

    private static final Logger logger = LoggerFactory.getLogger(ReactiveRequestLoggingFilter.class);
    private final int maxPayloadLength = 5000;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        Instant start = Instant.now();

        // 记录基本请求信息
        ServerHttpRequest request = exchange.getRequest();
        String method = request.getMethod().name();
        String path = request.getURI().getRawPath();
        String query = request.getURI().getRawQuery();
        String fullUrl = request.getURI().toString();

        logger.info("=== NEW REQUEST === {} {}", method, fullUrl);

        // 装饰 exchange 以便记录请求体和响应体
        ServerWebExchangeDecorator decoratedExchange = new ServerWebExchangeDecorator(exchange) {
            @Override
            public ServerHttpRequest getRequest() {
                return new LoggingRequestDecorator(super.getRequest());
            }

            @Override
            public ServerHttpResponse getResponse() {
                return new LoggingResponseDecorator(super.getResponse(), start, method, path + (query != null ? "?" + query : ""));
            }
        };

        return chain.filter(decoratedExchange);
    }

    // 记录请求体的装饰器（需要缓存 body）
    class LoggingRequestDecorator extends ServerHttpRequestDecorator {
        public LoggingRequestDecorator(ServerHttpRequest delegate) {
            super(delegate);
        }

        @Override
        public Flux<DataBuffer> getBody() {
            return super.getBody()
                    .collectList()
                    .doOnNext(buffers -> {
                        if (logger.isInfoEnabled()) {
                            String body = buffers.stream()
                                    .map(buf -> {
                                        byte[] bytes = new byte[buf.readableByteCount()];
                                        buf.read(bytes);
                                        DataBufferUtils.release(buf);
                                        return new String(bytes, StandardCharsets.UTF_8);
                                    })
                                    .collect(Collectors.joining());

                            String truncated = body.length() > maxPayloadLength
                                    ? body.substring(0, maxPayloadLength) + "...(truncated)"
                                    : body;

                            logger.info("Request Body: {}", truncated);
                        }
                    })
                    .flatMapMany(list -> Flux.fromIterable(list)); // 重新包装供下游使用
        }
    }

    // 记录响应体的装饰器
    class LoggingResponseDecorator extends ServerHttpResponseDecorator {
        private final Instant start;
        private final String method;
        private final String path;

        public LoggingResponseDecorator(ServerHttpResponse delegate, Instant start, String method, String path) {
            super(delegate);
            this.start = start;
            this.method = method;
            this.path = path;
        }

        @Override
        public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
            return super.writeWith(
                    DataBufferUtils.join(body)
                            .doOnNext(buffer -> {
                                byte[] bytes = new byte[buffer.readableByteCount()];
                                buffer.read(bytes);
                                DataBufferUtils.release(buffer);

                                String respBody = new String(bytes, StandardCharsets.UTF_8);
                                String truncated = respBody.length() > maxPayloadLength
                                        ? respBody.substring(0, maxPayloadLength) + "...(truncated)"
                                        : respBody;

                                long duration = Duration.between(start, Instant.now()).toMillis();
                                logger.info("Response: {} {} | Status: {} | {}ms | Body: {}",
                                        method, path, getDelegate().getStatusCode(), duration, truncated);
                            })
                            .flux()
            );
        }
    }
}
