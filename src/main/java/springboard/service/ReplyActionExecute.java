package springboard.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

import springboard.model.JDBCTemplateDAO;
import springboard.model.SpringBoardDTO;

public class ReplyActionExecute implements IBoardService {
	
	@Override
	public void execute(Model model) {
		
		//모델객체를 통해 컨트롤러가 전달한 모든 요청을 받아온다.
		Map<String, Object> paramMap = model.asMap();
		HttpServletRequest req = (HttpServletRequest)paramMap.get("req");
		SpringBoardDTO dto = (SpringBoardDTO)paramMap.get("SpringBoardDTO");
		
		//DAO로 모든 폼값을 인수로 전달한다.
		JDBCTemplateDAO dao = new JDBCTemplateDAO();
		dao.reply(dto);
		//dao.close();
	}
}
