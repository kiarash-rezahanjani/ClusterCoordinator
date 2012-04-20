package coordination;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class EventWatcher implements Watcher {

	InterProcessCoordinator coordinator ;
	
	public EventWatcher(InterProcessCoordinator coordinator)
	{
		this.coordinator = coordinator;
	}

	@Override
	public void process(WatchedEvent event) {
		// TODO Auto-generated method stub
		
	}
	


}
