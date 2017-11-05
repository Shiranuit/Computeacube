package dan200.computercraft.shared.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants.NBT;
@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
public class EntityUtil {
	
	public static HashMap getPlayerInfo(Entity ent) {
		if (ent != null)  {
		if (ent instanceof EntityPlayer) {
			EntityPlayer e = (EntityPlayer)ent;
	  		
	  		HashMap EntityInfos = new HashMap();
	  		HashMap EntityInventory = new HashMap();
	  		HashMap EntityEffect = new HashMap();
	  		HashMap EntityOpenContainerInventory = new HashMap();

	  		String TypeEntity = e.getClass().getSimpleName();
			//Informations diverses
	    	EntityInfos.put("name", e.getDisplayNameString());
	    	EntityInfos.put("type", TypeEntity.toString());
	    	EntityInfos.put("x", Double.toString(e.posX));
	    	EntityInfos.put("y", Double.toString(e.posY));
	    	EntityInfos.put("z", Double.toString(e.posZ));
	    	EntityInfos.put("onFire", e.isBurning());
	    	EntityInfos.put("fireImmunity", e.isImmuneToFire());
	    	EntityInfos.put("isInvisible", e.isInvisible());
	    	EntityInfos.put("isInWater", e.isInWater());
	    	EntityInfos.put("canBePushedByWater", e.isPushedByWater());
	    	EntityInfos.put("isRiding", e.isRiding());
	    	EntityInfos.put("isSneaking", e.isSneaking());
	    	EntityInfos.put("isSprinting", e.isSprinting());
	    	EntityInfos.put("isWet", e.isWet());
	    	EntityInfos.put("pitch", e.rotationPitch);
	    	EntityInfos.put("yaw", e.rotationYaw);
	    	EntityInfos.put("headYaw", e.getRotationYawHead());
	    	EntityInfos.put("UUID", e.getUniqueID().toString());
	    	EntityInfos.put("lookX", e.getLookVec().x);
	    	EntityInfos.put("lookY", e.getLookVec().y);
	    	EntityInfos.put("lookZ", e.getLookVec().z);
	    	EntityInfos.put("canBePushed", e.canBePushed());
	    	EntityInfos.put("isAlive", e.isEntityAlive());
	    	EntityInfos.put("portalCooldown", e.getPortalCooldown());
	    	EntityInfos.put("maxInPortalTime", e.getMaxInPortalTime());
	    	EntityInfos.put("chunkCoordX", e.chunkCoordX);
	    	EntityInfos.put("chunkCoordY", e.chunkCoordY);
	    	EntityInfos.put("chunkCoordZ", e.chunkCoordZ);
	    	EntityInfos.put("motionX", e.motionX);
	    	EntityInfos.put("motionY", e.motionY);
	    	EntityInfos.put("motionZ", e.motionZ);
	    	EntityInfos.put("onGround", e.onGround);
	    	EntityInfos.put("isPlayerSleeping", e.isPlayerSleeping());
	    	EntityInfos.put("isSilent", e.isSilent());
	    	EntityInfos.put("isInLava", e.isInLava());
	    	EntityInfos.put("dimension", e.dimension);
	    	EntityInfos.put("experience", e.experience);
	    	EntityInfos.put("experienceLevel", e.experienceLevel);
	    	EntityInfos.put("experienceTotal", e.experienceTotal);
	    	EntityInfos.put("xpCooldown", e.xpCooldown);
	    	EntityInfos.put("luck", e.getLuck());
	    	EntityInfos.put("isGlowing", e.isGlowing());
	    	EntityInfos.put("isBurning", e.isBurning());
	    	EntityInfos.put("isImmuneToExplosions", e.isImmuneToExplosions());
	    	EntityInfos.put("isImmuneToFire", e.isImmuneToFire());
	    	EntityInfos.put("isCollided", e.isCollided);
	    	EntityInfos.put("isCollidedHorizontally", e.isCollidedHorizontally);
	    	EntityInfos.put("isCollidedVertically", e.isCollidedVertically);
	    	EntityInfos.put("isCreative", e.isCreative());
	    	EntityInfos.put("isElytraFlying", e.isElytraFlying());
	    	EntityInfos.put("isEntityInsideOpaqueBlock", e.isEntityInsideOpaqueBlock());
	    	EntityInfos.put("isOnLadder", e.isOnLadder());
	    	EntityInfos.put("isOutsideBorder", e.isOutsideBorder());
	    	EntityInfos.put("isOverWater", e.isOverWater());
	    	EntityInfos.put("isPlayerFullyAsleep", e.isPlayerFullyAsleep());
	    	EntityInfos.put("isRiding", e.isRiding());
	    	EntityInfos.put("isSpectator", e.isSpectator());
	    	EntityInfos.put("isSprinting", e.isSprinting());
	    	EntityInfos.put("getArmorVisibility", e.getArmorVisibility());
	    	
	    	
	    	EntityInfos.put("health",e.getHealth());
	    	EntityInfos.put("foodLevel",e.getFoodStats().getFoodLevel());
	    	EntityInfos.put("totalArmor",e.getTotalArmorValue());
	    	EntityInfos.put("canBreathUnderwater",e.canBreatheUnderwater());
	    	EntityInfos.put("absorptionAmount",e.getAbsorptionAmount());
	    			
	    			
	    	//Effets Potions
	    	Collection<PotionEffect> potionEffects =  e.getActivePotionEffects();
	    	java.util.Iterator<PotionEffect> Iterator = potionEffects.iterator();
	    	if (potionEffects != null) {
	    		for(int j = 0;j < potionEffects.size();j++){
	    			HashMap EffectsInfos = new HashMap();
	    			if (Iterator.hasNext()) {
	    				PotionEffect Effet = Iterator.next();
	    				if (Effet != null) {
	    					EffectsInfos.put("name",Effet.getEffectName().replace("potion.", ""));
	    					EffectsInfos.put("duration",Effet.getDuration()/20);
	    					EffectsInfos.put("isAmbiant",Effet.getIsAmbient());
	    					EffectsInfos.put("id",Effet.getPotion().getName());
	    					EffectsInfos.put("amplifier",Effet.getAmplifier()+1);
	    					EntityEffect.put(j+1,EffectsInfos);
	    				}
	    			}
	    		}
	    	}
	    	
	    			
	    	// OpenContainer
	    	for(int j = 0;j < e.openContainer.inventoryItemStacks.size()-36 ;j++){
	    		ItemStack InventoryInfos = (ItemStack) e.openContainer.inventoryItemStacks.get(j);
	    		if (InventoryInfos != null){
	    			HashMap StackInfos = getInfo(InventoryInfos,j+1);
		    		EntityOpenContainerInventory.put(j+1,StackInfos);
	    		}
	    	}
	    	EntityInfos.put("openContainer",EntityOpenContainerInventory);
					    			
	    	//Inventaire
	    	for(int j = 0;j < e.inventory.getSizeInventory() ;j++){
	    		ItemStack InventoryInfos = (ItemStack) e.inventory.getStackInSlot(j);
	    		if (InventoryInfos != null){
	    			HashMap StackInfos = getInfo(InventoryInfos,j+1);
					EntityInventory.put(j+1,StackInfos);
	    		}
	    	}
			EntityInfos.put("effects",EntityEffect);
			EntityInfos.put("inventory",EntityInventory);
		return EntityInfos;
		}
		return null;
	}
		return null;
	}
	
