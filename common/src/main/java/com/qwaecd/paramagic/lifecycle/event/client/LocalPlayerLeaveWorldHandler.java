package com.qwaecd.paramagic.lifecycle.event.client;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;

@PlatformScope(PlatformScopeType.CLIENT)
public interface LocalPlayerLeaveWorldHandler {
    /**
     * 离开服务器 / 退出存档
     */
    void onLeave();
}
