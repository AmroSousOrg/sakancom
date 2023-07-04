package sakancom.test;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src\\test\\resources\\features\\login.feature",
        plugin = {"html:target\\HtmlReports\\loginReport.html"},
        glue = {"sakancom.test"},
        snippets = CucumberOptions.SnippetType.CAMELCASE,
        monochrome = true
)

public class LoginTest {
}
