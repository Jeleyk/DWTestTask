package me.jeleyka.testtask.game.entities;

import me.jeleyka.multiutils.UtilAlgo;
import me.jeleyka.testtask.Main;
import me.jeleyka.testtask.entities.SimpleMonster;
import me.jeleyka.testtask.entities.types.Fighter;
import me.jeleyka.testtask.utils.MobData;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;


public class SummonedZombie extends SimpleMonster implements Fighter {

    public SummonedZombie(MobData mobData) {
        super(EntityTypes.ZOMBIE, "&7Мелкий зомби", mobData);
        setBaby();
    }

    public SummonedZombie(Location location) {
        this(Main.getInstance().getMobsConfig()
                .getMobData(SummonedZombie.class).setLocation(location));
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.getDataWatcher().register((DataWatcherObject<Boolean>) UtilAlgo.
                getPrivateField(EntityZombie.class, "d", null), true);
    }

    public void setBaby() {
            if (this.world != null && !this.world.isClientSide) {
                AttributeModifiable attributemodifiable = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);
                AttributeModifier c = (AttributeModifier) UtilAlgo.
                        getPrivateField(EntityZombie.class, "c", null);
                attributemodifiable.removeModifier(c);
                attributemodifiable.b(c);
            }
    }

    @Override
    public double bb() {
        return 0.0D;
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return 0.93F;
    }


}
