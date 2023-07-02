package sakancom;

public class Application {
	
	public boolean loggedIn;
	
	public Application() {
		
		loggedIn = false;
	}
	
	public boolean loggin(String name, String password) {
		
		if (name.equals("Amro") && password.equals("123")) return true;
		
		return false;
	}
}
