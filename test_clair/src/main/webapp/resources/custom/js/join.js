// 달력 로컬라이제이션
const ko = {
	monthsShort: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
	months: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
	daysShort: ["일", "월", "화", "수", "목", "금", "토"]
};
dhx.i18n.setLocale("calendar", ko);
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
			validation: function(value) {
				const regex = /^(?=.*\d)(?=.*[a-z]).{4,16}$/;
				return regex.test(value);
			},
			preMessage: "ID는 영문/숫자 조합으로 4자리 이상, 16자리 이하여야 합니다.",
			errorMessage: "ID는 영문/숫자 조합으로 4자리 이상, 16자리 이하여야 합니다.",
			name: "id",
			minlength: "4",
			maxlength: "16",
			required: "true",
			labelPosition: "left",
			id: "userId"
		},
		{
			type: "input",
			inputType: "password",
			label: "PASSWORD",
			labelWidth: "100px",
			validation: function(value) {
				const regex = /(?=.{8,16})((?=.*\d)(?=.*[a-z])(?=.*[A-Z])|(?=.*\d)(?=.*[a-zA-Z])(?=.*[\W_])|(?=.*[a-z])(?=.*[A-Z])(?=.*[\W_])).*/;
				// const regex = /^(?=.*\d)(?=.*[a-zA-Z])(?=.*[\W]).{8,}$/gm; -- 4가지 모두를 충족하는 경우
				return regex.test(value);
			},
			preMessage: "암호는 영문 대소문자/숫자/특수문자 중 3가지 이상 포함하여 8자리 이상, 16자리 이하여야 합니다.",
			errorMessage: "암호는 영문 대소문자/숫자/특수문자 중 3가지 이상 포함하여 8자리 이상, 16자리 이하여야 합니다.",
			name: "pwd",
			minlength: "8",
			maxlength: "16",
			required: "true",
			labelPosition: "left",
			id: "userPwd",
			icon: "dxi dxi-eye"
		},
		{
			type: "input",
			label: "이름",
			value: "",
			labelWidth: "100px",
			validation: function(value) {
				return value.length > 0;
			},
			errorMessage: "이름이 입력되지 않았거나 너무 깁니다.",
			name: "name",
			maxlength: "128",
			required: "true",
			labelPosition: "left",
			id: "userName"
		},
		{
			type: "select",
			label: "등급",
			labelWidth: "100px",
			name: "level",
			width: "300px",
			options: [
				{
					value: "",
					content: ""
				},
				{
					value: "A",
					content: "A"
				},
				{
					value: "B",
					content: "B"
				},
				{
					value: "C",
					content: "C"
				},
				{
					value: "D",
					content: "D"
				}
			]
			,
			validation: function(value) {
				return value.length > 0;
			},
			errorMessage: "등급이 선택되지 않았습니다.",
			id: "userLevel",
			required: "true",
			labelPosition: "left"
		},
		{
			type: "textarea",
			label: "특이사항",
			labelWidth: "100px",
			name: "desc",
			height: "150px",
			value: "",
			validation: function(value) {
				return value.length <= 256;
			},
			errorMessage: "256자 이내로 작성해주세요.",
			labelPosition: "left"
		},
		{
			type: "datepicker",
			label: "생년월일",
			labelWidth: "100px",
			name: "regDate",
			dateFormat: "%Y-%m-%d",
			width: "300px",
			labelPosition: "left",
			disabledDates: function(date) {
				let max = new Date();
				return date > max
			},
			validation: function(value) {
				const regexDate = /^\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$/
				return regexDate.test(form.getItem("regDate").getValue());
			},
			errorMessage: "생년월일이 선택되지 않았습니다.",
			required: true
		},
		{
			type: "button",
			text: "회원가입",
			//submit: true, // send 누르면 submit하는 설정 // 이 설정 있거나 없거나 아래 form.send() 없으면 form data 서버로 전달 안됨
			size: "medium",
			view: "flat",
			color: "primary",
			name: "send",
			disabled: true,
			id: "btn",
			//url: "join/result"
		},
	]
});

const send = form.getItem("send");

$(document).ready(function() {

	// 입력 값 확인 후 SEND 버튼 활성화
	const regexId = /^(?=.*\d)(?=.*[a-z]).{4,16}$/;
	const regexPwd = /(?=.{8,})((?=.*\d)(?=.*[a-z])(?=.*[A-Z])|(?=.*\d)(?=.*[a-zA-Z])(?=.*[\W_])|(?=.*[a-z])(?=.*[A-Z])(?=.*[\W_])).*/;
	const regexDate = /^\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$/

	var val1, val2, val3, val4, val5;

	$("input").on("input", (function() {
		val1 = regexId.test($("#userId").val());
		val2 = regexPwd.test($("#userPwd").val());
		val3 = form.getItem("name").getValue().length;
		val4 = form.getItem("level").getValue().length;
		val5 = regexDate.test(form.getItem("regDate").getValue())
		console.log("--------------");
		console.log(val1);
		console.log(val2);
		console.log(val3);
		console.log(val4);
		console.log(val5);
		console.log("--------------");
		val1 && val2 && val3 > 0 && val4 > 0 && val5 ? send.enable() : send.disable();
	}));

	$("select").on("change", (function() {
		val1 = regexId.test($("#userId").val());
		val2 = regexPwd.test($("#userPwd").val());
		val3 = form.getItem("name").getValue().length;
		val4 = form.getItem("level").getValue().length;
		val5 = regexDate.test(form.getItem("regDate").getValue());
		console.log("--------------");
		console.log(val1);
		console.log(val2);
		console.log(val3);
		console.log(val4);
		console.log(val5);
		console.log("--------------");
		val1 && val2 && val3 > 0 && val4 > 0 && val5 ? send.enable() : send.disable();
	}));

	form.getItem("regDate").events.on("change", function() {
		val1 = regexId.test($("#userId").val());
		val2 = regexPwd.test($("#userPwd").val());
		val3 = form.getItem("name").getValue().length;
		val4 = form.getItem("level").getValue().length;
		val5 = regexDate.test(form.getItem("regDate").getValue());
		console.log("--------------");
		console.log(val1);
		console.log(val2);
		console.log(val3);
		console.log(val4);
		console.log(val5);
		console.log("--------------");
		val1 && val2 && val3 > 0 && val4 > 0 && val5 ? send.enable() : send.disable();
	});
});

