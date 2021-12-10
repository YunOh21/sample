const welcome = $("#welcome");
const grid = $("#grid");
const pagination = $("#pagination");
$(document).ready(function(){
	 // 회원가입 후 이동 시 웰컴 이미지 표시
	 if($("#join").val()!=""){
		 welcome.show();
		 grid.hide();
		 pagination.hide();
	 }
	 // 로그인 후 경로 입력하여 로그인/회원가입 시도 시, user.jsp로 자동이동 후 alert 문구 발생
	 const msg = $("#msg").val();
	 console.log(msg);
	 if(msg!=null && msg!=""){
		 Swal.fire('', msg);
	 }
	 // 세션 만료 시 로그인 페이지로 이동
	 if($("#success").val()=='[null 님]&nbsp;'){
		 Swal.fire('', "세션이 만료되었습니다. 로그인 화면으로 이동합니다.").then(function(){
			 location.replace('/user/signin');
		 });
		 /* }).catch(function(err)){ // catch 문법 재확인 후 사용
			 alert('알 수 없는 오류가 발생했습니다.'); // SweetAlert2 인식 못한 경우
		 } */
	 }
	 // 페이지 로드 시 전체 사용자 데이터 조회 (파일 업로드 성공 시 사용한 url 재사용)
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
						{ width: 248, id: "regDate", 
							header: [{ text: "생년월일" },
											{ text : "<input type='text' id='from' class='customDatepicker' readonly><span style='color: #c0c0c0'>&nbsp;~&nbsp;</span><input type='text' id='to' class='customDatepicker' readonly>", headerSort: false}],
							htmlEnable: true
						},
					],
					data: data
				});
				const pagination = new dhx.Pagination("pagination", {
				    css: "dhx_widget--bordered dhx_widget--no-border_top",
				    data: grid.data,
				    pageSize: 10
				});
				// 달력 로컬라이제이션
				const ko = {
						monthsShort: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
						months: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
						daysShort: ["일", "월", "화", "수", "목", "금", "토"]
				};
				dhx.i18n.setLocale("calendar", ko);
				// 달력 검색 from(시작일) 생성
				const fromCalendar = new dhx.Calendar(null, {dateFormat: "%Y-%m-%d"});
				const fromPopup = new dhx.Popup();
				fromPopup.attach(fromCalendar);
				const from = document.getElementById("from");
				from.addEventListener("click", function(){
					fromPopup.show(from);
				});
				fromCalendar.events.on("change", function() {
				  from.value = fromCalendar.getValue();
				  fromPopup.hide();
				  if(to.value!=""&&from.value>to.value){
					  Swal.fire('', '검색시작일은 검색종료일 이전이어야 합니다.');
					  from.value = "";
				  }
				});
				// 달력 검색 to(종료일) 생성
				const toCalendar = new dhx.Calendar(null, {dateFormat: "%Y-%m-%d"});
				const toPopup = new dhx.Popup();
				toPopup.attach(toCalendar);
				const to = document.getElementById("to");
				to.addEventListener("click", function(){
					toPopup.show(to);
				});
				toCalendar.events.on("change", function() {
				  to.value = toCalendar.getValue();
				  toPopup.hide();
				  if(from.value!=""&&from.value>to.value){
					  Swal.fire('', '검색종료일은 검색시작일 이후여야 합니다.');
					  to.value = "";
				  }
				});
				// 달력 검색 기능
				fromPopup.events.on("afterHide", function(){
					grid.data.filter(function(item){
						var regDate = item.regDate.split(" ")[0];
						if(to.value==""){
							return regDate >= from.value;
						}else if(to.value==from.value){
							return regDate == from.value;
						}else{
							return regDate >= from.value && regDate <= to.value;
						}
					});
				});
				toPopup.events.on("afterHide", function(){
					grid.data.filter(function(item){
						var regDate = item.regDate.split(" ")[0];
						if(from.value==""){
							return regDate <= to.value;
						}else if(to.value==from.value){
							return regDate == from.value;
						}else{
							return regDate >= from.value && regDate <= to.value;
						}
					});
				});
			}, error:function(){
				showError("서버연결에 실패하였습니다.");
			}
		})
}); 
// 로그아웃
function logout(){
	location.replace("/logout");
}
// 로그인
function login(){
	location.replace("/user/signin");
}
// 웰컴 이미지 클릭 시 grid 표시
function showGrid(){
	welcome.hide();
	grid.show();
	pagination.show();
}
// ajax 실패
function showError(msg){
	Swal.fire({
		icon: 'error',
		title: msg
	})
}