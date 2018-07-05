package dan200.computercraft.shared.turtle.core;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleCommand;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.shared.util.InventoryUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.items.IItemHandler;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

public class TurtleInspectInventory implements ITurtleCommand {
    private final InteractDirection m_direction;
    private final int m_slot;
    public TurtleInspectInventory( InteractDirection direction, int slot )
    {
        m_direction = direction;
        m_slot = slot;
    }
 

    @Nonnull
    @Override
    public TurtleCommandResult execute( @Nonnull ITurtleAccess turtle )
    {

        // Get world direction from direction
        EnumFacing direction = m_direction.toWorldDir( turtle );



        // Get inventory for thing in front
        World world = turtle.getWorld();
        BlockPos oldPosition = turtle.getPosition();
        BlockPos newPosition = oldPosition.offset( direction );
        EnumFacing side = direction.getOpposite();

        IItemHandler inventory = InventoryUtil.getInventory( world, newPosition, side );
        if( inventory != null )
        {

        	if (m_slot >= 1 && m_slot <= inventory.getSlots()) {
        		ItemStack stack = inventory.getStackInSlot(m_slot-1);
        		if (!stack.isEmpty()) {
  
                        Item item = stack.getItem();
                        String name = Item.REGISTRY.getNameForObject( item ).toString();
                        int damage = stack.getItemDamage();
                        int count = stack.getCount();

                        Map<Object, Object> table = new HashMap<Object, Object>();
                        table.put( "name", name );
                        table.put( "damage", damage );
                        table.put( "count", count );

                        HashMap<Object, Object> EnchantsList = new HashMap<Object, Object>();
                        HashMap<Object, Object> ShulkerItems = new HashMap<Object, Object>();
                        table.put("displayName", stack.getDisplayName());
                        table.put("lifeDuration", stack.getMaxDamage() - stack.getItemDamage());
                        table.put("lifeMaxDuration", stack.getMaxDamage());
                        table.put("maxStackSize", stack.getMaxStackSize());
                        table.put("hasDisplayName", stack.hasDisplayName());
                        table.put("metadata", stack.getMetadata());
            			table.put("repairCost", stack.getRepairCost());
            			table.put("isItemDamaged", stack.isItemDamaged());
            			table.put("isItemEnchantable", stack.isItemEnchantable());
            			table.put("isEnchanted", stack.isItemEnchanted());
            			table.put("isStackable", stack.isStackable());
            			
            			if (stack.getItem() instanceof ItemShulkerBox) {
            				NBTTagList lst = stack.serializeNBT().getCompoundTag("tag").getCompoundTag("BlockEntityTag").getTagList("Items", NBT.TAG_COMPOUND);
            				for (int m = 0; m <  lst.tagCount() ;m++) {
            					HashMap<Object, Object> itemdt = new HashMap<Object, Object>();
            					NBTTagCompound nitem = (NBTTagCompound) lst.getCompoundTagAt(m);
            					ItemStack stk = new ItemStack(nitem);
            					
            					itemdt.put( "name", Item.REGISTRY.getNameForObject( stk.getItem() ).toString() );
                                itemdt.put( "damage", stk.getItemDamage() );
                                itemdt.put( "count", stk.getCount() );
            				
            					itemdt.put("displayName", stk.getDisplayName());
            					itemdt.put("lifeDuration", stk.getMaxDamage() - stack.getItemDamage());
            					itemdt.put("lifeMaxDuration", stk.getMaxDamage());
            					itemdt.put("maxStackSize", stk.getMaxStackSize());
            					itemdt.put("hasDisplayName", stk.hasDisplayName());
            					itemdt.put("metadata", stk.getMetadata());
            					itemdt.put("repairCost", stk.getRepairCost());
            					itemdt.put("isItemDamaged", stk.isItemDamaged());
            					itemdt.put("isItemEnchantable", stk.isItemEnchantable());
            					itemdt.put("isEnchanted", stk.isItemEnchanted());
            					itemdt.put("isStackable", stk.isStackable());
                    			
            					ShulkerItems.put(m+1, itemdt);
            				}
            			}
            			
            			NBTTagList lst = stack.serializeNBT().getCompoundTag("tag").getTagList("ench", NBT.TAG_COMPOUND);
            			for (int m = 0; m <  lst.tagCount() ;m++) {
            				HashMap<Object, Object> Enchant = new HashMap<Object, Object>();
            				NBTTagCompound nitem = (NBTTagCompound) lst.getCompoundTagAt(m);
            				Enchant.put("enchantName", Enchantment.getEnchantmentByID(nitem.getInteger("id")).getName().replace("enchantment.", ""));
            				Enchant.put("enchantLvl",  nitem.getInteger("lvl"));
            				EnchantsList.put(m+1, Enchant);
            			}
            			table.put("Enchants", EnchantsList);
            			table.put("ShulkerContainer", ShulkerItems);
            			
                        return TurtleCommandResult.success(new Object[] { table });
                    }
                    else
                    {
                        return TurtleCommandResult.success(new Object[] { null });
                    }      		
        	} else {
        		return TurtleCommandResult.failure("slot number must be beween 1 and "+inventory.getSlots());
        	}
        }
        return TurtleCommandResult.failure("Inventory not found");
    }
}

