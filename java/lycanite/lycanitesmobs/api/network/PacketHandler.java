package lycanite.lycanitesmobs.api.network;

import lycanite.lycanitesmobs.LycanitesMobs;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {
	public final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(LycanitesMobs.modid);
	
	
	// ==================================================
	//                    Initialize
	// ==================================================
	/**
	 * Initializes the Packet Handler where Messages are registered.
	 */
	public void init() {
		int messageID = 0;
		
		// Server to Client:
		this.network.registerMessage(MessageBeastiary.class, MessageBeastiary.class, messageID++, Side.CLIENT);
		this.network.registerMessage(MessageCreatureKnowledge.class, MessageCreatureKnowledge.class, messageID++, Side.CLIENT);
		this.network.registerMessage(MessagePlayerStats.class, MessagePlayerStats.class, messageID++, Side.CLIENT);
		this.network.registerMessage(MessageSummonSet.class, MessageSummonSet.class, messageID++, Side.CLIENT);
		this.network.registerMessage(MessageSummonSetSelection.class, MessageSummonSetSelection.class, messageID++, Side.CLIENT);
		
		// Client to Server:
		this.network.registerMessage(MessageEntityGUICommand.class, MessageEntityGUICommand.class, messageID++, Side.SERVER);
		this.network.registerMessage(MessageGUIRequest.class, MessageGUIRequest.class, messageID++, Side.SERVER);
		this.network.registerMessage(MessagePlayerControl.class, MessagePlayerControl.class, messageID++, Side.SERVER);
		this.network.registerMessage(MessageSummonSet.class, MessageSummonSet.class, messageID++, Side.SERVER);
		this.network.registerMessage(MessageSummonSetSelection.class, MessageSummonSetSelection.class, messageID++, Side.SERVER);
	}
	
	
	// ==================================================
	//                    Send To All
	// ==================================================
	/**
	 * Sends a packet from the server to all players.
	 * @param packet
	 */
	public void sendToAll(IMessage message) {
		this.network.sendToAll(message);
	}
	
	
	// ==================================================
	//                   Send To Player
	// ==================================================
	/**
	 * Sends a packet from the server to the specified player.
	 * @param packet
	 * @param player
	 */
	public void sendToPlayer(IMessage message, EntityPlayerMP player) {
		this.network.sendTo(message, player);
	}
	
	
	// ==================================================
	//                 Send To All Around
	// ==================================================
	/**
	 * Sends a packet from the server to all players near the specified target point.
	 * @param packet
	 * @param point
	 */
	public void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
		this.network.sendToAllAround(message, point);
	}
	
	
	// ==================================================
	//                 Send To Dimension
	// ==================================================
	/**
	 * Sends a packet to all players within the specified dimension.
	 * @param packet
	 * @param dimensionID The ID of the dimension to use.
	 */
	public void sendToDimension(IMessage message, int dimensionID) {
		this.network.sendToDimension(message, dimensionID);
	}
	
	
	// ==================================================
	//                   Send To Server
	// ==================================================
	/**
	 * Sends a packet from the client player to the server.
	 * @param packet
	 */
	public void sendToServer(IMessage message) {
		this.network.sendToServer(message);
	}
}
