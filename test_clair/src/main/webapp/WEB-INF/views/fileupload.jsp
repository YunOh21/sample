<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
	<title>파일전송</title>
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
.btn {
  -webkit-appearance: none;
  -moz-appearance: none;
  appearance: none;
  
  background: #0288d1;
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

#btn {
	float: right;
}
</style>
<body>
		<header class="dhx_sample-header">
			<div class="dhx_sample-header__main">
				<nav class="dhx_sample-header__breadcrumbs">
					<ul class="dhx_sample-header-breadcrumbs">
						<li class="dhx_sample-header-breadcrumbs__item">
						</li>
					</ul>
				</nav>
				<h1 class="dhx_sample-header__title">
					<div class="dhx_sample-header__content">
						파일전송
					</div>
				</h1>
				<nav class="dhx_sample-header__breadcrumbs">
					<ul class="dhx_sample-header-breadcrumbs">
						<li class="dhx_sample-header-breadcrumbs__item">
							<a class="dhx_sample-header-breadcrumbs__link" style="cursor:pointer" id="user" onclick="user()">${success}</a>
							<a href="/user/signin" class="dhx_sample-header-breadcrumbs__link" id="login" onclick="login()">로그인</a>
							<a href="/logout" class="dhx_sample-header-breadcrumbs__link" id="logout" onclick="logout()" style="display:none">로그아웃</a>
						</li>
					</ul>
				</nav>
			</div>
		</header>
	${uploadFail}
		<section class="dhx_sample-container">
			<div id="form" style="height: 100%; margin: 20px;"></div>
			<form id="fileupload" action="/file/upload" method="POST" enctype="multipart/form-data">
			<label for="file" class="btn">파일 선택</label>
			<input type="file" accept=".dbfile" id="file" name="userFile" style="display:none">
			<input class="upload-name">
			<button class="btn" id="do">업로드</button>
			<input type="hidden" id="success" value="${success}">
			<input type="hidden" id="msg" value="${msg}">
		</form>
		</section>
		<script src="https://code.jquery.com/jquery-latest.js"></script>
		<script src="//cdn.jsdelivr.net/npm/sweetalert2@11"></script>
		<script src="/custom/js/fileupload.js"></script>
</body>
</html>
