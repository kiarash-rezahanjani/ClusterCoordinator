package protocol;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.zookeeper.data.Stat;

import coordination.InterProcessCoordinator;
import coordination.InterProcessCoordinator.ServerStatus;
import coordination.InterProcessCoordinator.Status;
import rpc.udp.SenderReceiver;
import utility.Configuration;
import utility.Znode.ServerData;

public class Protocol implements ReceivedMessageCallBack {

	private SenderReceiver senderReceiver;
	InterProcessCoordinator cdrHandle;

	LeaderBookKeeper lbk ;//= new LeaderBookKeeper();
	FollowerBookkeeper fbk ;//= new FollowerBookkeeper();
	Configuration config;
	
	public Protocol(Configuration config, InterProcessCoordinator interProcessCoordinator, LeaderBookKeeper lbk, FollowerBookkeeper fbk) {
		
		this.config = config;
		this.lbk = lbk;
		this.fbk = fbk;
		this.cdrHandle = interProcessCoordinator;
		senderReceiver = new SenderReceiver(config, this);
	}

	//for testing only
	boolean leader=false;
	//InetSocketAddress destination;
	public Protocol(Configuration config, InterProcessCoordinator interProcessCoordinator, boolean leader, LeaderBookKeeper lbk, FollowerBookkeeper fbk) 
	{
		this.config = config;
		this.lbk = lbk;
		this.fbk = fbk;
		this.cdrHandle = interProcessCoordinator;
		senderReceiver = new SenderReceiver(config, this);
		this.leader=leader;
		if(leader)
			leaderStartsFormingEnsemble(4);
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
		System.out.println("From: " + message.getSrcSocketAddress().getPort() + " To: " + senderReceiver.getServerSocketAddress().getPort() 
				+ " MsgType: " + message.getMessageType() + " Server: " + cdrHandle.getStatusHandle().getStatus());
		//addStat();
		//System.out.println("OOOO");
		//		Short s =cdrHandle.getStatus();
		Status statusHandle = cdrHandle.getStatusHandle();
		//short msgType = message.getMessageType();
		//InetSocketAddress srcSocketAddress = message.getSrcSocketAddress();
		//System.out.println("Message recev contained des add: "+srcSocketAddress.toString());
	//	synchronized(statusHandle)
		{
			switch(statusHandle.getStatus())
			{

			case ServerStatus.ALL_FUNCTIONAL_ACCEPT_REQUEST:
				if(message.getMessageType()== MessageType.ACCEPTED_JOIN_ENSEMBLE_REQUEST)
					abortOperation(message.getSrcSocketAddress());

				addStat();
				followerAcceptRequest(message, statusHandle);
				addStat();

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
				//printStattransition();
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
				//printStattransition();
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

	//	synchronized(status){
			if(status.getStatus()!=ServerStatus.ALL_FUNCTIONAL_ACCEPT_REQUEST  
					&& status.getStatus()!=ServerStatus.FORMING_ENSEMBLE_LEADER_STARTED ){
				System.out.println("Formin ensemble while status.getStatus()!=ServerStatus.ALL_FUNCTIONAL_ACCEPT_REQUEST . ");
				System.exit(-1);
			}

			if(!lbk.isEmpty()){
				System.out.println("An attemp is made to form ensemble and Leaderbook Keeper is not empty. ");
				System.exit(-1);
			}
			
			List<InetSocketAddress> candidates = cdrHandle.getSortedCandidates();//get candidates

			if(candidates.size() < replicationFactor-1){
				System.out.println("candidates.size() < replicationFactor");
				System.exit(-1);
			}
			//addStat();
			lbk.setEnsembleSize(replicationFactor);
			lbk.addCandidateList(candidates);
			addStat();
			status.setStatus(ServerStatus.FORMING_ENSEMBLE_LEADER_WAIT_FOR_ACCEPT);
			addStat();
			//-1 : leader is also part of the chain
			for(int i=0 ; i<replicationFactor-1; i++){
				joinRequest(candidates.get(i));
				Stat stat = cdrHandle.getZkHandle().setServerFailureDetector(candidates.get(i));
				//System.out.println(senderReceiver.getServerSocketAddress()+" SET DETECTOR ON 1 " + candidates.get(i));
				if(stat != null)
					lbk.putRequestedNode(candidates.get(i), false);
			}

			if(lbk.getRequestedNodeList().size() < lbk.getEnsembleSize()-1)
				rollBack("leaderStartsFormingEnsemble  lbk.getRequestedNodeList().size() < lbk.getEnsembleSize()-1");
	//	}
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

				/**
				 * set failure detector
				
				for(InetSocketAddress protocolSocketAddress : listOfEnsembleServers)
				{	
					Stat stat = cdrHandle.getZkHandle().setServerFailureDetector(protocolSocketAddress);
					if(stat==null)
					{
						//rollBack();
						return;
					}
				}
				 */
				listOfEnsembleServers.add(0, senderReceiver.getServerSocketAddress());
				lbk.setEnsembleMembers(listOfEnsembleServers);

				for(InetSocketAddress sa : lbk.getAcceptedList())
					connectSignal(sa, listOfEnsembleServers);
			}
		}

		if(message.getMessageType()== MessageType.REJECTED_JOIN_ENSEMBLE_REQUEST){
			lbk.putAcceptedNode(message.getSrcSocketAddress(), false);
			if(!lbk.waitForNextAcceptedMessage())
				rollBack("leaderProcessWaitForAccept !lbk.waitForNextAcceptedMessage()");
		}

	}


