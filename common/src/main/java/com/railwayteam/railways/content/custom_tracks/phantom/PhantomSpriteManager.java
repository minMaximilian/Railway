package com.railwayteam.railways.content.custom_tracks.phantom;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.mixin_interfaces.IPotentiallyInvisibleTextureAtlasSprite;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CRItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PhantomSpriteManager {
    private static final Map<ResourceLocation, WeakReference<TextureAtlasSprite>> map = new HashMap<>();
    private static boolean lastVisible = false;
    public static boolean firstRun = true;
    public static boolean hasChanged = false;

    public static boolean register(TextureAtlasSprite sprite) {
        if (sprite.getName().getNamespace().equals(Railways.MODID) && sprite.getName().getPath().startsWith("block/track/phantom/")) {
            map.put(sprite.getName(), new WeakReference<>(sprite));
            firstRun = true;
            return true;
        }
        return false;
    }

    public static void tick(Minecraft mc) {
        boolean visible = mc.player != null
            && (mc.player.getItemBySlot(EquipmentSlot.MAINHAND).getItem() == CRBlocks.PHANTOM_TRACK.get().asItem()
                || mc.player.getItemBySlot(EquipmentSlot.OFFHAND).getItem() == CRBlocks.PHANTOM_TRACK.get().asItem());
        if (visible != lastVisible || firstRun) {
            firstRun = false;
            lastVisible = visible;
            hasChanged = true;
            List<ResourceLocation> expired = new ArrayList<>();
            for (Map.Entry<ResourceLocation, WeakReference<TextureAtlasSprite>> entry : map.entrySet()) {
                if (entry.getValue().get() == null)
                    expired.add(entry.getKey());
            }
            for (ResourceLocation key : expired) {
                map.remove(key);
            }
        }
    }

    public static void renderTick(Minecraft mc) {
        if (hasChanged) {
            hasChanged = false;
            for (WeakReference<TextureAtlasSprite> ref : map.values()) {
                TextureAtlasSprite sprite = ref.get();
                if (sprite != null) {
                    ((IPotentiallyInvisibleTextureAtlasSprite) sprite).uploadFrame(lastVisible);
                }
            }
        }
    }
}
