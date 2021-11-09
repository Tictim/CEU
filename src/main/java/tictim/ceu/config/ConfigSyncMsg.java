package tictim.ceu.config;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ConfigSyncMsg implements IMessage{
	private CeuConfig cfg;

	@Override public void fromBytes(ByteBuf buf){
		cfg = new CeuConfig(ByteBufUtils.readTag(buf));
	}

	@Override public void toBytes(ByteBuf buf){
		NBTTagCompound nbt = new NBTTagCompound();
		CeuConfig.localConfig().serialize(nbt);
		ByteBufUtils.writeTag(buf, nbt);
	}

	public enum Handler implements IMessageHandler<ConfigSyncMsg, IMessage>{
		INSTANCE;

		@Override public IMessage onMessage(ConfigSyncMsg message, MessageContext ctx){
			CeuConfig.setSyncedConfig(message.cfg);
			return null;
		}
	}
}
