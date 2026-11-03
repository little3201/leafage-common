/*
 * Copyright(c) 2019-present the original author or authors.
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

package top.leafage.common.data.core.scope.context;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Holds the current data scope context.
 *
 * <p>The holder is a thread-local stack so nested service calls keep their own
 * action and so an ignored inner call can temporarily bypass filtering.</p>
 *
 * @author wq li
 */
public final class DataScopeContextHolder {

    private static final ThreadLocal<Deque<DataScopeContext>> STACK =
            ThreadLocal.withInitial(ArrayDeque::new);

    private static final AtomicLong NEXT_SCOPE = new AtomicLong();

    private static final ThreadLocal<Long> GENERATION = ThreadLocal.withInitial(() -> 0L);

    private DataScopeContextHolder() {
    }

    /**
     * Pushes a context.
     *
     * @param context the context
     */
    public static void push(DataScopeContext context) {
        Objects.requireNonNull(context, "context must not be null");
        Deque<DataScopeContext> stack = STACK.get();
        if (stack.isEmpty()) {
            GENERATION.set(NEXT_SCOPE.incrementAndGet());
        }
        stack.push(context);
    }

    /**
     * Pops the current context.
     *
     * @return {@code true} when no context remains on this thread
     */
    public static boolean pop() {
        Deque<DataScopeContext> stack = STACK.get();
        if (stack.isEmpty()) {
            return true;
        }
        stack.pop();
        if (stack.isEmpty()) {
            STACK.remove();
            GENERATION.set(NEXT_SCOPE.incrementAndGet());
            return true;
        }
        return false;
    }

    /**
     * Checks whether filtering is active for the current thread.
     *
     * @return {@code true} when an active context exists
     */
    public static boolean isActive() {
        Deque<DataScopeContext> stack = STACK.get();
        return !stack.isEmpty() && !stack.peek().ignored();
    }

    /**
     * Returns the current action.
     *
     * @return the action, or {@code null} when no active context exists
     */
    public static String currentAction() {
        Deque<DataScopeContext> stack = STACK.get();
        return stack.isEmpty() || stack.peek().ignored() ? null : stack.peek().action();
    }

    /**
     * Returns the optional resource restriction.
     *
     * @return the resource code, or {@code null} when no active context exists or
     *         the context does not restrict a resource
     */
    public static String currentResource() {
        Deque<DataScopeContext> stack = STACK.get();
        if (stack.isEmpty() || stack.peek().ignored()) {
            return null;
        }
        String resource = stack.peek().resource();
        return resource == null || resource.isBlank() ? null : resource;
    }

    /**
     * Returns the current context.
     *
     * @return the context, or {@code null} when no context exists
     */
    public static DataScopeContext current() {
        Deque<DataScopeContext> stack = STACK.get();
        return stack.isEmpty() ? null : stack.peek();
    }

    /**
     * Returns the current scope generation used by decision caches.
     *
     * @return a monotonically increasing generation per outermost scope
     */
    public static long generation() {
        return GENERATION.get();
    }

    /**
     * Removes the context stack from the current thread.
     */
    public static void clear() {
        STACK.remove();
        GENERATION.set(NEXT_SCOPE.incrementAndGet());
    }

    /**
     * Propagates the current context to another thread.
     *
     * @param task the task
     * @return the wrapped task
     */
    public static Runnable wrap(Runnable task) {
        Objects.requireNonNull(task, "task must not be null");
        DataScopeContext captured = current();
        return () -> {
            if (captured == null) {
                task.run();
                return;
            }
            push(captured);
            try {
                task.run();
            } finally {
                pop();
            }
        };
    }

    /**
     * Propagates the current context to another thread.
     *
     * @param task the task
     * @param <T>  the result type
     * @return the wrapped task
     */
    public static <T> Callable<T> wrap(Callable<T> task) {
        Objects.requireNonNull(task, "task must not be null");
        DataScopeContext captured = current();
        return () -> {
            if (captured == null) {
                return task.call();
            }
            push(captured);
            try {
                return task.call();
            } finally {
                pop();
            }
        };
    }
}
