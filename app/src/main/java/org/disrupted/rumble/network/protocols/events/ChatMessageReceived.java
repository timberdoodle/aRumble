/*
 * Copyright (C) 2014 Lucien Loiseau
 * This file is part of Rumble.
 * Rumble is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Rumble is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with Rumble.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.disrupted.rumble.network.protocols.events;

import org.disrupted.rumble.database.objects.ChatMessage;
import org.disrupted.rumble.network.events.NetworkEvent;
import org.disrupted.rumble.network.protocols.ProtocolChannel;

/**
 * @author Lucien Loiseau
 */
public class ChatMessageReceived extends NetworkEvent {

    public ChatMessage chatMessage;
    public ProtocolChannel channel;

    public ChatMessageReceived(ChatMessage chatMessage, ProtocolChannel channel) {
        this.chatMessage = chatMessage;
        this.channel = channel;
    }

    @Override
    public String shortDescription() {
        if(chatMessage != null)
            return chatMessage.getMessage()+" ("+chatMessage.getAuthor().getName()+")";
        else
            return null;
    }

}
