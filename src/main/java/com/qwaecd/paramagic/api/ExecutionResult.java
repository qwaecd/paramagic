package com.qwaecd.paramagic.api;

import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;

public class ExecutionResult {
    private final boolean success;
    private final Component errorMessage;
    private final Map<String, Object> returnData;

    public ExecutionResult(boolean success) {
        this(success, null, new HashMap<>());
    }

    public ExecutionResult(boolean success, Component errorMessage) {
        this(success, errorMessage, new HashMap<>());
    }

    public ExecutionResult(boolean success, Component errorMessage, Map<String, Object> returnData) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.returnData = returnData != null ? returnData : new HashMap<>();
    }

    public boolean isSuccess() {
        return success;
    }

    public Component getErrorMessage() {
        return errorMessage;
    }

    public Map<String, Object> getReturnData() {
        return returnData;
    }

    public static ExecutionResult success() {
        return new ExecutionResult(true);
    }

    public static ExecutionResult success(Map<String, Object> data) {
        return new ExecutionResult(true, null, data);
    }

    public static ExecutionResult failure(Component message) {
        return new ExecutionResult(false, message);
    }
}
