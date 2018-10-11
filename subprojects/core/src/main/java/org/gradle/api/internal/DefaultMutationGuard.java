/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal;

import org.gradle.api.Action;

public class DefaultMutationGuard extends AbstractMutationGuard {
    private final ThreadLocal<Boolean> MUTATION_GUARD_STATE = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return Boolean.TRUE;
        }
    };

    @Override
    public boolean isMutationAllowed() {
        return MUTATION_GUARD_STATE.get();
    }

    protected <T> Action<? super T> newActionWithMutation(final Action<? super T> action, final boolean allowMutationMethods) {
        return new Action<T>() {
            @Override
            public void execute(T t) {
                boolean oldIsMutationAllowed = isMutationAllowed();
                MUTATION_GUARD_STATE.set(allowMutationMethods);
                try {
                    action.execute(t);
                } finally {
                    MUTATION_GUARD_STATE.set(oldIsMutationAllowed);
                }
            }
        };
    }
}
