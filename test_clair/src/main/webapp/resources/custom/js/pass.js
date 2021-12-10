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