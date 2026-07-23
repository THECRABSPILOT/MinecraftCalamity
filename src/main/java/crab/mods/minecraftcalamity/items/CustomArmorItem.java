package crab.mods.minecraftcalamity.items;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.client.model.CustomArmorModel;
import crab.mods.minecraftcalamity.event.ClientModEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
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

public class CustomArmorItem extends ArmorItem {

    // Path to the texture png exported from Blockbench
    private static final ResourceLocation TEXTURE = new ResourceLocation(MinecraftCalamity.MODID, "textures/models/armor/custom_armor.png");

    public CustomArmorItem(ArmorMaterial material, Type type, Properties properties) {
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
                ModelPart root = Minecraft.getInstance().getEntityModels().bakeLayer(ClientModEvents.CUSTOM_ARMOR_LAYER);
                CustomArmorModel model = new CustomArmorModel(root);

                // Sync entity animations (crouching, riding, stealth, etc.)
                model.crouching = original.crouching;
                model.riding = original.riding;
                model.young = original.young;

                return model;
            }
        });
    }
}