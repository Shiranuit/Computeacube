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
import dan200.computercraft.shared.util.IEntityDropConsumer;
import dan200.computercraft.shared.util.InventoryUtil;
import dan200.computercraft.shared.util.WorldUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

public class TurtleSwingCommand implements ITurtleCommand
{
    private final InteractDirection m_direction;
    private final Object[] m_extraArguments;

    public TurtleSwingCommand( InteractDirection direction, Object[] arguments )
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
    
    protected static float getDamageMultiplier()
    {
        return 3.0f;
    }
    
    private  static boolean attack( final ITurtleAccess turtle, TurtlePlayer turtlePlayer, EnumFacing direction, ItemStack stack )
    {
        // Create a fake player, and orient it appropriately
        final World world = turtle.getWorld();
        final BlockPos position = turtle.getPosition();

        // See if there is an entity present
        Vec3d turtlePos = new Vec3d( turtlePlayer.posX, turtlePlayer.posY, turtlePlayer.posZ );
        Vec3d rayDir = turtlePlayer.getLook( 1.0f );
        Pair<Entity, Vec3d> hit = WorldUtil.rayTraceEntities( world, turtlePos, rayDir, 1.5 );
        if( hit != null )
        {
            // Load up the turtle's inventory
            ItemStack stackCopy = stack.copy();
            turtlePlayer.loadInventory( stackCopy );

            // Start claiming entity drops
            
            Entity hitEntity = hit.getKey();
            ComputerCraft.setEntityDropConsumer( hitEntity, new IEntityDropConsumer()
            {
                @Override
                public void consumeDrop( Entity entity, @Nonnull ItemStack drop )
                {
                    ItemStack remainder = InventoryUtil.storeItems( drop, turtle.getItemHandler(), turtle.getSelectedSlot() );
                    if( !remainder.isEmpty() )
                    {
                        WorldUtil.dropItemStack( remainder, world, position, turtle.getDirection().getOpposite() );
                    }
                }
            } );

            // Attack the entity
            boolean attacked = false;
            if( hitEntity.canBeAttackedWithItem() && !hitEntity.hitByEntity( turtlePlayer )
                && !MinecraftForge.EVENT_BUS.post( new AttackEntityEvent( turtlePlayer, hitEntity ) ) )
            {
                float damage = (float)turtlePlayer.getEntityAttribute( SharedMonsterAttributes.ATTACK_DAMAGE ).getAttributeValue();
                for (AttributeModifier modifier : stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).get(SharedMonsterAttributes.ATTACK_DAMAGE.getName()))
                {
                	damage += modifier.getAmount();
                }
                
                if( damage > 0.0f )
                {
                    DamageSource source = DamageSource.causePlayerDamage( turtlePlayer );
               
                    if( hitEntity instanceof EntityArmorStand )
                    {
                        // Special case for armor stands: attack twice to guarantee destroy
                        hitEntity.attackEntityFrom( source, damage );
                        if( !hitEntity.isDead )
                        {
                            hitEntity.attackEntityFrom( source, damage );
                        }
                        attacked = true;
                    }
                    else
                    {
                        if( hitEntity.attackEntityFrom( source, damage ) )
                        {
                            attacked = true;
                        }
                    }
                }
            }

            // Stop claiming drops
            ComputerCraft.clearEntityDropConsumer( hitEntity );

            // Put everything we collected into the turtles inventory, then return
            if( attacked )
            {
            	stack.hitEntity((EntityLivingBase)hitEntity, (EntityPlayer)turtlePlayer);
            	
            	turtlePlayer.loadInventory(stack);
             //   turtlePlayer.unloadInventory( turtle );
                return true;
            }
        }

        return false;
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

