package org.astemir.uniblend.core.display;

import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import org.astemir.uniblend.core.item.utils.ItemUtils;
import org.astemir.uniblend.io.json.*;
import org.astemir.uniblend.misc.RandomizedColor;
import org.astemir.uniblend.utils.NMSUtils;
import org.astemir.uniblend.utils.TextUtils;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;

public abstract class RenderProperty extends PropertyHolder{

    public static UJsonDeserializer<RenderProperty> DESERIALIZER = (json)->{
        JsonObject jsonObject = json.getAsJsonObject();
        String type = USerialization.getString(jsonObject,"type","item");
        Class<?> className = null;
        switch (type){
            case "item"-> className = ItemProperty.class;
            case "block"-> className = BlockProperty.class;
            case "text"-> className = TextProperty.class;
        }
        return PropertyHolder.newInstance((Class<? extends RenderProperty>) className, jsonObject);
    };

    public void randomizeColor(RandomizedColor color){
        if (this instanceof ItemProperty property){
            property.randomizeColor(color);
        }
    }

    abstract public void apply(Display display);

    abstract public EntityType getEntityType();

    public static class ItemProperty extends RenderProperty{
        @Property("item")
        private ItemStack itemStack;
        @Property(value = "display-mode",load = LoadType.ENUM)
        private RenderDisplayMode displayMode = RenderDisplayMode.FIXED;

        public ItemProperty(ItemStack itemStack, RenderDisplayMode displayMode) {
            this.itemStack = itemStack;
            this.displayMode = displayMode;
        }

        public ItemProperty() {}

        public ItemProperty(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        @Override
        public void apply(Display display) {
            if (display instanceof Display.ItemDisplay itemDisplay){
                itemDisplay.setItemTransform(displayMode.getContext());
                itemDisplay.setItemStack(NMSUtils.convert(itemStack));
            }
        }

        public void randomizeColor(RandomizedColor color){
            itemStack = ItemUtils.setItemColor(itemStack,TextUtils.color(color.get()));
        }


        @Override
        public EntityType getEntityType() {
            return EntityType.ITEM_DISPLAY;
        }
    }

    public static class BlockProperty extends RenderProperty{
        @Property("block")
        private BlockData blockData;
        public BlockProperty(BlockData blockData) {
            this.blockData = blockData;
        }

        public BlockProperty() {}

        @Override
        public void apply(Display display) {
            if (display instanceof Display.BlockDisplay blockDisplay){
                ((BlockDisplay)(blockDisplay.getBukkitEntity())).setBlock(blockData);
            }
        }

        @Override
        public EntityType getEntityType() {
            return EntityType.BLOCK_DISPLAY;
        }

    }

    public static class TextProperty extends RenderProperty{

        @Property("text")
        private Component component;
        @Property(value = "alignment",load = LoadType.ENUM)
        private TextDisplay.TextAlignment textAlignment = TextDisplay.TextAlignment.CENTER;
        @Property(value = "lineWidth")
        private int lineWidth = -1;
        @Property(value = "opacity")
        private byte opacity = -1;
        @Property(value = "shadowed")
        private boolean shadowed = true;

        public TextProperty(Component component) {
            this.component = component;
        }

        public TextProperty() {}

        @Override
        public void apply(Display display) {
            if (display instanceof Display.TextDisplay){
                TextDisplay textDisplay = ((TextDisplay)display.getBukkitEntity());
                textDisplay.text(component);
                if (lineWidth != -1) {
                    textDisplay.setLineWidth(lineWidth);
                }
                if (opacity != -1) {
                    textDisplay.setTextOpacity(opacity);
                }
                textDisplay.setShadowed(shadowed);
                textDisplay.setAlignment(textAlignment);
            }
        }


        public TextProperty lineWidth(int lineWidth) {
            this.lineWidth = lineWidth;
            return this;
        }

        public TextProperty opacity(byte opacity) {
            this.opacity = opacity;
            return this;
        }

        public TextProperty shadowed(boolean shadowed) {
            this.shadowed = shadowed;
            return this;
        }

        public TextProperty alignment(TextDisplay.TextAlignment textAlignment) {
            this.textAlignment = textAlignment;
            return this;
        }

        @Override
        public EntityType getEntityType() {
            return EntityType.TEXT_DISPLAY;
        }

    }
}
