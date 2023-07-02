package sakancom.test.definitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.junit.Assert;

import sakancom.Application;

public class LoginDefinition {

	private Application app;
	private boolean logginStatus;
	
	public LoginDefinition(Application app) {
		
		super();
		this.app = app;
	}
	
	
}
