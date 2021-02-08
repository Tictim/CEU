package tictim.ceu.mte;

import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.render.SimpleOverlayRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import tictim.ceu.contents.CeuResources;
import tictim.ceu.gui.InfiniteEnergyUIData;
import tictim.ceu.trait.infinite.TraitInfiniteIC2EUReceiver;

public class MTEInfiniteIC2EUReceiver extends MTEInfiniteEnergyBase<TraitInfiniteIC2EUReceiver>{
	public MTEInfiniteIC2EUReceiver(ResourceLocation metaTileEntityId){
		super(metaTileEntityId);
	}

	@Override protected TraitInfiniteIC2EUReceiver createTrait(){
		return new TraitInfiniteIC2EUReceiver(this);
	}
	@Override public boolean isDisabled(){
		return true; //CeuConfig.config().isEnergyReceiverDisabled(CommonEnergy.IC2EU);
	}
	@Override public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder){
		return new MTEInfiniteIC2EUReceiver(metaTileEntityId);
	}
	@Override protected ModularUI createUI(EntityPlayer entityPlayer){
		return InfiniteEnergyUIData.createWIPScreen(getHolder(), entityPlayer);
	}

	@Override protected SimpleOverlayRenderer getOverlay(){
		return CeuResources.IC2EU_RECEIVER_FACE;
	}
}
