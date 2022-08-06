package dev.smithed.companion.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.smithed.companion.SmithedMain.MODID;

// YES IM WELL AWARE THIS IS MESSY CODE
// THIS CODE EXISTS AS A SHITPOST AND A SHITPOST ONLY
// IT WAS MADE IN LIKE 5 MINUTES A JACHRO TROLL
@Mixin(PlayerEntityRenderer.class)
public abstract class JachroMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    private static final Identifier TEXTURE = new Identifier(MODID,"textures/jachro.png");
    private static final Identifier TEXTURE_HURT = new Identifier(MODID,"textures/jachro_hurt.png");

    public JachroMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
    public void renderJachro(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (abstractClientPlayerEntity.getEntityName().equals("Jachro")) {

            matrixStack.push();
            matrixStack.translate(0,0.5,0);
            matrixStack.scale(2.0F, 2.0F, 2.0F);
            matrixStack.multiply(this.dispatcher.getRotation());
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
            MatrixStack.Entry entry = matrixStack.peek();

            VertexConsumer jachroConsumer;
            if(abstractClientPlayerEntity.hurtTime > 0)
                jachroConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(TEXTURE_HURT));
            else
                jachroConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(TEXTURE));


            produceJachroVertex(jachroConsumer, matrixStack, i, entry);
            matrixStack.pop();

            matrixStack.push();
            matrixStack.translate(0,0.5,0);
            matrixStack.scale(2.0F, 2.0F, 2.0F);
            matrixStack.multiply(this.dispatcher.getRotation());
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
            MatrixStack.Entry entry2 = matrixStack.peek();

            VertexConsumer armorConsumer;
            if(abstractClientPlayerEntity.getInventory().armor.get(3).isOf(Items.GOLDEN_HELMET))
                armorConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getArmorCutoutNoCull(new Identifier(MODID,"textures/golden_helmet.png")));
            else if(abstractClientPlayerEntity.getInventory().armor.get(3).isOf(Items.CHAINMAIL_HELMET))
                armorConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getArmorCutoutNoCull(new Identifier(MODID,"textures/chainmail_helmet.png")));
            else if(abstractClientPlayerEntity.getInventory().armor.get(3).isOf(Items.DIAMOND_HELMET))
                armorConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getArmorCutoutNoCull(new Identifier(MODID,"textures/diamond_helmet.png")));
            else if(abstractClientPlayerEntity.getInventory().armor.get(3).isOf(Items.TURTLE_HELMET))
                armorConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getArmorCutoutNoCull(new Identifier(MODID,"textures/turtle_helmet.png")));
            else if(abstractClientPlayerEntity.getInventory().armor.get(3).isOf(Items.NETHERITE_HELMET))
                armorConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getArmorCutoutNoCull(new Identifier(MODID,"textures/netherite_helmet.png")));
            else if(abstractClientPlayerEntity.getInventory().armor.get(3).isOf(Items.LEATHER_HELMET))
                armorConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getArmorCutoutNoCull(new Identifier(MODID,"textures/leather_helmet.png")));
            else if(abstractClientPlayerEntity.getInventory().armor.get(3).isOf(Items.IRON_HELMET))
                armorConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getArmorCutoutNoCull(new Identifier(MODID,"textures/iron_helmet.png")));
            else
                armorConsumer = jachroConsumer;
            produceJachroVertex(armorConsumer, matrixStack, i, entry2);

            matrixStack.pop();
            ci.cancel();
        }

    }

    private void produceJachroVertex(VertexConsumer JachroConsumer, MatrixStack matrixStack, int light, MatrixStack.Entry entry) {

        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();

        produceVertex(JachroConsumer, matrix4f, matrix3f, light, 0.0F, 0, 0, 1);
        produceVertex(JachroConsumer, matrix4f, matrix3f, light, 1.0F, 0, 1, 1);
        produceVertex(JachroConsumer, matrix4f, matrix3f, light, 1.0F, 1, 1, 0);
        produceVertex(JachroConsumer, matrix4f, matrix3f, light, 0.0F, 1, 0, 0);
    }

    private static void produceVertex(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Matrix3f normalMatrix, int light, float x, int y, int textureU, int textureV) {
        vertexConsumer.vertex(positionMatrix, x - 0.5F, (float)y - 0.25F, 0.0F).color(255, 255, 255, 255).texture((float)textureU, (float)textureV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
    }

}
