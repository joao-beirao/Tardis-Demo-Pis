package dcr.runtime.monitoring;

import com.datastax.oss.driver.shaded.guava.common.graph.Graph;

import java.util.List;

public interface GraphObserver {
    public void onUpdate(List<StateUpdate> update);
}
