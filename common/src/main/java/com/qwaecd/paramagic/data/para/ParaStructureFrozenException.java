package com.qwaecd.paramagic.data.para;

public class ParaStructureFrozenException extends RuntimeException {
    public ParaStructureFrozenException() {
        super("Cannot get children before freezing structure.");
    }

    public ParaStructureFrozenException(String message) {
        super(message);
    }
}
