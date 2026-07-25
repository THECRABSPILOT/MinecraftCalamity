package crab.mods.minecraftcalamity.items;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.client.model.CalamititeArmorModel;
import crab.mods.minecraftcalamity.event.ClientModEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class CalamitieArmorItem extends ArmorItem {

    private static final ResourceLocation TEXTURE = new ResourceLocation(MinecraftCalamity.MODID, "textures/models/armor/calamitite_soldier_armor.png");

    public CalamitieArmorItem(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return TEXTURE.toString();
    }


    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                ModelPart root = Minecraft.getInstance().getEntityModels().bakeLayer(ClientModEvents.CALAMITITE_ARMOR_LAYER);
                CalamititeArmorModel model = new CalamititeArmorModel(root);

                model.setSlot(CalamitieArmorItem.this.getEquipmentSlot());

                @SuppressWarnings("unchecked")
                HumanoidModel<LivingEntity> castOriginal = (HumanoidModel<LivingEntity>) original;

                // Copy entity pose/rotation into your armor model
                castOriginal.copyPropertiesTo(model);

                // Hide the base player's outer cosmetic layers
                if (castOriginal instanceof PlayerModel<?> playerModel) {
                    // Hides outer head/hat layer
                    playerModel.hat.visible = false;

                    // Hides coat / jacket outer body layer
                    playerModel.jacket.visible = false;

                    // Optional: Hide outer sleeve layers if your armor covers arms
                    playerModel.leftSleeve.visible = false;
                    playerModel.rightSleeve.visible = false;

                    // Optional: Hide outer pant layers if needed
                    playerModel.leftPants.visible = false;
                    playerModel.rightPants.visible = false;
                }

                return model;
            }
        });
    }
}