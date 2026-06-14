/*
 * Copyright (c) 2026.  little3201.
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

package top.leafage.common.poi.word.reactive;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import top.leafage.common.poi.word.WordWriter;
import top.leafage.common.poi.word.element.WordElement;

import java.util.List;

/**
 * word writer
 *
 * @author wq li
 * @since 0.4.2
 */
public class ReactiveWordWriter {

    public static Mono<byte[]> write(List<WordElement> elements) {
        // 使用 boundedElastic 调度阻塞操作
        return Mono.fromCallable(() -> WordWriter.write(elements))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
