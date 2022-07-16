import com.github.ubaifadhli.pages.medium.mobile.*;
import com.github.ubaifadhli.pages.medium.web.*;
import com.github.ubaifadhli.util.*;
import io.appium.java_client.AppiumDriver;
import io.github.bonigarcia.wdm.config.DriverManagerType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestRunner {
    private WebHomePage webHomePage;
    private WebLoginPage webLoginPage;
    private WebMembershipPage webMembershipPage;

    private MobileHomePage mobileHomePage;
    private MobileLoginPage mobileLoginPage;
    private MobileMembershipPage mobileMembershipPage;

    @DataProvider(name = "drivers", parallel = true)
    public Object[][] getDataProvider() {
        return new Object[][]{
                new Object[]{"WEB", DriverFactory.createWebDriver(DriverManagerType.CHROME)},
                new Object[]{"MOBILE", DriverFactory.createMobileDriver()},
        };
    }

    public boolean isCurrentPlatformWeb(String platform) {
        return platform.equals("WEB");
    }

    public boolean isCurrentPlatformMobile(String platform) {
        return platform.equals("MOBILE");
    }

    public String getPropertyByPlatform(String propertyName, String platform) {
        PropertiesReader reader = new PropertiesReader("application.properties");

        return reader.getPropertyAsString(platform.toLowerCase() + "." + propertyName);
    }

    @Test(dataProvider = "drivers")
    public void login(String platform, RemoteWebDriver platformDriver) {
        String username = getPropertyByPlatform("login.twitter.username", platform);
        String password = getPropertyByPlatform("login.twitter.password", platform);

        if (isCurrentPlatformWeb(platform)) {
            webHomePage = new WebHomePage(platformDriver);
            webLoginPage = new WebLoginPage(platformDriver);

            webHomePage.openPage();
            webHomePage.goToTwitterLoginPage();

            webLoginPage.fillTwitterLoginCredentials(username, password);
        }

        if (isCurrentPlatformMobile(platform)) {
            mobileHomePage = new MobileHomePage((AppiumDriver) platformDriver);
            mobileLoginPage = new MobileLoginPage((AppiumDriver) platformDriver);

            mobileHomePage.goToTwitterLoginPage();

            mobileLoginPage.fillTwitterLoginCredentials(username, password);
            mobileLoginPage.waitForHomeTitle();
        }
    }

    @Test(dataProvider = "drivers")
    public void searchForArticle(String platform, RemoteWebDriver platformDriver) {
        if (isCurrentPlatformWeb(platform)) {
            webHomePage = new WebHomePage(platformDriver);

            webHomePage.openPage();

            webHomePage.searchForArticle("spring boot");

            assertThat(webHomePage.getFirstArticleTitle(), equalTo("How to scale Microservices with Message Queues, Spring Boot, and Kubernetes"));
        }

        if (isCurrentPlatformMobile(platform)) {
            mobileHomePage = new MobileHomePage((AppiumDriver) platformDriver);

            mobileHomePage.searchForArticle("spring boot");

            assertThat(mobileHomePage.getFirstArticleTitle(), equalTo("How to scale Microservices with Message Queues, Spring Boot, and Kubernetes"));
        }
    }

    @Test(dataProvider = "drivers")
    public void userChecksMembershipOffering(String platform, RemoteWebDriver platformDriver) {
        if (isCurrentPlatformWeb(platform)) {
            webHomePage = new WebHomePage(platformDriver);
            webMembershipPage = new WebMembershipPage(platformDriver);

            webHomePage.openPage();

            webHomePage.goToMembershipPage();

            assertThat("wrong price",webMembershipPage.getMonthlySubsPrice() == 5.0);
            assertThat("wrong price",webMembershipPage.getAnnualSubsPrice() ==  50.0);
        }

        if (isCurrentPlatformMobile(platform)) {
            mobileHomePage = new MobileHomePage((AppiumDriver) platformDriver);
            mobileMembershipPage = new MobileMembershipPage((AppiumDriver) platformDriver);

            mobileHomePage.goToSettingsPage();
            mobileHomePage.goToMembershipPage();

            assertThat("wrong price", mobileMembershipPage.getMonthlySubsPrice() == 4.99);
            assertThat("wrong price", mobileMembershipPage.getAnnualSubsPrice() == 49.99);
        }
    }
}
