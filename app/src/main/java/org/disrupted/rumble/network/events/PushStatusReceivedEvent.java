package org.disrupted.rumble.network.events;

import org.disrupted.rumble.database.objects.PushStatus;

/**
 * This event holds every information known on a received transmission that happened successfully.
 * These information includes:
 *
 * - The received status (as it was received)
 * - The name of the author as 'claimed' by the sender (see CacheManager for policy)
 * - The name of the group as 'claimed' by the sender (see CacheManager for policy)
 * - The sender
 * - The protocol used to transmit this status (rumble, firechat)
 * - The link layer used (bluetooth, wifi)
 * - The size of the data transmitted (bytes)
 * - The duration of the transmission (ms)
 *
 * These information will be used by different component to update some informations :
 *  - The CacheManager to update its list and the neighbour's queue as well
 *  - The LinkLayerAdapter to update its internal metric that is used by getBestInterface
 *  - The FragmentStatusList to provide a visual feedback to the user
 *
 * @author Marlinski
 */
public class PushStatusReceivedEvent {

    public PushStatus status;
    public String author_name;
    public String group_name;
    public String sender;
    public String protocolID;
    public String linkLayerIdentifier;
    public long size;
    public long duration;

    public PushStatusReceivedEvent(PushStatus status, String author_name, String group_name, String sender, String protocolID, String linkLayerIdentifier, long size, long duration) {
        this.author_name = author_name;
        this.group_name = group_name;
        this.status = status;
        this.sender = sender;
        this.protocolID = protocolID;
        this.linkLayerIdentifier = linkLayerIdentifier;
        this.size = size;
        this.duration = duration;
    }

}