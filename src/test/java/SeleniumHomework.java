import org.hamcrest.CoreMatchers;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class SeleniumHomework {

    private WebDriver driver;
    private String url;
    private Actions actions;
    private WebDriverWait wait;

    @Before
    public void setUp() {
        driver = new ChromeDriver();
        url = "http://automationpractice.com";
        wait = new WebDriverWait(driver, 10);
        actions = new Actions(driver);
    }

    private String getInitialPrice(int id) {
        // Pobranie ceny produktu o zadanym 'id' ze strony glownej //
        int j = (id*2) - 1;
        List<WebElement> initialPricesList = driver.findElements(By.cssSelector(".price.product-price"));
        return initialPricesList.get(j).getText();
    }

    private void addProductToCart(int id) {
        // Dodanie do koszyka produktu o zadanym 'id'                                //
        // Najechanie myszka na obszar produktu, by uwidocznic dodanie do koszyka    //
        // (krok potrzebny w przypadku okna przegladarki wiekszego niz 1366x768 px)  //
        WebElement productImg = driver.findElement(By.xpath("//a[contains(@href, 'id_product=" + id + "')]"));
        actions.moveToElement(productImg).build().perform();

        WebElement product = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@data-id-product='" + id + "']")));
        product.click();
    }

    private void addProductToCartWithOptions(@SuppressWarnings("SameParameterValue")
                                                     int id) {
        // Dodanie produktu o zadanym 'id' poprzez wejscie na podstrone oraz zmiane koloru i rozmiaru //
        WebElement productImg = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//a[contains(@href, 'id_product=" + id + "')]")));
        actions.moveToElement(productImg).build().perform();

        WebElement productMore = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//a[contains(@href, 'id_product=" + id + "') and (@title='View')]")));
        productMore.click();

        WebElement productColor = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("color_8")));
        productColor.click();

        Select dropList = new Select(driver.findElement(By.id("group_1")));
        dropList.selectByIndex(2);

        driver.findElement(By.id("add_to_cart")).click();
    }

    private void continueShopping() {
        // Kontynuacja zakupow //
        WebElement goOn = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//*[@title='Continue shopping']")));
        goOn.click();
    }

    private void finishShopping() {
        // Zakonczenie zakupow i przejscie do podsumowania //
        WebElement checkOut = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@title='Proceed to checkout']")));
        checkOut.click();
    }

    private String getPriceOnSummaryPage(int i) {
        // Odczyt ceny produktu w rozwijanym koszyku po najechaniu na niego kursorem //
        // Lub ceny produktu ze strony podsumowania                                  //
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".icon-trash")));

        WebElement dropCart = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//*[@title='View my shopping cart']")));
        actions.moveToElement(dropCart).build().perform();

        List<WebElement> pricesList = driver.findElements(By.cssSelector(".price"));
        return pricesList.get(i).getText();
    }


    private void clearShoppingCart(@SuppressWarnings("SameParameterValue")
                                           int numberOfProducts) {
        // Czyszczenie koszyka na podstawie ilosci dodanych produktow //
        List<WebElement> trashIconList = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".icon-trash")));

        for (int i = numberOfProducts - 1; i >= 0; i--) {
            trashIconList.get(i).click();
        }
    }

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    @Test
    public void autoShopping() {

        driver.manage().window().maximize();

        driver.get(url);

        String product7Price = getInitialPrice(7);
        String product4Price = getInitialPrice(4);
        String product2Price = getInitialPrice(2);

        addProductToCart(7);
        continueShopping();

        addProductToCart(4);
        continueShopping();

        addProductToCartWithOptions(2);
        finishShopping();

        String product7PriceDrop = getPriceOnSummaryPage(0);
        String product4PriceDrop = getPriceOnSummaryPage(1);
        String product2PriceDrop = getPriceOnSummaryPage(2);

        String product7PriceCart = getPriceOnSummaryPage(13);
        String product4PriceCart = getPriceOnSummaryPage(15);
        String product2PriceCart = getPriceOnSummaryPage(18);

        collector.checkThat("Cena w koszyku rozwijanym dla 1 produktu nie zgadza sie"
                ,product7Price, CoreMatchers.equalTo(product7PriceDrop));
        collector.checkThat("Cena w koszyku rozwijanym dla 2 produktu nie zgadza sie"
                ,product4Price, CoreMatchers.equalTo(product4PriceDrop));
        collector.checkThat("Cena w koszyku rozwijanym dla 3 produktu nie zgadza sie"
                ,product2Price, CoreMatchers.equalTo(product2PriceDrop));

        collector.checkThat("Cena w podsumowaniu dla 1 produktu nie zgadza sie"
                ,product7Price, CoreMatchers.equalTo(product7PriceCart));
        collector.checkThat("Cena w podsumowaniu dla 2 produktu nie zgadza sie"
                ,product4Price, CoreMatchers.equalTo(product4PriceCart));
        collector.checkThat("Cena w podsumowaniu dla 3 produktu nie zgadza sie"
                ,product2Price, CoreMatchers.equalTo(product2PriceCart));

        clearShoppingCart(3);

        Assert.assertTrue(driver.getPageSource().contains("Your shopping cart is empty."));
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}