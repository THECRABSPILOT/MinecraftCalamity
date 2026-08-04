package crab.mods.minecraftcalamity.items.spells.mage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "minecraftcalamity")
public class RougeSpells {

    // Keep data clean and structured
    private static final Object[][] SPELL_DATA = {
            {"crystal_shard", 10, 30},

    };

    public Object[][] getspelldata() {
        return SPELL_DATA;
    }


}
