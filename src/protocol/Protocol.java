package protocol;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import coordination.InterProcessCoordinator;
import coordination.InterProcessCoordinator.ServerStatus;
import coordination.InterProcessCoordinator.Status;
import rpc.udp.SenderReceiver;
import utility.Znode.ServerData;

public class Protocol implements ReceivedMessageCallBack {

	private SenderReceiver senderReceiver;
	InterProcessCoordinator cdrHandle;

	LeaderBookKeeper lbk = new LeaderBookKeeper();
	FollowerBookkeeper fbk = new FollowerBookkeeper();


	public Protocol(InterProcessCoordinator interProcessCoordinator) 
	{
		// TODO Auto-generated constructor stub

		this.cdrHandle = interProcessCoordinator;
		senderReceiver = new SenderReceiver(this, cdrHandle.getConfigurationHandle().getProtocolPort());
	}

	//for testing only
	boolean leader=false;
	//InetSocketAddress destination;
	public Protocol(InterProcessCoordinator interProcessCoordinator, boolean leader) 
	{
		// TODO Auto-generated constructor stub

		this.cdrHandle = interProcessCoordinator;
		senderReceiver = new SenderReceiver(this, cdrHandle.getConfigurationHandle().getProtocolPort());
		this.leader=leader;
		if(leader)
			leaderStartsFormingEnsemble(3);
	}
	
	public LeaderBookKeeper getLeaderBookKeeperHandle()
	{
		return lbk;
	}

	public FollowerBookkeeper getFollowerBookkeeperHandle()
	{
		return fbk;
	}
	@Override 
	public void received(Object msg, InetSocketAddress srcSocketAddress) {
		// TODO Auto-generated method stub
		processMessage((ProtocolMessage)msg);
	}

	void processMessage(ProtocolMessage message)
	{
		//addStat();
		//System.out.println("OOOO");
		//		Short s =cdrHandle.getStatus();
		Status statusHandle = cdrHandle.getStatusHandle();
		//short msgType = message.getMessageType();
		//InetSocketAddress srcSocketAddress = message.getSrcSocketAddress();
		//System.out.println("Message recev contained des add: "+srcSocketAddress.toString());
		synchronized(statusHandle)
		{
			switch(statusHandle.getStatus())
			{

			case ServerStatus.ALL_FUNCTIONAL_ACCEPT_REQUEST:
				if(message.getMessageType()== MessageType.ACCEPTED_JOIN_ENSEMBLE_REQUEST)
					abortOperation(message.getSrcSocketAddress());
				
				addStat();
				followerAcceptRequest(message, statusHandle);
				addStat();

				//	if(message.getMessageType() == MessageType.LEAVING_ENSEMBLE)
				//		;
				break;
				//======================================================================================
			case ServerStatus.FORMING_ENSEMBLE_LEADER_STARTED: 
				if(message.getMessageType()== MessageType.ACCEPTED_JOIN_ENSEMBLE_REQUEST)
					abortOperation(message.getSrcSocketAddress());
					
				break;
				//======================================================================================
			case ServerStatus.FORMING_ENSEMBLE_LEADER_WAIT_FOR_ACCEPT: 
				addStat();
				leaderProcessWaitForAccept( message, statusHandle);
				addStat();


				break;
				//======================================================================================
			case ServerStatus.FORMING_ENSEMBLE_LEADER_WAIT_FOR_CONNECTED_SIGNAL: 
				if(message.getMessageType()== MessageType.ACCEPTED_JOIN_ENSEMBLE_REQUEST)
					abortOperation(message.getSrcSocketAddress());
				
				addStat();
				leaderWaitForConnectedSignal(message, statusHandle);
				addStat();
				printStattransition();
				break;
				//======================================================================================
			case ServerStatus.FORMING_ENSEMBLE_LEADER_EXEC_ROLL_BACK: 
				if(message.getMessageType()==MessageType.OPERATION_FAILED )
					;

				break;
				//======================================================================================
			case ServerStatus.FORMING_ENSEMBLE_NOT_LEADER_STARTED: 

				addStat();
				followerStartConnections(message, statusHandle);
				addStat();


				break;
				//======================================================================================
			case ServerStatus.FORMING_ENSEMBLE_NOT_LEADER_CONNECTING: 
				if(message.getMessageType()==MessageType.OPERATION_FAILED )
					;
				break;
				//======================================================================================
			case ServerStatus.FORMING_ENSEMBLE_NOT_LEADER_WAIT_FOR_START_SIGNAL:
				addStat();
				followerWaitForStartService(message, statusHandle);
				addStat();
				printStattransition();
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

			default: System.out.println("WHAT THE HELLL...."); System.exit(-1);

			}
		}
	}


