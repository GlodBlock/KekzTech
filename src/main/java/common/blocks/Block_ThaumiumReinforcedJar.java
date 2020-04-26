package common.blocks;

import java.util.ArrayList;
import java.util.List;

import common.tileentities.TE_ThaumiumReinforcedJar;
import common.tileentities.TE_ThaumiumReinforcedVoidJar;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import thaumcraft.common.blocks.BlockJar;
import thaumcraft.common.config.ConfigBlocks;

public class Block_ThaumiumReinforcedJar extends BlockJar {
	
	private static Block_ThaumiumReinforcedJar instance;
	
	private Block_ThaumiumReinforcedJar() {
		super();
		
		super.setHardness(8.0F);
		super.setResistance(6.0F);
	}
	
	public static Block registerBlock() {
		if(instance == null) {
			instance = new Block_ThaumiumReinforcedJar();
		}
		
		final String blockName = "kekztech_thaumiumreinforcedjar_block";
		instance.setBlockName(blockName);
		GameRegistry.registerBlock(instance, blockName);
		
		return instance;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {
		super.iconLiquid = ir.registerIcon("thaumcraft:animatedglow");
		super.iconJarSide = ir.registerIcon("kekztech:thaumreinforced_jar_side");
		super.iconJarTop = ir.registerIcon("kekztech:thaumreinforced_jar_top");
		super.iconJarTopVoid = ir.registerIcon("kekztech:thaumreinforced_jar_top_void");
		super.iconJarSideVoid = ir.registerIcon("kekztech:thaumreinforced_jar_side_void");
		super.iconJarBottom = ir.registerIcon("kekztech:thaumreinforced_jar_bottom");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
		par3List.add(new ItemStack(par1, 1, 0)); // Normal jar
		par3List.add(new ItemStack(par1, 1, 3)); // Void jar
	}
	
	@Override
	public TileEntity createTileEntity(World world, int meta) {
		if(meta == 0) {
			return new TE_ThaumiumReinforcedJar();
		} else if (meta == 3) {
			return new TE_ThaumiumReinforcedVoidJar();
		} else {
			return null;
		}
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
		final TileEntity te = world.getTileEntity(x, y, z);
		if(te != null && te instanceof TE_ThaumiumReinforcedJar) {
			if(((TE_ThaumiumReinforcedJar) te).amount > 0) {
				// Create a small explosion in the center of the block (TNT has strength 4.0F)
				world.createExplosion(null, x + 0.5D, y + 0.5D, z + 0.5D, 1.0F, false);
				
				// Place some Flux in the area
				final int limit = ((TE_ThaumiumReinforcedJar) te).amount / 16;
				int created = 0;
				for(int i = 0; i < 50; i++) {
					final int xf = x + world.rand.nextInt(4) - world.rand.nextInt(4);
					final int yf = x + world.rand.nextInt(4) - world.rand.nextInt(4);
					final int zf = x + world.rand.nextInt(4) - world.rand.nextInt(4);
					if(world.isAirBlock(xf, yf, zf)) {
						if(yf > y) {
							world.setBlock(xf, yf, zf, ConfigBlocks.blockFluxGas, 8, 3);
						} else {
							world.setBlock(xf, yf, zf, ConfigBlocks.blockFluxGoo, 8, 3);
						}
						
						if(created++ > limit) {
							break;
						}
					}
				}
			}
		}
		
		super.breakBlock(world, x, y, z, par5, par6);
	}
	
	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		final ArrayList<ItemStack> drops = new ArrayList<>();
		drops.add(new ItemStack(this, 1, (meta == 3) ? 3 : 0));
		return drops;
	}
	
	@Override
	public boolean canDropFromExplosion(Explosion e) {
		return false;
	}
}