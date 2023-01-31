package springboard.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import springboard.model.JdbcTemplateConst;
import springboard.model.SpringBoardDTO;
import springboard.service.DeleteActionExecute;
import springboard.service.EditActionExecute;
import springboard.service.EditExecute;
import springboard.service.IBoardService;
import springboard.service.ListExecute;
import springboard.service.PasswordActionExecute;
import springboard.service.ReplyActionExecute;
import springboard.service.ReplyExecute;
import springboard.service.ViewExecute;
import springboard.service.WriteActionExecute;

/*
기본패키지로 설정한 곳에 컨트롤러를 선언하면 클라이언트의 요청이 들어왔을때
Auto Scan된다. 이를 통해 요청을 전달할 메서드를 찾는다.
해당 설정은 servlet-context.xml에서 추가한다.
*/
@Controller
public class BoardController {
	
	/*
	@Autowired
	: 스프링 컨테이너에 미리 생성되어있는 빈을 자동으로 주입받을 때 사용한다.
	자료형을 기반으로 자동주입되므로 만약 JdbcTemplate 타입의 빈이 생성되어 있지 않다면 에러가 발생하게 된다.
	해당 어노테이션은 setter(), 멤버변수 등에서 사용할 수 있다.
	*/
	private JdbcTemplate template;
	
	@Autowired
	public void setTemplate(JdbcTemplate template) {
		//멤버변수에 자동주입 받은 빈을 할당한다.
		this.template = template;
		//로그를 통해 자동주입 된 것을 확인한다.
		System.out.println("@Autowired=>JdbcTemplate 연결성공");
		/*
		template을 다른 클래스에서 사용하기 위해 static으로 선언한 변수에 할당한다.
		static(정적)변수는 프로그램 시작시 로딩되어 객체생성없이 클래스명 만으로 접근할 수 있는 특징이 있다.
		*/
		JdbcTemplateConst.template = this.template;
	}
	
	/* 멤버변수로 선언하여 클래스에서 전역적으로 사용할 수 있다.
	게시판 구현을 위한 모든 Service객체는 해당 인터페이스를 구현하여
	정의할 것이다.
	*/
	IBoardService service = null;
	
	@RequestMapping("/board/list.do")
	public String list(Model model, HttpServletRequest req ) {
		
		/*
		사용자로부터 받은 모든 요청은 request객체에 저장되고, 
		이를 ListExecute객체(Service객체)로 전달되기 위해 Model객체에
		저장한 후 매개변수로 전달한다.
		*/
		model.addAttribute("req",req);
		//Service 객체를 생성한다.
		service = new ListExecute();
		//Service 객체로 Model을 전달한다.
		service.execute(model);
		
		return "07Board/list";
	}
	
	//글쓰기 페이지에 대한 매핑 처리
	@RequestMapping("/board/write.do")
	public String write(Model model) {
		return "07Board/write";
	}
	
	/*
	글쓰기 처리에 대해 매핑처리를 한다. 전송방식은 post로 전송시 요청명은 value 속성에 기술한다.
	*/
	@RequestMapping(value="/board/writeAction.do",
			method=RequestMethod.POST)
	public String writeAction(Model model, HttpServletRequest req,
			SpringBoardDTO SpringBoardDTO) {
		/*
		글쓰기 페이지에서 전송된 모든 폼값은 DTO객체를 통해 한꺼번에 받을 수 있다. Spring에서 이와 같은 객체를 '커맨드객체'라고 한다.
		따라서 폼값을 받기위해 getParmeter()메서드를 일일히 사용할 필요가 없어진다.
		*/
		//request내장객체를 모델객체에 저장
		model.addAttribute("req", req);
		//DTO객체를 모델객체에 저장
		model.addAttribute("SpringBoardDTO", SpringBoardDTO);
		
		//service객체를 생성한 후 Model객체를 인수로 전달한다.
		service = new WriteActionExecute();
		service.execute(model);
		
		//뷰를 반환하지 않고, 지정된 URL(경로)로 이동한다.
		return "redirect:list.do?nowPage=1";
	}
	//내용보기
	@RequestMapping("/board/view.do")
	public String view(Model model, HttpServletRequest req) {
		/*
		목록에서 제목을 클릭할때 전달되는 파라미터는 idx, apgeNum등을
		저장한 request내장객체를 모델객체에 저장한다.
		*/
		model.addAttribute("req", req);
		//서비스 객체를 생성한 후 모델객체를 전달한다.
		service = new ViewExecute();
		service.execute(model);
		
		return "07Board/view";
	}
	
