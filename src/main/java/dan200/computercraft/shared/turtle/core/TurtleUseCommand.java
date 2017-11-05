/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.turtle.core;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleCommand;
import dan200.computercraft.api.turtle.TurtleAnimation;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.inventory.ContainerComputer;
import dan200.computercraft.shared.util.DirectionUtil;
import dan200.computercraft.shared.util.InventoryUtil;
import dan200.computercraft.shared.util.WorldUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;

public class TurtleUseCommand implements ITurtleCommand
{
    private final InteractDirection m_direction;
    private final Object[] m_extraArguments;

    public TurtleUseCommand( InteractDirection direction, Object[] arguments )
    {
        m_direction = direction;
        m_extraArguments = arguments;
    }

    @Nonnull
    @Override
    public TurtleCommandResult execute( @Nonnull ITurtleAccess turtle )
    {
        // Get thing to place
        ItemStack stack = turtle.getInventory().getStackInSlot( turtle.getSelectedSlot() );

        // Remember old block
        EnumFacing direction = m_direction.toWorldDir( turtle );
        World world = turtle.getWorld();
        BlockPos coordinates = WorldUtil.moveCoords( turtle.getPosition(), direction );

        IBlockState previousState;
        if( WorldUtil.isBlockInWorld( world, coordinates ) )
        {
            previousState = world.getBlockState( coordinates );
        }
        else
        {
            previousState = null;
        }

        // Do the deploying
        String[] errorMessage = new String[1];
        ItemStack remainder = deploy( stack, turtle, direction, m_extraArguments, errorMessage );
            // Put the remaining items back
            turtle.getInventory().setInventorySlotContents( turtle.getSelectedSlot(), remainder );
            turtle.getInventory().markDirty();

            // Remember the old block
            if( turtle instanceof TurtleBrain && previousState != null )
            {
                TurtleBrain brain = (TurtleBrain)turtle;
                brain.saveBlockChange( coordinates, previousState );
            }

            // Animate and return success
            turtle.playAnimation( TurtleAnimation.Wait );
            return result ? TurtleCommandResult.success() : TurtleCommandResult.failure();
        
        
    }
    
    private static boolean result = false;
    public static ItemStack deploy( @Nonnull ItemStack stack, ITurtleAccess turtle, EnumFacing direction, Object[] extraArguments, String[] o_errorMessage )
    {
        // Create a fake player, and orient it appropriately
    	result = false;
    	ItemStack copy = stack.copy();
        BlockPos playerPosition = WorldUtil.moveCoords( turtle.getPosition(), direction );
        TurtlePlayer turtlePlayer = createPlayer( turtle, playerPosition, direction );
        EnumFacing playerDir = direction.getOpposite();
        orientPlayer( turtle, turtlePlayer, playerPosition, playerDir );

        // Calculate where the turtle would hit the block
        float hitX = 0.5f + direction.getFrontOffsetX() * 0.5f;
        float hitY = 0.5f + direction.getFrontOffsetY() * 0.5f;
        float hitZ = 0.5f + direction.getFrontOffsetZ() * 0.5f;
        if( Math.abs( hitY - 0.5f ) < 0.01f )
        {
            hitY = 0.45f;
        }
        // Deploy on an entity
        ItemStack remainder = deployOnEntity( stack, turtle, turtlePlayer, direction, extraArguments, o_errorMessage );
        if( remainder != stack )
        {
            return remainder;
        }

        // Deploy on the block immediately in front
        BlockPos position = turtle.getPosition();
        BlockPos newPosition = WorldUtil.moveCoords( position, direction );
        IBlockState iblock = turtlePlayer.world.getBlockState(newPosition);
        Block block = iblock.getBlock();
        turtlePlayer.loadInventory(stack.copy());
        try {
        	EnumActionResult re = turtlePlayer.interactionManager.processRightClickBlock(turtlePlayer, turtlePlayer.world, stack.copy(), EnumHand.MAIN_HAND, newPosition, turtlePlayer.getHorizontalFacing(), hitX,hitY,hitZ);
        	result = re==EnumActionResult.SUCCESS ? true : false;
        	if (!result) {
        		EnumActionResult iref = stack.onItemUseFirst( (EntityPlayer)turtlePlayer, turtle.getWorld(), position, EnumHand.MAIN_HAND, direction, hitX, hitY, hitZ );
        		result = iref==EnumActionResult.SUCCESS ? true : false;
        	}
        	if (!result) {
        		ActionResult<ItemStack> ire = stack.useItemRightClick(turtlePlayer.world, turtlePlayer, EnumHand.MAIN_HAND);
        		turtlePlayer.loadInventory( ire.getResult() );
        		result = ire.getType()==EnumActionResult.SUCCESS ? true : false;
        	}
        	//result = block.onBlockActivated(turtlePlayer.world, newPosition, iblock, turtlePlayer, EnumHand.MAIN_HAND, turtlePlayer.getAdjustedHorizontalFacing(), 0, 0, 0);
        } catch (Exception e) {
        	
        }

        
        ItemStack t = turtlePlayer.unloadInventory(turtle);
        if (result) {
        	t.setItemDamage(copy.getItemDamage());
        }
        // If nothing worked, return the original stack unchanged
        return t;
    }

