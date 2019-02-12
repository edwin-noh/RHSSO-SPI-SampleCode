package com.redhat.spi.sample.db;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.test.page.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

@RunWith(Arquillian.class)
public class ArquillianDBStorageTest {
	private static final String KEYCLOAK_URL = "http://%s:%s/auth%s";

    @Page
    private LoginPage loginPage;

    @Page
    private ConsolePage consolePage;

    @Drone
    private WebDriver webDriver;

    @ArquillianResource
    private URL contextRoot;

    @Deployment(testable = false)
    public static Archive<?> createTestArchive() throws IOException {
        return ShrinkWrap.create(JavaArchive.class, "user-storage-jpa-example.jar")
                .addClasses(DBUserStorageProvider.class, DBUserStorageProviderFactory.class,
                        UserAdapter.class, UserEntity.class)
                .addAsResource("META-INF/services/org.keycloak.storage.UserStorageProviderFactory")
                .addAsResource("META-INF/persistence.xml");

    }


    @Before
    public void setup() {
        webDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        navigateTo("/admin");
    }

    @Test
    public void testUserFederationStorageCreation() throws MalformedURLException, InterruptedException {
        try {
            waitTillElementPresent(By.id("username"));
            loginPage.login("admin", "admin");

            consolePage.navigateToUserFederationMenu();
            consolePage.selectUserStorage();
            consolePage.save();
            consolePage.navigateToUserFederationMenu();

            assertNotNull("Storage provider should be created", consolePage.exampleFederationStorageLink());
            consolePage.delete();

            navigateTo("/realms/master/account");
            assertEquals("Should display admin", "admin", consolePage.getUser());
            consolePage.logout();
        } catch (Exception e) {
            debugTest(e);
            fail("Should create a user federation storage");
        }
    }

    private void waitTillElementPresent(By locator) {
        Graphene.waitGui().withTimeout(60, TimeUnit.SECONDS).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }
    
    private void navigateTo(String path) {
        webDriver.navigate().to(format(KEYCLOAK_URL,
                contextRoot.getHost(), contextRoot.getPort(), path));
    }
    
    private void debugTest(Exception e) {
        System.out.println(webDriver.getPageSource());
        e.printStackTrace();
    }
}
