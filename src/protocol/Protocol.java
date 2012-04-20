package protocol;

import java.net.InetSocketAddress;
import coordination.InterProcessCoordinator;
import coordination.InterProcessCoordinator.ServerStatus;
import rpc.udp.SenderReceiver;

public class Protocol implements ReceivedMessageCallBack {

	private SenderReceiver senderReceiver;
	InterProcessCoordinator cdrHandle;

	public Protocol(InterProcessCoordinator interProcessCoordinator) 
	{
		// TODO Auto-generated constructor stub
		this.cdrHandle = interProcessCoordinator;

	}

	public Protocol(InterProcessCoordinator interProcessCoordinator, int port) 
	{
		// TODO Auto-generated constructor stub
		this.cdrHandle = interProcessCoordinator;
	}

	@Override
	public void received(Object msg, InetSocketAddress srcSocketAddress) {
		// TODO Auto-generated method stub

	}

	synchronized void processServerStatus()
	{
		switch(cdrHandle.getStatus())
		{
		case ServerStatus.FORMING_ENSEMBLE_LEADER:
			break;
		case ServerStatus.FORMING_ENSEMBLE_NOT_LEADER:
			break;
		case ServerStatus.BROKEN_ENSEMBLE: 
			break;
		case ServerStatus.FIXING_ENSEMBLE_LEADER:
			break;
		case ServerStatus.FIXING_ENSEMBLE_NOT_LEADER: 
			break;
		case ServerStatus.I_AM_LEAVING_ENSEMBLE:
			break;
		case ServerStatus.A_MEMBER_LEAVING_ENSEMBLE:
			break;
		case ServerStatus.ALL_FUNCTIONAL_REJECT_REQUEST: 
			break;
		default:
		}

	}
}
