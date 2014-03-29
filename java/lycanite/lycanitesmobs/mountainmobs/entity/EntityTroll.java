package lycanite.lycanitesmobs.mountainmobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.DropRate;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureAgeable;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackRanged;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIBreakDoor;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.mountainmobs.MountainMobs;
import lycanite.lycanitesmobs.swampmobs.entity.EntityVenomShot;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityTroll extends EntityCreatureAgeable implements IMob {
	// ========== Unique Entity Variables ==========
	public boolean stoneForm = false;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityTroll(World par1World) {
        super(par1World);
        
        // Setup:
        this.entityName = "Troll";
        this.mod = MountainMobs.instance;
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 2;
        this.experience = 5;
        this.spawnsInDarkness = true;
        this.hasAttackSound = false;
        
        this.eggName = "MountainEgg";
        this.canGrow = false;
        this.babySpawnChance = 0.1D;

        this.setWidth = 1.5F;
        this.setHeight = 3.2F;
        this.setupMob();
    	
        // AI Tasks:
        this.getNavigator().setBreakDoors(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIBreakDoor(this));
        this.tasks.addTask(5, new EntityAIAttackRanged(this).setSpeed(0.5D).setRate(60).setRange(14.0F).setMinChaseDistance(5.0F).setChaseTime(-1));
        this.tasks.addTask(6, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 60D);
		baseAttributes.put("movementSpeed", 0.26D);
		baseAttributes.put("knockbackResistance", 1.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 6D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(Block.wood.blockID, 1).setMinAmount(2).setMaxAmount(6));
        this.drops.add(new DropRate(Item.bone.itemID, 1).setMinAmount(2).setMaxAmount(6));
        this.drops.add(new DropRate(Item.leather.itemID, 1).setMinAmount(2).setMaxAmount(6));
        this.drops.add(new DropRate(Item.coal.itemID, 1).setMinAmount(2).setMaxAmount(8));
	}
    
    
    // ==================================================
    //                       Name
    // ==================================================
    public String getTextureName() {
    	if(this.stoneForm)
    		return super.getTextureName() + "_Stone";
    	return super.getTextureName();
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Daylight Stone Form:
        if(!this.stoneForm) {
        	if(this.isDaytime() && this.worldObj.canBlockSeeTheSky((int)this.posX, (int)this.boundingBox.maxY, (int)this.posZ)) {
        		this.stoneForm = true;
        	}
        }
        else {
        	if(!this.isDaytime() || !this.worldObj.canBlockSeeTheSky((int)this.posX, (int)this.boundingBox.maxY, (int)this.posZ)) {
        		if(this.worldObj.isRemote) {
        			// PARTICLES!
        		}
        		this.stoneForm = false;
        	}
        }
        
        // Destroy Blocks:
 		if(!this.worldObj.isRemote)
 	        if(this.getAttackTarget() != null && this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
 		    	float distance = this.getAttackTarget().getDistanceToEntity(this);
 		    		if(distance <= this.width + 4.0F)
 		    			destroyArea((int)this.posX, (int)this.posY, (int)this.posZ, 4, true);
 	        }
    }
    
    // ========== Destroy Blocks ==========
    public void destroyArea(int x, int y, int z, float strength, boolean drop) {
    	destroyArea(x, y, z, strength, drop, 0);
    }
    public void destroyArea(int x, int y, int z, float strength, boolean drop, int range) {
    	for(int w = -((int)Math.ceil(this.width) + range); w <= (Math.ceil(this.width) + range); w++)
        	for(int d = -((int)Math.ceil(this.width) + range); d <= (Math.ceil(this.width) + range); d++)
		    	for(int h = 0; h <= Math.ceil(this.height); h++) {
		    		Block block = Block.blocksList[this.worldObj.getBlockId(x + w, y + h, z + d)];
		    		if(block instanceof Block) {
			    		float hardness = block.blockHardness;
			    		Material material = block.blockMaterial;
			    		if(hardness >= 0 && strength >= hardness && (strength * 5) >= block.blockResistance && material != Material.water && material != Material.lava)
			    			this.worldObj.destroyBlock(x + w, y + h, z + d, drop);
		    		}
		    	}
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
    public float getSpeedMod() {
    	if(this.stoneForm) // Slower in stone form.
    		return 0.25F;
    	return 1.0F;
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
	// ========== Ranged Attack ==========
    @Override
    public void rangedAttack(Entity target, float range) {
    	// Type:
    	EntityVenomShot projectile = new EntityVenomShot(this.worldObj, this);
        projectile.setProjectileScale(2f);
    	
    	// Y Offset:
    	projectile.posY -= this.height / 4;
    	
    	// Accuracy:
    	float accuracy = 3.0F * (this.getRNG().nextFloat() - 0.5F);
    	
    	// Set Velocities:
        double d0 = target.posX - this.posX + accuracy;
        double d1 = target.posY + (double)target.getEyeHeight() - 1.100000023841858D - projectile.posY + accuracy;
        double d2 = target.posZ - this.posZ + accuracy;
        float f1 = MathHelper.sqrt_double(d0 * d0 + d2 * d2) * 0.2F;
        float velocity = 1.2F;
        projectile.setThrowableHeading(d0, d1 + (double)f1, d2, velocity, 6.0F);
        
        // Launch:
        this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.worldObj.spawnEntityInWorld(projectile);
        super.rangedAttack(target, range);
    }
    
    
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== Damage Modifier ==========
    /** A multiplier that alters how much damage this mob receives from the given DamageSource, use for resistances and weaknesses. Note: The defense multiplier is handled before this. **/
    public float getDamageModifier(DamageSource damageSrc) {
    	if(this.stoneForm) {
    		if(damageSrc.getEntity() != null) {
        		if(damageSrc.getEntity() instanceof EntityPlayer) {
        			EntityPlayer entityPlayer = (EntityPlayer)damageSrc.getEntity();
    	    		if(entityPlayer.getHeldItem() != null) {
    	    			if(entityPlayer.getHeldItem().getItem() instanceof ItemPickaxe)
    	    				return 2.0F;
    	    		}
        		}
        		else if(damageSrc.getEntity() instanceof EntityLiving) {
    	    		EntityLiving entityLiving = (EntityLiving)damageSrc.getEntity();
    	    		if(entityLiving.getHeldItem() != null) {
    	    			if(entityLiving.getHeldItem().getItem() instanceof ItemPickaxe)
    	    				return 2.0F;
    	    		}
        		}
        	}
    		return 0.25F;
    	}
    	
    	if(damageSrc.isFireDamage())
    		return 2.0F;
    	return 1.0F;
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect par1PotionEffect) {
        if(par1PotionEffect.getPotionID() == Potion.digSlowdown.id) return false;
        if(par1PotionEffect.getPotionID() == ObjectManager.getPotionEffect("Weight").id) return false;
        super.isPotionApplicable(par1PotionEffect);
        return true;
    }
    
    @Override
    public float getFallResistance() {
    	return 50;
    }
    
    @Override
    public boolean canBurn() { return !this.stoneForm; }
	
	
	// ==================================================
  	//                      Breeding
  	// ==================================================
    // ========== Create Child ==========
    @Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityTroll(this.worldObj);
	}
}