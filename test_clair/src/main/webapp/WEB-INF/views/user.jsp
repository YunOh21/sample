<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
    .customDatepicker {
    	width:105px;
    	height:28px;
    	border-radius:2px;
    	box-shadow:inset 0 0 0 1px #dfdfdf;
    	border:0;
    	color: rgba(0,0,0,.7);
    	padding: 0 25px 0 4px;
    }
    .customDatepicker:focus {
    	outline: none;
    	box-shadow: 0 0 1px rgba(81, 203, 238, 1);
  		padding: 3px 0px 3px 3px;
  		margin: 5px 1px 3px 0px;
  		border: 1px solid rgba(81, 203, 238, 1);
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
		<div style="display:none">
			<input type="hidden" id="join" value="${join}">
			<input type="hidden" id="msg" value="${msg}">
			<input type="hidden" id="success" value="${success}">
		</div>
	</section>
</body>
<script src="https://code.jquery.com/jquery-latest.js"></script>
<script src="//cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script src="/custom/js/user.js"></script>
</html>