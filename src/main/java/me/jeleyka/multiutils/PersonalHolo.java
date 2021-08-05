package me.jeleyka.multiutils;


import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import me.jeleyka.testtask.Main;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Player;

import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class PersonalHolo extends EntityArmorStand {

    private final TextReceiver textReceiver;

    public PersonalHolo(TextReceiver textReceiver, final Location location) {
        super(((CraftWorld) location.getWorld()).getHandle(),
                location.getX(), location.getY() + 1.7, location.getZ());

        this.textReceiver = textReceiver;

        setCustomNameVisible(true);
        setInvisible(true);
        setInvulnerable(true);
        setNoGravity(true);
        setMarker(true);
        getWorld().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    public void update(final Player player) {
        if (!player.hasLineOfSight(getBukkitEntity())) return;

        WrapperPlayServerEntityMetadata wrapper = new WrapperPlayServerEntityMetadata();
        wrapper.setEntityID(this.getId());
        wrapper.setMetadata(new ArrayList<>());
        wrapper.sendPacket(player);
    }

    public void update() {
        Bukkit.getOnlinePlayers().forEach(this::update);
    }

    public interface TextReceiver {
        String getText(Player player);
    }

    static {
        if (Main.getInstance().isEnabled()) {
            ProtocolLibrary.getProtocolManager().addPacketListener(
                    new PacketAdapter(Main.getInstance(),
                            PacketType.Play.Server.ENTITY_METADATA) {
                        @Override
                        public void onPacketSending(PacketEvent event) {
                            WrapperPlayServerEntityMetadata wrapper =
                                    new WrapperPlayServerEntityMetadata(event.getPacket());

                            Entity entity = ((CraftEntity) wrapper.getEntity(event)).getHandle();

                            if (entity instanceof PersonalHolo) {
                                List<WrappedWatchableObject> metadata = wrapper.getMetadata();

                                metadata.add(new WrappedWatchableObject(new WrappedDataWatcher.WrappedDataWatcherObject(
                                        2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)),
                                        Optional.of(new ChatComponentText(((((PersonalHolo) entity)
                                                .textReceiver.getText(event.getPlayer())))))));

                                wrapper.setMetadata(metadata);

                            }
                        }
                    });
        }
    }

    public static void clearHolos() {
        Bukkit.getWorlds().forEach(
                w -> w.getEntities().stream()
                        .filter(e -> ((CraftEntity) e).getHandle() instanceof PersonalHolo)
                        .forEach(org.bukkit.entity.Entity::remove)
        );
    }


}

