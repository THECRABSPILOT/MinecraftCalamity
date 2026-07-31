package crab.mods.minecraftcalamity.items;

import crab.mods.minecraftcalamity.entity.custom.SwordProjectileEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnchantedSwordItem extends SwordItem {
    private final String hoverText;

    public EnchantedSwordItem(Tier tier, int attackDamageModifier, float attackSpeed, Properties properties, String hoverText) {
        super(tier, attackDamageModifier, attackSpeed, properties);
        this.hoverText = hoverText;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        if (hoverText != null && !hoverText.isEmpty()) {
            tooltipComponents.add(Component.literal(hoverText));
        }
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Apply a 0.5-second cooldown (10 ticks)
        player.getCooldowns().addCooldown(this, 10);

        if (!level.isClientSide()) {
            Vec3 lookVec = player.getLookAngle();
            double spawnX = player.getX() + lookVec.x * 1.5;
            double spawnY = player.getY() + player.getEyeHeight() + lookVec.y * 1.5;
            double spawnZ = player.getZ() + lookVec.z * 1.5;

            @SuppressWarnings("unchecked")
            EntityType<SwordProjectileEntity> projectileType = (EntityType<SwordProjectileEntity>) (EntityType<?>) net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.get(new net.minecraft.resources.ResourceLocation("minecraftcalamity", "sword_projectile"));

            SwordProjectileEntity projectile = new SwordProjectileEntity(projectileType, level);
            projectile.setPos(spawnX, spawnY, spawnZ);

            // Set initial rotation data immediately to prevent rotation lag/snapping after 1 second
            projectile.setYRot(player.getYRot());
            projectile.setXRot(player.getXRot());
            projectile.yRotO = player.getYRot();
            projectile.xRotO = player.getXRot();

            // Set projectile color to blue (Hex: 0x3D75FF or 0x0000FF)
            projectile.setProjectileColor(0x3D75FF);

            // Configure trail particles to use dust particles matching the color
            projectile.setParticleConfig("minecraft:dust", 0.1D, false, false);

            projectile.setStats(5.0D, 0.0D, 0.0D, false, 10.0F, null, 0, 0);

            // Set velocity and initial motion vector directly
            Vec3 motion = lookVec.scale(2.0);
            projectile.setDeltaMovement(motion);

            level.addFreshEntity(projectile);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}