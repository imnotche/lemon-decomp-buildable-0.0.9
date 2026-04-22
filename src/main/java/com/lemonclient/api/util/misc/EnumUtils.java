// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.misc;

public class EnumUtils
{
    public static <T extends Enum<T>> T next(final T value) {
        final T[] enumValues = value.getDeclaringClass().getEnumConstants();
        return enumValues[(value.ordinal() + 1) % enumValues.length];
    }
}
