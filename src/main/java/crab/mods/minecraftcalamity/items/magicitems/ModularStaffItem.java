package crab.mods.minecraftcalamity.items.magicitems;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;

import net.minecraft.world.entity.LightningBolt;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModularStaffItem extends Item {

    public ModularStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack staff = player.getItemInHand(hand);
        CompoundTag tag = staff.getTag();

        if (!level.isClientSide() && tag != null && tag.contains("SpellModifiers", Tag.TAG_LIST)) {
            ListTag modifiers = tag.getList("SpellModifiers", Tag.TAG_COMPOUND);

            boolean hasFireball = false;
            boolean hasLightning = false;
            boolean hasSplinter = false;
            boolean hasSpeedMod = false;
            boolean isBouncy = false;
            boolean isSplit = false;
            boolean hasBeaconLaser = false;

            for (int i = 0; i < modifiers.size(); i++) {
                CompoundTag modTag = modifiers.getCompound(i);
                String modId = modTag.getString("id");

                if (modId.equals("minecraftcalamity:fireball_core")) hasFireball = true;
                if (modId.equals("minecraftcalamity:lightning_core")) hasLightning = true;
                if (modId.equals("minecraftcalamity:splinter_modifier")) hasSplinter = true;
                if (modId.equals("minecraftcalamity:speed_modifier")) hasSpeedMod = true;
                if (modId.equals("minecraftcalamity:bounce_modifier")) isBouncy = true;
                if (modId.equals("minecraftcalamity:split_modifier")) isSplit = true;
                if (modId.equals("minecraftcalamity:beacon_laser_core")) hasBeaconLaser = true;
            }

            // Execute based on Core type
            if (hasFireball) {
                castFireball(level, player, tag, hasSpeedMod, isBouncy, isSplit);
            }
            if (hasLightning) {
                castLightning(level, player);
            }
            if (hasSplinter) {
                castSplinter(level, player);
            }

        }

        return InteractionResultHolder.sidedSuccess(staff, level.isClientSide());
    }

    private void castFireball(Level level, Player player, CompoundTag tag, boolean hasSpeedMod, boolean isBouncy, boolean isSplit) {
        Vec3 look = player.getLookAngle();
        int power = tag.contains("Power") ? tag.getInt("Power") : 2;

        double speedMultiplier = hasSpeedMod ? 2.5 : 1.2;

        LargeFireball fireball = new LargeFireball(level, player, look.x * speedMultiplier * 0.1, look.y * speedMultiplier * 0.1, look.z * speedMultiplier * 0.1, power);
        fireball.setPos(player.getX() + look.x * 1.2, player.getEyeY() + look.y * 1.2, player.getZ() + look.z * 1.2);
        fireball.setDeltaMovement(look.scale(speedMultiplier));

        // Store modifier properties and calamity weapon tag inside entity persistent data
        CompoundTag fireballData = fireball.getPersistentData();
        fireballData.putBoolean("IsCalamityWeaponFireball", true);

        if (isBouncy) {
            fireballData.putBoolean("IsBouncy", true);
            fireballData.putInt("BouncesLeft", 3);
        }
        if (isSplit) {
            fireballData.putBoolean("IsSplit", true);
            fireballData.putInt("SplitCount", 0); // Tracks total splits up to max 10
        }
    }

    private void castLightning(Level level, Player player) {
        Vec3 look = player.getLookAngle();
        Vec3 targetPos = player.position().add(look.scale(15.0)); // Casts lightning 15 blocks out in gaze direction
        BlockPos pos = new BlockPos((int) targetPos.x, (int) targetPos.y, (int) targetPos.z);

        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
        if (lightning != null && level instanceof ServerLevel serverLevel) {
            lightning.moveTo(Vec3.atBottomCenterOf(pos));
            serverLevel.addFreshEntity(lightning);
        }
    }



    private void castSplinter(Level level, Player player) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SPLASH_POTION_BREAK, SoundSource.PLAYERS, 1.0F, 0.5F);

        // Explode a circle of small particles/projectiles around the player
        int count = 8;
        double radius = 1.5;
        for (int i = 0; i < count; i++) {
            double angle = (2 * Math.PI / count) * i;
            double dx = Math.cos(angle);
            double dz = Math.sin(angle);

            // Use SmallFireball instead of LargeFireball so they look like tiny gross projectiles
            net.minecraft.world.entity.projectile.SmallFireball splinterProj = new net.minecraft.world.entity.projectile.SmallFireball(
                    level, player, dx * 0.5, 0.0, dz * 0.5
            );

            splinterProj.setPos(player.getX() + dx * radius, player.getY() + 0.5, player.getZ() + dz * radius);
            splinterProj.setDeltaMovement(dx * 0.8, 0.1, dz * 0.8);

            // Mark as a sticky splinter projectile
            CompoundTag data = splinterProj.getPersistentData();
            data.putBoolean("IsSplinter", true);

            level.addFreshEntity(splinterProj);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
        CompoundTag tag = stack.getTag();

        int totalSlots = (tag != null && tag.contains("SpellSlots")) ? tag.getInt("SpellSlots") : 2;
        tooltip.add(Component.literal("§7Available Slots: §b" + totalSlots));

        if (tag != null && tag.contains("SpellModifiers", Tag.TAG_LIST)) {
            ListTag spells = tag.getList("SpellModifiers", Tag.TAG_COMPOUND);
            if (!spells.isEmpty()) {
                tooltip.add(Component.literal("§6Bound Spells:"));
                for (int i = 0; i < spells.size(); i++) {
                    CompoundTag spell = spells.getCompound(i);
                    int slotIndex = spell.getInt("Slot");
                    String rawId = spell.getString("id");

                    ResourceLocation resLoc = ResourceLocation.tryParse(rawId);
                    Component spellDisplayName;

                    if (resLoc != null && ForgeRegistries.ITEMS.containsKey(resLoc)) {
                        Item spellItem = ForgeRegistries.ITEMS.getValue(resLoc);
                        spellDisplayName = (spellItem != null) ? spellItem.getDescription() : Component.literal(rawId);
                    } else {
                        spellDisplayName = Component.literal(rawId);
                    }

                    tooltip.add(Component.literal("  §8- §7Slot " + slotIndex + ": §f").append(spellDisplayName));
                }
            } else {
                tooltip.add(Component.literal("§7No Spells Bound"));
            }
        } else {
            tooltip.add(Component.literal("§7No Spells Bound"));
        }

        super.appendHoverText(stack, level, tooltip, isAdvanced);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("SpellSlots", 2);
        return stack;
    }
}