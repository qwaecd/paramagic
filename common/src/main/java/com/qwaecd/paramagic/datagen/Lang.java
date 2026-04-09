package com.qwaecd.paramagic.datagen;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class Lang {
    private static final Map<String, Lang> languages = new HashMap<>();
    @Getter
    public final String en_us;
    @Getter
    public final String zh_cn;

    protected Lang(String en_us, String zh_cn) {
        this.en_us = en_us;
        this.zh_cn = zh_cn;
    }

    public static Lang of(String key, String en_us, String zh_cn) {
        Lang lang = new Lang(en_us, zh_cn);
        languages.put(key, lang);
        return lang;
    }

    @FunctionalInterface
    public interface LangConsumer {
        void accept(String key, Lang lang);
    }

    public static void forEach(LangConsumer consumer) {
        languages.forEach(consumer::accept);
    }

    public static void init() {
        of("item_group.paramagic.main", "Paramagic", "参数魔法");
        of("item.paramagic.debug_wand", "Debug wand", "调试魔杖");
        of("item.paramagic.explosion_wand", "EXPLOSION WAND", "慧慧的法杖");
        of("block.paramagic.spell_edit_table", "Spell Edit Table", "法术编辑台");
        of("item.paramagic.void_operator", "Void Operator", "空参量");
        of("item.paramagic.accelerate_operator", "Accelerate", "加速");
        of("item.paramagic.gradual_acceleration_operator", "Gradual Acceleration", "逐渐加速");
        of("item.paramagic.heavy_operator", "Heavy", "沉重");
        of("item.paramagic.laser_operator", "Laser", "激光");
        of("item.paramagic.magic_arrow_operator", "Magic Arrow", "魔法箭");
        of("item.paramagic.para_crystal", "Para Crystal", "魔力水晶");
        of("item.paramagic.short_tracking_operator", "Short Tracking", "短距离追踪");
        of("item.paramagic.tracking_operator", "Tracking", "追踪");
        of("item.paramagic.weightless_operator", "Weightless", "失重");
        of("item.paramagic.gravity_collapse_operator", "Gravitational Collapse", "引力坍缩");
        operatorTooltip();
        gui();
        paraType();
    }

    private static void operatorTooltip() {
        of("tooltip.paramagic.para_operator_item.operator_type",
                "§lOperator Type: §r", "§l法术类型: §r");
        of("tooltip.paramagic.para_operator_item.transmission_delay",
                "§lTransmission Delay: §r", "§l传导延迟: §r");
        of("tooltip.paramagic.para_operator_item.cycle_cooldown",
                "§lCycle Cooldown: §r", "§l回转冷却: §r");

        of("tooltip.paramagic.magic_arrow_operator.describe", "Ordinary arrow", "普通的箭矢");
        of("tooltip.paramagic.void_operator.describe", "Does nothing", "什么也不做");
        of("tooltip.paramagic.accelerate_operator.describe", "Accelerates the launch of projectiles", "使投射物加速射出");
        of("tooltip.paramagic.gradual_acceleration_operator.describe", "Continuously accelerates projectiles to a higher speed", "让投射物持续向更高速度加速");
        of("tooltip.paramagic.heavy_operator.describe", "Makes projectiles fall faster and appear heavier", "让投射物更快下坠并显得更加沉重");
        of("tooltip.paramagic.laser_operator.describe", "Fires a high-speed laser with a glowing beam trail", "发射一束高速前进并带有发光束线的激光");
        of("tooltip.paramagic.short_tracking_operator.describe", "Strongly bends projectiles to track close-range targets", "强力扭转投射物以追向近距离目标");
        of("tooltip.paramagic.tracking_operator.describe", "Bends projectiles to track distant targets", "扭转投射物以追向较远目标");
        of("tooltip.paramagic.weightless_operator.describe", "Significantly reduces the gravity applied to projectiles", "显著降低投射物受到的重力");
        of("tooltip.paramagic.gravity_collapse_operator.describe1", "When you distort the world's magic field to its limit...", "当你将世界的魔力场扭曲至极限时...");
    }

    private static void paraType() {
        of("paramagic.para_component_type.void", "Void", "空");
        of("paramagic.para_component_type.ring", "Ring", "圆环");
        of("paramagic.para_component_type.polygon", "Polygon", "正多边形");
        of("paramagic.para_component_type.curvy_star", "Curvy Star", "弧边形");
    }

    private static void gui() {
        of("gui.paramagic.spell_edit_table.para_struct", "Para Struct", "参量结构");
        of("gui.paramagic.spell_edit_table.crystal_edit", "Crystal", "水晶编辑");
        of("gui.paramagic.spell_edit_table.para_tree", "Para Tree", "法术树");
        of("gui.paramagic.spell_edit_table.context_menu.add_path", "Add Para Path", "添加参量路径");
        of("gui.paramagic.spell_edit_table.context_menu.remove_path", "Remove Para Path", "移除参量路径");
        of("gui.paramagic.spell_edit_table.context_menu.open_window", "Open Edit Window", "打开编辑窗口");
        of("gui.paramagic.spell_edit_table.header_menu.create_cache", "Create Cache", "创建缓存");
        of("gui.paramagic.spell_edit_table.header_menu.rebuild_cache", "Rebuild Cache", "重建缓存");
        of("gui.paramagic.spell_edit_table.header_menu.clear_cache", "Clear Cache", "清空缓存");
        of("gui.paramagic.spell_edit_table.header_menu.submit_struct", "Submit Struct", "提交当前结构");
        of("gui.paramagic.spell_edit_table.header_status.no_cache", "No Cache", "无缓存");
        of("gui.paramagic.spell_edit_table.header_status.unsubmitted", "Unsubmitted", "未提交");
        of("gui.paramagic.spell_edit_table.header_status.pending", "Pending", "等待确认");
        of("gui.paramagic.spell_edit_table.header_status.submitted", "Submitted", "已提交");

        // Labels in edit window
        of("gui.paramagic.spell_edit_table.label_texts.color", "Color:", "颜色:");

        of("gui.paramagic.spell_edit_table.label_texts.name", "Name:", "节点名称:");
        of("gui.paramagic.spell_edit_table.label_texts.type", "ParaType:", "参量类型:");

        of("gui.paramagic.spell_edit_table.label_texts.radius", "Radius:", "半径:");
        of("gui.paramagic.spell_edit_table.label_texts.sides", "Sides:", "边数:");
        of("gui.paramagic.spell_edit_table.label_texts.start_angle_degrees", "Start Angle (deg):", "起始角（度）:");
        of("gui.paramagic.spell_edit_table.label_texts.line_width", "Line Width:", "线宽:");
        of("gui.paramagic.spell_edit_table.label_texts.inner_radius", "Inner Radius:", "内半径:");
        of("gui.paramagic.spell_edit_table.label_texts.outer_radius", "Outer Radius:", "外半径:");
        of("gui.paramagic.spell_edit_table.label_texts.segments", "Segments:", "分段数:");
        of("gui.paramagic.spell_edit_table.label_texts.curvature", "Curvature:", "曲率:");
        of("gui.paramagic.spell_edit_table.label_texts.intensity", "Intensity:", "发光强度:");
        of("gui.paramagic.spell_edit_table.label_texts.position", "Position:", "位置:");
        of("gui.paramagic.spell_edit_table.label_texts.rotation_degrees", "Rotation (deg):", "旋转（度）:");
        of("gui.paramagic.spell_edit_table.label_texts.scale", "Scale:", "缩放:");
    }
}
