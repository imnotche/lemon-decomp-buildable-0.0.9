// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.render;

import net.minecraft.world.DimensionType;
import com.lemonclient.api.util.render.Interpolation;
import java.util.function.Consumer;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import com.lemonclient.api.event.events.RenderEvent;
import net.minecraft.entity.Entity;
import java.util.Iterator;
import net.minecraft.entity.projectile.EntityArrow;
import java.util.function.Predicate;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import java.util.ArrayList;
import com.lemonclient.api.util.render.animation.AnimationMode;
import net.minecraft.network.play.server.SPacketSpawnObject;
import java.util.concurrent.ConcurrentHashMap;
import com.lemonclient.api.util.render.GSColor;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.math.Vec3d;
import java.util.List;
import com.lemonclient.api.util.render.animation.TimeAnimation;
import java.util.Map;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "Trails", category = Category.Render)
public class Trails extends Module
{
    BooleanSetting arrows;
    BooleanSetting pearls;
    BooleanSetting snowballs;
    IntegerSetting time;
    ColorSetting color;
    IntegerSetting alpha;
    DoubleSetting width;
    Map<Integer, TimeAnimation> ids;
    Map<Integer, List<Trace>> traceLists;
    Map<Integer, Trace> traces;
    public static final Vec3d ORIGIN;
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener;
    
    public Trails() {
        this.arrows = this.registerBoolean("Arrows", false);
        this.pearls = this.registerBoolean("Pearls", false);
        this.snowballs = this.registerBoolean("Snowballs", false);
        this.time = this.registerInteger("Time", 1, 1, 10);
        this.color = this.registerColor("Color", new GSColor(255, 0, 0, 255));
        this.alpha = this.registerInteger("Alpha", 255, 1, 255);
        this.width = this.registerDouble("Width", 1.600000023841858, 0.10000000149011612, 10.0);
        this.ids = new ConcurrentHashMap<Integer, TimeAnimation>();
        this.traceLists = new ConcurrentHashMap<Integer, List<Trace>>();
        this.traces = new ConcurrentHashMap<Integer, Trace>();
        this.receiveListener = new Listener<PacketEvent.Receive>(event -> {
            if (Trails.mc.world != null) {
                if (event.getPacket() instanceof SPacketSpawnObject) {
                    final SPacketSpawnObject packet = (SPacketSpawnObject)event.getPacket();
                    if ((this.pearls.getValue() && packet.getType() == 65) || (this.arrows.getValue() && packet.getType() == 60) || (this.snowballs.getValue() && packet.getType() == 61)) {
                        final TimeAnimation animation = new TimeAnimation(this.time.getValue() * 1000, 0.0, this.alpha.getValue(), false, AnimationMode.LINEAR);
                        animation.stop();
                        this.ids.put(packet.getEntityID(), animation);
                        this.traceLists.put(packet.getEntityID(), new ArrayList<Trace>());
                        final Map<Integer, Trace> traces = this.traces;
                        packet.getEntityID();
                        new Trace(0, null, Trails.mc.world.provider.getDimensionType(), new Vec3d(packet.getX(), packet.getY(), packet.getZ()), new ArrayList<Trace.TracePos>());
                        final Trace trace = null;
                        final Object o = new Object();
                        traces.put((Integer)o, trace);
                    }
                }
                if (event.getPacket() instanceof SPacketDestroyEntities) {
                    ((SPacketDestroyEntities)event.getPacket()).getEntityIDs();
                    final int[] array = new int[0];
                    int i = 0;
                    for (int length = array.length; i < length; ++i) {
                        final int id = array[i];
                        if (this.ids.containsKey(id)) {
                            this.ids.get(id).play();
                        }
                    }
                }
            }
        }, new Predicate[0]);
    }
    
