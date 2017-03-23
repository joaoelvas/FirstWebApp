package pt.unl.fct.di.apdc.firstwebapp.util;

public class RegisterData {
	
	public String username;
	public String password;
	public String email;
	public String name;
	
	public RegisterData() {
		
	}
	
	public RegisterData(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public RegisterData(String username, String password, String email, String name) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.name = name;
	}
}
