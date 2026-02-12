package com.qwaecd.paramagic.spell.session.client;

import com.qwaecd.paramagic.spell.session.store.SessionDataStore;
import com.qwaecd.paramagic.spell.view.CasterTransformSource;

public interface ClientSessionView {
    /**
     * 获取用于渲染的变换信息, 返回值可以被 lambda 捕获.
     * @return 施法者的变换信息来源.
     */
    CasterTransformSource casterSource();
    int casterNetId() throws NullPointerException;
    SessionDataStore getDataStore();
}
