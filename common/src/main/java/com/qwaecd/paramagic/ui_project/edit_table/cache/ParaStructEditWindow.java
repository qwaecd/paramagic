package com.qwaecd.paramagic.ui_project.edit_table.cache;

import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.util.NineSliceSprite;
import com.qwaecd.paramagic.ui.widget.UILabel;
import com.qwaecd.paramagic.ui.widget.node.MouseCaptureNode;
import com.qwaecd.paramagic.ui.widget.node.TypingBox;
import com.qwaecd.paramagic.ui_project.edit_table.EditTableSprite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ParaStructEditWindow extends MouseCaptureNode {
    private static final NineSliceSprite sprite =
            NineSliceSprite.builder(ModRL.inModSpace("textures/gui/edit_table.png"), EditTableSprite.TEX_W, EditTableSprite.TEX_H)
                    .slice(0, 80,  224, 16, 16)
                    .slice(1, 96,  224, 16, 16)
                    .slice(2, 112, 224, 16, 16)
                    .slice(3, 80,  240, 16, 16)
                    .slice(4, 96,  240, 16, 16)
                    .slice(5, 112, 240, 16, 16)
                    .slice(6, 80,  256, 16, 16)
                    .slice(7, 96,  256, 16, 16)
                    .slice(8, 112, 256, 16, 16)
                    .build();

    private static final float PADDING = 6.0f;
    private static final float ROW_GAP = 2.0f;
    private static final float INPUT_HEIGHT = 14.0f;
    private static final float LABEL_HEIGHT = 10.0f;
    private static final float COMPONENT_GAP = 4.0f;
    private static final float WINDOW_WIDTH = 200.0f;

    @Nullable
    private ParaStruct struct;

    // Section labels
    private final UILabel nameLabel;
    private final UILabel positionLabel;
    private final UILabel rotationLabel;
    private final UILabel scaleLabel;
    private final UILabel colorLabel;
    private final UILabel intensityLabel;

    // Component labels
    private final UILabel[] posCompLabels;
    private final UILabel[] rotCompLabels;
    private final UILabel[] scaleCompLabels;
    private final UILabel[] colorCompLabels;

    // Input boxes
    private final TypingBox nameBox;
    private final TypingBox[] posBoxes;
    private final TypingBox[] rotBoxes;
    private final TypingBox[] scaleBoxes;
    private final TypingBox[] colorBoxes;
    private final TypingBox intensityBox;

    public ParaStructEditWindow(@Nonnull ParaStruct struct) {
        this.struct = struct;
        this.localRect.setWH(WINDOW_WIDTH, 0);
        this.localRect.setXY(50.0f, 40.0f);

        this.nameLabel = createLabel("Name:");
        this.positionLabel = createLabel("Position:");
        this.rotationLabel = createLabel("Rotation:");
        this.scaleLabel = createLabel("Scale:");
        this.colorLabel = createLabel("Color:");
        this.intensityLabel = createLabel("Intensity:");

        this.posCompLabels = createLabels("x:", "y:", "z:");
        this.rotCompLabels = createLabels("x:", "y:", "z:", "w:");
        this.scaleCompLabels = createLabels("x:", "y:", "z:");
        this.colorCompLabels = createLabels("r:", "g:", "b:", "a:");

        this.nameBox = createInputBox(128);
        this.posBoxes = createInputBoxes(3, 16);
        this.rotBoxes = createInputBoxes(4, 16);
        this.scaleBoxes = createInputBoxes(3, 16);
        this.colorBoxes = createInputBoxes(4, 16);
        this.intensityBox = createInputBox(16);

        this.setupCallbacks();
    }

    @Override
    protected void afterChildAttachedToManager() {
        this.syncFromStruct();
    }

    // ---- Factory helpers ----

    private UILabel createLabel(String text) {
        UILabel label = new UILabel(text);
        label.setHitTestable(false);
        this.addChild(label);
        return label;
    }

    private UILabel[] createLabels(String... texts) {
        UILabel[] labels = new UILabel[texts.length];
        for (int i = 0; i < texts.length; i++) {
            labels[i] = createLabel(texts[i]);
        }
        return labels;
    }

    private TypingBox createInputBox(int maxLength) {
        TypingBox box = new TypingBox();
        box.localRect.set(0, 0, 0, INPUT_HEIGHT);
        box.setMaxLength(maxLength);
        this.addChild(box);
        return box;
    }

    private TypingBox[] createInputBoxes(int count, int maxLength) {
        TypingBox[] boxes = new TypingBox[count];
        for (int i = 0; i < count; i++) {
            boxes[i] = createInputBox(maxLength);
        }
        return boxes;
    }

    // ---- Data binding ----

    private void setupCallbacks() {
        this.nameBox.setFocusChangeListener(focused -> {
            if (!focused && this.struct != null) {
                String text = this.nameBox.getText();
                this.struct.setName(text.isEmpty() ? null : text);
            }
        });

        // Position
        bindFloat(this.posBoxes[0],
                () -> this.struct != null ? this.struct.getPosition().x : 0,
                v -> { if (this.struct != null) this.struct.getPosition().x = v; });
        bindFloat(this.posBoxes[1],
                () -> this.struct != null ? this.struct.getPosition().y : 0,
                v -> { if (this.struct != null) this.struct.getPosition().y = v; });
        bindFloat(this.posBoxes[2],
                () -> this.struct != null ? this.struct.getPosition().z : 0,
                v -> { if (this.struct != null) this.struct.getPosition().z = v; });

        // Rotation
        bindFloat(this.rotBoxes[0],
                () -> this.struct != null ? this.struct.getRotation().x : 0,
                v -> { if (this.struct != null) this.struct.getRotation().x = v; });
        bindFloat(this.rotBoxes[1],
                () -> this.struct != null ? this.struct.getRotation().y : 0,
                v -> { if (this.struct != null) this.struct.getRotation().y = v; });
        bindFloat(this.rotBoxes[2],
                () -> this.struct != null ? this.struct.getRotation().z : 0,
                v -> { if (this.struct != null) this.struct.getRotation().z = v; });
        bindFloat(this.rotBoxes[3],
                () -> this.struct != null ? this.struct.getRotation().w : 0,
                v -> { if (this.struct != null) this.struct.getRotation().w = v; });

        // Scale
        bindFloat(this.scaleBoxes[0],
                () -> this.struct != null ? this.struct.getScale().x : 0,
                v -> { if (this.struct != null) this.struct.getScale().x = v; });
        bindFloat(this.scaleBoxes[1],
                () -> this.struct != null ? this.struct.getScale().y : 0,
                v -> { if (this.struct != null) this.struct.getScale().y = v; });
        bindFloat(this.scaleBoxes[2],
                () -> this.struct != null ? this.struct.getScale().z : 0,
                v -> { if (this.struct != null) this.struct.getScale().z = v; });

        // Color
        bindFloat(this.colorBoxes[0],
                () -> this.struct != null ? this.struct.getColor().x : 0,
                v -> { if (this.struct != null) this.struct.getColor().x = v; });
        bindFloat(this.colorBoxes[1],
                () -> this.struct != null ? this.struct.getColor().y : 0,
                v -> { if (this.struct != null) this.struct.getColor().y = v; });
        bindFloat(this.colorBoxes[2],
                () -> this.struct != null ? this.struct.getColor().z : 0,
                v -> { if (this.struct != null) this.struct.getColor().z = v; });
        bindFloat(this.colorBoxes[3],
                () -> this.struct != null ? this.struct.getColor().w : 0,
                v -> { if (this.struct != null) this.struct.getColor().w = v; });

        // Intensity
        bindFloat(this.intensityBox,
                () -> this.struct != null ? this.struct.getIntensity() : 0,
                v -> { if (this.struct != null) this.struct.setIntensity(v); });
    }

    private void bindFloat(TypingBox box, FloatSupplier getter, FloatConsumer setter) {
        box.setFocusChangeListener(focused -> {
            if (focused) return;
            String text = box.getText();
            try {
                float value = Float.parseFloat(text);
                if (Float.isFinite(value)) {
                    setter.accept(value);
                } else {
                    box.setText(String.valueOf(getter.get()));
                }
            } catch (NumberFormatException e) {
                box.setText(String.valueOf(getter.get()));
            }
        });
    }

    @FunctionalInterface
    private interface FloatSupplier {
        float get();
    }

    @FunctionalInterface
    private interface FloatConsumer {
        void accept(float value);
    }

    public void setEditStruct(@Nonnull ParaStruct struct) {
        this.struct = struct;
        this.syncFromStruct();
    }

    private void syncFromStruct() {
        if (this.struct == null) {
            return;
        }
        String name = this.struct.getName();
        this.nameBox.setText(name != null ? name : "");

        this.posBoxes[0].setText(String.valueOf(this.struct.getPosition().x));
        this.posBoxes[1].setText(String.valueOf(this.struct.getPosition().y));
        this.posBoxes[2].setText(String.valueOf(this.struct.getPosition().z));

        this.rotBoxes[0].setText(String.valueOf(this.struct.getRotation().x));
        this.rotBoxes[1].setText(String.valueOf(this.struct.getRotation().y));
        this.rotBoxes[2].setText(String.valueOf(this.struct.getRotation().z));
        this.rotBoxes[3].setText(String.valueOf(this.struct.getRotation().w));

        this.scaleBoxes[0].setText(String.valueOf(this.struct.getScale().x));
        this.scaleBoxes[1].setText(String.valueOf(this.struct.getScale().y));
        this.scaleBoxes[2].setText(String.valueOf(this.struct.getScale().z));

        this.colorBoxes[0].setText(String.valueOf(this.struct.getColor().x));
        this.colorBoxes[1].setText(String.valueOf(this.struct.getColor().y));
        this.colorBoxes[2].setText(String.valueOf(this.struct.getColor().z));
        this.colorBoxes[3].setText(String.valueOf(this.struct.getColor().w));

        this.intensityBox.setText(String.valueOf(this.struct.getIntensity()));
    }

    // ---- Layout ----

    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        Font font = Minecraft.getInstance().font;
        float contentW = this.localRect.w - PADDING * 2;
        int compLabelW = font.width("w:") + 1;
        float textVOffset = (INPUT_HEIGHT - font.lineHeight) / 2.0f;
        float y = PADDING;

        // Name: [input]
        this.nameLabel.localRect.setXY(PADDING, y + textVOffset);
        float nameLabelW = font.width("Name:") + 2;
        this.nameBox.localRect.set(PADDING + nameLabelW, y, contentW - nameLabelW, INPUT_HEIGHT);
        y += INPUT_HEIGHT + ROW_GAP;

        // Position
        this.positionLabel.localRect.setXY(PADDING, y);
        y += LABEL_HEIGHT + 1;
        layoutVecRow(this.posCompLabels, this.posBoxes, y, contentW, compLabelW, textVOffset);
        y += INPUT_HEIGHT + ROW_GAP;

        // Rotation
        this.rotationLabel.localRect.setXY(PADDING, y);
        y += LABEL_HEIGHT + 1;
        layoutVecRow(this.rotCompLabels, this.rotBoxes, y, contentW, compLabelW, textVOffset);
        y += INPUT_HEIGHT + ROW_GAP;

        // Scale
        this.scaleLabel.localRect.setXY(PADDING, y);
        y += LABEL_HEIGHT + 1;
        layoutVecRow(this.scaleCompLabels, this.scaleBoxes, y, contentW, compLabelW, textVOffset);
        y += INPUT_HEIGHT + ROW_GAP;

        // Color
        this.colorLabel.localRect.setXY(PADDING, y);
        y += LABEL_HEIGHT + 1;
        layoutVecRow(this.colorCompLabels, this.colorBoxes, y, contentW, compLabelW, textVOffset);
        y += INPUT_HEIGHT + ROW_GAP;

        // Intensity
        this.intensityLabel.localRect.setXY(PADDING, y);
        y += LABEL_HEIGHT + 1;
        this.intensityBox.localRect.set(PADDING, y, contentW, INPUT_HEIGHT);
        y += INPUT_HEIGHT + PADDING;

        this.localRect.h = y;
        super.layout(parentX, parentY, parentW, parentH);
    }

    private void layoutVecRow(UILabel[] labels, TypingBox[] boxes,
                              float y, float contentW, int labelW, float textVOffset) {
        int count = boxes.length;
        float boxW = (contentW - count * labelW - (count - 1) * COMPONENT_GAP) / count;
        float x = PADDING;
        for (int i = 0; i < count; i++) {
            labels[i].localRect.setXY(x, y + textVOffset);
            boxes[i].localRect.set(x + labelW, y, boxW, INPUT_HEIGHT);
            x += labelW + boxW + COMPONENT_GAP;
        }
    }

    // ---- Rendering ----

    @Override
    protected void renderBackGround(UIRenderContext context) {
        context.renderNineSliceSprite(
                sprite,
                (int) this.worldRect.x,
                (int) this.worldRect.y,
                (int) this.worldRect.w,
                (int) this.worldRect.h
        );
    }
}