	//------------------------------------------LEADER----------------------------------------
	void leaderStartsFormingEnsemble(int replicationFactor)
	{

		Status status = cdrHandle.getStatusHandle();

		synchronized(status)
		{
			//addStat();
			if(status.getStatus()!=ServerStatus.ALL_FUNCTIONAL_ACCEPT_REQUEST  
					&& status.getStatus()!=ServerStatus.FORMING_ENSEMBLE_LEADER_STARTED )
			{
				System.out.println("Formin ensemble while status.getStatus()!=ServerStatus.ALL_FUNCTIONAL_ACCEPT_REQUEST . ");
				System.exit(-1);
			}
			//addStat();
			if(!lbk.isEmpty())
			{
				System.out.println("An attemp is made to form ensemble and Leaderbook Keeper is not empty. ");
				System.exit(-1);
			}
			//addStat();
			//System.out.println("1");
			List<InetSocketAddress> candidates = cdrHandle.getSortedCandidates();//get candidates
			//System.out.println("2");
			System.out.println("Leader.My candidates: "+candidates);
			addStat();
			if(candidates.size() < replicationFactor-1)
			{
				System.out.println("candidates.size() < replicationFactor");
				System.exit(-1);
			}
			//addStat();
			lbk.setEnsembleSize(replicationFactor);
			lbk.addCandidateList(candidates);

			status.setStatus(ServerStatus.FORMING_ENSEMBLE_LEADER_WAIT_FOR_ACCEPT);
			//-1 : leader is also part of the chain
			for(int i=0 ; i<replicationFactor-1; i++)
			{
				joinRequest(candidates.get(i));
				lbk.putRequestedNode(candidates.get(i), false);
			}
			//addStat();
		}
	}

	void leaderFixEnsemble(int replicationFactor, List<InetSocketAddress> aliveNodes)
	{

	}

	void leaderProcessWaitForAccept(ProtocolMessage message, Status status)
	{
		if(message.getMessageType()== MessageType.ACCEPTED_JOIN_ENSEMBLE_REQUEST)
		{
			lbk.putAcceptedNode(message.getSrcSocketAddress(), true);

			//sufficient number of accept messages has been received
			if(lbk.isAcceptedComplete())
			{
				status.setStatus(ServerStatus.FORMING_ENSEMBLE_LEADER_WAIT_FOR_CONNECTED_SIGNAL);// need to check if there is enough capacity left

				List<InetSocketAddress> listOfEnsembleServers = new ArrayList<InetSocketAddress>();
				listOfEnsembleServers.addAll(lbk.getAcceptedList());

				//set failure detector
				for(InetSocketAddress protocolSocketAddress : listOfEnsembleServers)
					cdrHandle.getZkHandle().setServerFailureDetector(protocolSocketAddress);

				listOfEnsembleServers.add(0, senderReceiver.getServerSocketAddress());
				lbk.setEnsembleMembers(listOfEnsembleServers);

				for(InetSocketAddress sa : lbk.getAcceptedList())
					connectSignal(sa, listOfEnsembleServers);

			}
		}

		if(message.getMessageType()== MessageType.REJECTED_JOIN_ENSEMBLE_REQUEST)
			lbk.putAcceptedNode(message.getSrcSocketAddress(), false);

	}


	void leaderWaitForConnectedSignal(ProtocolMessage message, Status status){
		if(message.getMessageType()== MessageType.SUCEEDED_ENSEMBLE_CONNECTION)
		{
			lbk.putConnectedNode(message.getSrcSocketAddress(), true);

			if(lbk.isConnectedComplete())
			{
				status.setStatus(ServerStatus.ALL_FUNCTIONAL_ACCEPT_REQUEST);
				String ensemblePath = cdrHandle.leaderCreatesEnsemble(lbk.getEnsembleMembers());
				if(ensemblePath==null)
				{
					rollBack();
					return;
				}
				for(InetSocketAddress sa : lbk.getConnectedList())
				{
					startServiceSignal(sa, ensemblePath);
					//System.out.println("sending start sig to:"+ sa.toString());
				}
				cdrHandle.leaderStartsService(ensemblePath);
			}
		}
		if(message.getMessageType()== MessageType.FAILED_ENSEMBLE_CONNECTION)
			lbk.putConnectedNode(message.getSrcSocketAddress(), false);
	}

	void startConnecting(List<InetSocketAddress> ensembleMembers)
	{

	}

	//-------------------------------------------Follower----------------------------------	
	private void followerAcceptRequest(ProtocolMessage message,	Status statusHandle) 
	{
		if(message.getMessageType()==MessageType.JOIN_ENSEMBLE_REQUEST )
		{
			//check pointing the current status
			cdrHandle.getLastCheckpointedStatus().setStatus(statusHandle.getStatus());
			statusHandle.setStatus(ServerStatus.FORMING_ENSEMBLE_NOT_LEADER_STARTED);

			fbk.setLeader(message.getSrcSocketAddress());
			//set failure detector on leader
			cdrHandle.getZkHandle().setServerFailureDetector(message.getSrcSocketAddress());
			acceptJoinRequest(message.getSrcSocketAddress());
		}

		if(message.getMessageType()==MessageType.OPERATION_FAILED )
			rollBack();
	}

