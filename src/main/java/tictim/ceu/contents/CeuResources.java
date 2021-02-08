package tictim.ceu.contents;

import gregtech.api.gui.resources.TextureArea;
import gregtech.api.render.SimpleOverlayRenderer;
import net.minecraft.util.ResourceLocation;
import tictim.ceu.CeuMod;

public final class CeuResources{
	private CeuResources(){}

	public static void init(){} // SimpleOverlayRenderer needs to be created as fast as possible becuase GTCE fucking dumb >:(

	public static final SimpleOverlayRenderer CEU_FACE = new SimpleOverlayRenderer("overlay/machine/ceu/ceu");
	public static final SimpleOverlayRenderer CEF_FACE = new SimpleOverlayRenderer("overlay/machine/ceu/cef");
	public static final SimpleOverlayRenderer ICEU_FACE = new SimpleOverlayRenderer("overlay/machine/ceu/iceu");
	public static final SimpleOverlayRenderer ICEF_FACE = new SimpleOverlayRenderer("overlay/machine/ceu/icef");

	public static final SimpleOverlayRenderer GTEU_EMITTER_FACE = new SimpleOverlayRenderer("overlay/machine/ceu/gteu_emitter");
	public static final SimpleOverlayRenderer GTEU_RECEIVER_FACE = new SimpleOverlayRenderer("overlay/machine/ceu/gteu_receiver");
	public static final SimpleOverlayRenderer FE_EMITTER_FACE = new SimpleOverlayRenderer("overlay/machine/ceu/fe_emitter");
	public static final SimpleOverlayRenderer FE_RECEIVER_FACE = new SimpleOverlayRenderer("overlay/machine/ceu/fe_receiver");
	public static final SimpleOverlayRenderer IC2EU_EMITTER_FACE = new SimpleOverlayRenderer("overlay/machine/ceu/ic2eu_emitter");
	public static final SimpleOverlayRenderer IC2EU_RECEIVER_FACE = new SimpleOverlayRenderer("overlay/machine/ceu/ic2eu_receiver");

	public static final TextureArea FE_IO_MODE_BUTTON = new TextureArea(new ResourceLocation(CeuMod.MODID, "textures/gui/widget/fe_io_mode_button.png"), 0.0, 0.0, 1.0, 1.0);
	public static final TextureArea GTEU_IO_MODE_BUTTON = new TextureArea(new ResourceLocation(CeuMod.MODID, "textures/gui/widget/gteu_io_mode_button.png"), 0.0, 0.0, 1.0, 1.0);
	public static final TextureArea CHARGE_STRATEGY_BUTTON = new TextureArea(new ResourceLocation(CeuMod.MODID, "textures/gui/widget/charge_strategy_button.png"), 0.0, 0.0, 1.0, 1.0);
}
