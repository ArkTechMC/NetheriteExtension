package com.iafenvoy.netherite.client.render;

import com.iafenvoy.netherite.NetheriteExtension;
import com.iafenvoy.netherite.block.NetheriteShulkerBoxBlock;
import com.iafenvoy.netherite.block.entity.NetheriteShulkerBoxBlockEntity;
import com.iafenvoy.netherite.registry.NetheriteBlocks;
import com.iafenvoy.netherite.registry.NetheriteItems;
import com.iafenvoy.netherite.registry.NetheriteRenderers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.Comparator;

public class NetheritePlusBuiltinItemModelRenderer {
    private static final SpriteIdentifier NETHERITE_SHIELD_BASE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.of(NetheriteExtension.MOD_ID, "entity/netherite_shield_base"));
    private static final SpriteIdentifier NETHERITE_SHIELD_BASE_NO_PATTERN = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.of(NetheriteExtension.MOD_ID, "entity/netherite_shield_base_nopattern"));
    private static final NetheriteShulkerBoxBlockEntity RENDER_NETHERITE_SHULKER_BOX = new NetheriteShulkerBoxBlockEntity(BlockPos.ORIGIN, NetheriteBlocks.NETHERITE_SHULKER_BOX.get().getDefaultState());
    private static final NetheriteShulkerBoxBlockEntity[] RENDER_NETHERITE_SHULKER_BOX_DYED = Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(dyeColor -> new NetheriteShulkerBoxBlockEntity(dyeColor, BlockPos.ORIGIN, NetheriteBlocks.NETHERITE_SHULKER_BOX.get().getDefaultState())).toArray(NetheriteShulkerBoxBlockEntity[]::new);
    private static ShieldEntityModel modelNetheriteShield;
    private final EntityModelLoader entityModelLoader;
    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;

    public NetheritePlusBuiltinItemModelRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelLoader entityModelLoader) {
        this.blockEntityRenderDispatcher = blockEntityRenderDispatcher;
        this.entityModelLoader = entityModelLoader;
    }

    public void loadShieldModel() {
        modelNetheriteShield = new ShieldEntityModel(this.entityModelLoader.getModelPart(NetheriteRenderers.NETHERITE_SHIELD_MODEL_LAYER));
    }

    public static void renderTrident(TridentEntityModel model, ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (mode.getIndex() >= 5) {
            matrices.pop();
            MinecraftClient.getInstance().getItemRenderer().renderItem(
                    stack, mode, false, matrices, vertexConsumers, light, overlay,
                    MinecraftClient.getInstance().getBakedModelManager().getModel(new ModelIdentifier(Identifier.of(NetheriteExtension.MOD_ID, "netherite_trident"), "inventory")));
            matrices.push();
        } else {
            matrices.push();
            matrices.scale(1.0F, -1.0F, -1.0F);
            VertexConsumer consumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, model.getLayer(Identifier.of(NetheriteExtension.MOD_ID, "textures/entity/netherite_trident.png")), false, stack.hasGlint());
            model.render(matrices, consumer, light, overlay, -1);
            matrices.pop();
        }
    }

    public void render(ItemStack itemStack, ModelTransformationMode transformType, MatrixStack matrices, VertexConsumerProvider vertices, int light, int overlay) {
        if (itemStack.isOf(NetheriteItems.NETHERITE_TRIDENT.get()))
            renderTrident(new TridentEntityModel(this.entityModelLoader.getModelPart(EntityModelLayers.TRIDENT)), itemStack, transformType, matrices, vertices, light, overlay);
        else if (itemStack.isOf(NetheriteItems.NETHERITE_SHIELD.get())) {
            if (modelNetheriteShield == null) loadShieldModel();
            boolean bl = itemStack.contains(DataComponentTypes.BASE_COLOR) && itemStack.contains(DataComponentTypes.BANNER_PATTERNS);
            matrices.push();
            matrices.scale(1.0F, -1.0F, -1.0F);
            SpriteIdentifier spriteIdentifier = bl ? NETHERITE_SHIELD_BASE : NETHERITE_SHIELD_BASE_NO_PATTERN;
            VertexConsumer vertexConsumer = spriteIdentifier.getSprite().getTextureSpecificVertexConsumer(ItemRenderer.getDirectItemGlintConsumer(vertices, modelNetheriteShield.getLayer(spriteIdentifier.getAtlasId()), true, itemStack.hasGlint()));
            modelNetheriteShield.getHandle().render(matrices, vertexConsumer, light, overlay, -1);
            if (bl)
                BannerBlockEntityRenderer.renderCanvas(matrices, vertices, light, overlay, modelNetheriteShield.getPlate(), spriteIdentifier, false, itemStack.get(DataComponentTypes.BASE_COLOR), itemStack.get(DataComponentTypes.BANNER_PATTERNS), itemStack.hasGlint());
            else
                modelNetheriteShield.getPlate().render(matrices, vertexConsumer, light, overlay, -1);

            matrices.pop();
        } else if (itemStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof NetheriteShulkerBoxBlock block) {
            DyeColor dyecolor = block.getColor();
            NetheriteShulkerBoxBlockEntity entity = dyecolor == null ? RENDER_NETHERITE_SHULKER_BOX : RENDER_NETHERITE_SHULKER_BOX_DYED[dyecolor.getId()];
            this.blockEntityRenderDispatcher.renderEntity(entity, matrices, vertices, light, overlay);
        }
    }
}
