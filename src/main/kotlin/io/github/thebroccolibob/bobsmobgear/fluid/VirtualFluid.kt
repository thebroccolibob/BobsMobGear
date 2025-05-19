package io.github.thebroccolibob.bobsmobgear.fluid

import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.fluid.Fluid
import net.minecraft.fluid.FluidState
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.WorldView

open class VirtualFluid : Fluid() {
    override fun getBucketItem(): Item = Items.AIR

    override fun canBeReplacedWith(
        state: FluidState?,
        world: BlockView?,
        pos: BlockPos?,
        fluid: Fluid?,
        direction: Direction?
    ): Boolean = true

    override fun getVelocity(world: BlockView?, pos: BlockPos?, state: FluidState?): Vec3d = Vec3d.ZERO

    override fun getTickRate(world: WorldView?): Int = 5

    override fun getBlastResistance(): Float = 100f

    override fun getHeight(state: FluidState?, world: BlockView?, pos: BlockPos?): Float = getHeight(state)

    override fun getHeight(state: FluidState?): Float = getLevel(state) / 9f

    override fun toBlockState(state: FluidState?): BlockState = Blocks.AIR.defaultState

    override fun isStill(state: FluidState?): Boolean = true

    override fun getLevel(state: FluidState?): Int = 8

    override fun getShape(state: FluidState?, world: BlockView?, pos: BlockPos?): VoxelShape = VoxelShapes.fullCube()
}