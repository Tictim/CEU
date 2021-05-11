package tictim.ceu.mte;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.render.SimpleOverlayRenderer;
import gregtech.api.render.SimpleSidedCubeRenderer;
import gregtech.api.render.Textures;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;
import tictim.ceu.trait.infinite.TraitInfiniteEnergy;
import tictim.ceu.util.Record;

import javax.annotation.Nullable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class MTEInfiniteEnergyBase<TRAIT extends TraitInfiniteEnergy> extends MetaTileEntity{
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");

	protected final TRAIT trait;
	@Nullable private Record record;

	public MTEInfiniteEnergyBase(ResourceLocation metaTileEntityId){
		super(metaTileEntityId);
		trait = createTrait();
	}

	protected abstract TRAIT createTrait();
	public abstract boolean isDisabled();

	@Nullable public Record getRecord(){
		return record;
	}

	@Override
	public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline){
		Textures.VOLTAGE_CASINGS[GTValues.MAX].render(renderState, translation, pipeline);
		for(EnumFacing facing : EnumFacing.VALUES)
			getOverlay().renderSided(facing, renderState, translation, pipeline);
	}

	@Override public boolean onWrenchClick(EntityPlayer player, EnumHand hand, EnumFacing wrenchSide, CuboidRayTraceResult hitResult){
		if(!player.isCreative()||player.isSneaking()) return super.onWrenchClick(player, hand, wrenchSide, hitResult);

		if(!getWorld().isRemote){
			if(record==null){
				long t = getWorld().getTotalWorldTime();
				record = new Record(t+1, t+6000, player.getGameProfile().getId()); // hardcoded to last until 5 minutes. in case some asshole turns on record and forgets about it or something
				player.sendStatusMessage(new TextComponentString("Record started."), true);
			}else record.setEnd(getWorld().getTotalWorldTime()); // Will end the record at the next tick
		}
		return true;
	}

	@Override public void update(){
		super.update();
		if(!getWorld().isRemote&&record!=null&&record.getEnd()<getWorld().getTotalWorldTime()){
			stopAndSaveRecord();
		}
	}

	public void stopAndSaveRecord(){
		if(record==null) return;

		Record r = this.record;
		this.record = null;

		EntityPlayer player = getRecordedPlayer(r);

		String fileName = "record_"+DATE_FORMAT.format(new Date())+"_"+metaTileEntityId.getResourcePath();
		if(player!=null) fileName += "_"+player.getName();

		boolean succeed = r.save(fileName+".csv");

		if(player!=null) player.sendStatusMessage(
				succeed ?
						new TextComponentString("Record saved.") :
						new TextComponentString("Failed to save record.")
								.setStyle(new Style().setColor(TextFormatting.RED)),
				true);
	}

	@Nullable private EntityPlayer getRecordedPlayer(Record record){
		if(record.getRecordingPlayer()!=null){
			MinecraftServer server = getWorld().getMinecraftServer();
			if(server!=null)
				return server.getPlayerList().getPlayerByUUID(record.getRecordingPlayer());
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	protected abstract SimpleOverlayRenderer getOverlay();

	@Override
	@SideOnly(Side.CLIENT)
	public Pair<TextureAtlasSprite, Integer> getParticleTexture(){
		return Pair.of(Textures.VOLTAGE_CASINGS[GTValues.MAX].getSpriteOnSide(SimpleSidedCubeRenderer.RenderSide.TOP), 0xFFFFFF);
	}
}
