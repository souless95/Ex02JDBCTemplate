package springboard.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

import springboard.model.JDBCTemplateDAO;
import springboard.model.SpringBoardDTO;

public class ReplyExecute implements IBoardService {

	@Override
	public void execute(Model model) {
		//모델객체를 Map컬렉션으로 변환한 후 request내장객체를 가져온다.
		Map<String, Object> map = model.asMap();
		HttpServletRequest req = (HttpServletRequest)map.get("req");
		
		//일련번호를 파라미터로 받아온다.
		String idx= req.getParameter("idx");
		
		//DAO의 view()메서드를 통해 게시물을 인출한다.
		JDBCTemplateDAO dao = new JDBCTemplateDAO();
		SpringBoardDTO dto = dao.view(idx);
		
		/*
		원본글에 대한 답변글을 작성하는 폼이므로
		제목에는 [Re]를 추가하고, 내용에는 [원본글]을 추가해준다.
		추가한 내용을 setter()를 통해 저장한다.
		*/
		dto.setTitle("[RE]"+dto.getTitle());
		dto.setContents("\n\r\n\r---[원본글]---\n\r"+dto.getContents());
		
		//모델객체에 저장한다.
		model.addAttribute("replyRow", dto);
		//dao.close();
	}
}
