package com.qwaecd.paramagic.network.api;

public enum PacketDirection {
    /**
     * client -> server<br>
     * handled on server side
     */
    SERVER,
    /**
     * server -> client<br>
     * handled on client side
     */
    CLIENT
}
