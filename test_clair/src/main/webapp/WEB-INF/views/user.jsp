<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="false" %>
<html>
<head>
	<title>홈페이지</title>
	<meta name="description" content="">
	<meta name="viewport" content="width=device-width, initial-scale=1.0" charset="utf-8">
	<link rel="shortcut icon" href="/common/favicon/favicon.ico" type="image/x-icon" />
	<link rel="icon" href="/common/favicon/icon-16.png" sizes="16x16" />
	<link rel="icon" href="/common/favicon/icon-32.png" sizes="32x32" />
	<link rel="icon" href="/common/favicon/icon-48.png" sizes="48x48" />
	<link rel="icon" href="/common/favicon/icon-96.png" sizes="96x96" />
	<link rel="icon" href="/common/favicon/icon-144.png" sizes="144x144" />
	<!-- end meta block -->
	<script type="text/javascript" src="/codebase/suite.js?v=7.2.5"></script>
	<link rel="stylesheet" href="/codebase/suite.css?v=7.2.5">
	<link rel="stylesheet" href="/common/index.css?v=7.2.5">
</head>
<style>
    .flex-container {
      display: flex;
      justify-content: center;
      align-items: center;
      flex-direction: column;
    }
    #grid {
    	width: 1200px;
    	height: 500px;
    }
    #pagination {
    	width: 300px;
    }
</style>
<body>
	<header class="dhx_sample-header">
		<div class="dhx_sample-header__main">
			<nav class="dhx_sample-header__breadcrumbs">
				<ul class="dhx_sample-header-breadcrumbs">
					<li class="dhx_sample-header-breadcrumbs__item">
						<a href="/" class="dhx_sample-header-breadcrumbs__link">파일전송</a>
					</li>
				</ul>
			</nav>
			<h1 class="dhx_sample-header__title">
				<div class="dhx_sample-header__content">
					${join }
				</div>
			</h1>
			<nav class="dhx_sample-header__breadcrumbs">
				<ul class="dhx_sample-header-breadcrumbs">
					<li class="dhx_sample-header-breadcrumbs__item">
						<a class="dhx_sample-header-breadcrumbs__link"><span id="user">${success}</span></a>
						<a href="/logout" class="dhx_sample-header-breadcrumbs__link" id="logout" onclick="logout()">로그아웃</a>
					</li>
				</ul>
			</nav>
		</div>
	</header>
	<section class="dhx_sample-container" style="height: 80%">
		<div class="flex-container">
			<div id="welcome" title="클릭하면 모든 사용자를 조회합니다." style="display:none;text-align:center;cursor:pointer" onclick="showGrid()"><img src="/img/welcome.jpg" width="80%"></div>
			<div id="grid"></div>
			<div id="pagination" style="padding: 20px;"></div>
		</div>
	</section>
</body>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script>
const welcome = $("#welcome");
const grid = $("#grid");
const pagination = $("#pagination");
$(document).ready(function(){
	 // 로그인 후 경로 입력하여 로그인/회원가입 시도 시 alert 문구 발생
	 const msg = "${msg}";
	 console.log(msg);
	 if(msg!=null && msg!=""){
		 alert(msg);
	 }
	 // 회원가입 후 이동 시 웰컴 이미지 표시
	 if('${join}'!=""){
		 welcome.show();
		 grid.hide();
		 pagination.hide();
	 }
		$.ajax({
			url : '/file/success',
			dataType : 'json',
			success:function(data){
				const grid = new dhx.Grid("grid", {
					columns: [
						{ width: 200, id: "id", header: [{ text: "ID" }, { content: "inputFilter" }] },
						{ width: 200, id: "pwd", header: [{ text: "패스워드" }, { content: "inputFilter" }] },
						{ width: 150, id: "name", header: [{ text: "이름" }, { content: "inputFilter" }] },
						{ width: 100, id: "level", header: [{ text: "등급" }, { content: "selectFilter" }] },
						{ width: 300, id: "desc", header: [{ text: "특이사항" }, { content: "inputFilter" }] },
						// calendar(or datepicker)로 from-to 검색방법 확인중
						{ width: 248, id: "regDate", header: [{ text: "등록일자" }, { content: "selectFilter" }] },
					],
					data: data,
					htmlEnable: true
				});
				const pagination = new dhx.Pagination("pagination", {
				    css: "dhx_widget--bordered dhx_widget--no-border_top",
				    data: grid.data,
				    pageSize: 10
				});
			}, error:function(){
				alert("서버연결에 실패하였습니다.");
			}
		})
}); 
//로그아웃
function logout(){
	location.replace("/logout");
}
//로그인
function login(){
	location.replace("/user/signin");
}
//웰컴 이미지 클릭 시 grid 표시
function showGrid(){
	welcome.hide();
	grid.show();
	pagination.show();
}
</script>
</html>