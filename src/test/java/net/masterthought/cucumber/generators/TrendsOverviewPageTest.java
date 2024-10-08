package net.masterthought.cucumber.generators;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import net.masterthought.cucumber.ReportBuilder;
import net.masterthought.cucumber.ReportResult;
import net.masterthought.cucumber.Trends;
import net.masterthought.cucumber.generators.integrations.PageTest;
import org.apache.velocity.VelocityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.powermock.reflect.Whitebox;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
class TrendsOverviewPageTest extends PageTest {

    private final String TRENDS_FILE = pathToSampleFile("cucumber-trends.json");
    private final String TRENDS_TMP_FILE = TRENDS_FILE + "-tmp";

    @BeforeEach
    void setUp() throws IOException {
        setUpWithJson(SAMPLE_JSON);
        // refresh the file if it was already copied by another/previous test
        Files.copy(Paths.get(TRENDS_FILE), Paths.get(TRENDS_TMP_FILE), StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    void getWebPage_ReturnsTrendsOverviewFileName() {

        // given
        page = new TrendsOverviewPage(reportResult, configuration, null);

        // when
        String fileName = page.getWebPage();

        // then
        assertThat(fileName).isEqualTo(TrendsOverviewPage.WEB_PAGE);
    }

    @Test
    void prepareReport_AddsCustomProperties() throws Exception {

        // given
        configuration.setBuildNumber("myBuild");
        Trends trends = Whitebox.invokeMethod(ReportBuilder.class, "loadTrends", new File(TRENDS_TMP_FILE));
        page = new TrendsOverviewPage(reportResult, configuration, trends);

        Whitebox.setInternalState(page, "reportResult", new ReportResult(features, configuration));

        // when
        page.prepareReport();

        // then
        VelocityContext context = page.context;
        assertThat(context.getKeys()).hasSize(24);

        assertThat(context.get("buildNumbers")).isEqualTo(new String[]{"01_first", "other build", "05last"});
        assertThat(context.get("failedFeatures")).isEqualTo(new int[]{1, 2, 5});
        assertThat(context.get("passedFeatures")).isEqualTo(new int[]{9, 18, 25});

        assertThat(context.get("failedScenarios")).isEqualTo(new int[]{10, 20, 20});
        assertThat(context.get("passedScenarios")).isEqualTo(new int[]{10, 20, 20});

        assertThat(context.get("passedSteps")).isEqualTo(new int[]{1, 3, 5});
        assertThat(context.get("failedSteps")).isEqualTo(new int[]{10, 30, 50});
        assertThat(context.get("skippedSteps")).isEqualTo(new int[]{100, 300, 500});
        assertThat(context.get("pendingSteps")).isEqualTo(new int[]{1000, 3000, 5000});
        assertThat(context.get("undefinedSteps")).isEqualTo(new int[]{10000, 30000, 50000});

        assertThat(context.get("durations")).isEqualTo(new long[]{3206126182398L, 3206126182399L, 3206126182310L});
    }
}
