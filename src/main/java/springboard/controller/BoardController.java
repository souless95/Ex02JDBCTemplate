package springboard.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import springboard.model.JdbcTemplateConst;
import springboard.service.IBoardService;
import springboard.service.ListExecute;

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
}
