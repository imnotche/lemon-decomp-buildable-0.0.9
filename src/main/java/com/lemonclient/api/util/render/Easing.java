// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.render;

public interface Easing
{
    Easing LINEAR = (t, b, c, d) -> c * t / d + b;
    Easing QUAD_IN = (t, b, c, d) -> {
        t /= d;
        return c * t * t + b;
    };
    Easing QUAD_OUT = (t, b, c, d) -> {
        t /= d;
        return -c * t * (t - 2.0f) + b;
    };
    Easing QUAD_IN_OUT = (t, b, c, d) -> {
        t /= d / 2.0f;
        if (t < 1.0f) {
            return c / 2.0f * t * t + b;
        }
        --t;
        return -c / 2.0f * (t * (t - 2.0f) - 1.0f) + b;
    };
    Easing CUBIC_IN = (t, b, c, d) -> {
        t /= d;
        return c * t * t * t + b;
    };
    Easing CUBIC_OUT = (t, b, c, d) -> {
        t = t / d - 1.0f;
        return c * (t * t * t + 1.0f) + b;
    };
    Easing CUBIC_IN_OUT = (t, b, c, d) -> {
        t /= d / 2.0f;
        if (t < 1.0f) {
            return c / 2.0f * t * t * t + b;
        }
        t -= 2.0f;
        return c / 2.0f * (t * t * t + 2.0f) + b;
    };
    Easing QUARTIC_IN = (t, b, c, d) -> {
        t /= d;
        return c * t * t * t * t + b;
    };
    Easing QUARTIC_OUT = (t, b, c, d) -> {
        t = t / d - 1.0f;
        return -c * (t * t * t * t - 1.0f) + b;
    };
    Easing QUARTIC_IN_OUT = (t, b, c, d) -> {
        t /= d / 2.0f;
        if (t < 1.0f) {
            return c / 2.0f * t * t * t * t + b;
        }
        t -= 2.0f;
        return -c / 2.0f * (t * t * t * t - 2.0f) + b;
    };
    Easing QUINTIC_IN = (t, b, c, d) -> {
        t /= d;
        return c * t * t * t * t * t + b;
    };
    Easing QUINTIC_OUT = (t, b, c, d) -> {
        t = t / d - 1.0f;
        return c * (t * t * t * t * t + 1.0f) + b;
    };
    Easing QUINTIC_IN_OUT = (t, b, c, d) -> {
        t /= d / 2.0f;
        if (t < 1.0f) {
            return c / 2.0f * t * t * t * t * t + b;
        }
        t -= 2.0f;
        return c / 2.0f * (t * t * t * t * t + 2.0f) + b;
    };
    Easing SINE_IN = (t, b, c, d) -> -c * (float)Math.cos(t / d * 1.5707963267948966) + c + b;
    Easing SINE_OUT = (t, b, c, d) -> c * (float)Math.sin(t / d * 1.5707963267948966) + b;
    Easing SINE_IN_OUT = (t, b, c, d) -> -c / 2.0f * ((float)Math.cos(3.141592653589793 * t / d) - 1.0f) + b;
    Easing EXPO_IN = (t, b, c, d) -> (t == 0.0f) ? b : (c * (float)Math.pow(2.0, 10.0f * (t / d - 1.0f)) + b);
    Easing EXPO_OUT = (t, b, c, d) -> (t == d) ? (b + c) : (c * (-(float)Math.pow(2.0, -10.0f * t / d) + 1.0f) + b);
    Easing EXPO_IN_OUT = (t, b, c, d) -> {
        if (t == 0.0f) {
            return b;
        }
        else if (t == d) {
            return b + c;
        }
        else {
            t /= d / 2.0f;
            if (t < 1.0f) {
                return c / 2.0f * (float)Math.pow(2.0, 10.0f * (t - 1.0f)) + b;
            }
            else {
                --t;
                return c / 2.0f * (-(float)Math.pow(2.0, -10.0f * t) + 2.0f) + b;
            }
        }
    };
    Easing CIRC_IN = (t, b, c, d) -> {
        t /= d;
        return -c * ((float)Math.sqrt(1.0f - t * t) - 1.0f) + b;
    };
    Easing CIRC_OUT = (t, b, c, d) -> {
        t = t / d - 1.0f;
        return c * (float)Math.sqrt(1.0f - t * t) + b;
    };
    Easing CIRC_IN_OUT = (t, b, c, d) -> {
        t /= d / 2.0f;
        if (t < 1.0f) {
            return -c / 2.0f * ((float)Math.sqrt(1.0f - t * t) - 1.0f) + b;
        }
        else {
            t -= 2.0f;
            return c / 2.0f * ((float)Math.sqrt(1.0f - t * t) + 1.0f) + b;
        }
    };
    Elastic ELASTIC_IN = new ElasticIn();
    Elastic ELASTIC_OUT = new ElasticOut();
    Elastic ELASTIC_IN_OUT = new ElasticInOut();
    Back BACK_IN = new BackIn();
    Back BACK_OUT = new BackOut();
    Back BACK_IN_OUT = new BackInOut();
    Easing BOUNCE_OUT = (t, b, c, d) -> {
        t /= d;
        if (t < 0.36363637f) {
            return c * (7.5625f * t * t) + b;
        }
        else if (t < 0.72727275f) {
            t -= 0.54545456f;
            return c * (7.5625f * t * t + 0.75f) + b;
        }
        else if (t < 0.90909094f) {
            t -= 0.8181818f;
            return c * (7.5625f * t * t + 0.9375f) + b;
        }
        else {
            t -= 0.95454544f;
            return c * (7.5625f * t * t + 0.984375f) + b;
        }
    };
    Easing BOUNCE_IN = (t, b, c, d) -> c - Easing.BOUNCE_OUT.ease(d - t, 0.0f, c, d) + b;
    Easing BOUNCE_IN_OUT = (t, b, c, d) -> {
        if (t < d / 2.0f) {
            return Easing.BOUNCE_IN.ease(t * 2.0f, 0.0f, c, d) * 0.5f + b;
        }
        else {
            return Easing.BOUNCE_OUT.ease(t * 2.0f - d, 0.0f, c, d) * 0.5f + c * 0.5f + b;
        }
    };
    
