/*
 * Copyright (C) 2014 Disrupted Systems
 *
 * This file is part of Rumble.
 *
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
 * You should have received a copy of the GNU General Public License
 * along with Rumble.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.disrupted.rumble.network.protocols.rumble.packetformat;

import org.disrupted.rumble.network.linklayer.LinkLayerConnection;
import org.disrupted.rumble.network.linklayer.exception.InputOutputStreamException;
import org.disrupted.rumble.network.protocols.rumble.packetformat.exceptions.MalformedBlock;
import org.disrupted.rumble.network.protocols.rumble.packetformat.exceptions.MalformedBlockPayload;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Marlinski
 */
public abstract class Block {

    protected BlockHeader  header;

    protected Block(BlockHeader header) {
        this.header = header;
    }

    protected final BlockHeader getHeader() {
        return header;
    }

    public abstract long readBlock(LinkLayerConnection con) throws MalformedBlockPayload, IOException, InputOutputStreamException;

    public abstract long writeBlock(LinkLayerConnection con) throws IOException, InputOutputStreamException;

    public abstract void dismiss();
}
