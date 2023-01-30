package springboard.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

import springboard.model.JDBCTemplateDAO;

public class PasswordActionExecute implements IBoardService {

	@Override
	public void execute(Model model) {
		
		//모델객체를 맵 컬렉션으로 변환한다.
		Map<String, Object> paramMap = model.asMap();
		HttpServletRequest req = 
				(HttpServletRequest)paramMap.get("req");
		
		//전송된 폼값을 request 내장객체로 받아온다.
		String mode= req.getParameter("mode");
		String idx= req.getParameter("idx");
		String nowPage= req.getParameter("nowPage");
		String pass= req.getParameter("pass");
		
		//DAO에서 일련번호와 패스워드를 통해 검증을 진행한다.
		JDBCTemplateDAO dao = new JDBCTemplateDAO();
		int existIdx = dao.password(idx,pass);
		
		/*
		DAO에서 일련번호와 패스워드를 통해 select한 게시물이 존재한다면
		1이상의 값이 반환되고, 만약 패스워드가 틀렷다면 0이 반환된다.
		이 값을 모델객체에 저장한다.
		*/
		model.addAttribute("existIdx", existIdx);
	}
}
