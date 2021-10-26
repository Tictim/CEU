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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;
import tictim.ceu.util.Record;

import javax.annotation.Nullable;
import java.math.BigInteger;

public abstract class InfiniteEnergyMTE extends MetaTileEntity{
	public static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
	public static final BigInteger MAX_INT = BigInteger.valueOf(Integer.MAX_VALUE);

	protected BigInteger energy = BigInteger.ZERO;

	@Nullable private Record record;

	public InfiniteEnergyMTE(ResourceLocation metaTileEntityId){
		super(metaTileEntityId);
	}

	public BigInteger getEnergy(){
		return energy;
	}
	public void setEnergy(BigInteger bigInteger){
		energy = bigInteger.signum()==1 ? bigInteger : BigInteger.ZERO;
	}

	protected void add(BigInteger bInt){
		switch(bInt.signum()){
			case 1:
				energy = energy.add(bInt);
				break;
			case -1:
				subtract(bInt.negate());
		}
	}
	protected void subtract(BigInteger bInt){
		energy = energy.compareTo(bInt)>0 ? energy.subtract(bInt) : BigInteger.ZERO;
	}

	protected void addToRecord(long value){
		Record r = record;
		if(r!=null) r.add(getWorld().getTotalWorldTime(), value);
	}

	@Nullable public Record getRecord(){
		return record;
	}

	@Override public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline){
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
			if(record==null) return;

			Record r = this.record;
			this.record = null;

			EntityPlayer player = getRecordedPlayer(r);

			boolean succeed = r.saveWithDefaultName(metaTileEntityId.getPath(), player!=null ? player.getName() : null);
			if(player!=null) player.sendStatusMessage(
					succeed ?
							new TextComponentString("Record saved.") :
							new TextComponentString("Failed to save record.")
									.setStyle(new Style().setColor(TextFormatting.RED)),
					true);
		}
	}

	@Nullable private EntityPlayer getRecordedPlayer(Record record){
		if(record.getRecordingPlayer()!=null){
			MinecraftServer server = getWorld().getMinecraftServer();
			if(server!=null)
				return server.getPlayerList().getPlayerByUUID(record.getRecordingPlayer());
		}
		return null;
	}

	@SideOnly(Side.CLIENT) protected abstract SimpleOverlayRenderer getOverlay();

	@Override public Pair<TextureAtlasSprite, Integer> getParticleTexture(){
		return Pair.of(Textures.VOLTAGE_CASINGS[GTValues.MAX].getSpriteOnSide(SimpleSidedCubeRenderer.RenderSide.TOP), 0xFFFFFF);
	}

	@Override public NBTTagCompound writeToNBT(NBTTagCompound data){
		super.writeToNBT(data);
		if(energy.signum()>0) data.setByteArray("Energy", energy.toByteArray());
		return data;
	}
	@Override public void readFromNBT(NBTTagCompound data){
		super.readFromNBT(data);
		if(data.hasKey("Energy", Constants.NBT.TAG_BYTE_ARRAY)){
			byte[] bArr = data.getByteArray("Energy");
			energy = bArr.length>0 ? new BigInteger(bArr) : BigInteger.ZERO;
		}else energy = BigInteger.ZERO;
	}
}
