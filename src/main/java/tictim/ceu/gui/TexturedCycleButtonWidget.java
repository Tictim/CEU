package tictim.ceu.gui;

import gregtech.api.gui.IRenderContext;
import gregtech.api.gui.resources.SizedTextureArea;
import gregtech.api.gui.resources.TextureArea;
import gregtech.api.gui.widgets.CycleButtonWidget;
import gregtech.api.util.GTUtility;
import gregtech.api.util.Position;
import gregtech.api.util.Size;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 * Modified CycleButtonWidget for better tooltips.
 * I realized the class name is terrible at describing itself while writing this comment.
 */
public class TexturedCycleButtonWidget extends CycleButtonWidget{
	private final String[] optionTooltipNames;
	private final boolean cycleTexture;

	public TexturedCycleButtonWidget(
			int xPosition,
			int yPosition,
			int width,
			int height,
			String[] optionTooltipNames,
			IntSupplier currentOptionSupplier,
			IntConsumer setOptionExecutor){
		this(xPosition,
				yPosition,
				width,
				height,
				optionTooltipNames,
				currentOptionSupplier,
				setOptionExecutor,
				null,
				false);
	}
	public <T extends Enum<T> & IStringSerializable> TexturedCycleButtonWidget(
			int xPosition,
			int yPosition,
			int width,
			int height,
			Class<T> enumClass,
			Supplier<T> supplier,
			Consumer<T> updater){
		this(xPosition,
				yPosition,
				width,
				height,
				enumClass,
				supplier,
				updater,
				null,
				false);
	}
	public <T extends Enum<T> & IStringSerializable> TexturedCycleButtonWidget(
			int xPosition,
			int yPosition,
			int width,
			int height,
			Class<T> enumClass,
			Supplier<T> supplier,
			Consumer<T> updater,
			@Nullable TextureArea buttonTexture,
			boolean cycleTexture){
		this(xPosition,
				yPosition,
				width,
				height,
				GTUtility.mapToString(enumClass.getEnumConstants(), IStringSerializable::getName),
				() -> supplier.get().ordinal(),
				(newIndex) -> updater.accept(enumClass.getEnumConstants()[newIndex]),
				buttonTexture,
				cycleTexture);
	}

	public TexturedCycleButtonWidget(
			int xPosition,
			int yPosition,
			int width,
			int height,
			String[] optionTooltipNames,
			IntSupplier currentOptionSupplier,
			IntConsumer setOptionExecutor,
			@Nullable TextureArea buttonTexture,
			boolean cycleTexture){
		super(xPosition, yPosition, width, height, optionTooltipNames, currentOptionSupplier, setOptionExecutor);
		this.optionTooltipNames = optionTooltipNames;
		if(buttonTexture!=null) setButtonTexture(buttonTexture);
		this.cycleTexture = cycleTexture;
	}

	@Override public void drawInBackground(int mouseX, int mouseY, IRenderContext context){
		Position pos = getPosition();
		Size size = getSize();
		if(buttonTexture instanceof SizedTextureArea){
			((SizedTextureArea)buttonTexture).drawHorizontalCutSubArea(pos.x, pos.y, size.width, size.height, 0.0, 1.0);
		}else{
			buttonTexture.drawSubArea(pos.x,
					pos.y,
					size.width,
					size.height,
					0.0,
					cycleTexture ? (double)currentOption/optionTooltipNames.length : 0,
					1.0,
					cycleTexture ? 1.0/optionTooltipNames.length : 0);
		}
	}

	@Override public void drawInForeground(int mouseX, int mouseY){
		if(isMouseOverElement(mouseX, mouseY))
			drawHoveringText(ItemStack.EMPTY, Arrays.asList(I18n.format(optionTooltipNames[currentOption]).split("/n")), 300, mouseX, mouseY);
	}
}
