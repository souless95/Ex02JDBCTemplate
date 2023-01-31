package springboard.util;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.support.ResourcePropertySource;

public class EnvFileReader {
	
	//static으로 정의한 메서드(클래스명으로 직접 접근이 가능함)
	public static String getValue(String envFile, String keyName) {
		
		//Spring 컨테이너 생성
		ConfigurableApplicationContext ctx = new GenericXmlApplicationContext();
		//Environment 객체 생성
		ConfigurableEnvironment env = ctx.getEnvironment();
		//PropertySources 객체 생성
		MutablePropertySources propertySources = env.getPropertySources();
		
		String envStr = "";
		
		try {
			//프로퍼티 파일명과 키값을 매개변수로 처리
			//프로퍼티 파일의 경로 설정
			String envPath = "classpath:"+envFile;
			//설정된 경로를 통해 파일을 읽어온다.
			propertySources.addLast(new ResourcePropertySource(envPath));
			//키값을 통해 속성값을 읽어온다.
			envStr = env.getProperty(keyName);
			System.out.println("프로퍼티값="+envStr);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return envStr;
	}
}
