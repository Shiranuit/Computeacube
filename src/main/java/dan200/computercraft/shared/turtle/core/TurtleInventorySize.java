package dan200.computercraft.shared.turtle.core;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleCommand;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.shared.util.InventoryUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;


import javax.annotation.Nonnull;

public class TurtleInventorySize implements ITurtleCommand {
    private final InteractDirection m_direction;
    public TurtleInventorySize( InteractDirection direction )
    {
        m_direction = direction;
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
        	return TurtleCommandResult.success(new Object[] {inventory.getSlots()});
        }
        return TurtleCommandResult.failure("Inventory not found");
    }
}

