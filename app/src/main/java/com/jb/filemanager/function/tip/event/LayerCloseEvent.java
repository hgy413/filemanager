package com.jb.filemanager.function.tip.event;

/**
 * Created by nieyh on 17-7-17.
 */

public class LayerCloseEvent {

    public int mLayerType;
    //自定义协议字段 此处为当用户点击停用时
    public boolean isEffect;

    public LayerCloseEvent(int layerType) {
        mLayerType = layerType;
    }

    public LayerCloseEvent(int layerType, boolean isEffect) {
        this.mLayerType = layerType;
        this.isEffect = isEffect;
    }
}
