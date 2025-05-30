package com.qwaecd.paramagic.feature;

import java.util.ArrayList;
import java.util.List;

public class ManaLine {
    private final String id;
    private final List<String> nodeSequence;

    public ManaLine(String id) {
        this.id = id;
        this.nodeSequence = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public List<String> getNodeSequence() {
        return new ArrayList<>(nodeSequence);
    }

    public void addNode(String nodeId) {
        nodeSequence.add(nodeId);
    }

    public void removeNode(String nodeId) {
        nodeSequence.remove(nodeId);
    }

    public void insertNode(int index, String nodeId) {
        if (index >= 0 && index <= nodeSequence.size()) {
            nodeSequence.add(index, nodeId);
        }
    }

    public boolean isEmpty() {
        return nodeSequence.isEmpty();
    }

    public int size() {
        return nodeSequence.size();
    }
}
