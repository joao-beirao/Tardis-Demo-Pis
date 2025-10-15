package dcr.runtime.communication;

import dcr.common.events.userset.values.UserVal;
import pt.unl.fct.di.novasys.network.data.Host;

public interface MembershipLayer {

    interface Neighbour {
        UserVal user();

        String hostName();

        Host host();

        default String role() {
           return  user().role();
        }

    }

    void onNeighborUp(Neighbour neighbourUp);

    void onNeighborDown(Neighbour neighbourDown);


}
