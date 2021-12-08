<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="false" %>
<html>
<head>
	<title>업로드 실패</title>
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
					성공 ${success}건 / 실패 ${fail}건
				</div>
			</h1>
		</div>
	</header>
	<section class="dhx_sample-container" style="height: 80%">
		<div style="height: 500px; width: 1200px" id="grid"></div>
	</section>
	<script>
		const grid = new dhx.Grid("grid", {
			columns: [
				{ width: 80, id: "lineNum", header: [{ text: "라인번호", align: "center" }], align: "center" },
				{ width: 640, id: "lineText", header: [{ text: "내용" }] },
				{ width: 498, id: "failReason", header: [{ text: "실패사유" }] },
			],
			data: ${failData},
			adjust: true
		});
	</script>
</body>
</html>