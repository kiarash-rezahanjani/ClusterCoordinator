package protocol;

import java.net.InetSocketAddress;
import coordination.InterProcessCoordinator;
import coordination.InterProcessCoordinator.ServerStatus;
import rpc.udp.SenderReceiver;
import utility.Znode.ServerData;

public class Protocol implements ReceivedMessageCallBack {

	private SenderReceiver senderReceiver;
	InterProcessCoordinator cdrHandle;

	public Protocol(InterProcessCoordinator interProcessCoordinator) 
	{
		// TODO Auto-generated constructor stub
		senderReceiver = new SenderReceiver(this);
		this.cdrHandle = interProcessCoordinator;

	}

	public Protocol(InterProcessCoordinator interProcessCoordinator, int receiverServerport) 
	{
		// TODO Auto-generated constructor stub
		senderReceiver = new SenderReceiver(this, receiverServerport);
		this.cdrHandle = interProcessCoordinator;
	}

	@Override
	public void received(Object msg, InetSocketAddress srcSocketAddress) {
		// TODO Auto-generated method stub

		ProtocolMessage message = (ProtocolMessage)msg;


	}

	void processServerStatus(ProtocolMessage message)
	{
		//		Short s =cdrHandle.getStatus();
		Short statusHandle = cdrHandle.getStatusHandle();
		short msgType = message.getMessageType();
		
		synchronized(statusHandle)
		{
			switch(statusHandle.shortValue())
			{
	 
			case ServerStatus.ALL_FUNCTIONAL_ACCEPT_REQUEST:
				if(message.getMessageType()==MessageType.JOIN_ENSEMBLE_REQUEST )
					;
			//	if(message.getMessageType() == MessageType.LEAVING_ENSEMBLE)
			//		;
				break;
				//======================================================================================
			case ServerStatus.FORMING_ENSEMBLE_LEADER_STARTED: 

				break;
				//======================================================================================
			case ServerStatus.FORMING_ENSEMBLE_LEADER_WAIT_FOR_ACCEPT: 
				if(message.getMessageType()==MessageType.ACCEPTED_JOIN_ENSEMBLE_REQUEST )
					;
				
				if(message.getMessageType()==MessageType.REJECTED_JOIN_ENSEMBLE_REQUEST )
					;
				break;
			//======================================================================================
			case ServerStatus.FORMING_ENSEMBLE_LEADER_WAIT_FOR_CONNECTED_SIGNAL: 
				if(message.getMessageType()==MessageType.SUCEEDED_ENSEMBLE_CONNECTION )
					;
				if(message.getMessageType()==MessageType.FAILED_ENSEMBLE_CONNECTION )
					;
				break;
			//======================================================================================
			case ServerStatus.FORMING_ENSEMBLE_LEADER_EXEC_ROLL_BACK: 
				if(message.getMessageType()==MessageType.OPERATION_FAILED )
					;
				
				break;
			//======================================================================================
			case ServerStatus.FORMING_ENSEMBLE_NOT_LEADER_STARTED: 
				if(message.getMessageType()==MessageType.START_ENSEMBLE_CONNECTION )
					;
				break;
				//======================================================================================
			case ServerStatus.FORMING_ENSEMBLE_NOT_LEADER_CONNECTING: 
				if(message.getMessageType()==MessageType.START_SERVICE )
					;
				break;
			//======================================================================================
			case ServerStatus.FORMING_ENSEMBLE_NOT_LEADER_WAIT_FOR_START_SIGNAL: 
				if(message.getMessageType()==MessageType.START_ENSEMBLE_CONNECTION)
					;
				break;		
			//======================================================================================
			case ServerStatus.FORMING_ENSEMBLE_NOT_LEADER_ROLL_BACK_ALL_OPERATIONS: 
				if(message.getMessageType()==MessageType.OPERATION_FAILED)
					;
				break;
			//=======================DONE LATER===============================================================
			case ServerStatus.BROKEN_ENSEMBLE: 

				break; 
			//======================================================================================
			case ServerStatus.BROKEN_ENSEMBLE_FINDING_REPLACEMENT: break;
			//======================================================================================
			case ServerStatus.FIXING_ENSEMBLE_LEADER_WAIT_FOR_ACCEPT: break;
			//======================================================================================
			case ServerStatus.FIXING_ENSEMBLE_LEADER_WAIT_FOR_CONNECTED_SIGNAL: break;
			//======================================================================================

			case ServerStatus.FIXING_ENSEMBLE_NOT_LEADER_CONNECTING: break;
			//======================================================================================
			case ServerStatus.FIXING_ENSEMBLE_NOT_LEADER_WAIT_FOR_START_SIGNAL: break;
			//======================================================================================

			/*
			//later to be completed
			case I_AM_LEAVING_ENSEMBLE;
			case A_MEMBER_LEAVING_ENSEMBLE;
			case ALL_FUNCTIONAL_REJECT_REQUEST;
			case ALL_FUNCTIONAL_ACCEPT_REQUEST;
			 */

			//default: 
				
			}
		}
	}

}
