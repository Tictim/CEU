package tictim.ceu.gui;

import gregtech.api.gui.widgets.TextFieldWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

public class InfiniteEnergyTextField extends TextFieldWidget{
	private final InfiniteEnergyUIData data;

	public InfiniteEnergyTextField(
			int xPosition,
			int yPosition,
			int width,
			int height,
			boolean enableBackground,
			InfiniteEnergyUIData data){
		super(xPosition, yPosition, width, height, enableBackground, data::getEnergyText, data::setEnergyText);
		this.data = data;
		if(FMLCommonHandler.instance().getSide().isClient()){
			this.textField.setCanLoseFocus(false);
			this.textField.setFocused(true);
		}
	}

	@Override
	public void handleClientAction(int id, PacketBuffer buffer){
		super.handleClientAction(id, buffer);
		if(id==35){
			if(buffer.readBoolean()) data.markDirty(false);
			this.gui.entityPlayer.closeScreen();
		}
	}

	@Override
	public boolean keyTyped(char charTyped, int keyCode){
		if(!super.keyTyped(charTyped, keyCode)){
			GuiCloseAction action = GuiCloseAction.getFromKey(keyCode);
			if(action==GuiCloseAction.NONE) return false;
			else writeClientAction(35, buffer -> buffer.writeBoolean(action==GuiCloseAction.CLOSE_WITHOUT_SAVE));
		}
		return true;
	}

	@SideOnly(Side.CLIENT)
	private enum GuiCloseAction{
		CLOSE,
		CLOSE_WITHOUT_SAVE,
		NONE;

		private static GuiCloseAction getFromKey(int keyCode){
			if(keyCode==Keyboard.KEY_RETURN) return CLOSE;
			else if(keyCode==1||Minecraft.getMinecraft().gameSettings.keyBindInventory.isActiveAndMatches(keyCode)) return CLOSE_WITHOUT_SAVE;
			else return NONE;
		}
	}
}