        boolean hasAttacked = attack(turtle, turtlePlayer, direction, stack);
        if (hasAttacked) {
        	result = false;
        } else {
            turtlePlayer.loadInventory(stack.copy());
        	if (stack != null && stack.getItem() instanceof ItemTool) {

                try {
                	// Good Tool
                	if (ForgeHooks.isToolEffective(turtlePlayer.world, newPosition, stack) || stack.canHarvestBlock(iblock)) {
                		// Not air, water, lava
                		if (block != Blocks.AIR &&  block != Blocks.WATER && block != Blocks.LAVA) {
                			// can be harvest
                		if (ForgeHooks.canHarvestBlock(block, turtlePlayer, turtlePlayer.world, newPosition) || stack.canHarvestBlock(iblock)) {
                			//Doesn't break instant
                			if (iblock.getBlockHardness(turtlePlayer.world, newPosition) > 0) {
        		                List<ItemStack> items = getBlockDropped( turtlePlayer.world, newPosition, (EntityPlayer)turtlePlayer, stack );
        		                if( items != null && items.size() > 0 )
        		                {
        		                    for( ItemStack item : items )
        		                    {
        		                        ItemStack remainder2 = InventoryUtil.storeItems( item, turtle.getItemHandler(), turtle.getSelectedSlot() );
        		                        if( !remainder2.isEmpty() )
        		                        {
        		                            // If there's no room for the items, drop them
        		                            WorldUtil.dropItemStack( remainder2, turtlePlayer.world, position, direction );
        		                        }
        		                    }
        		                }
        		                
            					boolean destroyed = stack.attemptDamageItem(1, new Random(), turtlePlayer);
            					if (destroyed) {
            						stack = ItemStack.EMPTY;
            					}
                  				 turtlePlayer.world.playEvent(2001, newPosition, Block.getStateId(iblock));
                   				 turtlePlayer.world.setBlockToAir(newPosition);
                   				 turtlePlayer.loadInventory(stack);
                   				 result = true;
                   				 //Break Instant
                				} else if (iblock.getBlockHardness(turtlePlayer.world, newPosition) == 0) {
            		                List<ItemStack> items = getBlockDropped( turtlePlayer.world, newPosition, (EntityPlayer)turtlePlayer, stack );
            		                if( items != null && items.size() > 0 )
            		                {
            		                    for( ItemStack item : items )
            		                    {
            		                        ItemStack remainder2 = InventoryUtil.storeItems( item, turtle.getItemHandler(), turtle.getSelectedSlot() );
            		                        if( !remainder2.isEmpty() )
            		                        {
            		                            // If there's no room for the items, drop them
            		                            WorldUtil.dropItemStack( remainder2, turtlePlayer.world, position, direction );
            		                        }
            		                    }
            		                }
                      				 turtlePlayer.world.playEvent(2001, newPosition, Block.getStateId(iblock));
                       				 turtlePlayer.world.setBlockToAir(newPosition);
                					//Bedrock
                				} else {
                					result = false;
                				}
                			// Cannot be harvest
                			} else {
                				// Not Bedrock
                				if (iblock.getBlockHardness(turtlePlayer.world, newPosition) > 0) {
                					boolean destroyed = stack.attemptDamageItem(1, new Random(), turtlePlayer);
                					if (destroyed) {
                						stack = ItemStack.EMPTY;
                					}
                      				 turtlePlayer.world.playEvent(2001, newPosition, Block.getStateId(iblock));
                       				 turtlePlayer.world.setBlockToAir(newPosition);
                       				 turtlePlayer.loadInventory(stack);
                       				result = true;
                				} else if (iblock.getBlockHardness(turtlePlayer.world, newPosition) == 0) {
                					// instant break
                      				 turtlePlayer.world.playEvent(2001, newPosition, Block.getStateId(iblock));
                       				 turtlePlayer.world.setBlockToAir(newPosition);
                       				 result = true;
                				} else {
                					// Bedrock
                					result = false;
                				}
                			}
                		// is Air, Water, Lava
                		} else {
                			result = false;
                		}
                	} else {
                		if (block != Blocks.AIR &&  block != Blocks.WATER && block != Blocks.LAVA) {

                				if (iblock.getBlockHardness(turtlePlayer.world, newPosition) > 0) {
        			                if (asEnchant(Enchantments.SILK_TOUCH, stack)) {
        	    		                List<ItemStack> items = getBlockDropped( turtlePlayer.world, newPosition, (EntityPlayer)turtlePlayer, stack );
        	    		                if( items != null && items.size() > 0 )
        	    		                {
        	    		                    for( ItemStack item : items )
        	    		                    {
        	    		                        ItemStack remainder2 = InventoryUtil.storeItems( item, turtle.getItemHandler(), turtle.getSelectedSlot() );
        	    		                        if( !remainder2.isEmpty() )
        	    		                        {
        	    		                            // If there's no room for the items, drop them
        	    		                            WorldUtil.dropItemStack( remainder2, turtlePlayer.world, position, direction );
        	    		                        }
        	    		                    }
        	    		                }
        			                }
        	    					boolean destroyed = stack.attemptDamageItem(1, new Random(), turtlePlayer);
        	    					if (destroyed) {
        	    						stack = ItemStack.EMPTY;
        	    					}
        			                
        	          				 turtlePlayer.world.playEvent(2001, newPosition, Block.getStateId(iblock));
        	           				 turtlePlayer.world.setBlockToAir(newPosition);
        	           				 turtlePlayer.loadInventory(stack);
        	           				result = true;
                				} else if (iblock.getBlockHardness(turtlePlayer.world, newPosition) == 0) {
        			                if (asEnchant(Enchantments.SILK_TOUCH, stack)) {
        	    		                List<ItemStack> items = getBlockDropped( turtlePlayer.world, newPosition, (EntityPlayer)turtlePlayer, stack );
        	    		                if( items != null && items.size() > 0 )
        	    		                {
        	    		                    for( ItemStack item : items )
        	    		                    {
        	    		                        ItemStack remainder2 = InventoryUtil.storeItems( item, turtle.getItemHandler(), turtle.getSelectedSlot() );
        	    		                        if( !remainder2.isEmpty() )
        	    		                        {
        	    		                            // If there's no room for the items, drop them
        	    		                            WorldUtil.dropItemStack( remainder2, turtlePlayer.world, position, direction );
        	    		                        }
        	    		                    }
        	    		                }
        			                }
        	          				 turtlePlayer.world.playEvent(2001, newPosition, Block.getStateId(iblock));
        	           				 turtlePlayer.world.setBlockToAir(newPosition);
        	           				 result = true;
                				} else {
                					result = false;
                				}

                		} else {
                			result = false;
                		}
                	}


                } catch (Exception e) {
                		result = false;
                }
                } else {
                	result = false;
                }
        }
        
        


