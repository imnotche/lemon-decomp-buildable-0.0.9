// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.misc;

import java.util.Objects;

@FunctionalInterface
public interface TriPredicate<T, U, V>
{
    boolean test(final T p0, final U p1, final V p2);
    
    default TriPredicate<T, U, V> and(final TriPredicate<? super T, ? super U, ? super V> other) {
        Objects.requireNonNull(other);
        return (t, u, v) -> this.test(t, u, v) && other.test(t, u, v);
    }
    
    default TriPredicate<T, U, V> negate() {
        return (t, u, v) -> !this.test(t, u, v);
    }
    
    default TriPredicate<T, U, V> or(final TriPredicate<? super T, ? super U, ? super V> other) {
        Objects.requireNonNull(other);
        return (t, u, v) -> this.test(t, u, v) || other.test(t, u, v);
    }
}