    float ease(final float p0, final float p1, final float p2, final float p3);
    
    abstract class Elastic implements Easing
    {
        private float amplitude;
        private float period;
        
        public Elastic(final float amplitude, final float period) {
            this.amplitude = amplitude;
            this.period = period;
        }
        
        public Elastic() {
            this(-1.0f, 0.0f);
        }
        
        public float getPeriod() {
            return this.period;
        }
        
        public void setPeriod(final float period) {
            this.period = period;
        }
        
        public float getAmplitude() {
            return this.amplitude;
        }
        
        public void setAmplitude(final float amplitude) {
            this.amplitude = amplitude;
        }
    }
    
    class ElasticIn extends Elastic
    {
        public ElasticIn(final float amplitude, final float period) {
            super(amplitude, period);
        }
        
        public ElasticIn() {
        }
        
        @Override
        public float ease(float time, final float startTime, final float change, final float endTime) {
            float a = this.getAmplitude();
            float p = this.getPeriod();
            if (time == 0.0f) {
                return startTime;
            }
            if ((time /= endTime) == 1.0f) {
                return startTime + change;
            }
            if (p == 0.0f) {
                p = endTime * 0.3f;
            }
            float s;
            if (a < Math.abs(change)) {
                a = change;
                s = p / 4.0f;
            }
            else {
                s = p / 6.2831855f * (float)Math.asin(change / a);
            }
            return -(a * (float)Math.pow(2.0, 10.0f * --time) * (float)Math.sin((time * endTime - s) * 6.283185307179586 / p)) + startTime;
        }
    }
    
