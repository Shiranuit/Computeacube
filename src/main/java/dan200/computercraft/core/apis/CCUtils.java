package dan200.computercraft.core.apis;

import java.util.HashMap;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.shared.computer.blocks.TileCommandComputer;
import dan200.computercraft.shared.computer.core.ServerComputer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants.NBT;

public class CCUtils {
	public static final String[] s_sideNames = { "bottom", "top", "back", "front", "right", "left" };
	public static boolean isCommandComputer(IComputerAccess computer) {
		try {
			ServerComputer thiscomputer = ComputerCraft.serverComputerRegistry.lookup(computer.getID());
			if (thiscomputer != null) {
				TileEntity computerTile = thiscomputer.getWorld().getTileEntity(thiscomputer.getPosition());
				if (computerTile != null) {
					if (computerTile instanceof TileCommandComputer) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			
		}
		return false;
	}
	
	public static ServerComputer getComputer(int id) {
		return ComputerCraft.serverComputerRegistry.lookup(id);
	}
	
	  public static int parseSide(Object[] args) throws LuaException
	  {
		  if ((args.length < 1) || (args[0] == null) || (!(args[0] instanceof String))) {
			  throw new LuaException("Expected string");
		  }
		  String side = (String)args[0];
		  for (int n = 0; n < s_sideNames.length; n++) {
		      if (side.equals(s_sideNames[n])) {
		        return n;
		      }
		  }
		  throw new LuaException("Invalid side.");
	  }
	  
	  
		public static HashMap<Object, Object> GetTags(NBTTagCompound Ctag) {
			HashMap<Object, Object> infos = new HashMap<Object, Object>();
			
			if (Ctag != null) {
				for (Object key : Ctag.getKeySet()) {
					String currentTag = (String)key;
					if (Ctag.hasKey(currentTag, NBT.TAG_END)) {
						//Rien a mettre ici
					}
					else if (Ctag.hasKey(currentTag, NBT.TAG_BYTE)) {
						infos.put(currentTag, ((Byte)Ctag.getByte(currentTag)).intValue());
					}
					else if (Ctag.hasKey(currentTag, NBT.TAG_SHORT)) {
						infos.put(currentTag, ((Short)Ctag.getShort(currentTag)).intValue());
					}
					else if (Ctag.hasKey(currentTag, NBT.TAG_INT)) {
						infos.put(currentTag, ((Integer)Ctag.getInteger(currentTag)).intValue());
					}
					else if (Ctag.hasKey(currentTag, NBT.TAG_LONG)) {
						infos.put(currentTag, ((Long)Ctag.getLong(currentTag)).intValue());
					}
					else if (Ctag.hasKey(currentTag, NBT.TAG_FLOAT)) {
						infos.put(currentTag, ((Float)Ctag.getFloat(currentTag)).intValue());
					}
					else if (Ctag.hasKey(currentTag, NBT.TAG_DOUBLE)) {
						infos.put(currentTag, ((Double)Ctag.getDouble(currentTag)).intValue());
					}
					else if (Ctag.hasKey(currentTag, NBT.TAG_STRING)) {
						infos.put(currentTag, Ctag.getString(currentTag).toString());
					}
					else if (Ctag.hasKey(currentTag, NBT.TAG_LIST)) {
						HashMap<Object, Object> newInfos = new HashMap<Object, Object>();
						NBTTagList lst = Ctag.getTagList(currentTag, NBT.TAG_COMPOUND);
						for (int i=0; i < lst.tagCount(); i++) {
							newInfos.put(i+1, GetTags(lst.getCompoundTagAt(i)));
						}
						infos.put(currentTag, newInfos);
					}
					else if (Ctag.hasKey(currentTag, NBT.TAG_COMPOUND)) {
						infos.put(currentTag, GetTags(Ctag.getCompoundTag(currentTag)));
					}
				}
			}
			return infos;
		}
	  
	  public static EntityPlayer getPlayer(String name){
		    PlayerList pList = ComputerCraft.instance.server.getPlayerList();
		    return pList.getPlayerByUsername(name);
		}
	  
	  public static int parseColour(Object[] args, boolean _enableColours) throws LuaException
		{
			if ((args.length != 1) || (args[0] == null) || (!(args[0] instanceof Double))) {
				throw new LuaException("Expected number");
			}
			int colour = (int)((Double)args[0]).doubleValue();
			if (colour <= 0) {
				throw new LuaException("Colour out of range");
			}
			colour = getHighestBit(colour) - 1;
			if ((colour < 0) || (colour > 15)) {
				throw new LuaException("Colour out of range");
			}
			if ((!_enableColours) && (colour != 0) && (colour != 15) && (colour != 7) && (colour != 8)) {
				throw new LuaException("Colour not supported");
			}
			return colour;
		}
	  
	  private static int getHighestBit(int group)
	  {
	    int bit = 0;
	    while (group > 0)
	    {
	      group >>= 1;
	      bit++;
	    }
	    return bit;
	  }
	  
	  
}
