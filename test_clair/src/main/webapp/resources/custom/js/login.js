const form = new dhx.Form("form", {
	css: "dhx_widget--bordered",
	padding: 40,
	width: 600,
	rows: [
		{
			type: "input",
			label: "ID",
			labelWidth: "100px",
			value: "",
			name: "id",
			maxlength: "16",
			labelPosition: "left"
		},
		{
			type: "input",
			inputType: "password",
			labelWidth: "100px",
			label: "PASSWORD",
			value: "",
			name: "pwd",
			maxlength: "32",
			labelPosition: "left"
		},
		{
			type: "button",
			text: "로그인",
			size: "medium",
			view: "flat",
			color: "primary",
			name: "send",
			id: "btn",
			disabled: true
		},
	]
});

const send = form.getItem("send");

// SEND 버튼 입력 값 확인 후 활성화
// ISSUE: dhtmlx의 afterValidate나 events.on("change")는 화면의 다른 부분 클릭해야 동작
form.events.on("keydown", function(){
	form.getItem("id").getValue()!="" && form.getItem("pwd").getValue()!=""? send.enable() : send.disable();
});
	
send.events.on("click", function(){
	const jsonData = JSON.stringify(form.getValue());
	//alert(jsonData);
	$.ajax({
		type: "POST",
		url: '/user/signin',
		contentType: 'application/json', // 서버로 전송하는 데이터 형식
		data: jsonData, // 전송하는 데이터
		dataType: "json", // 서버에서 반환하는 데이터 형식
		success:function(data){
			//alert(data.id);
			location.replace("/" + data.id); // href와의 차이: 이전 페이지로 이동할 수 없다
		}, error:function(data){
			var msg = "";
			switch (data.status){
				case 401:
					msg = '권한없음 - 아이디 혹은 패스워드 틀림';
					showError401(msg);
					break;
				case 500:
					msg = '서버측 기타오류';
					showError500(msg);
					break;
			}
		}
	})
});
function showError401(msg){
	Swal.fire({
		icon: 'error',
		title: msg
	})
}
function showError500(msg){
	Swal.fire({
		icon: 'error',
		title: msg
	})
}