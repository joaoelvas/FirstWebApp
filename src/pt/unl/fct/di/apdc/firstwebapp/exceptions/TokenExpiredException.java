package pt.unl.fct.di.apdc.firstwebapp.exceptions;

public class TokenExpiredException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TokenExpiredException() {
		super();
	}
	
	public TokenExpiredException(String msg) {
		super(msg);
	}
}
