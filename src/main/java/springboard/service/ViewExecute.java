package springboard.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

import springboard.model.JDBCTemplateDAO;
import springboard.model.SpringBoardDTO;

public class ViewExecute implements IBoardService {

	@Override
	public void execute(Model model) {
		//모델객체를 Map컬렉션으로 변환한 후 request내장객체를 가져온다.
		Map<String, Object> paramMap = model.asMap();
		HttpServletRequest req = 
				(HttpServletRequest)paramMap.get("req");
		
		//request내장객체를 통해 전달된 파라미터를 읽어온다.
		String idx= req.getParameter("idx");
		String nowPage = req.getParameter("nowPage");
		
		/*
		DAO객체 생성 및 DTO 객체를 생성한다. 
		view()메서드에서 select쿼리를 실행한 후 인출한 레코드를 DTO에 저장한 후 반환한다.
		*/
		JDBCTemplateDAO dao = new JDBCTemplateDAO();
		SpringBoardDTO dto = new SpringBoardDTO();
		//게시물의 일련번호를 인수로 전달하여 하나으 ㅣ레코드를 인출한다.
		dto = dao.view(idx);
		
		//내용은 줄바꿈처리를 위해 replace()를 호출한다.
		dto.setContents(dto.getContents().replace("\r\n", "<br/>"));
		model.addAttribute("viewRow", dto);
		model.addAttribute("nowPage", nowPage);
	}
}
