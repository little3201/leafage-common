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


import java.util.Map;

/**
 * Logging
 *
 * @param module       the operation module
 * @param action       the operation module
 * @param params       the request params
 * @param body         the request body
 * @param ip           the request ip
 * @param sessionId    the  request sessionId
 * @param userAgent    the  request userAgent
 * @param statusCode   the  response status code
 * @param responseBody the response body
 * @since 0.4.0
 */
public record Logging(
        String module,
        String action,
        Map<String, Object> params,
        String body,
        String ip,
        String sessionId,
        String userAgent,
        int statusCode,
        String responseBody) {
}
