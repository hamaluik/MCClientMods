package com.mcnsa.worldeditui;

import java.nio.charset.Charset;
import java.util.HashMap;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;

import com.mcnsa.worldeditui.event.CylinderEvent;
import com.mcnsa.worldeditui.event.EllipsoidEvent;
import com.mcnsa.worldeditui.event.UIEvent;
import com.mcnsa.worldeditui.event.MinMaxEvent;
import com.mcnsa.worldeditui.event.Point2DEvent;
import com.mcnsa.worldeditui.event.PointEvent;
import com.mcnsa.worldeditui.event.SelectionEvent;
import com.mcnsa.worldeditui.event.UpdateEvent;
import com.mcnsa.worldeditui.hooks.HookEntity;
import com.mcnsa.worldeditui.hooks.RenderHook;
import com.mcnsa.worldeditui.proxy.CommonProxy;
import com.mcnsa.worldeditui.render.CubeRegion;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.modloader.BaseModProxy;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.EntityRegistry;

@Mod(modid="mod_WorldEditUI", name="WorldEditUI", useMetadata=true)
@NetworkMod(clientSideRequired=true, serverSideRequired=false)
public class WorldEditUI implements IPacketHandler, IConnectionHandler {
	// define some things for our packet protocol
    public static final int protocolVersion = 2;
    public final static Charset UTF_8_CHARSET = Charset.forName("UTF-8");
    
	@Instance("mod_WorldEditUI")
	public static WorldEditUI instance;
    
    // where the client and server 'proxy' code is loaded.
    @SidedProxy(clientSide="com.mcnsa.worldeditui.proxy.ClientProxy", serverSide="com.mcnsa.worldeditui.proxy.CommonProxy")
    public static CommonProxy proxy;
    
    // keep track of all our events
    private static HashMap<String, UIEvent> uiEvents = new HashMap<String, UIEvent>();
    
    // and our current selection
    private static CubeRegion selection = new CubeRegion();
	
	public WorldEditUI() {
		// store our instance
		instance = this;
		
		// store our events
		uiEvents.put(SelectionEvent.identifier, new SelectionEvent());
		uiEvents.put(PointEvent.identifier, new PointEvent());
		uiEvents.put(Point2DEvent.identifier, new Point2DEvent());
		uiEvents.put(EllipsoidEvent.identifier, new EllipsoidEvent());
		uiEvents.put(CylinderEvent.identifier, new CylinderEvent());
		uiEvents.put(MinMaxEvent.identifier, new MinMaxEvent());
		uiEvents.put(UpdateEvent.identifier, new UpdateEvent());
	}
	
    // do configuration loading in here
	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		
	}
	
	// do initialization here
	@Init
	public void load(FMLInitializationEvent event) {
		// start our proxy
		proxy.initialize();
		
		// register our entity render hook
		EntityRegistry.registerModEntity(HookEntity.class, "weuiHookEntity", 1, this, 9999999, 3, false);
		
		// register our packet channel
		NetworkRegistry.instance().registerChannel(this, "WECUI");
		NetworkRegistry.instance().registerConnectionHandler(this);
		
		// register our selection renderer
		RenderHook.registerOverlay(selection);
	}
	
	@PostInit
	public void postInit(FMLPostInitializationEvent event) {
	}
	
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		//FMLLog.info("[WorldEditUI] Intercepted packet on channel %s: %s", packet.channel, new String(packet.data));
		// parse the packet
		String message = new String(packet.data, UTF_8_CHARSET);
		String[] parts = message.split("[|]");
		String type = parts[0];
		String[] args = message.substring(type.length() + 1).split("[|]");
		
		// handle it
		if(uiEvents.containsKey(type)) {
			uiEvents.get(type).handle(args);
		}
		else {
			FMLLog.warning("Unhandled packet type: %s (with args: %s)", type, message.substring(type.length() + 1));
		}
	}

	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager) {
		// ignore
		//FMLLog.info("[WorldEditUI] player logged in");
	}

	@Override
	public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) {
		// ignore
		//FMLLog.info("[WorldEditUI] connection recieved");
		return "";
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) {
		//FMLLog.info("[WorldEditUI] remote connection opened");
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager) {
		//FMLLog.info("[WorldEditUI] local connection opened");
	}

	@Override
	public void connectionClosed(INetworkManager manager) {
		// ignore
		//FMLLog.info("[WorldEditUI] connection closed");
	}
	
	private Packet250CustomPayload newPayloadPacket(String channel, int length, byte[] data) {
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = channel;
		packet.length = length;
		packet.data = data;
		return packet;
	}

	@Override
	public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) {
		// tell worldedit about us
		byte[] buffer = ("v|" + protocolVersion).getBytes(UTF_8_CHARSET);
		PacketDispatcher.sendPacketToServer(newPayloadPacket("WECUI", buffer.length, buffer));
		FMLLog.info("[WorldEditUI] client logged in");
	}
	
	public static CubeRegion getSelection() {
		return selection;
	}
}
