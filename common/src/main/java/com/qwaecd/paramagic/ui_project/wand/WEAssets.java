package com.qwaecd.paramagic.ui_project.wand;

import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.ui.util.HorizontalSliceSprite;
import com.qwaecd.paramagic.ui.util.NineSliceSprite;
import com.qwaecd.paramagic.ui.util.Sprite;
import net.minecraft.resources.ResourceLocation;

public final class WEAssets {
    private WEAssets() {
    }
    public static final ResourceLocation WAND_EDIT_RL = ModRL.inModSpace("textures/gui/wand_edit.png");
    public static final int texW = 128;
    public static final int texH = 128;

    public static final NineSliceSprite INVENTORY_RECT =
            NineSliceSprite.builder(WAND_EDIT_RL, texW, texH)
                    .slice(0, 0, 0, 13, 13)
                    .slice(1, 13, 0, 24, 13)
                    .slice(2, 37, 0, 13, 13)
                    .slice(3, 0, 13, 13, 24)
                    .slice(4, 13, 13, 24, 24)
                    .slice(5, 37, 13, 13, 24)
                    .slice(6, 0, 37, 13, 13)
                    .slice(7, 13, 37, 24, 13)
                    .slice(8, 37, 37, 13, 13)
                    .build();

    public static final NineSliceSprite RECT_1 =
            NineSliceSprite.builder(WAND_EDIT_RL, texW, texH)
                    .slice(0, 81, 31, 8, 8)
                    .slice(1, 89, 31, 7, 8)
                    .slice(2, 96, 31, 8, 8)
                    .slice(3, 81, 39, 8, 7)
                    .slice(4, 89, 39, 7, 7)
                    .slice(5, 96, 39, 8, 7)
                    .slice(6, 81, 46, 8, 8)
                    .slice(7, 89, 46, 7, 8)
                    .slice(8, 96, 46, 8, 8)
                    .build();

    public static final NineSliceSprite SPELL_EDIT_RECT =
            NineSliceSprite.builder(WAND_EDIT_RL, texW, texH)
                    .slice(0, 52, 0, 8, 9)
                    .slice(1, 60, 0, 10, 9)
                    .slice(2, 70, 0, 8, 9)
                    .slice(3, 52, 9, 8, 7)
                    .slice(4, 60, 9, 10, 7)
                    .slice(5, 70, 9, 8, 7)
                    .slice(6, 52, 16, 8, 10)
                    .slice(7, 60, 16, 10, 10)
                    .slice(8, 70, 16, 8, 10)
                    .build();

    /**
     * 两头不拉伸，中心拉伸
     */
    public static final HorizontalSliceSprite HEAD_LINE =
            HorizontalSliceSprite.builder(WAND_EDIT_RL, texW, texH)
                    .slice(0, 81, 20, 11, 9)
                    .slice(1, 92, 20, 7, 9)
                    .slice(2, 99, 20, 11, 9)
                    .build();

    public static final NineSliceSprite SLIDER_LINE =
            NineSliceSprite.builder(WAND_EDIT_RL, texW, texH)
                    .slice(0, 53, 28, 11, 13)
                    .slice(1, 64, 28, 2, 13)
                    .slice(2, 66, 28, 13, 13)
                    .slice(3, 53, 41, 11, 2)
                    .slice(4, 64, 41, 2, 2)
                    .slice(5, 66, 41, 13, 2)
                    .slice(6, 53, 43, 11, 11)
                    .slice(7, 64, 43, 2, 11)
                    .slice(8, 66, 43, 13, 11)
                    .build();

    public static final Sprite SLIDER_HOR = new Sprite(WAND_EDIT_RL, 91, 58, 12, 7, texW, texH);
    public static final Sprite SLIDER_VER = new Sprite(WAND_EDIT_RL, 83, 58, 7, 12, texW, texH);

    public static final Sprite ARROW_UP = new Sprite(WAND_EDIT_RL, 45, 56, 35, 9, texW, texH);
    public static final Sprite ARROW_DOWN = new Sprite(WAND_EDIT_RL, 45, 66, 35, 9, texW, texH);

    public static final Sprite ITEM_RECT = new Sprite(WAND_EDIT_RL, 1, 53, 20, 20, texW, texH);
}
