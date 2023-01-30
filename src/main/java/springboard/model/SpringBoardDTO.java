package springboard.model;

import java.sql.Date;

public class SpringBoardDTO {
	private int idx; /* 일련번호 */
	private String name; 
	private String title;
	private String contents;
	private Date postdate;
	private int hits; /* 조회수 */
	private int bgroup; /* 답변글의 그룹 일련번호 */
	private int bstep; /* 답변글의 정렬 순서*/
	private int bindent; /* 답변글의 들여쓰기 깊이 */
	private String pass;
	//게시물의 가상번호 부여를 위한 멤버변수 추가
	private int virtualNum;
	 
	public int getIdx() {
		return idx;
	}
	public void setIdx(int idx) {
		this.idx = idx;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getPostdate() {
		return postdate;
	}
	public void setPostdate(Date postdate) {
		this.postdate = postdate;
	}
	public int getHits() {
		return hits;
	}
	public void setHits(int hits) {
		this.hits = hits;
	}
	public int getBgroup() {
		return bgroup;
	}
	public void setBgroup(int bgroup) {
		this.bgroup = bgroup;
	}
	public int getBstep() {
		return bstep;
	}
	public void setBstep(int bstep) {
		this.bstep = bstep;
	}
	public int getBindent() {
		return bindent;
	}
	public void setBindent(int bindent) {
		this.bindent = bindent;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	public int getVirtualNum() {
		return virtualNum;
	}
	public void setVirtualNum(int virtualNum) {
		this.virtualNum = virtualNum;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	 
	 
}
