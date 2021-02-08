package tictim.ceu.mte;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.render.SimpleOverlayRenderer;
import gregtech.api.render.SimpleSidedCubeRenderer;
import gregtech.api.render.Textures;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;
import tictim.ceu.trait.infinite.TraitInfiniteEnergy;

public abstract class MTEInfiniteEnergyBase<TRAIT extends TraitInfiniteEnergy> extends MetaTileEntity{
	protected final TRAIT trait;

	public MTEInfiniteEnergyBase(ResourceLocation metaTileEntityId){
		super(metaTileEntityId);
		trait = createTrait();
	}

	protected abstract TRAIT createTrait();
	public abstract boolean isDisabled();

	@Override
	public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline){
		Textures.VOLTAGE_CASINGS[GTValues.MAX].render(renderState, translation, pipeline);
		for(EnumFacing facing : EnumFacing.VALUES)
			getOverlay().renderSided(facing, renderState, translation, pipeline);
	}

	@SideOnly(Side.CLIENT)
	protected abstract SimpleOverlayRenderer getOverlay();

	@Override
	@SideOnly(Side.CLIENT)
	public Pair<TextureAtlasSprite, Integer> getParticleTexture(){
		return Pair.of(Textures.VOLTAGE_CASINGS[GTValues.MAX].getSpriteOnSide(SimpleSidedCubeRenderer.RenderSide.TOP), 0xFFFFFF);
	}
}
