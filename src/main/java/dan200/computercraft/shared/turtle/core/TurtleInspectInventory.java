package dan200.computercraft.shared.turtle.core;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleCommand;
import dan200.computercraft.api.turtle.TurtleAnimation;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.shared.util.EntityUtil;
import dan200.computercraft.shared.util.InventoryUtil;
import dan200.computercraft.shared.util.WorldUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import java.util.HashMap;

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
        		ItemStack item = inventory.getStackInSlot(m_slot-1);
        		if (!item.isEmpty()) {
        			HashMap info = EntityUtil.getInfo(item);
        			return TurtleCommandResult.success(new Object[] {info});
        		} else {
        			return TurtleCommandResult.success();
        		}
        		
        		
        	} else {
        		return TurtleCommandResult.failure("slot number must be beween 1 and "+inventory.getSlots());
        	}
        }
        return TurtleCommandResult.failure("Inventory not found");
    }
}