	void leaderWaitForConnectedSignal(ProtocolMessage message, Status status){
	//	System.out.println("leaderWaitForConnectedSignal: Rec Status:"+ message.getMessageType());

		if(message.getMessageType()== MessageType.SUCEEDED_ENSEMBLE_CONNECTION){
			lbk.putConnectedNode(message.getSrcSocketAddress(), true);

			if(lbk.isConnectedComplete()){
				addStat();
				status.setStatus(ServerStatus.ALL_FUNCTIONAL_ACCEPT_REQUEST);
				addStat();
				printStattransition();
				String ensemblePath = cdrHandle.leaderCreatesEnsemble(lbk.getEnsembleMembers());
				if(ensemblePath==null){
					rollBack("leaderWaitForConnectedSignal ensmble node creation failed");
					return;
				}
				for(InetSocketAddress sa : lbk.getConnectedList()){
					startServiceSignal(sa, ensemblePath);
					//System.out.println("sending start sig to:"+ sa.toString());
				}
				cdrHandle.leaderStartsService(ensemblePath);
			}
		}

		if(message.getMessageType()== MessageType.FAILED_ENSEMBLE_CONNECTION){
			lbk.putConnectedNode(message.getSrcSocketAddress(), false);
			if(!lbk.waitForNextConnectedMessage())
				rollBack("leaderWaitForConnectedSignal !lbk.waitForNextConnectedMessage()");
		}
	}

	void startConnecting(List<InetSocketAddress> ensembleMembers)
	{

	}