        // If nothing worked, return the original stack unchanged
    	ItemStack t = turtlePlayer.unloadInventory(turtle);
    	return t;
    }
    
    public static boolean asEnchant(Enchantment enchant, ItemStack item) {
    	if (item != null) {
    		NBTTagList lst = item.serializeNBT().getCompoundTag("tag").getTagList("ench", NBT.TAG_COMPOUND);
			for (int m = 0; m <  lst.tagCount() ;m++) {
				NBTTagCompound itemt = (NBTTagCompound) lst.getCompoundTagAt(m);
				if (Enchantment.getEnchantmentByID(itemt.getInteger("id")) == enchant) {
					return true;
				}
			}
    	}
    	return false;
    }
    
    private static List<ItemStack> getBlockDropped( World world, BlockPos pos, EntityPlayer player, ItemStack item )
    {
    	int fortune = 0;
    	boolean silkTouch = false;
    	if (item != null) {
    		NBTTagList lst = item.serializeNBT().getCompoundTag("tag").getTagList("ench", NBT.TAG_COMPOUND);
			for (int m = 0; m <  lst.tagCount() ;m++) {

				NBTTagCompound itemt = (NBTTagCompound) lst.getCompoundTagAt(m);
				if (Enchantment.getEnchantmentByID(itemt.getInteger("id")) == Enchantments.FORTUNE) {
					fortune = itemt.getInteger("lvl");
				}
				if (Enchantment.getEnchantmentByID(itemt.getInteger("id")) == Enchantments.SILK_TOUCH) {
					silkTouch = true;
				}
			}
    	}
        IBlockState state = world.getBlockState( pos );
        Block block = state.getBlock();
        List<ItemStack> drops = new ArrayList<ItemStack>();
        if (silkTouch) {
        	
        	drops.add(new ItemStack(block,1,block.getMetaFromState(state)));
        } else {
        	drops = block.getDrops( world, pos, world.getBlockState( pos ), fortune );
        }
        
        double chance = ForgeEventFactory.fireBlockHarvesting( drops, world, pos, state, fortune, 1, silkTouch, player );

        for( int i = drops.size() - 1; i >= 0; i-- )
        {
            if( world.rand.nextFloat() > chance )
            {
                drops.remove( i );
            }
        }
        return drops;
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
        ComputerCraft.setEntityDropConsumer( hitEntity, new IEntityDropConsumer()
        {
            @Override
            public void consumeDrop( Entity entity, @Nonnull ItemStack drop )
            {
                ItemStack remainder = InventoryUtil.storeItems( drop, turtle.getItemHandler(), turtle.getSelectedSlot() );
                if( !remainder.isEmpty() )
                {
                    WorldUtil.dropItemStack( remainder, world, position, turtle.getDirection().getOpposite() );
                }
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
