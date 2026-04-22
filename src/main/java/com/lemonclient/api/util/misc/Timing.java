// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.misc;

public class Timing
{
    private long time;
    
    public Timing() {
        this.time = -1L;
    }
    
    public boolean passedS(final double s) {
        return this.passedMs((long)s * 1000L);
    }
    
    public boolean passedDms(final double dms) {
        return this.passedMs((long)dms * 10L);
    }
    
    public boolean passedDs(final double ds) {
        return this.passedMs((long)ds * 100L);
    }
    
    public boolean passedMs(final long ms) {
        return this.passedNS(this.convertToNS(ms));
    }
    
    public boolean passedNS(final long ns) {
        return System.nanoTime() - this.time >= ns;
    }
    
    public boolean passedX(final double dms) {
        return this.getMs(System.nanoTime() - this.time) >= (long)(dms * 3.0);
    }
    
    public long getPassedTimeMs() {
        return this.getMs(System.nanoTime() - this.time);
    }
    
    public void reset() {
        this.time = System.nanoTime();
    }
    
    public void set() {
        this.time = System.currentTimeMillis();
    }
    
    public void setMs(final long ms) {
        this.time = System.nanoTime() - this.convertToNS(ms);
    }
    
    public long getTime() {
        return System.nanoTime() - this.time;
    }
    
    public void setTime(final long set) {
        this.time = System.nanoTime() - set;
    }
    
    public long getMs(final long time) {
        return time / 1000000L;
    }
    
    public long convertToNS(final long time) {
        return time * 1000000L;
    }
    
    public boolean passedTick(final double tick) {
        return this.passedMs((long)tick * 50L);
    }
}
