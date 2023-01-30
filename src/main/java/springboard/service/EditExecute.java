package springboard.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

import springboard.model.JDBCTemplateDAO;
import springboard.model.SpringBoardDTO;

public class EditExecute implements IBoardService {
	
	@Override
	public void execute(Model model) {
		
		//모델객체에 저장된 request내장객체를 얻어온다. 
		Map<String, Object> paramMap = model.asMap();
		HttpServletRequest req = (HttpServletRequest)paramMap.get("req");
		
		//일련번호를 파라미터로 받은 후 
		String idx= req.getParameter("idx");
		
		//DAO에서 수정할 게시물을 인출한다.
		JDBCTemplateDAO dao = new JDBCTemplateDAO();
		SpringBoardDTO dto = dao.view(idx);
		//뷰로 전달한다.
		model.addAttribute("viewRow", dto);
		//dao.close();
	}
}
