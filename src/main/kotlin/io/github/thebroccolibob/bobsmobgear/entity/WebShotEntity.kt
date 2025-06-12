package io.github.thebroccolibob.bobsmobgear.entity

import io.github.thebroccolibob.bobsmobgear.duck.WebShotUser
import io.github.thebroccolibob.bobsmobgear.duck.webShot
import io.github.thebroccolibob.bobsmobgear.item.SpiderDaggerItem
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearEntities
import io.github.thebroccolibob.bobsmobgear.util.*
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.entity.projectile.ProjectileUtil
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext
import net.minecraft.world.World
import java.util.*


class WebShotEntity(type: EntityType<out WebShotEntity>, world: World) : ProjectileEntity(type, world) {
    init {
        ignoreCameraFrustum = true
    }

    private var hookedEntityId by HOOKED_ENTITY_ID

    var hookedEntity: Entity? = null
        private set(value: Entity?) {
            field = value
            hookedEntityId = value?.id
        }

    var isHookedOnBlock by HOOKED_ON_BLOCK
        private set
    var hookedBlockSide by HOOKED_BLOCK_SIDE
        private set
    var hookedBlockDistance by HOOKED_BLOCK_DISTANCE
        private set

    private var state = State.FLYING

    constructor(world: World, owner: LivingEntity) : this(BobsMobGearEntities.WEB_SHOT, world) {
        setOwner(owner)
        setPos(owner.x, owner.eyeY - 0.1, owner.z)
        setNoGravity(true)
    }

    val Entity.movementPos: Vec3d get() =
        if (hookedBlockSide == Direction.UP) pos else eyePos

    override fun initDataTracker(builder: DataTracker.Builder) {
        builder.add(HOOKED_ENTITY_ID, OptionalInt.empty())
        builder.add(HOOKED_ON_BLOCK, false)
        builder.add(HOOKED_BLOCK_SIDE, Direction.UP)
        builder.add(HOOKED_BLOCK_DISTANCE, 0f)
    }

    override fun onTrackedDataSet(data: TrackedData<*>) {
        if (data == HOOKED_ENTITY_ID) {
            hookedEntity = hookedEntityId?.let(world::getEntityById)
        }
        super.onTrackedDataSet(data)
    }

    val isHooked get() = state != State.FLYING

    override fun setOwner(entity: Entity?) {
        super.setOwner(entity)
        (entity as? WebShotUser)?.webShot = this
    }

    override fun onBlockHit(blockHitResult: BlockHitResult) {
        super.onBlockHit(blockHitResult)
        if (!world.isClient) {
            setPosition(blockHitResult.pos)
            isHookedOnBlock = true
            hookedBlockSide = blockHitResult.side ?: Direction.UP
            hookedBlockDistance = owner?.let { (pos - it.movementPos).length().toFloat() } ?: 0f
        }
    }

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        super.onEntityHit(entityHitResult)
        if (!world.isClient) hookedEntity = entityHitResult.entity
    }

    override fun onRemoved() {
        super.onRemoved()
        (owner as? WebShotUser)?.webShot = null
    }

    override fun remove(reason: RemovalReason) {
        (owner as? WebShotUser)?.webShot = null
        super.remove(reason)
    }

    override fun shouldRender(distance: Double): Boolean =
        (owner as? PlayerEntity)?.isMainPlayer == true || super.shouldRender(distance)

    override fun canHit(entity: Entity): Boolean =
        super.canHit(entity) || entity.isAlive && entity is ItemEntity

    override fun tick() {
        super.tick()

        val owner = owner as? LivingEntity ?: return discard()

        if (!world.isClient && removeIfInvalid(owner)) return

        if (state == State.FLYING) {
            if (!world.isClient)
                hitOrDeflect(ProjectileUtil.getCollision(this, ::canHit))

            if (hookedEntity != null) {
                velocity = Vec3d.ZERO
                state = State.HOOKED_IN_ENTITY
                return
            }

            if (isHookedOnBlock) {
                velocity = Vec3d.ZERO
                state = State.HOOKED_IN_BLOCK
                return
            }

            val velocity = velocity
            setPos(x + velocity.x, y + velocity.y, z + velocity.z)
            updateRotation()
            refreshPosition()
            return
        }

        if (state == State.HOOKED_IN_BLOCK) {
            if (!world.isClient && world.raycast(
                    RaycastContext(
                        pos.offset(hookedBlockSide, 0.0625),
                        pos.offset(hookedBlockSide, -0.0625),
                        RaycastContext.ShapeType.COLLIDER,
                        RaycastContext.FluidHandling.NONE,
                        this
                    )
                ).type === HitResult.Type.MISS
            ) {
                discard()
                return
            }

            val difference = pos - owner.movementPos
            val length = difference.length()
            if (length <= hookedBlockDistance)
                return
            owner.addVelocity(difference * (length - hookedBlockDistance.toDouble()) / length * 0.05)
        } else { // if (state == State.HOOKED_IN_ENTITY) {
            val hookedEntity = hookedEntity ?: return

            if (hookedEntity.isAlive && hookedEntity.world.registryKey === world.registryKey) {
                setPosition(hookedEntity.x, hookedEntity.getBodyY(0.8), hookedEntity.z)
            } else if (!world.isClient) discard()
        }
    }

    private fun removeIfInvalid(owner: LivingEntity): Boolean {
        if (!owner.isRemoved && owner.isAlive
            && squaredDistanceTo(owner) <= MAX_DISTANCE * MAX_DISTANCE &&
            (owner.mainHandStack.item !is SpiderDaggerItem || owner.offHandStack.item !is SpiderDaggerItem)
        ) return false

        discard()
        return true
    }

    enum class State {
        FLYING, HOOKED_IN_BLOCK, HOOKED_IN_ENTITY
    }

    companion object {
        val HOOKED_ENTITY_ID: TrackedData<OptionalInt> =
            DataTracker.registerData(WebShotEntity::class.java, TrackedDataHandlerRegistry.OPTIONAL_INT)
        val HOOKED_ON_BLOCK: TrackedData<Boolean> =
            DataTracker.registerData(WebShotEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)
        val HOOKED_BLOCK_SIDE: TrackedData<Direction> =
            DataTracker.registerData(WebShotEntity::class.java, TrackedDataHandlerRegistry.FACING)
        val HOOKED_BLOCK_DISTANCE: TrackedData<Float> =
            DataTracker.registerData(WebShotEntity::class.java, TrackedDataHandlerRegistry.FLOAT)

        const val MAX_VELOCITY: Double = 1.0
        const val MIN_DISTANCE: Double = 1.5
        const val MAX_DISTANCE: Double = 64.0
    }
}