<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="false" %>
<html>
<head>
	<title>파일 업로드</title>
</head>
<style>
.btn {
  -webkit-appearance: none;
  -moz-appearance: none;
  appearance: none;
  
  background: #28a745;
  color: #ffffff;
  
  margin: 0;
  padding: 0.5rem 1rem;
  
  font-family: 'Noto Sans KR', sans-serif;
  font-size: 1rem;
  font-weight: 400;
  text-align: center;
  text-decoration: none;
  
  border: none;
  border-radius: 4px;
  
  display: inline-block;
  width: auto;
  
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  
  cursor: pointer;
  
  transition: 0.5s;
}
.upload-name {
  display: inline-block;
  height: 35px;
  font-size:18px; 
  padding: 0 10px;
  vertical-align: middle;
  background-color: #f5f5f5;
  border: 1px solid #ebebeb;
  border-radius: 5px;
}
</style>
<body style="text-align: center">
	${uploadFail}
	<div style="float:right; padding:20px;">
		<span type="text" id="user" style="display:none">${success }</span>
		<button class="btn" id="logout" onclick="logout()" style="display:none">로그아웃</button>
		<button class="btn" id="login" onclick="login()" style="display:none">로그인</button>
	</div>
	<div style="padding:100px">
		<h1>업로드할 파일을 선택하세요.</h1>
		<h3>※ dbfile 확장자만 업로드 가능합니다.</h3>
		<form id="fileupload" action="/file/upload" method="POST" enctype="multipart/form-data">
			<label for="file" class="btn">파일 선택</label>
			<input type="file" accept=".dbfile" id="file" name="userFile" style="display:none" onchange="showFileName()">
			<input class="upload-name">
			<button class="btn" id="do">업로드</button>
		</form>
	</div>
</body>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script>
// 파일명 표시
$(document).ready(function(){ 
	 var fileTarget = $('#file'); 
	 fileTarget.on('change', function(){
	     var cur=$("#file").val().replace(/C:\\fakepath\\/i,'');
	   $(".upload-name").val(cur);
	});
	 var logout = $('#logout');
	 var login = $('#login');
	 var user = $('#user');
	 if('${success}'!=''&&'${success}'!='null님'){
		 logout.show();
		 user.show();
		 login.hide();
	 }else{
		 logout.hide();
		 user.hide();
		 login.show();
	 }
	 var msg = "${msg}";
	 console.log(msg);
	 if(msg!=null && msg!=""){
		 alert(msg);
	 }
	 
}); 
// submit
var send = document.getElementById("do");
send.addEventListener("click", function () {
 	var form = document.getElementById("fileupload");
 	var fileName = $("#file").val();
 	// substr와 substring의 차이: substr의 두 번째 파라미터는 length이고, substring의 두 번째 파라미터는 endIndex이다.
 	// index는 0부터 시작하므로 문자열의 마지막 문자까지 자르려면, 아래 함수에서 두 번째 인자에 fileName.length-1을 입력해야 맞다.
 	var fileExtension = fileName.substr(fileName.lastIndexOf('.')+1, fileName.length);
 	if(fileName==""){
 		alert('업로드할 파일을 선택해주세요.');
 		event.preventDefault();
 	}else if(fileExtension!="dbfile"){
 		alert('dbfile만 업로드 가능합니다.');
 		event.preventDefault();
 	}else{
 		form.submit();
 	}
});
// 로그아웃
function logout(){
	location.replace("/logout");
}
//로그인
function login(){
	location.replace("/user/signin");
}
</script>
</html>