	//-------------------------------------------Follower----------------------------------	
	private void followerAcceptRequest(ProtocolMessage message,	Status statusHandle) 
	{
		try {//testing
			Thread.sleep(5500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(message.getMessageType()==MessageType.JOIN_ENSEMBLE_REQUEST )
		{
			//check pointing the current status
			cdrHandle.getLastCheckpointedStatus().setStatus(statusHandle.getStatus());
			statusHandle.setStatus(ServerStatus.FORMING_ENSEMBLE_NOT_LEADER_STARTED);

			fbk.setLeader(message.getSrcSocketAddress());
			//set failure detector on leader
			Stat stat = cdrHandle.getZkHandle().setServerFailureDetector(fbk.getLeader());
		//	System.out.println(senderReceiver.getServerSocketAddress()+" SET DETECTOR ON 2 " + fbk.getLeader());
			if(stat==null){
				rollBack("followerAcceptRequest leadreDEATH");
				return;
			}

			acceptJoinRequest(message.getSrcSocketAddress());
		}

		if(message.getMessageType()==MessageType.OPERATION_FAILED )
			rollBack("followerAcceptRequest OPERATION_FAILED");

	}

	void followerStartConnections(ProtocolMessage message, Status status)
	{

		if(message.getMessageType()==MessageType.START_ENSEMBLE_CONNECTION ){
			status.setStatus(ServerStatus.FORMING_ENSEMBLE_NOT_LEADER_CONNECTING);
			addStat();
			List<InetSocketAddress> ensembleMembers = (List<InetSocketAddress> ) message.msgContent;

			//System.out.println( "Follower Start Connecting to: " + ensembleMembers );		
			boolean success = cdrHandle.followerConnectsEnsemble(ensembleMembers);

			if(success){	
				fbk.setEnsembleMembers(ensembleMembers);	
				followerConnectedSignal(message.getSrcSocketAddress());
				status.setStatus(ServerStatus.FORMING_ENSEMBLE_NOT_LEADER_WAIT_FOR_START_SIGNAL);// need to check if there is enough capacity left
			}else{
				followerFailedConnectedSignal(message.getSrcSocketAddress());
				rollBack("followerWaitForStartService    failed = cdrHandle.followerConnectsEnsemble(ensembleMembers");
				return;
			}
		}

		if(message.getMessageType()==MessageType.OPERATION_FAILED )
			rollBack("followerStartConnections OPERATION_FAILED");
	}

	void followerWaitForStartService(ProtocolMessage message, Status statusHandle)
	{
		if(message.getMessageType()==MessageType.START_SERVICE){
			String ensemblePath = message.getMsgContent().toString();
			//System.out.println("FOLLOWER:"+ ensemblePath);

			if(ensemblePath==null || ensemblePath.length()==0){
				System.out.println("followerWaitForStartService() ensemblepath null!");
				System.exit(-1);
			}

			fbk.setEnsemblePath(ensemblePath);
			cdrHandle.followerStartsService(ensemblePath);
			statusHandle.setStatus(ServerStatus.ALL_FUNCTIONAL_ACCEPT_REQUEST);// need to check if there is enough capacity left
			//signal the coordinator
			addStat();
			printStattransition();
		}

		if(message.getMessageType()==MessageType.OPERATION_FAILED )
			rollBack("followerWaitForStartService OPERATION_FAILED");
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
	public void rollBack(String message)
	{
		Status status = cdrHandle.getStatusHandle();
		System.out.println("Attemp to rollback : " + message + " MYServer:" + senderReceiver.getServerSocketAddress() +"passed port: "+ cdrHandle.getConfigurationHandle().getProtocolPort());
		synchronized(status){
			System.out.println("Passed in to rollback");
			//if(cdrHandle.getStatusHandle().getStatus())
		//	System.out.println("ROLLBACKing"+cdrHandle.getConfigurationHandle().getProtocolPort());
			addStat();
			//if I am the leader send abort operation to all the followers
			if(cdrHandle.isLeader()==true){
				for(InetSocketAddress follower : lbk.getRequestedNodeList())
					abortOperation(follower);
			}
			//recover the latest status before start of operation 
			//	System.out.println("InitialState:"+cdrHandle.getLastCheckpointedStatus().getStatus());
			status.setStatus(cdrHandle.getLastCheckpointedStatus().getStatus());
			//	System.out.println("InitialState:"+cdrHandle.getLastCheckpointedStatus().getStatus());
			addStat();
			printStattransition();
		//	System.out.println("ROLLBACKED "+ senderReceiver.getServerSocketAddress());

			lbk.clear();
			fbk.clear();
		}
	}

	public enum FailurePoint{
		SENDING_JOIN_REQUEST,
		WAIT_FOR_ACCEPT
	}
}
