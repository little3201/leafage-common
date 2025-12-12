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

package top.leafage.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.AbstractRequestLoggingFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Logging filter
 *
 * @since 0.4.0
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends AbstractRequestLoggingFilter {

    // 使用 ThreadLocal 存储响应，因为 afterRequest 方法只能获取到请求
    private static final ThreadLocal<HttpServletResponse> responseHolder = new ThreadLocal<>();
    private final Consumer<Logging> persister;

    /**
     * constructor with persister
     *
     * @param persister the persister repository
     */
    public RequestLoggingFilter(Consumer<Logging> persister) {
        this.persister = persister != null ? persister : logMap -> {
        };
        // 配置父类属性
        setIncludeClientInfo(true);        // 包含客户端信息
        setIncludeQueryString(true);       // 包含查询参数
        setIncludePayload(true);           // 包含请求体
        setIncludeHeaders(true);           // 包含请求头
        setMaxPayloadLength(10000);        // 最大请求体长度
        setBeforeMessagePrefix("");        // 去掉前缀
        setBeforeMessageSuffix("");
        setAfterMessagePrefix("");
        setAfterMessageSuffix("");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request, 10 * 1024);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        // 保存响应对象到 ThreadLocal，以便在 afterRequest 中使用
        responseHolder.set(responseWrapper);

        try {
            super.doFilterInternal(requestWrapper, responseWrapper, filterChain);
        } finally {
            // 确保响应内容被写回
            responseWrapper.copyBodyToResponse();
            // 清理 ThreadLocal
            responseHolder.remove();
        }
    }

    @Override
    protected void beforeRequest(@NonNull HttpServletRequest request, @NonNull String message) {
        // 只需要 afterRequest
    }

    /**
     * Writes a log message after the request is processed.
     */
    @Override
    protected void afterRequest(@NonNull HttpServletRequest request, @NonNull String message) {
        HttpServletResponse response = responseHolder.get();
        if (response == null) {
            logger.warn("Response not found in ThreadLocal");
            return;
        }
        // 获取响应体
        String responseBody = "";
        if (response instanceof ContentCachingResponseWrapper responseWrapper) {
            byte[] buf = responseWrapper.getContentAsByteArray();
            if (buf.length > 0) {
                try {
                    responseBody = new String(buf, response.getCharacterEncoding());
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        Logging logging = new Logging(
                request.getRequestURI(),
                request.getMethod(),
                getParameters(request),
                getMessagePayload(request),
                request.getRemoteAddr(),
                request.getRequestedSessionId(),
                request.getHeader("User-Agent"),
                response.getStatus(),
                responseBody
        );

        // 异步入库（强烈推荐！防止阻塞主线程）
        CompletableFuture.runAsync(() -> persister.accept(logging));
    }

    // 获取 Query 参数 + PathVariable（最准确）
    private Map<String, Object> getParameters(HttpServletRequest request) {
        Map<String, Object> params = new LinkedHashMap<>();

        // 1. Query 参数
        request.getParameterMap().forEach((k, v) -> params.put(k, v.length == 1 ? v[0] : Arrays.toString(v)));

        // 2. PathVariable（通过 Spring 的 HandlerMapping）
        Object uriVariables = request.getAttribute("HandlerMapping.uriTemplateVariables");
        if (uriVariables instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, String> pathVars = (Map<String, String>) uriVariables;
            pathVars.forEach((k, v) -> params.put("{" + k + "}", v));
        }

        return params;
    }

}
