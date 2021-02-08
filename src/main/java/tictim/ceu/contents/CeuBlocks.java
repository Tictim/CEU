package tictim.ceu.contents;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import tictim.ceu.CeuMod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = CeuMod.MODID)
public final class CeuBlocks{
	private CeuBlocks(){}

	public static final Block TEST = new Block(Material.CLOTH){
		@Override public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced){
			tooltip.add(I18n.format("tile.test.ohno.0"));
			tooltip.add(I18n.format("tile.test.ohno.1", getRandomTextFormatting()));
		}
	}.setRegistryName("test").setUnlocalizedName("broken_ceu").setBlockUnbreakable().setResistance(6000000);

	public static final Item TEST_ITEM = new ItemBlock(TEST).setRegistryName(TEST.getRegistryName());


	@SubscribeEvent
	public static void blockRegister(RegistryEvent.Register<Block> event){
		event.getRegistry().register(TEST);
	}
	@SubscribeEvent
	public static void itemRegister(RegistryEvent.Register<Item> event){
		event.getRegistry().register(TEST_ITEM);
	}

	private static final Random rng = new Random();
	private static TextFormatting cache;

	private static TextFormatting getRandomTextFormatting(){
		if(cache==null) return cache = TextFormatting.values()[rng.nextInt(17)];
		else{
			TextFormatting f = cache;
			cache = null;
			return f;
		}
	}

	@Mod.EventBusSubscriber(modid = CeuMod.MODID, value = Side.CLIENT)
	public static final class Client{
		private Client(){}

		@SubscribeEvent
		public static void modelRegister(ModelRegistryEvent event){
			for(int i = 0; i<4; i++) ModelLoader.setCustomModelResourceLocation(TEST_ITEM, 0, new ModelResourceLocation(TEST.getRegistryName(), "inventory"));
		}
	}
}
