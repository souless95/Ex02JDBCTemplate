package springboard.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
/*
JDBCTemplate 클래스의 주요 메서드
Object queryForObject(String sql, RowMapper rm)
: 하나의 레코드나 결과값을 반환하는 select 계열의 쿼리문을 실행한다.

List query(String sql, RowMapper rm)
:여러개의 레코드를 반환하는 select 계열의 쿼리문을 실행한다.

int update(String sql)
:updaste, delete, insert 쿼리문을 실행한다. 행의 변과가 생기므로
변화된 행의 갯수가 int로 반환된다.

※만약 위 메서드에서 파라미터가 필요한 경우에는 Object형 배열을 매개변수로 추가할 수 있다.
*/
public class JDBCTemplateDAO {
	
	/*
	멤버변수는 DB연결과 Spring-JDBC사용을 위해 선언한다.
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
		//sql += " ORDER BY idx DESC";
		
		sql += " ORDER BY bgroup DESC, bstep ASC";
		
		/*
		RowMapper가 select를 통해 얻어온 ResultSet을 갯수만큼 반복하여
		DTO에 저장한 후 List컬렉션에 추가하여 반환해준다.
		그러므로 DAO에서 개발자가 반복적으로 작성했던 코드를 생략할 수 있다.
		*/
		return (ArrayList<SpringBoardDTO>)template.query(
				sql, new BeanPropertyRowMapper<SpringBoardDTO>(SpringBoardDTO.class));
	}
	
	//글쓰기 처리
	public int write(SpringBoardDTO SpringBoardDTO) {
		//폼값이 저장된 DTO(커맨드객체)을 매개변수로 받아온다.
		
		/*
		insert와 같이 행의 변화가 생기는 쿼리문을 실행할때는 update()메서드를 사용한다.
		PreparedStatementCreator()를 익명클래스로 정의하여 메서드를 오버라이딩한 후 
		인파라미터가 있는 쿼리문에 값을 설정하고 실행한다.
		*/
		int result = template.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				//인파라미터가 있는 insert쿼리문 작성
				String sql = "INSERT INTO springboard ("
						+ "idx, name, title, contents, hits, "
						+ "bgroup, bstep, bindent, pass) "
						+ "VALUES ("
						+ "springboard_seq.NEXTVAL,?,?,?,0,"
						+ "springboard_seq.NEXTVAL,0,0,?)";
				/*
				답변형 게시판에서는 원본글인 경우 idx와 bgroup은 같은 값을 입력받게 된다.
				이때 nextval을 한문장에서 두번 사용하게 되는데, 한문장에서 두번 문장이 나오더라도
				항상 하나의 시퀀스만 반환하게된다.
				*/
				
				//익명클래스 내부에서 prepared객체를 생성한다.
				PreparedStatement psmt = con.prepareStatement(sql);
				//쿼리문에 인파라미터를 설정한다.
				psmt.setString(1, SpringBoardDTO.getName());
				psmt.setString(2, SpringBoardDTO.getTitle());
				psmt.setString(3, SpringBoardDTO.getContents());
				psmt.setString(4, SpringBoardDTO.getPass());
				//설정 후 완성된 쿼리문을 가진 psmt객체를 반환하면 update() 메서드가 실행된 후 결과값을 반환한다.
				return psmt;
			}
		});
		//insert쿼리를 실행한 결과는 1 아니면 0이다. 따라서 해당
		//메서드의 반환타입은 int로 설정되어 있다.
		return result;
	}
	//내용보기시 조회수를 1증가 시킴.
	public void updateHit(String idx) {
		
		//인파라미터가 있는 쿼리문을 작성한다.
		String sql = "UPDATE springboard SET "
				+ "hits=hits+1 "
				+ "WHERE idx=?";
		
		/*
		update() 메서드의 첫번째 인수로 쿼리문을 전달한다.
		두번째 인수로 PreparedStatementSetter를 익명클래스로 정의한다.
		오버라이딩된 메서드를 이용해서 인파라미터를 설정한다.
		*/
		template.update(sql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, Integer.parseInt(idx));
			}
		});
		/* update() 메서드는 int형의 반환값이 있지만 필요없는 경우에는 생략할 수 있다.
		여기서는 필요하지 않아 void로 선언되어 있다.
		*/
	}
	
	//내용보기 : 일련번호에 해당하는 게시물을 인출
	public SpringBoardDTO view(String idx) {
		//내용보기 시 조회수를 증가시킨다. 
		updateHit(idx);
		
		//레코드 저장을 위해 DTO객체를 생성한다.
		SpringBoardDTO dto = new SpringBoardDTO();
		//인파라미터가 있는 select 쿼리문을 작성한다.
		String sql = "SELECT * FROM springboard "
				+ "WHERE idx=?";
		/*
		queryForObject()메서드는 쿼리문을 실행한 후 반드시 하나의 결과를 반환해야 한다.
		그렇지 않으면 예외가 발생하므로 반드시 예외처리를 해주는것이 좋다.
		*/
		try {
			/*
			인파라미터가 있는 쿼리문을 실행할 경우 세번째 인수로 Object형 배열을 선언한 후
			값을 할당하면 순서대로 인파라미터에 추가된다.
			*/
			dto = template.queryForObject(sql, new BeanPropertyRowMapper<SpringBoardDTO>(SpringBoardDTO.class),
					new Object[] {idx});
			/*
			BeanPropertyRowMapper객체는 쿼리의 실행결과를 DTO에 저장해주는 역할을 한다. 이때 테이블의 컬럼명과 DTO의 멤버변수명이 일치해야한다.
			따라서 테이블의 컬럼명, 작성폼의 name속성값, DTO의 멤버변수를 모두 하나의 이름으로 일치시켜야 한다.
			*/
		} 
		catch (Exception e) {
			System.out.println("View()실행시 예외발생");
		}
		return dto;
	}
	//패스워드 검증 : 일련번호와 패스워드를 통해 검증한다.
	public int password(String idx, String pass) {
		int resultIdx = 0;
		String sql = "SELECT * FROM springboard "
				+ "WHERE pass=? AND idx=?";
		System.out.println(sql);
		try {
			/*
			패스워드와 일련번호의 조건에 만족하는 레코드가 없는 경우 예외가 발생하게되므로
			반드시 예외처리를 해야한다.
			*/
			SpringBoardDTO dto = template.queryForObject(sql, 
					new BeanPropertyRowMapper<SpringBoardDTO>(SpringBoardDTO.class),
					new Object[] {pass,idx});
			/*
			인파라미터가 2개이므로 이를 대체할 값도 Object형 배열에 추가해주면 된다.
			*/
			resultIdx = dto.getIdx();
			
		} 
		catch (Exception e) {
			System.out.println("password() 예외발생");
		}
		/*
		일련번호는 시퀀스를 사용하므로 반드시 1이상의 값을 가지게 된다.
		따라서 초기값인 0을 반환한다면 패스워드 검증에 실패한 것으로 판단할 수 있다.
		*/
		return resultIdx;
	}
	
	//수정 처리
	public void edit(SpringBoardDTO dto) {
		//인파라미터가 있는 update 쿼리문 작성
		String sql = "UPDATE springboard"
				+ " SET name=?, title=?, contents=?"
				+ " WHERE idx=? AND pass=?";
		//update() 메서드의 2번째 인수로 Object형 배열을 사용한다.
		//실행시 인파라미터를 순서대로 매칭하여 처리한다.
		template.update(sql, new Object[] {dto.getName(), dto.getTitle(),
				dto.getContents(),dto.getIdx(),dto.getPass()});
	}
	
	//삭제처리
	public void delete(String idx, String pass) {
		//인파라미터가 있는 delete 쿼리문 작성
		String sql = "DELETE FROM springboard "
				+ "WHERE idx=? AND pass=?";
		
		//PreparedStatementSetter 익명클래스를 통해 인파라미터를
		//설정한 후 쿼리문을 실행한다. update() 메서드는 정수값을
		//반환하지만 필요없는 경우 사용하지 않아도 된다.
		template.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, idx);
				ps.setString(2, pass);
			}
		});
	}
	//답변글 쓰기 처리
	public void reply(final SpringBoardDTO dto) {
		
		//답변글 추가전에 bstep(그룹내의 정렬)을 일괄적으로 업데이트한다.
		replyPrevUpdate(dto.getBgroup(), dto.getBstep());
		
		/*
		글쓰기의 경우 원본글이므로 idx와 bgroup은 같은 값을 입력한다.
		하지만 답변글은 원본글을 기반으로 작성되므로 idx는 새로운 시퀀스를 사용하면되고,
		bgroup은 원본글과 동일한 값을 입력해야한다.
		즉 bgroup컬럼을 통해 원본글과 답변글을 그룹화한다.
		*/
		String sql = "INSERT INTO springboard ("
				+ "idx, name, title, contents, pass, "
				+ "bgroup, bstep, bindent) "
				+ "VALUES ("
				+ "springboard_seq.NEXTVAL,?,?,?,?,"
				+ "?,?,?)";
		
		template.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, dto.getName());
				ps.setString(2, dto.getTitle());
				ps.setString(3, dto.getContents());
				ps.setString(4, dto.getPass());
				//그룹번호는 원본글과 동일하게 입력하면 된다.
				ps.setInt(5, dto.getBgroup());
				/*
				답변글은 원본글 아래족에 노출되어야 하고, 또한 들여쓰기도 되어야한다.
				따라서 원본글에 +1 해준후 입력해야한다.
				*/
				//스탭은 게시물의 그룹내에서의 정렬순서를 의미한다.
				ps.setInt(6, dto.getBstep()+1);
				//인덴트는 들여쓰기의 깊이 즉 depth를 의미한다.
				ps.setInt(7, dto.getBindent()+1);
			}
		});
	}
	/*
	답변글을 입력하기전 현재 step보다 큰 게시물들을 
	step+1 처리해서 뒤로 일괄적으로 밀어주는 작업을 진행한다.
	원본글에 답변을 2번 이상 작성하는 경우 step에 입력되는 
	값이 동일해지는 현상을 방지하기 위한 목적으로 업데이트 하는 것이다.
	*/
	public void replyPrevUpdate(int bGroup, int bStep) {
		String sql = "UPDATE springboard SET bstep=bstep+1 "
				+ "WHERE bgroup=? AND bstep>?";
		template.update(sql, new Object[] {bGroup, bStep});
	}
	
	public ArrayList<SpringBoardDTO> listPage(
			Map<String, Object> map){

		int start = Integer.parseInt(map.get("start").toString());
		int end = Integer.parseInt(map.get("end").toString());
		
		String sql = ""
				+"SELECT * FROM ("
				+"    SELECT Tb.*, rownum rNum FROM ("
				+"        SELECT * FROM springboard ";				
			if(map.get("Word")!=null){
				sql +=" WHERE "+map.get("Column")+" "
					+ " LIKE '%"+map.get("Word")+"%' ";				
			}			
			sql += " ORDER BY bgroup DESC, bstep ASC"
			+"    ) Tb"
			+")"
			+" WHERE rNum BETWEEN "+start+" and "+end;
		
		return (ArrayList<SpringBoardDTO>)
			template.query(sql, 
				new BeanPropertyRowMapper<SpringBoardDTO>(
						SpringBoardDTO.class));
	}

	
}
