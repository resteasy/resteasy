package se.unlogic.standardutils.populators;


public class UnableToFindSuitablePopulatorException extends Exception {

	private static final long serialVersionUID = -214177380194928711L;

	public UnableToFindSuitablePopulatorException(String message, Throwable cause) {

		super(message, cause);
	}

	public UnableToFindSuitablePopulatorException(String message) {

		super(message);
	}

	public UnableToFindSuitablePopulatorException(Throwable cause) {

		super(cause);
	}

}
