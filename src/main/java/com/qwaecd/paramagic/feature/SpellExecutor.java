package com.qwaecd.paramagic.feature;

import com.qwaecd.paramagic.api.ExecutionResult;
import com.qwaecd.paramagic.api.IMagicMap;
import com.qwaecd.paramagic.api.ManaContext;
import com.qwaecd.paramagic.init.MagicMapRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public class SpellExecutor {
    private static final int MAX_DEPTH = 30;

    public static ExecutionResult executeSpell(Spell spell, ServerLevel level, Player caster, ItemStack wand, BlockPos targetPos) {
        return executeSpell(spell, level, caster, wand, targetPos, 0);
    }

    private static ExecutionResult executeSpell(Spell spell, ServerLevel level, Player caster, ItemStack wand, BlockPos targetPos, int depth) {
        if (depth > MAX_DEPTH) {
            return ExecutionResult.failure("Maximum nesting depth exceeded");
        }

        // Get current mana from wand
        int manaPool = getManaFromWand(wand);
        if (manaPool <= 0) {
            return ExecutionResult.failure("Insufficient mana");
        }

        ManaContext context = new ManaContext(level, caster, wand, targetPos, manaPool);

        // Execute each mana line
        for (ManaLine line : spell.getManaLines()) {
            ExecutionResult lineResult = executeManaLine(line, spell, context, depth);
            if (!lineResult.isSuccess()) {
                return lineResult;
            }
        }

        // Update wand mana
        setManaToWand(wand, context.getAvailableMana());
        return ExecutionResult.success();
    }

    private static ExecutionResult executeManaLine(ManaLine line, Spell spell, ManaContext context, int depth) {
        for (String nodeId : line.getNodeSequence()) {
            ManaNode node = spell.getNode(nodeId);
            if (node == null) {
                continue;
            }

            if (node.isEndNode()) {
                break; // Stop at end node
            }

            // Consume mana for this node
            int consumed = node.consumeMana(context.getAvailableMana());
            if (!context.consumeMana(consumed)) {
                return ExecutionResult.failure("Insufficient mana at node: " + node.getName());
            }

            // Execute all bound magic maps
            for (String mapId : node.getBoundMagicMaps()) {
                IMagicMap magicMap = MagicMapRegistry.get(mapId);
                if (magicMap == null) {
                    continue;
                }

                ExecutionResult result = magicMap.execute(context);
                if (!result.isSuccess()) {
                    return result;
                }

                // Handle nested spell execution
                if (result.getReturnData().containsKey("nested_spell")) {
                    Spell nestedSpell = (Spell) result.getReturnData().get("nested_spell");
                    ExecutionResult nestedResult = executeSpell(nestedSpell, context.getLevel(),
                            context.getCaster(), context.getWand(), context.getTargetPos(), depth + 1);
                    if (!nestedResult.isSuccess()) {
                        return nestedResult;
                    }
                }

                // Pass return data to context for next magic maps
                for (Map.Entry<String, Object> entry : result.getReturnData().entrySet()) {
                    context.setParameter(entry.getKey(), entry.getValue());
                }
            }
        }

        return ExecutionResult.success();
    }

    private static int getManaFromWand(ItemStack wand) {
        // TODO: Implement with Forge Capability system
        return wand.getOrCreateTag().getInt("mana");
    }

    private static void setManaToWand(ItemStack wand, int mana) {
        // TODO: Implement with Forge Capability system
        wand.getOrCreateTag().putInt("mana", mana);
    }
}
