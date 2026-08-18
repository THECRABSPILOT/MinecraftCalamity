package crab.mods.minecraftcalamity.items.accessory;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TestAccessoryItem extends Item {

    public TestAccessoryItem(Properties properties) {
        super(properties);
    }

    // Called every tick when the item is equipped/processed
    public void onEquippedTick(Player player) {
        if (!player.level().isClientSide()) {
            // Apply Fire Resistance for 20 ticks (1 second) every tick to keep it active seamlessly
            // Arguments: (Effect, Duration, Amplifier, Ambient, Visible/Particles, ShowIcon)
            player.addEffect(new MobEffectInstance(
                    MobEffects.FIRE_RESISTANCE,
                    40,    // Duration: 2 seconds (refreshed constantly)
                    0,     // Amplifier: Level 1
                    false, // ambient: false
                    false, // visible: false (Hides potion particles!)
                    false  // showIcon: false (Hides HUD icon!)
            ));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.literal("§7Grants permanent fire immunity."));
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}