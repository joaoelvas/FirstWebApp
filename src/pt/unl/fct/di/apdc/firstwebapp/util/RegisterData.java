package pt.unl.fct.di.apdc.firstwebapp.util;

public class RegisterData {
	
	public String email;
	public String fixedPhone;
	public String mobilePhone;
	public String street;
	public String comp;
	public String local;
	public String cp;
	public String nif;
	public String cc;
	public String password;
	
	public RegisterData() {
		
	}
	
	public RegisterData(String email, String fixedPhone, String mobilePhone, String street, String comp, String local, String cp, String nif, String cc, String password) {
		this.email = email;
		this.fixedPhone = fixedPhone;
		this.mobilePhone = mobilePhone;
		this.street = street;
		this.comp = comp;
		this.local = local;
		this.cp = cp;
		this.nif = nif;
		this.cc = cc;
		this.password = password;
	}
}
