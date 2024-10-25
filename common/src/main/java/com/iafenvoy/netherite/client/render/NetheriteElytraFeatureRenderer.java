package com.iafenvoy.netherite.client.render;

import com.iafenvoy.netherite.NetheriteExtension;
import com.iafenvoy.netherite.registry.NetheriteExtItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class NetheriteElytraFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    public static final Identifier NETHERITE_ELYTRA_SKIN = new Identifier(NetheriteExtension.MOD_ID, "textures/entity/netherite_elytra.png");
    private final ElytraEntityModel<T> elytra;

    public NetheriteElytraFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext, EntityModelLoader entityModelLoader) {
        super(featureRendererContext);
        this.elytra = new ElytraEntityModel<>(entityModelLoader.getModelPart(EntityModelLayers.ELYTRA));
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
        ItemStack itemStack = livingEntity.getEquippedStack(EquipmentSlot.CHEST);
        if (itemStack.isOf(NetheriteExtItems.NETHERITE_ELYTRA.get())) {
            Identifier identifier4;
            if (livingEntity instanceof AbstractClientPlayerEntity abstractClientPlayerEntity) {
                if (abstractClientPlayerEntity.canRenderElytraTexture() && abstractClientPlayerEntity.getElytraTexture() != null)
                    identifier4 = abstractClientPlayerEntity.getElytraTexture();
                else if (abstractClientPlayerEntity.canRenderCapeTexture() && abstractClientPlayerEntity.getCapeTexture() != null && abstractClientPlayerEntity.isPartVisible(PlayerModelPart.CAPE))
                    identifier4 = abstractClientPlayerEntity.getCapeTexture();
                else identifier4 = NETHERITE_ELYTRA_SKIN;
            } else identifier4 = NETHERITE_ELYTRA_SKIN;

            matrixStack.push();
            matrixStack.translate(0.0D, 0.0D, 0.125D);
            this.getContextModel().copyStateTo(this.elytra);
            this.elytra.setAngles(livingEntity, f, g, j, k, l);
            VertexConsumer vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumerProvider, this.elytra.getLayer(identifier4), false, itemStack.hasGlint());
            this.elytra.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStack.pop();
        }
    }
}
