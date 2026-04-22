// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.render;

public interface Easings
{
    String[] easings = { "none", "cubic", "quint", "quad", "quart", "expo", "sine", "circ" };
    
    default double toOutEasing(final String easing, final double value) {
        switch (easing) {
            case "cubic": {
                return cubicOut(value);
            }
            case "quint": {
                return quintOut(value);
            }
            case "quad": {
                return quadOut(value);
            }
            case "quart": {
                return quartOut(value);
            }
            case "expo": {
                return expoOut(value);
            }
            case "sine": {
                return sineOut(value);
            }
            case "circ": {
                return circOut(value);
            }
            default: {
                return value;
            }
        }
    }
    
    default double toInEasing(final String easing, final double value) {
        switch (easing) {
            case "cubic": {
                return cubicIn(value);
            }
            case "quint": {
                return quintIn(value);
            }
            case "quad": {
                return quadIn(value);
            }
            case "quart": {
                return quartIn(value);
            }
            case "expo": {
                return expoIn(value);
            }
            case "sine": {
                return sineIn(value);
            }
            case "circ": {
                return circIn(value);
            }
            default: {
                return value;
            }
        }
    }
    
    default double inOutEasing(final String easing, final double value) {
        switch (easing) {
            case "cubic": {
                return cubicInOut(value);
            }
            case "quint": {
                return quintInOut(value);
            }
            case "quad": {
                return quadInOut(value);
            }
            case "quart": {
                return quartInOut(value);
            }
            case "expo": {
                return expoInOut(value);
            }
            case "sine": {
                return sineInOut(value);
            }
            case "circ": {
                return circInOut(value);
            }
            default: {
                return value;
            }
        }
    }
    
    default double cubicIn(final double value) {
        return value * value * value;
    }
    
    default double cubicOut(final double value) {
        return 1.0 - Math.pow(1.0 - value, 3.0);
    }
    
    default double cubicInOut(final double value) {
        return (value < 0.5) ? (4.0 * value * value * value) : (1.0 - Math.pow(-2.0 * value + 2.0, 3.0) / 2.0);
    }
    
    default double quintIn(final double value) {
        return value * value * value * value * value;
    }
    
    default double quintOut(final double value) {
        return 1.0 - Math.pow(1.0 - value, 5.0);
    }
    
    default double quintInOut(final double value) {
        return (value < 0.5) ? (16.0 * value * value * value * value * value) : (1.0 - Math.pow(-2.0 * value + 2.0, 5.0) / 2.0);
    }
    
    default double quadIn(final double value) {
        return value * value;
    }
    
    default double quadOut(final double value) {
        return 1.0 - (1.0 - value) * (1.0 - value);
    }
    
    default double quadInOut(final double value) {
        return (value < 0.5) ? (2.0 * value * value) : (1.0 - Math.pow(-2.0 * value + 2.0, 2.0) / 2.0);
    }
    
    default double quartIn(final double value) {
        return value * value * value * value;
    }
    
    default double quartOut(final double value) {
        return 1.0 - Math.pow(1.0 - value, 4.0);
    }
    
    default double quartInOut(final double value) {
        return (value < 0.5) ? (8.0 * value * value * value * value) : (1.0 - Math.pow(-2.0 * value + 2.0, 4.0) / 2.0);
    }
    
    default double expoIn(final double value) {
        return (value == 0.0) ? 0.0 : Math.pow(2.0, 10.0 * value - 10.0);
    }
    
    default double expoOut(final double value) {
        return (value == 1.0) ? 1.0 : (1.0 - Math.pow(2.0, -10.0 * value));
    }
    
    default double expoInOut(final double value) {
        return (value == 0.0) ? 0.0 : ((value == 1.0) ? 1.0 : ((value < 0.5) ? (Math.pow(2.0, 20.0 * value - 10.0) / 2.0) : ((2.0 - Math.pow(2.0, -20.0 * value + 10.0)) / 2.0)));
    }
    
    default double sineIn(final double value) {
        return 1.0 - Math.cos(value * 3.141592653589793 / 2.0);
    }
    
    default double sineOut(final double value) {
        return Math.sin(value * 3.141592653589793 / 2.0);
    }
    
    default double sineInOut(final double value) {
        return -(Math.cos(3.141592653589793 * value) - 1.0) / 2.0;
    }
    
    default double circIn(final double value) {
        return 1.0 - Math.sqrt(1.0 - Math.pow(value, 2.0));
    }
    
    default double circOut(final double value) {
        return Math.sqrt(1.0 - Math.pow(value - 1.0, 2.0));
    }
    
    default double circInOut(final double value) {
        return (value < 0.5) ? ((1.0 - Math.sqrt(1.0 - Math.pow(2.0 * value, 2.0))) / 2.0) : ((Math.sqrt(1.0 - Math.pow(-2.0 * value + 2.0, 2.0)) + 1.0) / 2.0);
    }
}
