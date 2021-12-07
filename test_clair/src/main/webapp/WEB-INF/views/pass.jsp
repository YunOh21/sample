<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="false" %>
<html>
<head>
	<title>업로드 성공</title>
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
						<a href="/" class="dhx_sample-header-breadcrumbs__link">홈 화면으로</a>
					</li>
				</ul>
			</nav>
			<h1 class="dhx_sample-header__title">
				<div class="dhx_sample-header__content">
				레코드건수 ${success}건 입력 성공
				</div>
			</h1>
			<nav class="dhx_sample-header__breadcrumbs">
				<ul class="dhx_sample-header-breadcrumbs">
					<li class="dhx_sample-header-breadcrumbs__item">
						<a class="dhx_sample-header-breadcrumbs__link" id="showResult" style="cursor:pointer">현재까지 레코드 조회</a>
					</li>
				</ul>
			</nav>
		</div>
	</header>
	<section class="dhx_sample-container" style="height: 80%">
		<div class="flex-container">
			<div id="grid"></div><br>
			<div id="pagination" style="padding: 0 20px;"></div>
		</div>
	</section>
</body>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script>
$('#showResult').click(function(){
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
					{ width: 248, id: "regDate", header: [{ text: "등록일자" }, { content: "selectFilter" }] },
				],
				data: data
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
})
</script>
</html>