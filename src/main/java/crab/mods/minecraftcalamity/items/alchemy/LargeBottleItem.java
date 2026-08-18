package crab.mods.minecraftcalamity.items.alchemy;

import crab.mods.minecraftcalamity.client.renderer.LargeBottleRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class LargeBottleItem extends Item {

    public static final int CAPACITY = 1000;
    public static final int DRINK_AMOUNT = 250;

    public LargeBottleItem(Properties properties) {
        super(properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        // DEBUG: Confirm initializeClient is executing on startup


        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {

                return new LargeBottleRenderer();
            }
        });
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack bottle = player.getItemInHand(hand);
        ItemStack offhand = player.getOffhandItem();


        // ==============================
        // Fill bottle with potion
        // ==============================

        if (player.isShiftKeyDown()
                && hand == InteractionHand.MAIN_HAND
                && offhand.is(Items.POTION)) {

            if (getFluidAmount(bottle) < CAPACITY) {
                if (!level.isClientSide) {
                    addPotion(bottle, offhand);

                    if (!player.getAbilities().instabuild) {
                        player.setItemInHand(
                                InteractionHand.OFF_HAND,
                                new ItemStack(Items.GLASS_BOTTLE)
                        );
                    }
                }
                return InteractionResultHolder.success(bottle);
            }
            return InteractionResultHolder.fail(bottle);
        }

        // ==============================
        // Drink mixture
        // ==============================

        if (getFluidAmount(bottle) >= DRINK_AMOUNT) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(bottle);
        }

        return InteractionResultHolder.fail(bottle);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide && entity instanceof ServerPlayer player) {
            CompoundTag tag = stack.getOrCreateTag();

            if (tag.contains("Effects")) {
                ListTag effects = tag.getList("Effects", 10);

                for (int i = 0; i < effects.size(); i++) {
                    MobEffectInstance effect = MobEffectInstance.load(effects.getCompound(i));
                    if (effect != null) {
                        player.addEffect(new MobEffectInstance(effect));
                    }
                }
            }

            setFluidAmount(stack, getFluidAmount(stack) - DRINK_AMOUNT);

            if (getFluidAmount(stack) <= 0) {
                tag.remove("Effects");
                setFluidAmount(stack, 0);
            }
        }
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    // ==============================
    // Tank Storage
    // ==============================

    public static int getFluidAmount(ItemStack stack) {
        return stack.getOrCreateTag().getInt("Fluid");
    }

    public static void setFluidAmount(ItemStack stack, int amount) {
        stack.getOrCreateTag().putInt(
                "Fluid",
                Math.max(0, Math.min(CAPACITY, amount))
        );
    }

    public static boolean isFull(ItemStack stack) {
        return getFluidAmount(stack) >= CAPACITY;
    }

    // ==============================
    // Potion Mixing
    // ==============================

    public static void addPotion(ItemStack bottle, ItemStack potion) {
        CompoundTag tag = bottle.getOrCreateTag();
        ListTag storedEffects;

        if (tag.contains("Effects")) {
            storedEffects = tag.getList("Effects", 10);
        } else {
            storedEffects = new ListTag();
        }

        for (MobEffectInstance effect : PotionUtils.getMobEffects(potion)) {
            boolean merged = false;

            for (int i = 0; i < storedEffects.size(); i++) {
                MobEffectInstance oldEffect = MobEffectInstance.load(storedEffects.getCompound(i));

                if (oldEffect != null && oldEffect.getEffect() == effect.getEffect()) {
                    int duration = Math.max(oldEffect.getDuration(), effect.getDuration());
                    int amplifier = Math.max(oldEffect.getAmplifier(), effect.getAmplifier());

                    MobEffectInstance combined = new MobEffectInstance(effect.getEffect(), duration, amplifier);
                    storedEffects.set(i, combined.save(new CompoundTag()));

                    merged = true;
                    break;
                }
            }

            if (!merged) {
                storedEffects.add(effect.save(new CompoundTag()));
            }
        }

        tag.put("Effects", storedEffects);
        setFluidAmount(bottle, getFluidAmount(bottle) + DRINK_AMOUNT);
    }

    // ==============================
    // Tooltip
    // ==============================

    @Override
    public void appendHoverText(ItemStack stack, Level level, java.util.List<Component> tooltip, TooltipFlag flag) {
        int amount = getFluidAmount(stack);

        tooltip.add(Component.literal("Contents: " + amount + " / " + CAPACITY + " mB"));

        CompoundTag tag = stack.getOrCreateTag();

        if (tag.contains("Effects")) {
            ListTag effects = tag.getList("Effects", 10);

            if (!effects.isEmpty()) {
                tooltip.add(Component.literal("Stored Effects:"));

                for (int i = 0; i < effects.size(); i++) {
                    MobEffectInstance effect = MobEffectInstance.load(effects.getCompound(i));
                    if (effect != null) {
                        tooltip.add(Component.literal("- " + effect.getDescriptionId() + " " + (effect.getAmplifier() + 1)));
                    }
                }
            } else {
                tooltip.add(Component.literal("Empty"));
            }
        } else {
            tooltip.add(Component.literal("Empty"));
        }

        super.appendHoverText(stack, level, tooltip, flag);
    }
}