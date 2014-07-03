package lycanite.lycanitesmobs.saltwatermobs.entity;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupAnimal;
import lycanite.lycanitesmobs.api.IGroupPredator;
import lycanite.lycanitesmobs.api.entity.EntityCreatureAgeable;
import lycanite.lycanitesmobs.api.entity.ai.*;
import lycanite.lycanitesmobs.api.info.DropRate;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import lycanite.lycanitesmobs.saltwatermobs.SaltwaterMobs;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntityIka extends EntityCreatureAgeable implements IAnimals, IGroupAnimal {

	EntityAIWander wanderAI = new EntityAIWander(this);

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityIka(World world) {
        super(world);
        
        // Setup:
        this.mod = SaltwaterMobs.instance;
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 0;
        this.experience = 7;
        this.spawnsInDarkness = true;
        this.spawnsOnLand = false;
        this.spawnsInWater = true;
        this.hasAttackSound = true;
        
        this.eggName = "SaltwaterEgg";
        this.babySpawnChance = 0.1D;
        this.canGrow = true;
        
        this.setWidth = 1.5F;
        this.setHeight = 1.6F;
        this.setupMob();
        
        // AI Tasks:
        this.getNavigator().setCanSwim(true);
        this.getNavigator().setAvoidsWater(false);
        this.tasks.addTask(0, new EntityAISwimming(this).setSink(true));
        this.tasks.addTask(1, new EntityAIStayByWater(this));
        this.tasks.addTask(2, new EntityAIAvoid(this).setNearSpeed(1.3D).setFarSpeed(1.2D).setNearDistance(5.0D).setFarDistance(20.0D));
        this.tasks.addTask(3, new EntityAIMate(this));
        this.tasks.addTask(4, new EntityAITempt(this).setItemList("Vegetables"));
        this.tasks.addTask(5, new EntityAIFollowParent(this).setSpeed(1.0D));
        this.tasks.addTask(6, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(2, new EntityAITargetParent(this).setSightCheck(false).setDistance(32.0D));
        this.targetTasks.addTask(3, new EntityAITargetAvoid(this).setTargetClass(IGroupPredator.class));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 10D);
		baseAttributes.put("movementSpeed", 0.24D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 32D);
		baseAttributes.put("attackDamage", 2D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("IkaMeatRaw")), 1).setBurningDrop(new ItemStack(ObjectManager.getItem("IkaMeatCooked"))).setMinAmount(2).setMaxAmount(5));
        this.drops.add(new DropRate(new ItemStack(Items.dye, 1, 0), 0.25F).setMinAmount(1).setMaxAmount(1));
    }
    
    
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Wander Pause Rates:
		if(this.isInWater())
			this.wanderAI.setPauseRate(20);
		else
			this.wanderAI.setPauseRate(0);
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
	// ========== Movement Speed Modifier ==========
    @Override
    public float getSpeedMod() {
        float waterSpeed = 1.0F;
        if(this.isInWater()) // Checks specifically just for water.
            waterSpeed = 8.0F;
        else if(this.waterContact()) // Checks for water, rain, etc.
            waterSpeed = 1.5F;

    	if(this.getHealth() > (this.getMaxHealth() / 2)) // Slower with shell.
    		return waterSpeed * 0.25F;
    	return waterSpeed;
    }
	
    // Pathing Weight:
	@Override
	public float getBlockPathWeight(int par1, int par2, int par3) {
		int waterWeight = 10;
		
        if(this.worldObj.getBlock(par1, par2, par3) == Blocks.water)
        	return (super.getBlockPathWeight(par1, par2, par3) + 1) * (waterWeight + 1);
		if(this.worldObj.getBlock(par1, par2, par3) == Blocks.flowing_water)
			return (super.getBlockPathWeight(par1, par2, par3) + 1) * waterWeight;
        if(this.worldObj.isRaining() && this.worldObj.canBlockSeeTheSky(par1, par2, par3))
        	return (super.getBlockPathWeight(par1, par2, par3) + 1) * (waterWeight + 1);
        
        if(this.getAttackTarget() != null)
        	return super.getBlockPathWeight(par1, par2, par3);
        if(this.waterContact())
			return -999999.0F;
		
		return super.getBlockPathWeight(par1, par2, par3);
    }

    // Pushed By Water:
    @Override
    public boolean isPushedByWater() {
        return false;
    }

    // ========== Can leash ==========
    @Override
    public boolean canLeash(EntityPlayer player) { return true; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
    	if(ObjectManager.getPotionEffect("Weight") != null)
        	if(potionEffect.getPotionID() == ObjectManager.getPotionEffect("Weight").id) return false;
        if(potionEffect.getPotionID() == Potion.blindness.id) return false;
        super.isPotionApplicable(potionEffect);
        return true;
    }
    
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    @Override
    public boolean canBreatheAboveWater() {
        return false;
    }
    
    
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== Damage Modifier ==========
    /** A multiplier that alters how much damage this mob receives from the given DamageSource, use for resistances and weaknesses. Note: The defense multiplier is handled before this. **/
    public float getDamageModifier(DamageSource damageSrc) {
    	if(this.getHealth() > (this.getMaxHealth() / 2)) // Stronger with shell.
    		return 0.25F;
    	return 1.0F;
    }


    // ==================================================
    //                     Breeding
    // ==================================================
    // ========== Create Child ==========
    @Override
    public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
        return new EntityIka(this.worldObj);
    }

    // ========== Breeding Item ==========
    @Override
    public boolean isBreedingItem(ItemStack testStack) {
        return ObjectLists.inItemList("Vegetables", testStack);
    }
}
