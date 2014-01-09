package com.mcnsa.chatbubbles;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet3Chat;

import com.mcnsa.chatbubbles.client.ClientTickHandler;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.IChatListener;

public class CommonProxy implements IChatListener {
	private Pattern pattern = Pattern.compile(ChatBubbles.CHAT_FORMAT);
	
	public void initialize() {
		
	}
	
	public void recompileChatPattern() {
		pattern = Pattern.compile(ChatBubbles.CHAT_FORMAT);
	}

	@Override
	public Packet3Chat serverChat(NetHandler handler, Packet3Chat message) {
		return message;
	}

	@Override
	public Packet3Chat clientChat(NetHandler handler, Packet3Chat message) {
		Matcher matcher = pattern.matcher(message.message);
		if(matcher.matches()) {
			// yup, it matches!
			String username = matcher.group(1);
			if(username != null) {
				username = username.replaceAll("(\u00A7([a-f0-9klmnor]))", "").trim();
			}
			else {
				username = "null";
			}
			String chatLine = matcher.group(2);
			if(chatLine != null) {
				chatLine = chatLine.replaceAll("(\u00A7([a-f0-9klmnor]))", "").trim();
			}
			else {
				chatLine = "null";
			}
			
			// now split long lines
			// (but don't split words in half!)
			String words[] = ((String)chatLine).split(" ");
			StringBuilder sb = new StringBuilder();

			// loop over all words in the set
			for(int l = 0; l < words.length; l++) {
				if(words[l].length() > 50) {
					int charNum;
					String str;
					for(charNum = 0; (charNum + 1) * 50 < words[l].length();) {
						str = words[l].substring(charNum * 50, (charNum + 1) * 50);
						charNum++;
						ChatBubbles.enqueueChat(username, str.trim());
					}

					str = words[l].substring(charNum * 50, words[l].length());
					if(l < words.length - 1) {
						ChatBubbles.enqueueChat(username, str.trim());
					}
					continue;
				}
				// check if we need to wrap
				// the -1 is to disregard the extra spaces at the end
				if(words[l].length() + sb.toString().length() - 1 > ChatBubbles.MAX_WIDTH) {
					// post what we have
					ChatBubbles.enqueueChat(username, sb.toString().trim());
					sb = new StringBuilder();
					sb.append(words[l]).append(" ");
				}
				else {
					sb.append(words[l]).append(" ");
				}
			}

			// post it!
			ChatBubbles.enqueueChat(username, sb.toString().trim());
		}
		
		return message;
	}
}
