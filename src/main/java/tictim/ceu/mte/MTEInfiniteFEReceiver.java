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
import tictim.ceu.trait.infinite.TraitInfiniteFEReceiver;

public class MTEInfiniteFEReceiver extends MTEInfiniteEnergyBase<TraitInfiniteFEReceiver>{
	public MTEInfiniteFEReceiver(ResourceLocation metaTileEntityId){
		super(metaTileEntityId);
	}

	@Override protected TraitInfiniteFEReceiver createTrait(){
		return new TraitInfiniteFEReceiver(this);
	}
	@Override public boolean isDisabled(){
		return CeuConfig.config().isEnergyReceiverDisabled(CommonEnergy.FE);
	}
	@Override public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder){
		return new MTEInfiniteFEReceiver(metaTileEntityId);
	}
	@Override protected ModularUI createUI(EntityPlayer entityPlayer){
		InfiniteEnergyUIData d = new InfiniteEnergyUIData();
		d.setEnergy(trait.getEnergy());
		return d.guiBuilder()
				.energyInput("FE", trait::setEnergy)
				.buttonAcceptDecline()
				.createUI(getHolder(), entityPlayer);
	}

	@Override protected SimpleOverlayRenderer getOverlay(){
		return CeuResources.FE_RECEIVER_FACE;
	}
}
