package springboard.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

import springboard.model.JDBCTemplateDAO;

public class DeleteActionExecute implements IBoardService {
	
	@Override
	public void execute(Model model) {
		
		//모든 요청을 한꺼번에 받아온다.
		Map<String, Object> map = model.asMap();
		HttpServletRequest req = (HttpServletRequest)map.get("req");
		
		//일련번호와 패스워드를 파라미터로 받아온다.
		String idx= req.getParameter("idx");
		String pass= req.getParameter("pass");
		
		//DAO의 delete() 메서드를 호출한다. 
		JDBCTemplateDAO dao = new JDBCTemplateDAO();
		dao.delete(idx, pass);
	}
}
