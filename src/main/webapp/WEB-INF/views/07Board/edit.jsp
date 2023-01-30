<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
	<link rel="stylesheet" href="../static/bootstrap5.3.0/css/bootstrap.min.css">
	<script src="../static/bootstrap5.3.0/js/bootstrap.bundle.min.js"></script>
	<script src="../static/jquery/jquery-3.6.3.min.js"></script>
<title>Insert title here</title>
</head>
<body>
<script type="text/javascript">
	function checkValidate(f){
		if(f.name.value==""){
			alert("이름을 입력하세요");
			f.name.focus();
			return false;
		}
		/*
		패스워드는 앞에서 이미 검증했으므로 input이 필요없다.
		if(f.pass.value==""){
			alert("패스워드를 입력하세요");
			f.pass.focus();
			return false;
		} */
		if(f.title.value==""){
			alert("제목을 입력하세요");
			f.title.focus();
			return false;
		}
		if(f.contents.value==""){
			alert("내용을 입력하세요");
			f.contents.focus();
			return false;
		}
	}
</script>
<div class="container">
	<h2>비회원제 게시판 - 글수정 폼</h2>
	
	<form name="writeFrm" method="post" 
		action="./editAction.do" 
		onsubmit="return checkValidate(this);">
	
	<!-- 
	수정처리시 전송할 게시물의 일련번호를 hidden으로 추가한다.
	또한 패스워드 입력란을 삭제처리 했으므로 hidden으로 추가해둔다.
	-->
	<input type="hid den" name="idx" value="${viewRow.idx }">
	<input type="hid den" name="nowPage" value="${param.nowPage }">
	<input type="hid den" name="pass" value="${viewRow.pass }">
	<table border=1 width=800>
	<colgroup>
		<col width="25%"/>
		<col width="*"/>
	</colgroup>
	<tr>
		<td>작성자</td>
		<td>
			<input type="text" name="name" style="width:50%;" value="${viewRow.name }"/>
		</td>
	</tr>
	<!--<tr>
		<td>패스워드</td>
		<td>
			<input type="password" name="pass" style="width:30%;" />
		</td>
	</tr>-->
	<tr>
		<td>제목</td>
		<td>
			<input type="text" name="title" style="width:90%;" value="${viewRow.title }" />
		</td>
	</tr>
	<tr>
		<td>내용</td>
		<td>
			<textarea name="contents" 
				style="width:90%;height:200px;">${viewRow.contents }</textarea>
		</td>
	</tr>
	<tr>
		<td colspan="2" align="center">
			<button type="submit">작성완료</button>
			<button type="reset">RESET</button>
			<button type="button" onclick="location.href='./list.do';">
				리스트바로가기
			</button>
		</td>
	</tr>
	</table>	
	</form>
</div>
</body>
</html>