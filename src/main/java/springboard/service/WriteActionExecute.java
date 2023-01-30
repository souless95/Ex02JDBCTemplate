package springboard.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

import springboard.model.JDBCTemplateDAO;
import springboard.model.SpringBoardDTO;

//서비스 객체의 부모인 인터페이스를 구현한다.
public class WriteActionExecute implements IBoardService {
	
	//추상메서드를 필수 오버라이딩한다.
	@Override
	public void execute(Model model) {
		/*
		Java에서 학습했던 asList(컬렉션에서 학습함) 메서드와 같이
		asMap() 메서드는 Model객체에 저장된 값들을 key로 쉽게
		가져오기 위해 Map컬렉션으로 변환한다.
		*/
		Map<String, Object> paramMap = model.asMap();
		//request내장객체와 DTO객체를 key로 접근하여 가져온다.
		//그리고 사용을 위해 형변환(다운캐스팅)을 진행한다.
		HttpServletRequest req = (HttpServletRequest)paramMap.get("req");
		SpringBoardDTO SpringBoardDTO = (SpringBoardDTO)paramMap.get("SpringBoardDTO");
		//제목 정도만 콘솔에서 확인해본다.(디버깅용)
		System.out.println("SpringBoardDTO.title="+SpringBoardDTO.getTitle());
		
		//폼값을 테이블에 입력하기 위해 DAO객체를 생성한다.
		JDBCTemplateDAO dao = new JDBCTemplateDAO();
		//insert쿼리 실행을 위해 write()메서드를 호출한다.
		int affected = dao.write(SpringBoardDTO);
		System.out.println("입력된 결과:"+affected);
		//Spring-JDBC에서는 자원해제를 하지 않는다.
		//dao.close();
	}
}
