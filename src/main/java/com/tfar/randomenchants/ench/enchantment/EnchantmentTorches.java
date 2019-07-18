package com.tfar.randomenchants.ench.enchantment;

import com.tfar.randomenchants.RandomEnchants;
import com.tfar.randomenchants.util.EnchantUtils;
import net.minecraft.block.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.tfar.randomenchants.EnchantmentConfig.EnumAccessLevel.*;
import static com.tfar.randomenchants.EnchantmentConfig.weapons;
import static com.tfar.randomenchants.RandomEnchants.ObjectHolders.LIGHTING;

@Mod.EventBusSubscriber(modid = RandomEnchants.MOD_ID)
public class EnchantmentTorches extends Enchantment {
  public EnchantmentTorches() {

    super(Rarity.RARE, EnchantmentType.BOW, new EquipmentSlotType[]{
            EquipmentSlotType.MAINHAND
    });
    setRegistryName("torches");
  }

  @Override
  public int getMinEnchantability(int level) {
    return 15;
  }

  @Override
  public int getMaxLevel() {
    return 1;
  }

  @Override
  public boolean canApply(ItemStack stack) {
    return weapons.enableTorches != DISABLED && super.canApply(stack);
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) {
    return weapons.enableTorches != DISABLED && super.canApplyAtEnchantingTable(stack);
  }

  @Override
  public boolean isAllowedOnBooks() {
    return weapons.enableTorches == NORMAL;
  }

  @Override
  public boolean isTreasureEnchantment() {
    return weapons.enableTorches == ANVIL;
  }

  @SubscribeEvent
  public static void onBlockHit(ProjectileImpactEvent e) {
    Entity arrow = e.getEntity();
    if (!(arrow instanceof AbstractArrowEntity)) return;
    RayTraceResult result = e.getRayTraceResult();
    if (!(result instanceof BlockRayTraceResult)) return;
    Entity shooter = ((AbstractArrowEntity) arrow).getShooter();
    if (!(shooter instanceof LivingEntity)) return;
    if (result.getType() == RayTraceResult.Type.MISS) return;
    LivingEntity user = (LivingEntity) ((AbstractArrowEntity) arrow).getShooter();
    if (user == null) return;
    if (!EnchantUtils.hasEnch(user, LIGHTING)) return;
    World world = arrow.world;
    //BlockItem torchitem = Items.TORCH;
    if (!world.isRemote) {
      BlockPos pos = arrow.getPosition();
      if (!world.isAirBlock(pos))return;
      Direction dir = ((BlockRayTraceResult) result).getFace();
      BlockState blockState;
      if (dir == Direction.UP) blockState = Blocks.TORCH.getDefaultState();
      else if (dir != Direction.DOWN)
        blockState = Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.HORIZONTAL_FACING, dir);
      else return;
      world.setBlockState(pos, blockState);
      arrow.remove();
    }
  }
}


