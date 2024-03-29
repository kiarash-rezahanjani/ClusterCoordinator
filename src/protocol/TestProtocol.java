package protocol;

import java.net.InetSocketAddress;

import coordination.InterProcessCoordinator;

public class TestProtocol {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		InterProcessCoordinator co1 = new InterProcessCoordinator();
		InterProcessCoordinator co2 = new InterProcessCoordinator();
		System.out.println("PROTOCOL 1.");
		Protocol p1 = new Protocol(co1, 1111, false);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("PROTOCOL 2.");
		Protocol p2 = new Protocol(co2, 2222, true);
		p2.joinRequest(new InetSocketAddress("localhost",1111));
	}

}
