package com.mcnsa.chatbubbles;

public class ChatItem {
	public String chat;
	public long timeoutTime;
	
	public ChatItem(String chat, long deltaTimeoutTime) {
		this.chat = chat;
		this.timeoutTime = System.currentTimeMillis() + deltaTimeoutTime;
	}
}
