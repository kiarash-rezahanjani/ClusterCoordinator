package protocol;

public interface MessageType {
	
	short JOIN_ENSEMBLE_REQUEST = 0;
	short ACCEPTED_JOIN_ENSEMBLE_REQUEST = 1;
	short REJECTED_JOIN_ENSEMBLE_REQUEST = 2;
	
	short START_ENSEMBLE_CONNECTION = 3;
	short SUCEEDED_ENSEMBLE_CONNECTION = 4;
	short FAILED_ENSEMBLE_CONNECTION = 8;
	short START_SERVICE = 5;

	short LEAVING_ENSEMBLE = 6;
	
	short UNKNOWN_MESSAGE = 7;
	short OPERATION_FAILED = 9;
}