    class ElasticOut extends Elastic
    {
        public ElasticOut(final float amplitude, final float period) {
            super(amplitude, period);
        }
        
        public ElasticOut() {
        }
        
        @Override
        public float ease(float time, final float startTime, final float change, final float endTime) {
            float a = this.getAmplitude();
            float p = this.getPeriod();
            if (time == 0.0f) {
                return startTime;
            }
            if ((time /= endTime) == 1.0f) {
                return startTime + change;
            }
            if (p == 0.0f) {
                p = endTime * 0.3f;
            }
            float s;
            if (a < Math.abs(change)) {
                a = change;
                s = p / 4.0f;
            }
            else {
                s = p / 6.2831855f * (float)Math.asin(change / a);
            }
            return a * (float)Math.pow(2.0, -10.0f * time) * (float)Math.sin((time * endTime - s) * 6.283185307179586 / p) + change + startTime;
        }
    }
    
    class ElasticInOut extends Elastic
    {
        public ElasticInOut(final float amplitude, final float period) {
            super(amplitude, period);
        }
        
        public ElasticInOut() {
        }
        
        @Override
        public float ease(float time, final float startTime, final float change, final float endTime) {
            float a = this.getAmplitude();
            float p = this.getPeriod();
            if (time == 0.0f) {
                return startTime;
            }
            if ((time /= endTime / 2.0f) == 2.0f) {
                return startTime + change;
            }
            if (p == 0.0f) {
                p = endTime * 0.45000002f;
            }
            float s;
            if (a < Math.abs(change)) {
                a = change;
                s = p / 4.0f;
            }
            else {
                s = p / 6.2831855f * (float)Math.asin(change / a);
            }
            if (time < 1.0f) {
                return -0.5f * (a * (float)Math.pow(2.0, 10.0f * --time) * (float)Math.sin((time * endTime - s) * 6.283185307179586 / p)) + startTime;
            }
            return a * (float)Math.pow(2.0, -10.0f * --time) * (float)Math.sin((time * endTime - s) * 6.283185307179586 / p) * 0.5f + change + startTime;
        }
    }
    
    abstract class Back implements Easing
    {
        public static final float DEFAULT_OVERSHOOT = 1.70158f;
        private float overshoot;
        
        public Back() {
            this(1.70158f);
        }
        
        public Back(final float overshoot) {
            this.overshoot = overshoot;
        }
        
        public float getOvershoot() {
            return this.overshoot;
        }
        
        public void setOvershoot(final float overshoot) {
            this.overshoot = overshoot;
        }
    }
    
    class BackIn extends Back
    {
        public BackIn() {
        }
        
        public BackIn(final float overshoot) {
            super(overshoot);
        }
        
        @Override
        public float ease(float time, final float startTime, final float change, final float endTime) {
            final float s = this.getOvershoot();
            return change * (time /= endTime) * time * ((s + 1.0f) * time - s) + startTime;
        }
    }
    
    class BackOut extends Back
    {
        public BackOut() {
        }
        
        public BackOut(final float overshoot) {
            super(overshoot);
        }
        
        @Override
        public float ease(float time, final float startTime, final float change, final float endTime) {
            final float s = this.getOvershoot();
            return change * ((time = time / endTime - 1.0f) * time * ((s + 1.0f) * time + s) + 1.0f) + startTime;
        }
    }
    
    class BackInOut extends Back
    {
        public BackInOut() {
        }
        
        public BackInOut(final float overshoot) {
            super(overshoot);
        }
        
        @Override
        public float ease(float time, final float startTime, final float change, final float endTime) {
            float s = this.getOvershoot();
            if ((time /= endTime / 2.0f) < 1.0f) {
                return change / 2.0f * (time * time * (((s *= (float)1.525) + 1.0f) * time - s)) + startTime;
            }
            return change / 2.0f * ((time -= 2.0f) * time * (((s *= (float)1.525) + 1.0f) * time + s) + 2.0f) + startTime;
        }
    }
}