    public static TurtlePlayer createPlayer( ITurtleAccess turtle, BlockPos position, EnumFacing direction )
    {
        TurtlePlayer turtlePlayer = new TurtlePlayer( (WorldServer)turtle.getWorld() );
        orientPlayer( turtle, turtlePlayer, position, direction );
        return turtlePlayer;
    }

    public static void orientPlayer( ITurtleAccess turtle, TurtlePlayer turtlePlayer, BlockPos position, EnumFacing direction )
    {
        turtlePlayer.posX = position.getX() + 0.5;
        turtlePlayer.posY = position.getY() + 0.5;
        turtlePlayer.posZ = position.getZ() + 0.5;

        // Stop intersection with the turtle itself
        if( turtle.getPosition().equals( position ) )
        {
            turtlePlayer.posX += 0.48 * direction.getFrontOffsetX();
            turtlePlayer.posY += 0.48 * direction.getFrontOffsetY();
            turtlePlayer.posZ += 0.48 * direction.getFrontOffsetZ();
        }

        if( direction.getAxis() != EnumFacing.Axis.Y )
        {
            turtlePlayer.rotationYaw = DirectionUtil.toYawAngle( direction );
            turtlePlayer.rotationPitch = 0.0f;
        }
        else
        {
            turtlePlayer.rotationYaw = DirectionUtil.toYawAngle( turtle.getDirection() );
            turtlePlayer.rotationPitch = DirectionUtil.toPitchAngle( direction );
        }

        turtlePlayer.prevPosX = turtlePlayer.posX;
        turtlePlayer.prevPosY = turtlePlayer.posY;
        turtlePlayer.prevPosZ = turtlePlayer.posZ;
        turtlePlayer.prevRotationPitch = turtlePlayer.rotationPitch;
        turtlePlayer.prevRotationYaw = turtlePlayer.rotationYaw;

        turtlePlayer.rotationYawHead = turtlePlayer.rotationYaw;
        turtlePlayer.prevRotationYawHead = turtlePlayer.rotationYawHead;
    }

    @Nonnull
    private static ItemStack deployOnEntity( @Nonnull ItemStack stack, final ITurtleAccess turtle, TurtlePlayer turtlePlayer, EnumFacing direction, Object[] extraArguments, String[] o_errorMessage )
    {
        // See if there is an entity present
        final World world = turtle.getWorld();
        final BlockPos position = turtle.getPosition();
        Vec3d turtlePos = new Vec3d( turtlePlayer.posX, turtlePlayer.posY, turtlePlayer.posZ );
        Vec3d rayDir = turtlePlayer.getLook( 1.0f );
        Pair<Entity, Vec3d> hit = WorldUtil.rayTraceEntities( world, turtlePos, rayDir, 1.5 );
        if( hit == null )
        {
            return stack;
        }

        // Load up the turtle's inventory
        ItemStack stackCopy = stack.copy();
        turtlePlayer.loadInventory( stackCopy );

        // Start claiming entity drops
        Entity hitEntity = hit.getKey();
        Vec3d hitPos = hit.getValue();
        ComputerCraft.setEntityDropConsumer( hitEntity, ( entity, drop ) ->
        {
            ItemStack remainder = InventoryUtil.storeItems( drop, turtle.getItemHandler(), turtle.getSelectedSlot() );
            if( !remainder.isEmpty() )
            {
                WorldUtil.dropItemStack( remainder, world, position, turtle.getDirection().getOpposite() );
            }
        } );

        // Place on the entity
        boolean placed = false;
        EnumActionResult cancelResult = ForgeHooks.onInteractEntityAt( turtlePlayer, hitEntity, hitPos, EnumHand.MAIN_HAND );
        if( cancelResult == null )
        {
            cancelResult = hitEntity.applyPlayerInteraction( turtlePlayer, hitPos, EnumHand.MAIN_HAND );
        }

        if( cancelResult == EnumActionResult.SUCCESS )
        {
            placed = true;
        }
        else
        {
            // See EntityPlayer.interactOn
            cancelResult = ForgeHooks.onInteractEntity( turtlePlayer, hitEntity, EnumHand.MAIN_HAND );
            if( cancelResult == EnumActionResult.SUCCESS )
            {
                placed = true;
            }
            else if( cancelResult == null )
            {
                if( hitEntity.processInitialInteract( turtlePlayer, EnumHand.MAIN_HAND ) )
                {
                    placed = true;
                }
                else if( hitEntity instanceof EntityLivingBase )
                {
                    placed = stackCopy.interactWithEntity( turtlePlayer, (EntityLivingBase) hitEntity, EnumHand.MAIN_HAND );
                    if( placed ) turtlePlayer.loadInventory( stackCopy );
                }
            }
        }

        // Stop claiming drops
        ComputerCraft.clearEntityDropConsumer( hitEntity );

        // Put everything we collected into the turtles inventory, then return
        ItemStack remainder = turtlePlayer.unloadInventory( turtle );
        if( !placed && ItemStack.areItemStacksEqual( stack, remainder ) )
        {
            return stack;
        }
        else if( !remainder.isEmpty() )
        {
            return remainder;
        }
        else
        {
            return ItemStack.EMPTY;
        }
    }

}
