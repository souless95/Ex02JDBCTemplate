package springboard.service;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

import springboard.model.JDBCTemplateDAO;
import springboard.model.SpringBoardDTO;
import springboard.util.EnvFileReader;
import springboard.util.PagingUtil;

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
		
		//출력할 게시물에 가상번호를 추가한다.
		int virtualNum = 0;
		int countNum = 0;
		
		/* 페이지 처리 Start */
		//Environment객체를 이용해서 외부파일을 읽어온다.
		//게시판에서 한페이지에 출력할 게시물수, 한블럭당 표시할 페이지번호갯수
		int pageSize = Integer.parseInt(EnvFileReader.getValue("SpringBbsInit.properties", "springBoard.pageSize"));
		int blockPage = Integer.parseInt(EnvFileReader.getValue("SpringBbsInit.properties", "springBoard.blockPage"));
		
		//전체페이지수를 계산한다.(전체 게시물수/페이지당 갯수)
		int totalPage = (int)Math.ceil((double)totalRecordCount/pageSize);
		
		//파라미터로 전달되는 페이지번호를 읽어온다.
		int nowPage = req.getParameter("nowPage")==null ? 1 : Integer.parseInt(req.getParameter("nowPage"));
		
		//출력할 게시물의 구간을 계산한다.
		int start = (nowPage-1) * pageSize + 1;
		int end = nowPage * pageSize;
		//계산된 값을 Map에 저장한다.(차후 Model로 전달할 것임)
		paramMap.put("start", start);
		paramMap.put("end", end);
		/* 페이지 처리 End */

		//View에 출력할 레코드 가져오기(페이지 처리 없음)
		ArrayList<SpringBoardDTO> listRows = dao.listPage(paramMap);

		//페이지 처리 없는 경우(#paging X)
		//전체게시물 객수에서 하나씩 차감하여 가상번호를 부여한다.
		//virtualNum = totalRecordCount --;
		
		//DAO에서 반환된 List컬렉션을 반복하여 데이터를 가공한다.
		for(SpringBoardDTO row : listRows) {
			
			
			//페이지 처리 있는 경우(#paging O)
			//현재페이지를 적용해서 가상번호를 계산한다.
			virtualNum = totalRecordCount-(((nowPage-1)*pageSize)+ countNum++);

			//가상번호를 setter를 통해 DTO에 저장한다.
			row.setVirtualNum(virtualNum);
			
			//답변게시물에 대한 들여쓰기와 블릿 처리
			String reSpace ="";
			//bindent가 0보다 크다면 답변글을 대상으로 한다.
			if(row.getBindent() > 0) {
				//bindent의 값만큼 반복한다.
				for(int i=0; i<row.getBindent(); i++) {
					//들여쓰기 처리한다.
					reSpace += "&nbsp;&nbsp;";
				}
				//답변글 블릿을 제목앞에 공백과 함께 추가한다.
				row.setTitle(reSpace+
						"<img src='../images/re3.gif'>"
						+row.getTitle());
			}
		}
		/*페이지 처리 관련 속성값 저장 Start*/
		String pagingImg = PagingUtil.pagingImg(totalRecordCount, pageSize, blockPage, nowPage, req.getContextPath()+"/board/list.do?"+addQueryString);;
		
		model.addAttribute("pagingImg",pagingImg);
		model.addAttribute("totalPage",totalPage); //전체 페이지수
		model.addAttribute("nowPage",nowPage); //현재 페이지 번호
		//View로 전달하기 위해 Model객체에 저장한다.
		model.addAttribute("listRows",listRows);
		/*페이지 처리 관련 속성값 저장 End*/
		
		/*
		JdbcTemplate을 사용할때는 자원반납은 하지 않는다.
		스프링 컨테이너가 시작될때 자동으로 연결되므로 자원이 반납되면 다시 연결할 수 없기 때문이다. 
		자원관리는 스프링 컨테이너가 알아서 해준다.
		*/
		//dao.close();
	}
}
