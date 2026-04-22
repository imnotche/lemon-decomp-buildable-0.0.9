// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import com.lukflug.panelstudio.base.IInterface;
import com.lemonclient.client.clickgui.LemonClientGUI;
import com.lukflug.panelstudio.theme.ITheme;
import java.awt.Point;
import com.lukflug.panelstudio.component.IFixedComponent;

public abstract class HUDModule extends Module
{
    public static final int LIST_BORDER = 1;
    protected IFixedComponent component;
    protected Point position;
    
    public HUDModule() {
        this.position = new Point(this.getDeclaration().posX(), this.getDeclaration().posZ());
    }
    
    private Declaration getDeclaration() {
        return this.getClass().getAnnotation(Declaration.class);
    }
    
    public abstract void populate(final ITheme p0);
    
    public IFixedComponent getComponent() {
        return this.component;
    }
    
    public void resetPosition() {
        this.component.setPosition(LemonClientGUI.guiInterface, this.position);
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE })
    public @interface Declaration {
        int posX();
        
        int posZ();
    }
}
