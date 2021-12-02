<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="false" %>
<html>
<head>
	<title>업로드 실패</title>
</head>
<body style="text-align:center">
	<h1>성공 ${success}건 / 실패 ${fail}건</h1>
	<h3>실패한 라인은 다음과 같습니다.</h3>
	<div style="text-align:left; display:flex;justify-content:center">${cause}</div>
</body>
</html>