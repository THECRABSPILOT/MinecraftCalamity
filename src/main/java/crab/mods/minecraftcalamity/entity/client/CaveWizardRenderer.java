package crab.mods.minecraftcalamity.entity.client;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.entity.custom.CaveWizardEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CaveWizardRenderer extends MobRenderer<CaveWizardEntity, CaveWizardModel<CaveWizardEntity>> {
    public CaveWizardRenderer(EntityRendererProvider.Context context) {
        super(context, new CaveWizardModel<>(context.bakeLayer(CaveWizardModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(CaveWizardEntity entity) {
        return new ResourceLocation(MinecraftCalamity.MODID, "textures/entity/cave_wizard.png");
    }
}