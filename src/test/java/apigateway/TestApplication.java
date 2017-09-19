package apigateway;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestApplication {
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		Demo demo = (Demo) context.getBean("demo");
		demo.setName("zs");
		demo.setPass("234");
		
		System.out.println(demo);
	}

}
