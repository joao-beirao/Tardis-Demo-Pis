package dcr.runtime.communication;

import dcr.common.events.Event;
import dcr.common.events.userset.values.UserSetVal;
import dcr.common.events.userset.values.UserVal;

import java.util.Set;

public interface CommunicationLayer {
    Set<UserVal> uponSendRequest(UserVal requester, String eventId, UserSetVal receivers,
            Event.Marking marking,
            String uidExtension);
}