	void followerStartConnections(ProtocolMessage message, Status status)
	{		//testing
		try {
			Thread.sleep(5500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(message.getMessageType()==MessageType.START_ENSEMBLE_CONNECTION )
		{
			status.setStatus(ServerStatus.FORMING_ENSEMBLE_NOT_LEADER_CONNECTING);
			addStat();
			List<InetSocketAddress> ensembleMembers = (List<InetSocketAddress> ) message.msgContent;
			fbk.setEnsembleMembers(ensembleMembers);
			System.out.println( "Start Connecting to: " + ensembleMembers );		
			boolean success = cdrHandle.followerConnectsEnsemble(ensembleMembers);

			if(success)
			{			
				followerConnectedSignal(message.getSrcSocketAddress());
				status.setStatus(ServerStatus.FORMING_ENSEMBLE_NOT_LEADER_WAIT_FOR_START_SIGNAL);// need to check if there is enough capacity left
			}else
			{
				followerFailedConnectedSignal(message.getSrcSocketAddress());
				rollBack();
			}
		}

		if(message.getMessageType()==MessageType.OPERATION_FAILED )
			rollBack();
	}

	void followerWaitForStartService(ProtocolMessage message, Status statusHandle)
	{

		if(message.getMessageType()==MessageType.START_SERVICE)
		{
			String ensemblePath = message.getMsgContent().toString();
			System.out.println("FOLLOWER:"+ ensemblePath);
			if(ensemblePath==null || ensemblePath.length()==0)
			{
				System.out.println("followerWaitForStartService() ensemblepath null!");
				System.exit(-1);
			}

			fbk.setEnsemblePath(ensemblePath);
			cdrHandle.followerStartsService(ensemblePath);
			
			statusHandle.setStatus(ServerStatus.ALL_FUNCTIONAL_ACCEPT_REQUEST);// need to check if there is enough capacity left
			//signal the coordinator
		}

		if(message.getMessageType()==MessageType.OPERATION_FAILED )
			rollBack();
		
	}


	//-----------------------------------------------------------------------------------------	
	//for testing
	//senderReceiver.send(srcSocketAddress, new ProtocolMessage(MessageType.ACCEPTED_JOIN_ENSEMBLE_REQUEST));
	public void joinRequest(InetSocketAddress srcSocketAddress)
	{
		senderReceiver.send(srcSocketAddress, new ProtocolMessage(MessageType.JOIN_ENSEMBLE_REQUEST, " "));
	}

	public void acceptJoinRequest(InetSocketAddress srcSocketAddress)
	{
		senderReceiver.send(srcSocketAddress, new ProtocolMessage(MessageType.ACCEPTED_JOIN_ENSEMBLE_REQUEST, " "));
		//System.out.println("send accept join");
	}

	/**
	 * Signals a follower to start establishing the connections and creating required data structures for start an ensemble with given list of servers.
	 * @param srcSocketAddress
	 * @param list of servers in ensemble. the first element of the list is the leader of ensemble.
	 */
	public void connectSignal(InetSocketAddress srcSocketAddress, List<InetSocketAddress> list)
	{
		senderReceiver.send(srcSocketAddress, new ProtocolMessage(MessageType.START_ENSEMBLE_CONNECTION, list));
	}

	public void followerConnectedSignal(InetSocketAddress srcSocketAddress)
	{
		senderReceiver.send(srcSocketAddress, new ProtocolMessage(MessageType.SUCEEDED_ENSEMBLE_CONNECTION,  " "));
	}

	public void followerFailedConnectedSignal(InetSocketAddress srcSocketAddress)
	{
		senderReceiver.send(srcSocketAddress, new ProtocolMessage(MessageType.FAILED_ENSEMBLE_CONNECTION,  " "));
	}

	public void startServiceSignal(InetSocketAddress srcSocketAddress, String ensemblePath)
	{
		senderReceiver.send(srcSocketAddress, new ProtocolMessage(MessageType.START_SERVICE, ensemblePath));
	}

	public void abortOperation(InetSocketAddress srcSocketAddress)
	{
		senderReceiver.send(srcSocketAddress, new ProtocolMessage(MessageType.OPERATION_FAILED,  " "));
	}

	//for testing
	List<Short> statTransition = new ArrayList<Short>();
	void addStat()
	{
		statTransition.add(cdrHandle.getStatusHandle().getStatus());
	}
	//For testing
	void printStattransition()
	{
		System.out.println("Me:" + senderReceiver.getServerSocketAddress()+ " Leader:" + leader + " Status:" + statTransition );
	}

	/**
	 * 	Might have to think harder rolling back the status , maybe checkpoint the status before forming an ensemble
	 *  If already connection are established and we receive cancel then other data structure have to e garbage collected
	 */
	public void rollBack()
	{
		addStat();
		//if I am the leader send abort operation to all the followers
		if(cdrHandle.isLeader()==true)
		{
			for(InetSocketAddress follower : lbk.getAcceptedList())
				abortOperation(follower);
		}
		addStat();
		printStattransition();
		lbk.clear();
		fbk.clear();
		//recover the latest status before start of operation 
		cdrHandle.getStatusHandle().setStatus(cdrHandle.getLastCheckpointedStatus().getStatus());


	}
}
