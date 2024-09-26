/*
 *  Copyright 2018-2024 little3201.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package top.leafage.common.reactive.audit;

import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import reactor.core.publisher.Mono;

/**
 * auditor aware.
 *
 * @author wq li
 * @since 0.3.0
 */
public class ReactiveAuditorAwareImpl implements ReactiveAuditorAware<String> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<String> getCurrentAuditor() {
        return Mono.defer(() -> Mono.justOrEmpty(SecurityContextHolder.getContext()))
                .map(SecurityContext::getAuthentication)
                .filter(auth -> auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof User)
                .map(auth -> ((User) auth.getPrincipal()).getUsername());
    }
}
