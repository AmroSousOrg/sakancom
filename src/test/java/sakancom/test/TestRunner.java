package sakancom.test;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        plugin = {"html:target/HtmlReports/testReport.html"},
        glue = {"sakancom.test"},
        snippets = CucumberOptions.SnippetType.CAMELCASE,
        monochrome = true
)

public class TestRunner {
}
