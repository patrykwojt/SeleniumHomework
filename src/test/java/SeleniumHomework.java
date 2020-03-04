import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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

    @Before
    public void setUp() {
        driver = new ChromeDriver();
        url = "http://automationpractice.com";
    }

    @Test
    public void autoShopping() {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        Actions actions = new Actions(driver);

        driver.manage().window().maximize();

        driver.get(url);

        // Pobranie ceny produktu nr 7 //
        String product7Price = driver.findElements(By.cssSelector(".price" + ".product-price")).get(13).getText();

        // Najechanie myszka na obszar produktu, by uwidocznic dodanie do koszyka    //
        // (krok potrzebny w przypadku okna przegladarki wiekszego niz 1366x768 px)  //
        WebElement product7Img = driver.findElement(By.xpath("//a[contains(@href, 'id_product=7')]"));
        actions.moveToElement(product7Img).build().perform();

        // Dodanie produktu do koszyka //
        WebElement product7 = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@data-id-product='7']")));
        product7.click();

        // Kontynuacja zakupow //
        WebElement goOn = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//*[@title='Continue shopping']")));
        goOn.click();

        // Analogicznie dla kolejnego produktu, zaczynajac od waita //
        WebElement product4Img = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(@href, 'id_product=4')]")));
        String product4Price = driver.findElements(By.cssSelector(".price" + ".product-price")).get(7).getText();
        actions.moveToElement(product4Img).build().perform();

        WebElement product4 = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@data-id-product='4']")));
        product4.click();

        wait.until(ExpectedConditions.elementToBeClickable(goOn));
        goOn.click();

        // Dodanie trzeciego produktu, tym razem poprzez wejscie na podstrone produktu oraz zmiane koloru i rozmiaru //
        WebElement product2Img = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(@href, 'id_product=2')]")));
        String product2Price = driver.findElements(By.cssSelector(".price" + ".product-price")).get(3).getText();
        actions.moveToElement(product2Img).build().perform();

        WebElement product2More = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.linkText("More")));
        product2More.click();

        WebElement product2Color = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("color_8")));
        product2Color.click();

        Select dropList = new Select(driver.findElement(By.id("group_1")));
        dropList.selectByIndex(2);

        driver.findElement(By.id("add_to_cart")).click();

        WebElement checkOut = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@title='Proceed to checkout']")));
        checkOut.click();

        List<WebElement> trashIconList = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".icon-trash")));

        // Odczyt cen produktow w rozwijanym koszyku po najechaniu na niego kursorem           //
        // Dla poprawnego pobrania moveToElement okazuje sie konieczne przed kazdym pobraniem  //
        WebElement dropCart = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//*[@title='View my shopping cart']")));
        actions.moveToElement(dropCart).build().perform();
        List<WebElement> pricesList = driver.findElements(By.cssSelector(".price"));
        String product7PriceDrop = pricesList.get(0).getText();
        actions.moveToElement(dropCart).build().perform();
        String product4PriceDrop = pricesList.get(1).getText();
        actions.moveToElement(dropCart).build().perform();
        String product2PriceDrop = pricesList.get(2).getText();

        // Odczyt cen produktow na stronie podsumowania //
        String product7PriceCart = pricesList.get(13).getText();
        String product4PriceCart = pricesList.get(15).getText();
        String product2PriceCart = pricesList.get(18).getText();

        // Weryfikacja cen, zdecydowalem sie na 'if', poniewaz metoda Verify loguje tylko error, a metoda Assert zatrzymuje skrypt //
        if (product7Price.equals(product7PriceDrop)) {
            System.out.println("Cena w koszyku rozwijanym dla 1 produktu zgadza sie");
        } else {
            System.out.println("Cena w koszyku rozwijanym dla 1 produktu nie zgadza sie");
        }
        if (product4Price.equals(product4PriceDrop)) {
            System.out.println("Cena w koszyku rozwijanym dla 2 produktu zgadza sie");
        } else {
            System.out.println("Cena w koszyku rozwijanym dla 2 produktu nie zgadza sie");
        }
        if (product2Price.equals(product2PriceDrop)) {
            System.out.println("Cena w koszyku rozwijanym dla 3 produktu zgadza sie");
        } else {
            System.out.println("Cena w koszyku rozwijanym dla 3 produktu nie zgadza sie");
        }

        // Weryfikacja cen na stronie podsumowania
        if (product7Price.equals(product7PriceCart)) {
            System.out.println("Cena w podsumowaniu dla 1 produktu zgadza sie");
        } else {
            System.out.println("Cena w podsumowaniu dla 1 produktu nie zgadza sie");
        }
        if (product4Price.equals(product4PriceCart)) {
            System.out.println("Cena w podsumowaniu dla 2 produktu zgadza sie");
        } else {
            System.out.println("Cena w podsumowaniu dla 2 produktu nie zgadza sie");
        }
        if (product2Price.equals(product2PriceCart)) {
            System.out.println("Cena w podsumowaniu dla 3 produktu zgadza sie");
        } else {
            System.out.println("Cena w podsumowaniu dla 3 produktu nie zgadza sie");
        }

        // Czyszczenie koszyka
        for (int i = 2; i >= 0; i--) {
            trashIconList.get(i).click();
        }

        Assert.assertTrue(driver.getPageSource().contains("Your shopping cart is empty."));
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}