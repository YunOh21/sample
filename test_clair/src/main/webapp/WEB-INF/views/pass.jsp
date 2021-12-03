<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="false" %>
<html>
<head>
	<title>업로드 성공</title>
</head>
<style>
body {
	text-align : center
}
table {
	width: 75%;
	margin: 10px auto;
	border : 0.5px solid gray;
	border-collapse: collapse;
	text-align: center;
	line-height: 1.5
}
td {
	border : 0.5px solid gray
}
button {
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
</style>
<body>
	<h3 style="display:inline">레코드건수 ${success}건 입력 성공</h3>
	<button id="showResult">현재까지 레코드 조회</button>
	<div id="resultDiv"></div>
</body>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script>
$('#showResult').click(function(){
	
	let data = {
		  id: "string",
		  password: "string"
		};
	
	$.ajax({
		url : '/file/success',
		dataType : 'json',
		success:function(data){
			const str = "<table id='resultTable'>";
			$('#resultDiv').append(str);
			 $.each(data, function(index,value){
				let record = "<tr><td>"+value['id']+"</td><td>"+value['pwd']+"</td><td>"+value['name']+"</td><td>"+value['level']+"</td><td>"+value['desc']+"</td><td>"+value['regDate']+"</td></tr>";
				$('#resultTable').append(record);
			})
		}, error:function(){
			alert("서버연결에 실패하였습니다.");
		}
	})
})
</script>
</html>