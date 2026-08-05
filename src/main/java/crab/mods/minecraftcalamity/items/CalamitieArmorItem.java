package crab.mods.minecraftcalamity.items;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.client.model.CalamititeChampionArmorModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class CalamitieArmorItem extends ArmorItem {

    private static final ResourceLocation TEXTURE = new ResourceLocation(MinecraftCalamity.MODID, "textures/models/armor/calamititechampionarmor.png");

    public CalamitieArmorItem(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        // Standard vanilla equipment swap logic
        return this.swapWithEquipmentSlot(this, level, player, hand);
    }
    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return TEXTURE.toString();
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private CalamititeChampionArmorModel cachedModel;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                if (this.cachedModel == null) {
                    // Switch to CalamititeChampionArmorModel.LAYER_LOCATION here
                    ModelPart root = Minecraft.getInstance().getEntityModels().bakeLayer(CalamititeChampionArmorModel.LAYER_LOCATION);
                    this.cachedModel = new CalamititeChampionArmorModel(root);
                }

                // Configure active slot
                this.cachedModel.setSlot(slot);

                // Copy vanilla animations, body pose, crouch state, and rotation angles
                original.copyPropertiesTo((HumanoidModel) this.cachedModel);

                return this.cachedModel;
            }
        });
    }
}