	@RequestMapping("/board/password.do")
	public String password(Model model, HttpServletRequest req) {
		/*
		파라미터로 전달된 값 중에서 일련번호만 request내장객체를 통해 받은 후 모델객체에 저장한다.
		나머지 값은 View에서 직접 EL을 통해 받을 것이다.
		*/
		model.addAttribute("idx",req.getParameter("idx"));
		return "07Board/password";
	}
	//패스워드 검증을 진행한 후 수정 혹은 삭제처리를 한다.
	@RequestMapping("/board/passwordAction.do")
	public String passwordAction(Model model, HttpServletRequest req) {
		//폼값을 저장한 request객체를 모델객체에 저장한 후 서비스 객체를 호출한다.
		model.addAttribute("req", req);
		service = new PasswordActionExecute();
		service.execute(model);
		
		//모델객체를 Map컬렉션으로 변환한다.
		Map<String, Object> paramMap = model.asMap();
		//idx는 정수이므로 Integer로 형변환 후 변수에 저장한다.
		int existIdx = (Integer)paramMap.get("existIdx");
		//패스워드 검증이 완료된 게시물의 idx값을 로그에 출력한다.
		System.out.println("existIdx="+existIdx);
		
		//전송된 파라미터를 받아온다.
		String mode = req.getParameter("mode");
		String idx = req.getParameter("idx");
		//패스워드 검증 후 반환할 뷰의 경로를 저장할 변수 선언
		String modePage = null;
		if(existIdx<=0) {
			//패스워드 검증에 실패했다면, 메세지를 모델객체에 저장한다.
			model.addAttribute("isCorrMsg", "패스워드가 일치하지 않습니다");
			model.addAttribute("idx", idx);
			//패스워드 페이지로 포워드한다.
			modePage = "07Board/password";
		}
		else {
			/* 패스워드 검증이 완료되었다면 수정의 경우 수정페이지를
			포워드하고, 삭제의 경우에는 즉시 게시물을 삭제 처리한다. */
			System.out.println("검증완료");
			
			if(mode.equals("edit")) {
				//수정인 경우 서비스객체로 모델객체를 전달하고..
				model.addAttribute("req", req);
				service = new EditExecute();
				//수정페이지를 포워드한다.
				service.execute(model);
				
				modePage = "07Board/edit";
			}
			
			else if(mode.equals("delete")) {
				//삭제인 경우 게시물을 즉시 삭제한다.
				model.addAttribute("req", req);
				service = new DeleteActionExecute();
				service.execute(model);	
				
				model.addAttribute("nowPage", req.getParameter("nowPage"));
				modePage = "redirect:list.do";
			}
		}
		return modePage;
	}
	//수정처리
	@RequestMapping("/board/editAction.do")
	public String editAction(Model model, HttpServletRequest req, SpringBoardDTO SpringBoardDTO) {
		//폼값을 저장한 request객체를 모델객체에 저장한 후 서비스 객체를 호출한다.
		model.addAttribute("req", req);
		model.addAttribute("SpringBoardDTO", SpringBoardDTO);
		
		//서비스 객체 생성
		service = new EditActionExecute();
		service.execute(model);
		
		/*
		수정처리가 완료되면 상세페이지로 이동하게 되는데 이때 idx와 같은 파라미터가 필요하다.
		Model객체에 저장한 후 redirect하면 자동으로 쿼리스트링으로 요청명뒤에 추가하게된다.
		*/
		model.addAttribute("idx", req.getParameter("idx"));
		model.addAttribute("nowPage", req.getParameter("nowPage"));
		return "redirect:view.do";
	}
	
	@RequestMapping("/board/reply.do")
	public String reply(HttpServletRequest req, Model model) {
		
		//request내장객체를 모델객체에 저장한 후 서비스로 전달
		model.addAttribute("req", req);
		service = new ReplyExecute();
		service.execute(model);
		
		//일련번호를 모델객체에 저장한 후 뷰 호출
		model.addAttribute("idx", req.getParameter("idx"));
		return "07Board/reply";
	}
	//답변글 쓰기 처리
	@RequestMapping("/board/replyAction.do")
	public String replyAction(HttpServletRequest req, Model model,
			SpringBoardDTO SpringBoardDTO) {
		//커맨드객체를 통해 폼값을 한꺼번에 받아서 처리한다.
		model.addAttribute("SpringBoardDTO", SpringBoardDTO);
		model.addAttribute("req", req);
		//서비스객체 생성 및 모델객체를 통해 요청을 전달한다.
		service = new ReplyActionExecute();
		service.execute(model);
		//답변글을 작성한 후 리스트로 이동한다.
		model.addAttribute("nowPage", req.getParameter("nowPage"));
		return "redirect:list.do";
	}
	
	
}
