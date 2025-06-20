package io.github.thebroccolibob.bobsmobgear.client.render.entity

import io.github.thebroccolibob.bobsmobgear.entity.WebShotEntity
import io.github.thebroccolibob.bobsmobgear.item.SpiderDaggerItem
import io.github.thebroccolibob.bobsmobgear.util.minus
import io.github.thebroccolibob.bobsmobgear.util.plus
import io.github.thebroccolibob.bobsmobgear.util.times
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.LivingEntity
import net.minecraft.util.Arm
import net.minecraft.util.Colors
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import kotlin.math.sin
import kotlin.math.sqrt

class WebShotEntityRenderer(ctx: EntityRendererFactory.Context) : EntityRenderer<WebShotEntity>(ctx) {
    override fun render(
        entity: WebShotEntity,
        yaw: Float,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int
    ) {
        val owner = entity.owner as? LivingEntity
        if (owner != null) {
            val handAngle =
                sin(sqrt(owner.getHandSwingProgress(tickDelta)) * Math.PI.toFloat())
            val start = entity.getLerpedPos(tickDelta).add(0.0, 0.25, 0.0)
            val end = getHandPos(owner, handAngle, tickDelta)
            val difference = end - start
            val normal = difference.normalize()

            val entry = matrices.peek()
            with (vertexConsumers.getBuffer(RenderLayer.LINE_STRIP)) {
                vertex(entry, Vec3d.ZERO, Colors.WHITE, normal)
                vertex(entry, difference, Colors.WHITE, normal)
            }
        }

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light)
    }

    private fun getHandPos(owner: LivingEntity, f: Float, tickDelta: Float): Vec3d {
        val inOffHand = owner.offHandStack.item is SpiderDaggerItem && owner.mainHandStack.item !is SpiderDaggerItem
        val mx = if (inOffHand xor (owner.mainArm == Arm.LEFT)) -1 else 1
        val appliedHandSwing = if (inOffHand) 0f else f

        if (dispatcher.gameOptions.perspective.isFirstPerson && owner === MinecraftClient.getInstance().player) {
            val m = 960.0 / dispatcher.gameOptions.fov.value.toInt()
            val vec3d = (dispatcher.camera.projection.getPosition(0.525f * mx, -0.1f) * m).rotateY(appliedHandSwing * 0.5F).rotateX(-appliedHandSwing * 0.7F);
            return owner.getCameraPosVec(tickDelta) + vec3d
        } else {
            val g = MathHelper.lerp(tickDelta, owner.prevBodyYaw, owner.bodyYaw) * (Math.PI / 180.0).toFloat()
            val d = MathHelper.sin(g).toDouble()
            val e = MathHelper.cos(g).toDouble()
            val h = owner.scale
            val j = mx * 0.35 * h
            val k = 0.8 * h
            val l = if (owner.isInSneakingPose) -0.1875f else 0.0f
            return owner.getCameraPosVec(tickDelta)
                .add(-e * j - d * k, l - 0.45 * h, -d * j + e * k)
        }
    }

    override fun getTexture(entity: WebShotEntity): Identifier = TEXTURE

    companion object {
        private val TEXTURE: Identifier = Identifier.ofVanilla("textures/entity/fishing_hook.png")

        fun VertexConsumer.vertex(entry: MatrixStack.Entry, pos: Vec3d, color: Int, normal: Vec3d) {
            vertex(entry, pos.x.toFloat(), pos.y.toFloat(), pos.z.toFloat())
            color(color)
            normal(entry, normal.x.toFloat(), normal.y.toFloat(), normal.z.toFloat())
        }
    }
}