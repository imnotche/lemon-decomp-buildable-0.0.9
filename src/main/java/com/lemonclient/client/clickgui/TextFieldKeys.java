// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.clickgui;

import com.lukflug.panelstudio.widget.ITextFieldKeys;

public class TextFieldKeys implements ITextFieldKeys
{
    @Override
    public boolean isBackspaceKey(final int scancode) {
        return scancode == 14;
    }
    
    @Override
    public boolean isDeleteKey(final int scancode) {
        return scancode == 211;
    }
    
    @Override
    public boolean isInsertKey(final int scancode) {
        return scancode == 210;
    }
    
    @Override
    public boolean isLeftKey(final int scancode) {
        return scancode == 203;
    }
    
    @Override
    public boolean isRightKey(final int scancode) {
        return scancode == 205;
    }
    
    @Override
    public boolean isHomeKey(final int scancode) {
        return scancode == 199;
    }
    
    @Override
    public boolean isEndKey(final int scancode) {
        return scancode == 207;
    }
    
    @Override
    public boolean isCopyKey(final int scancode) {
        return scancode == 46;
    }
    
    @Override
    public boolean isPasteKey(final int scancode) {
        return scancode == 47;
    }
    
    @Override
    public boolean isCutKey(final int scancode) {
        return scancode == 45;
    }
    
    @Override
    public boolean isAllKey(final int scancode) {
        return scancode == 30;
    }
}
