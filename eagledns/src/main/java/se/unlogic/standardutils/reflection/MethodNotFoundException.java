package se.unlogic.standardutils.reflection;


public class MethodNotFoundException extends Exception {

	private static final long serialVersionUID = -8580739778092215878L;

	public MethodNotFoundException() {

		super();
	}

	public MethodNotFoundException(String message, Throwable cause) {

		super(message, cause);
	}

	public MethodNotFoundException(String message) {

		super(message);
	}

	public MethodNotFoundException(Throwable cause) {

		super(cause);
	}
}
