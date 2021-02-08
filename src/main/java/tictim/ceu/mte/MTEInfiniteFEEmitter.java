package tictim.ceu.mte;

import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.render.SimpleOverlayRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import tictim.ceu.contents.CeuResources;
import tictim.ceu.enums.CommonEnergy;
import tictim.ceu.config.CeuConfig;
import tictim.ceu.gui.InfiniteEnergyUIData;
import tictim.ceu.trait.infinite.TraitInfiniteFEEmitter;

public class MTEInfiniteFEEmitter extends MTEInfiniteEnergyBase<TraitInfiniteFEEmitter>{
	public MTEInfiniteFEEmitter(ResourceLocation metaTileEntityId){
		super(metaTileEntityId);
	}

	@Override protected TraitInfiniteFEEmitter createTrait(){
		return new TraitInfiniteFEEmitter(this);
	}
	@Override public boolean isDisabled(){
		return CeuConfig.config().isEnergyEmitterDisabled(CommonEnergy.FE);
	}
	@Override public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder){
		return new MTEInfiniteFEEmitter(metaTileEntityId);
	}
	@Override protected ModularUI createUI(EntityPlayer entityPlayer){
		InfiniteEnergyUIData d = new InfiniteEnergyUIData();
		d.setEnergy(trait.getEnergy());
		d.setInfinite(trait.isInfinite());
		return d.guiBuilder()
				.buttonInfinite(trait::setInfinite)
				.energyInput("FE", trait::setEnergy)
				.buttonAcceptDecline()
				.createUI(getHolder(), entityPlayer);
	}

	@Override protected SimpleOverlayRenderer getOverlay(){
		return CeuResources.FE_EMITTER_FACE;
	}
}
