package springboard.service;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

import springboard.model.JDBCTemplateDAO;
import springboard.model.SpringBoardDTO;

/*
IBoardService 인터페이스를 구현했으므로 execute()메서드는 반드시 오버라이딩 해야한다.
또한 해당 객체는 부모객체인 IBoardService 객체로 참조할 수 있다.
*/
public class ListExecute implements IBoardService {
	
	@Override
	public void execute(Model model) {
		System.out.println("ListCommand > execute() 호출");
		
		/*
		컨트롤러에서 인수로 전달한 Model객체에는 request객체가 저장되어 있다.
		asMap() 메서드를 통해 Map컬렉션으로 변환한 후 모든 요청을 얻어올 수 있다.
		*/
		Map<String, Object> paramMap = model.asMap();
		/*
		Model객체에 저장될때는 Object타입으로 변환되어 저장되므로,
		사용을 위해 원래의 타입으로 형변환 해야한다.
		Model객체는 4가지 영역과 동일한 특성을 가진다.
		*/
		HttpServletRequest req = (HttpServletRequest)paramMap.get("req");
		//DAO객체 생성
		JDBCTemplateDAO dao = new JDBCTemplateDAO();
		
		//검색어 처리
		String addQueryString = "";
		//request 내장객체를 통해 파라미터를 받아온다.
		String searchColumn = req.getParameter("searchColumn");
		String searchWord = req.getParameter("searchWord");
		//만약 검색어가 있다면..
		if(searchWord!=null && searchWord!="") {
			//쿼리스트링 형태의 문자열을 생성한다.
			addQueryString = String.format("searchColumn=%s"+
						"&searchWord=%s&", searchColumn, searchWord);
			//Map컬렉션에 2개의 폼값을 저장한다.
			paramMap.put("Column", searchColumn);
			paramMap.put("Word", searchWord);
		}
		
		//전체 게시물 갯수 카운트
		int totalRecordCount = dao.getTotalCount(paramMap);
		
		//View에 출력할 레코드 가져오기(페이지 처리 없음)
		ArrayList<SpringBoardDTO> listRows = dao.list(paramMap);
		
		//출력할 게시물에 가상번호를 추가한다.
		int virtualNum = 0;
		int countNum = 0;
		//DAO에서 반환된 List컬렉션을 반복하여 데이터를 가공한다.
		for(SpringBoardDTO row : listRows) {
			//전체게시물 객수에서 하나씩 차감하여 가상번호를 부여한다.
			virtualNum = totalRecordCount --;
			//가상번호를 setter를 통해 DTO에 저장한다.
			row.setVirtualNum(virtualNum);
		}
		//View로 전달하기 위해 Model객체에 저장한다.
		model.addAttribute("listRows",listRows);
		
		/*
		JdbcTemplate을 사용할때는 자원반납은 하지 않는다.
		스프링 컨테이너가 시작될때 자동으로 연결되므로 자원이 반납되면 다시 연결할 수 없기 때문이다. 
		자원관리는 스프링 컨테이너가 알아서 해준다.
		*/
		//dao.close();
	}
}
