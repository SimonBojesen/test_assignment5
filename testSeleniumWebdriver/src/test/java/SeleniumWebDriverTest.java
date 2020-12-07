import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SeleniumWebDriverTest {
    final static String url = "https://sqengineer.com/practice-sites/practice-tables-selenium/";
    static WebDriver webDriver;
    static Logger logger = Logger.getLogger(SeleniumWebDriverTest.class);

    @BeforeAll
    public void init() {
        System.setProperty("webdriver.gecko.driver", "geckodriver.exe");
        FirefoxOptions options = new FirefoxOptions();
        options.setHeadless(true);
        webDriver = new FirefoxDriver(options);
    }

    @BeforeEach
    public void setup() {
        webDriver.get(url);
    }

    @Test
    void mustPrintAllRowDataTable1() {
        var NoOfRows = webDriver.findElements(By.xpath("//*[@id='table1']/tbody/tr")).size();
        var expected = "Selenium, Open Source, https://www.seleniumhq.org/, UFT, Commercial, Unified Functional Tester, Ranorex, Commercial, https://www.ranorex.com/, TestComplete, Commercial, Test Complete";
        var actual = "";
        System.out.println("Starting the iteration from i=2 so as to exclude header");
        for (var i = 2; i <= NoOfRows; i++) {
            var col1 = webDriver.findElement(By.xpath("//*[@id='table1']/tbody/tr[" + i + "]/td[1]")).getText();
            var col2 = webDriver.findElement(By.xpath("//*[@id='table1']/tbody/tr[" + i + "]/td[2]")).getText();
            var col3 = webDriver.findElement(By.xpath("//*[@id='table1']/tbody/tr[" + i + "]/td[3]")).getText();
            var rowData = col1 + ", " + col2 + ", " + col3;
            System.out.println("Row " + i + " data: " + rowData);
            if (actual.equals("")) {
                actual += rowData;
            } else {
                actual += ", " + rowData;
            }
        }
        assertEquals(expected, actual);
    }

    private static Stream rownumberAndExpectedRowdata() {
        return Stream.of(
                Arguments.of(2, "Selenium, Open Source, https://www.seleniumhq.org/"),
                Arguments.of(3, "UFT, Commercial, Unified Functional Tester"),
                Arguments.of(4, "Ranorex, Commercial, https://www.ranorex.com/"),
                Arguments.of(5, "TestComplete, Commercial, Test Complete")
        );
    }

    @ParameterizedTest
    @MethodSource("rownumberAndExpectedRowdata")
    void mustPrintAllRowDataTable1Parameterized(int rownumber, String expected) {
        var col1 = webDriver.findElement(By.xpath("//*[@id='table1']/tbody/tr[" + rownumber + "]/td[1]")).getText();
        var col2 = webDriver.findElement(By.xpath("//*[@id='table1']/tbody/tr[" + rownumber + "]/td[2]")).getText();
        var col3 = webDriver.findElement(By.xpath("//*[@id='table1']/tbody/tr[" + rownumber + "]/td[3]")).getText();
        var actual = col1 + ", " + col2 + ", " + col3;
        assertEquals(expected, actual);
    }

    @Test
    void mustClickFirstLink() {
        var expected = "https://www.selenium.dev/";
        webDriver.findElement(By.xpath("//*[@id='table1']/tbody/tr/td[3]")).click();
        assertEquals(expected, webDriver.getCurrentUrl());
    }

    private static Stream indexAndUrl() {
        return Stream.of(
                Arguments.of(1, "https://www.selenium.dev/"),
                Arguments.of(2, "https://www.microfocus.com/en-us/products/uft-one/overview"),
                Arguments.of(3, "https://www.ranorex.com/"),
                Arguments.of(4, "https://smartbear.com/product/testcomplete/overview/")
        );
    }

    @ParameterizedTest
    @MethodSource("indexAndUrl")
    void mustClickAllLinksTable1(int index, String url) {
        webDriver.findElement(By.xpath(String.format("//*[@id='table1']/descendant::a[%s]", index))).click();
        assertEquals(url, webDriver.getCurrentUrl());
    }

    private static Stream tableRow_RowIndex_SportName() {
        return Stream.of(
                Arguments.of(1, 1, "Table Tennis"),
                Arguments.of(3, 1, "Badminton"),
                Arguments.of(1, 3, "Cricket"),
                Arguments.of(5, 3, "Volley ball")
        );
    }

    private static Stream index_SportName() {
        return Stream.of(
                Arguments.of(1, "Table Tennis"),
                Arguments.of(8, "Badminton"),
                Arguments.of(3, "Cricket"),
                Arguments.of(15, "Volley ball")
        );
    }

    @ParameterizedTest
    @MethodSource("tableRow_RowIndex_SportName")
    void mustPrintSportNameTable2V1(int table_row, int row_index, String sport_name) {
        String actual = webDriver.findElement(By.xpath("//*[@id='table2']/tbody/tr["+ table_row +"]/td["+ row_index +"]")).getText();
        System.out.println(actual);

        assertEquals(sport_name, actual);
    }

    @ParameterizedTest
    @MethodSource("index_SportName")
    void mustPrintSportNameTable2V2(int index, String sport_name) {
        String actual = webDriver.findElement(By.xpath(String.format("//*[@id='table2']/descendant::td[%s]", index))).getText();
        System.out.println(actual);

        assertEquals(sport_name, actual);
    }

    @Test
    void mustFindCatVideoUploadedToday(){
        //LoggerConfig
        PropertyConfigurator.configure("log4j.properties");

        //Gets url "funny cat videos" and wait for loading
        webDriver.get("https://www.youtube.com/results?search_query=funny+cat+videos");
        System.out.println("Loading...");
        WebDriverWait wait = new WebDriverWait(webDriver, 20);
        wait.until(webDriver -> ((JavascriptExecutor)webDriver).executeScript("return document.readyState").equals("complete"));
        System.out.println("...done!");

        //click on "filter" and then on "today"
        webDriver.findElement(By.xpath("//ytd-toggle-button-renderer/a/paper-button/yt-formatted-string")).click();
        webDriver.findElement(By.xpath("(//div[@id='label']/yt-formatted-string)[2]")).click();

        //Get all eager loaded a element on the page (always 22 elements)
        List<WebElement> catvids = webDriver.findElements(By.xpath("//a[@id='video-title']"));
        System.out.println(catvids.size());

        List<String> actual = new ArrayList<>();
        //take the first 10 and log them to logs/cats.log
        for (int i = 0; i < catvids.size(); i++) {
            try {
                actual.add(catvids.get(i).getAttribute("href"));
                logger.info(catvids.get(i).getAttribute("href"));
                if (actual.size() == 10) break;
            } catch(StaleElementReferenceException ex){
                ex.printStackTrace();
            }
        }

        assertEquals(10, actual.size());
    }
}