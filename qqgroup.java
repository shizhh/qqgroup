package com.selenium.qqgroup;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.os.WindowsUtils;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class qqgroup {

	public static final String BASE_URL = "http://w.qq.com/";
	
	public static void main(String[] args) throws InterruptedException {
		//format time
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		//kill thread
		WindowsUtils.tryToKillByName("chromedriver.exe");
		//set property 
		System.setProperty("webdriver.chrome.driver", ".\\res\\chromedriver.exe");
		//open chrome
		final WebDriver dr = new ChromeDriver();
		
		//加载插件firefox
    	/*WindowsUtils.tryToKillByName("firefox.exe");
		System.setProperty("webdriver.firefox.bin", "C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe");
		File file = new File(".\\res\\firebug-1.12.6-fx.xpi");
		FirefoxProfile firefoxProfile = new FirefoxProfile();
		try {
			firefoxProfile.addExtension(file);
		}catch (IOException e){
			e.printStackTrace();
		}
		firefoxProfile.setPreference("extensions.firebug.currentVersion","1.12.6");
		WebDriver dr = new FirefoxDriver(firefoxProfile);*/
		
		//maximize the window
		dr.manage().window().maximize();
		//访问qq
		dr.get(BASE_URL);
		//隐性等待(全局)1s
		dr.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		
		dr.switchTo().frame(0);
		
//		qqLogin(dr, "userName", "password");
		qqLogin(dr, "userName", "password");
//		qqLogin(dr, "userName", "password");
		
		WebDriverWait wait = new WebDriverWait(dr,10);
//		wait.until(ExpectedConditions.elementToBeClickable(By.id("contact")));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("contact"))); //会出错
//		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("contact")));

//		Thread.sleep(10000);	
		//点击联系人
		dr.findElement(By.id("contact")).click();  //会出错
		//点击群
		dr.findElement(By.cssSelector("[param=\"group\"]")).click();
		
		WebElement group = dr.findElement(By.cssSelector("[id=\"g_list\"]"));
//		WebElement group = dr.findElement(By.cssSelector("div[id=\"group_list_scroll_area\"]"));
		
		List<WebElement> grouplists = group.findElements(By.cssSelector("p[class=\"member_nick\"]"));
		
		System.out.println("grouplists="+grouplists.size());
		
		System.out.println("该用户有以下群：");
		
		/*for (WebElement e : grouplists){
			System.out.println("####################");
			System.out.println(e.getText());
		}*/
		
		for(int i=0;i<grouplists.size();i++){
			WebElement groupmember =group.findElement(By.xpath("//ul[@id=\"g_list\"]/li["+(i+1)+"]"));
			((JavascriptExecutor) dr).executeScript("arguments[0].scrollIntoView();", groupmember);
			System.out.println("---------------------");
			System.out.println("第"+(i+1)+"个群: "+grouplists.get(i).getText());
		}
		
		//begin with n
		int n = 0;
		int N = grouplists.size();//total N groups grouplists.size()
//		group.findElement(By.xpath("//ul[@id=\"g_list\"]/li["+(n+1)+"]")).click();
		
		long[] num_save = new long[N];//save num of Msgs
		for (int i = 0; i<N; i++){
			num_save[i]= 1L;
		}
//		long num = 1L;
		while(true){
			lock.lock();
			try {
				//改变当前要检索的群,从第一个开始
				WebElement groupElement =group.findElement(By.xpath("//ul[@id=\"g_list\"]/li["+(n+1)+"]"));
				((JavascriptExecutor) dr).executeScript("arguments[0].scrollIntoView();", groupElement);
				groupElement.click();
				askStop = false;
			} finally{
				lock.unlock();
				new Timer().start();
			}
			long num = num_save[n];
			while(Continue())
			{
				if (ifHaveNewMsgs(dr, num)) {
					boolean found = true;
					int trytimes = 1;
					while (found && trytimes < 10) {
						try {
							WebElement chat = dr.findElement(By.xpath("//div[@id=\"panelBody-5\"]/div[p]["+num+"]"));
							System.out.println("第"+(n+1)+"个群的消息");
							System.out.print("num="+num+";");
							System.out.print(time.format(new Date())+" text:");
							String chat_nick = chat.findElement(By.className("chat_nick")).getText();
							String chat_content = chat.findElement(By.className("chat_content")).getText();
							System.out.print(chat_nick+": ");
							System.out.println(chat_content);
							found = false;
						}catch(NoSuchElementException e) {
							System.out.print("num="+num+";");
							System.out.println("%%%%%%%%%% for N E, Sorry , we can not get this chat content! %%%%%%%");
							trytimes += 1;
						}catch(StaleElementReferenceException ex){
							System.out.print("num="+num+";");
							System.out.println("%%%%%%%%%% for S E, Sorry , we can not get this chat content! %%%%%%%");
							trytimes += 1;
						}
					}//while(found && trytimes)
					num += 1;
				}//if
				else {
	//				System.out.println("trytimes="+trytimes);
	//				trytimes += 1;
	//				Thread.sleep(500);
//					System.out.println(time.format(new Date()));
				}
			}//while(true)
			//save num
			num_save[n] = num;
			n=(n+1)%N;
		}
	}//main
	
	/*static private boolean isElementPresent(WebDriver driver,By by){
		try {
			driver.findElement(by);
			return true;
		}catch (Exception e ){
			System.out.println("Element is not present!!!!");
			return false;
		}
	}*/
	
	static private ReentrantLock lock= new ReentrantLock();
	static private boolean askStop = false;
	static private  boolean Continue(){
		boolean rtn = true;
		lock.lock();
		try {
			if(askStop == true){
				rtn = false;
			}
		} finally{
			lock.unlock();
		}
		return rtn;
	}
	static public void timeout(){
		lock.lock();
		try {
			askStop = true;
		} finally {
			lock.unlock();
		}
	}
	static private boolean ifHaveNewMsgs(WebDriver dl,long count)
	{
		try{
			dl.findElement(By.xpath("//div[@id=\"panelBody-5\"]/div[p]["+count+"]/p[@class=\"chat_nick\"]")).getText();
			return true;
		}catch(NoSuchElementException e){
			return false;
		}catch(StaleElementReferenceException e1){
			return false;
		}
	}
	
	static private void qqLogin (WebDriver dr,String user,String passwd){
		//输入账号密码
		dr.findElement(By.id("u")).sendKeys(user);
		dr.findElement(By.id("p")).sendKeys(passwd);
		
		//设置状态
		dr.findElement(By.cssSelector("div#loginState")).click();
		dr.findElement(By.cssSelector("li[state=\"silent\"]")).click();
		//手动处理验证码问题,等待3s时间
		try {
			Thread.sleep(0000);
		} catch (InterruptedException e1) {
			System.out.println("验证码出错！！！！");
		}
		//click the button
		dr.findElement(By.cssSelector("#login_button")).click();
	}
	
}
