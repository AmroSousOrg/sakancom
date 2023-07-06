package sakancom.test;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src\\test\\resources\\features\\newAccount.feature",
        plugin = {"html:target\\HtmlReports\\newAccountReport.html"},
        glue = {"sakancom.test"},
        snippets = CucumberOptions.SnippetType.CAMELCASE,
        monochrome = true
)

public class NewAccountTest {
}
