package tictim.ceu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;

import java.util.Set;

@SuppressWarnings("unused")
public class CeuConfigGuiFactory implements IModGuiFactory{
	@Override public void initialize(Minecraft mc){}
	@Override public boolean hasConfigGui(){
		return true;
	}
	@Override public GuiScreen createConfigGui(GuiScreen s){
		return new GuiConfig(s, CeuMod.collectProperties(), CeuMod.MODID, false, false, CeuMod.NAME);
	}
	@Override public Set<RuntimeOptionCategoryElement> runtimeGuiCategories(){
		return null;
	}
}
