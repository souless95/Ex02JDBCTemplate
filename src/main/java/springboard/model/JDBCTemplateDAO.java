package springboard.model;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public class JDBCTemplateDAO {
	
	/*
	메머변수는 DB연결과 Spring-JDBC사용을 위해 선언한다.
	컨트롤러에서 @Autowired를 통해 자동주입받은 빈을 정적변수인
	JdbcTemplateConst.template에 할당하였으므로, DB작업을 DAO에서도 수행할 수 있다.
	*/
	JdbcTemplate template;

	public JDBCTemplateDAO() {
		this.template = JdbcTemplateConst.template;
		System.out.println("JDBCTemplateDAO() 생성자 호출");
	}
	public void close() {
		//JDBCTemplate에서는 자원해제를 하지 않는다.
	}
	
	//게시물 갯수 카운트
	public int getTotalCount(Map<String, Object> map) {
		
		//count(*) 함수를 통해 게시물의 갯수를 카운트한다.
		String sql = "SELECT COUNT(*) FROM springboard ";
		if(map.get("Word")!=null) {
			sql +=" WHERE "+map.get("Column")+" "
				+ "		LIKE '%"+map.get("Word")+"%' ";
		}
		System.out.println("sql="+sql);
		//쿼리문을 실행한 후 결과값을 정수형으로 반환한다.
		return template.queryForObject(sql, Integer.class);
	}
	
	//게시판 리스트에 출력할 게시물을 인출한다.(페이지 처리 없음)
	public ArrayList<SpringBoardDTO> list (Map<String, Object> map) {
		//쿼리문 작성 및 검색어 처리
		String sql = "SELECT * FROM springboard ";
		if(map.get("Word")!=null) {
			sql +=" WHERE "+map.get("Column")+" "
				+ "		LIKE '%"+map.get("Word")+"%' ";
		}
		//게시판 목록은 최근게시물이 위로 출력되야 하므로 내림차순 정렬한 상태로 가져와야 한다.
		sql += " ORDER BY idx DESC";
		/*
		RowMapper가 select를 통해 얻어온 ResultSet을 갯수만큼 반복하여
		DTO에 저장한 후 List컬렉션에 추가하여 반환해준다.
		그러므로 DAO에서 개발자가 반복적으로 작성했던 코드를 생략할 수 있다.
		*/
		return (ArrayList<SpringBoardDTO>)template.query(
				sql, new BeanPropertyRowMapper<SpringBoardDTO>(SpringBoardDTO.class));
	}
	
}
