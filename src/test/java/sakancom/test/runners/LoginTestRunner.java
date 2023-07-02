package sakancom.test.runners;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.CucumberOptions.SnippetType;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = "src\\test\\resources\\features",
		plugin = {"html:target\\HtmlReports\\loginReport.html"},
		monochrome = true, 
		snippets = SnippetType.CAMELCASE,
		glue = {"sakancom.test.runners"})

public class LoginTestRunner {
}