    @Override
    public void onTick() {
        if (Trails.mc.world == null) {
            return;
        }
        if (this.ids.isEmpty()) {
            return;
        }
        for (final Integer id : this.ids.keySet()) {
            if (id == null) {
                continue;
            }
            if (Trails.mc.world.loadedEntityList == null) {
                return;
            }
            if (Trails.mc.world.loadedEntityList.isEmpty()) {
                return;
            }
            Trace idTrace = this.traces.get(id);
            final Entity entity = Trails.mc.world.getEntityByID(id);
            if (entity != null) {
                final Vec3d vec = entity.getPositionVector();
                if (vec.equals(Trails.ORIGIN)) {
                    continue;
                }
                if (!this.traces.containsKey(id) || idTrace == null) {
                    this.traces.put(id, new Trace(0, null, Trails.mc.world.provider.getDimensionType(), vec, new ArrayList<Trace.TracePos>()));
                    idTrace = this.traces.get(id);
                }
                List<Trace.TracePos> trace = idTrace.getTrace();
                final Vec3d vec3d = trace.isEmpty() ? vec : trace.get(trace.size() - 1).getPos();
                if (!trace.isEmpty() && (vec.distanceTo(vec3d) > 100.0 || idTrace.getType() != Trails.mc.world.provider.getDimensionType())) {
                    this.traceLists.get(id).add(idTrace);
                    trace = new ArrayList<Trace.TracePos>();
                    this.traces.put(id, new Trace(this.traceLists.get(id).size() + 1, null, Trails.mc.world.provider.getDimensionType(), vec, new ArrayList<Trace.TracePos>()));
                }
                if (trace.isEmpty() || !vec.equals(vec3d)) {
                    trace.add(new Trace.TracePos(vec));
                }
            }
            final TimeAnimation animation = this.ids.get(id);
            if (entity instanceof EntityArrow && (entity.onGround || entity.collided || !entity.isAirBorne)) {
                animation.play();
            }
            if (animation == null || this.alpha.getValue() - animation.getCurrent() > 0.0) {
                continue;
            }
            animation.stop();
            this.ids.remove(id);
            this.traceLists.remove(id);
            this.traces.remove(id);
        }
    }
    
    @Override
    public void onWorldRender(final RenderEvent event) {
        for (final Map.Entry<Integer, List<Trace>> entry : this.traceLists.entrySet()) {
            GL11.glLineWidth(this.width.getValue().floatValue());
            final TimeAnimation animation = this.ids.get(entry.getKey());
            animation.add();
            GL11.glColor4f((float)this.color.getColor().getRed(), (float)this.color.getColor().getGreen(), (float)this.color.getColor().getBlue(), MathHelper.clamp((float)(this.alpha.getValue() - animation.getCurrent() / 255.0), 0.0f, 255.0f));
            final Trace activeTrace = this.traces.get(entry.getKey());
            entry.getValue().forEach(savedTrace -> {
                GL11.glBegin(3);
                savedTrace.getTrace().forEach(this::renderVec);
                GL11.glEnd();
            });
            GL11.glColor4f((float)this.color.getColor().getRed(), (float)this.color.getColor().getGreen(), (float)this.color.getColor().getBlue(), MathHelper.clamp((float)(this.alpha.getValue() - animation.getCurrent() / 255.0), 0.0f, 255.0f));
            GL11.glBegin(3);
            if (activeTrace != null) {
                activeTrace.getTrace().forEach(this::renderVec);
            }
            GL11.glEnd();
        }
    }
    
    private void renderVec(final Trace.TracePos tracePos) {
        final double x = tracePos.getPos().x - Interpolation.getRenderPosX();
        final double y = tracePos.getPos().y - Interpolation.getRenderPosY();
        final double z = tracePos.getPos().z - Interpolation.getRenderPosZ();
        GL11.glVertex3d(x, y, z);
    }
    
    static {
        ORIGIN = new Vec3d(8.0, 64.0, 8.0);
    }
    
    public static class Trace
    {
        private String name;
        private int index;
        private Vec3d pos;
        private final List<TracePos> trace;
        private DimensionType type;
        
        public Trace(final int index, final String name, final DimensionType type, final Vec3d pos, final List<TracePos> trace) {
            this.index = index;
            this.name = name;
            this.type = type;
            this.pos = pos;
            this.trace = trace;
        }
        
        public int getIndex() {
            return this.index;
        }
        
        public DimensionType getType() {
            return this.type;
        }
        
        public List<TracePos> getTrace() {
            return this.trace;
        }
        
        public String getName() {
            return this.name;
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public void setPos(final Vec3d pos) {
            this.pos = pos;
        }
        
        public void setIndex(final int index) {
            this.index = index;
        }
        
        public Vec3d getPos() {
            return this.pos;
        }
        
        public void setType(final DimensionType type) {
            this.type = type;
        }
        
        public static class TracePos
        {
            private final Vec3d pos;
            
            public TracePos(final Vec3d pos) {
                this.pos = pos;
            }
            
            public Vec3d getPos() {
                return this.pos;
            }
        }
    }
}
