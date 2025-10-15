package dcr.runtime.monitoring;

import dcr.runtime.elements.events.EventInstance;

public record EventUpdate(EventInstance event, StateUpdate.UpdateType updateType)
        implements StateUpdate {
}