const hangeul = /[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]/;
const special = /[\{\}\[\]\/?.,;:|\)*~`!^\-_+┼<>@\#$%&\'\"\\\(\=]/gi;
// const space = /\s/g; -- 공백 정규식: dhtmlx input 자체에서 스페이스 입력 시 공백처리(문구는 발생x)
const num = /\d/;

// 눈모양 클릭 시 아이콘 변경, 비밀번호 보이기 -- ing
$('.dxi').on("click", function(){
	form.getItem("pwd").setProperties({icon: "dxi dxi-eye-off", inputType: "text"});
});

// ID에 한글,특수문자 입력 불가
form.getItem("id").events.on("input", function() {
	const inputVal = form.getItem("id").getValue();
	if (hangeul.test(inputVal)) {
		form.getItem("id").setValue("");
		form.getItem("id").setProperties({ errorMessage: "ID에 한글은 입력 불가합니다." });
	} else if (special.test(inputVal)) {
		form.getItem("id").setValue("");
		form.getItem("id").setProperties({ errorMessage: "ID에 특수문자는 입력 불가합니다." });
	} else if (!hangeul.test(inputVal) && !special.test(inputVal)) {
		form.getItem("id").setProperties({ errorMessage: "ID는 영문/숫자 조합으로 4자리 이상, 16자리 이하여야 합니다." });
	}
});

// 패스워드에 한글 입력 불가
// ISSUE: 패스워드 입력 시 한글로 입력 시 영문으로 인식함
form.getItem("pwd").events.on("input", function() {
	const inputVal = form.getItem("pwd").getValue();
	if (hangeul.test(inputVal)) {
		form.getItem("pwd").setValue("");
		form.getItem("pwd").setProperties({ errorMessage: "패스워드에 한글은 입력 불가합니다." });
	}
});

// 이름에 숫자,특수문자 입력 불가
form.getItem("name").events.on("input", function() {
	const inputVal = form.getItem("name").getValue();
	if (num.test(inputVal)) {
		form.getItem("name").setValue("");
		form.getItem("name").setProperties({ errorMessage: "이름에 숫자는 입력 불가합니다." });
	} else if (special.test(inputVal)) {
		form.getItem("name").setValue("");
		form.getItem("name").setProperties({ errorMessage: "이름에 특수문자는 입력 불가합니다." });
	} else {
		form.getItem("name").setProperties({ errorMessage: "" });
	}
});

// 이름 입력 없이 다른 칸 이동 시 에러
// ISSUE: preMessage/errorMessage 동작 조건 파악 부족
form.getItem("name").events.on("blur", function() {
	//console.log(form.getItem("name").getValue().length);
	if (form.getItem("name").getValue().length == 0) {
		form.getItem("name").setProperties({ preMessage: "이름이 입력되지 않았습니다." }); // error아닌 경우(사유 파악 못함) preMessage 표시
		form.getItem("name").setProperties({ errorMessage: "이름이 입력되지 않았습니다." });
	}
});

// ajax
send.events.on("click", function() {
	const jsonData = JSON.stringify(form.getValue());
	//alert(jsonData);
	$.ajax({
		type: "POST",
		url: '/user/signup',
		contentType: 'application/json', // 서버로 전송하는 데이터 형식
		data: jsonData, // 전송하는 데이터
		// dataType: "application/json;charset=utf-8", // 서버에서 반환하는 데이터 형식 -- parseError 발생하여 삭제
		success: function(data) {
			location.replace("/" + JSON.parse(data).id); // href와의 차이: 이전 페이지로 이동할 수 없다
		}, error: function(data) {
			// 						alert(JSON.stringify(data));
			// 						alert(data.status);
			// 						alert(JSON.parse(data.responseText).code);
			// 						alert(JSON.parse(data.responseText).fields.length);
			// 						alert(JSON.parse(data.responseText).fields[0].field);
			// 						alert(JSON.parse(data.responseText).fields[0].reason);
			switch (data.status) {
				case 400:
					var code = "[에러코드: " + JSON.parse(data.responseText).code + "]";
					var msg = "아래 항목을 다시 확인해주세요.";
					var detail = "";
					for (i = 0; i < JSON.parse(data.responseText).fields.length; i++) {
						detail += "- " + JSON.parse(data.responseText).fields[i].field + " "
							+ "(" + JSON.parse(data.responseText).fields[i].reason + ")<br>";
					}
					//alert(code);
					//alert(msg);
					//alert(detail);
					showError400(code, msg, detail);
					break;
				case 500:
					var msg2 = "서버측 기타오류";
					showError500(msg2);
					break;
			}
		}
	})
});

// 에러 alert
/* 함수명이 같으면 파라미터가 다르더라도 같이 실행됨(자바스크립트는 오버로딩을 구현하지 않음) */
function showError400(code, msg, detail) {
	Swal.fire({
		icon: 'error',
		title: code,
		text: msg,
		footer: detail
	})
}

function showError500(param) {
	Swal.fire({
		icon: 'error',
		title: '[에러코드: 1003]',
		text: param
	})
}