	public static HashMap bpos(BlockPos pos) {
		HashMap npos = new HashMap();
		npos.put("x", pos.getX());
		npos.put("y", pos.getY());
		npos.put("z", pos.getZ());
		return npos;
	}
	
	public static HashMap GetTags(NBTTagCompound Ctag) {
		HashMap infos = new HashMap();
		if (Ctag != null) {
			Iterator iterator = Ctag.getKeySet().iterator();
			while (iterator.hasNext()) {
				String currentTag = (String)iterator.next();
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
					HashMap newInfos = new HashMap();
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
	
	
	public static HashMap getInfo(ItemStack itemstack, int slot){
		HashMap StackInfos = new HashMap();
		HashMap EnchantsList = new HashMap();
		if (itemstack != null){
			StackInfos.put("slot", slot);
			StackInfos.put("stackSize", itemstack.getCount());
			StackInfos.put("displayName", itemstack.getDisplayName());
			StackInfos.put("name", itemstack.getItem().getRegistryName());
			StackInfos.put("lifeDuration", itemstack.getMaxDamage() - itemstack.getItemDamage());
			StackInfos.put("lifeMaxDuration", itemstack.getMaxDamage());
			StackInfos.put("maxStackSize", itemstack.getMaxStackSize());
			StackInfos.put("hasDisplayName", itemstack.hasDisplayName());
			StackInfos.put("metadata", itemstack.getMetadata());
			StackInfos.put("repairCost", itemstack.getRepairCost());
			StackInfos.put("isItemDamaged", itemstack.isItemDamaged());
			StackInfos.put("isItemEnchantable", itemstack.isItemEnchantable());
			StackInfos.put("isEnchanted", itemstack.isItemEnchanted());
			StackInfos.put("isStackable", itemstack.isStackable());
			if (itemstack.serializeNBT() != null) {
				StackInfos.put("datatags", GetTags(itemstack.serializeNBT()));
			}
			//Enchantements
			NBTTagList lst = itemstack.serializeNBT().getCompoundTag("tag").getTagList("ench", NBT.TAG_COMPOUND);
			for (int m = 0; m <  lst.tagCount() ;m++) {
				HashMap Enchant = new HashMap();
				NBTTagCompound item = (NBTTagCompound) lst.getCompoundTagAt(m);
				Enchant.put("enchantName", Enchantment.getEnchantmentByID(item.getInteger("id")).getName().replace("enchantment.", ""));
				Enchant.put("enchantLvl",  item.getInteger("lvl"));
				EnchantsList.put(m+1, Enchant);
			}
			StackInfos.put("Enchants", EnchantsList);
		}
		return StackInfos;
	}

	public static HashMap getInfo(ItemStack itemstack){
		HashMap StackInfos = new HashMap();
		HashMap EnchantsList = new HashMap();
		if (itemstack != null){
			StackInfos.put("stackSize", itemstack.getCount());
			StackInfos.put("displayName", itemstack.getDisplayName());
			StackInfos.put("name", itemstack.getItem().getRegistryName());
			StackInfos.put("lifeDuration", itemstack.getMaxDamage() - itemstack.getItemDamage());
			StackInfos.put("lifeMaxDuration", itemstack.getMaxDamage());
			StackInfos.put("maxStackSize", itemstack.getMaxStackSize());
			StackInfos.put("hasDisplayName", itemstack.hasDisplayName());
			StackInfos.put("metadata", itemstack.getMetadata());
			StackInfos.put("repairCost", itemstack.getRepairCost());
			StackInfos.put("isItemDamaged", itemstack.isItemDamaged());
			StackInfos.put("isItemEnchantable", itemstack.isItemEnchantable());
			StackInfos.put("isEnchanted", itemstack.isItemEnchanted());
			StackInfos.put("isStackable", itemstack.isStackable());
			if (itemstack.serializeNBT() != null) {
				StackInfos.put("datatags", GetTags(itemstack.serializeNBT()));
			}
			//Enchantements
			NBTTagList lst = itemstack.serializeNBT().getCompoundTag("tag").getTagList("ench", NBT.TAG_COMPOUND);
			for (int m = 0; m <  lst.tagCount() ;m++) {
				HashMap Enchant = new HashMap();
				NBTTagCompound item = (NBTTagCompound) lst.getCompoundTagAt(m);
				Enchant.put("enchantName", Enchantment.getEnchantmentByID(item.getInteger("id")).getName().replace("enchantment.", ""));
				Enchant.put("enchantLvl",  item.getInteger("lvl"));
				EnchantsList.put(m+1, Enchant);
			}
			StackInfos.put("Enchants", EnchantsList);
		}
		return StackInfos;
	}
}
