// 파일명 표시
$(document).ready(function() {
	var fileTarget = $('#file');
	fileTarget.on('change', function() {
		var cur = $("#file").val().replace(/C:\\fakepath\\/i, '');
		$(".upload-name").val(cur);
	});
	var logout = $('#logout');
	var login = $('#login');
	var user = $('#user');
	if ($("#success").val() != '') {
		logout.show();
		user.show();
		login.hide();
	} else {
		logout.hide();
		user.hide();
		login.show();
	}
	var msg = $("#msg").val();
	console.log(msg);
	if (msg != null && msg != "") {
		showError(msg);
	}
});
// 로그아웃
function logout() {
	location.replace("/logout");
}
//로그인
function login() {
	location.replace("/user/signin");
}
//사용자 페이지 = 전체 유저 조회
function user() {
	location.replace("/${userId}");
}
// 첨부된 파일이 있을 때 전송버튼 활성화
const send = document.getElementById("do");
send.addEventListener("click", function() {
	var form = document.getElementById("fileupload");
	var fileName = $("#file").val();
	// substr와 substring의 차이: substr의 두 번째 파라미터는 length이고, substring의 두 번째 파라미터는 endIndex이다.
	// index는 0부터 시작하므로 문자열의 마지막 문자까지 자르려면, 아래 함수에서 두 번째 인자에 fileName.length-1을 입력해야 맞다.
	var fileExtension = fileName.substr(fileName.lastIndexOf('.') + 1, fileName.length);
	if (fileName == "") {
		alert('업로드할 파일을 선택해주세요.');
		event.preventDefault();
	} else if (fileExtension != "dbfile") {
		alert('dbfile만 업로드 가능합니다.');
		event.preventDefault();
	} else {
		form.submit();
	}
});
// 에러 alert
function showError(msg) {
	Swal.fire({
		icon: 'error',
		title: msg
	})
}