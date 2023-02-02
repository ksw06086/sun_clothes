<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file = "setting.jsp" %>
<link type = "text/css" rel = "stylesheet" href = "${project}cssall/h_QnA.css"/>
<html>
<body>
<%@ include file = "topmenu.jsp" %>
<script type="text/javascript">
$(function() {
	$("#daytypeoption tr td").click(function() { /* 첫번째 링크를 클릭한 경우 */
		$("#daytypeoption tr td").css("background-color", "#fff");
		$("#daytypeoption tr td").css("font-weight", "normal");
		$(this).css("background-color", "#EAECEE");
		$(this).css("font-weight", "bold");
	});
	
	
	$("#QnAallcheck").change(function(){
		var is_check = $(this).is(":checked");
		
		// 전체선택시 개별을 일괄체크
		if(is_check){
			$(".QnAchecks").prop("checked","true");
		} else {
			$(".QnAchecks").prop("checked","");
		}
	});
	
	document.getElementById('firstday').value = new Date().toISOString().substring(0, 10);
	document.getElementById('lastday').value = new Date().toISOString().substring(0, 10);
	if("${dayNum}" != ""){
		document.searchForm.dayNum.value = "${dayNum}";
		$("#daytypeoption tr td").eq(${dayNum}).click();
	}
	if("${schType}" != ""){
		document.searchForm.srhType.value = "${schType}";
		document.searchForm.searchType.value = "${schType}";
	}
});
</script>
<section>
	<table id = "middle">
		<col style = "width:15%;">
		<col>
		<tr>
			<%@ include file = "h_boardLeft.jsp" %>
			<td id = "tabright">
				<div id = "righttop">
					<p><b>[Q&A] 게시판 리스트</b></p>
				</div>
				<form action = "QnAselect.do" method = "post" name = "searchForm">
					<input type = "hidden" name = "choose" value = "3">
					<table id = "searchifs">
						<col style = "width:15%;">
						<col>
						<tr>
							<td>작성일</td>
				     		<td><span><select id = "orderstate" style = "padding: 3px; font-size: 1em; vertical-align:middle;">
					     			<option value = "allorder">등록일</option>
					     		</select></span>
				     			<span><input type = "date" id = "firstday" value = "" style = "vertical-align:middle;">&nbsp;&nbsp;-&nbsp;&nbsp;
				     			<input type = "date" id = "lastday" value = "" style = "vertical-align:middle;"></span>
				     			<span>
				 				<input type = "hidden" name = "dayNum" value = "0">
				     			<table id = "daytypeoption" style = "display: inline;">
				     				<tr>
				     					<td onclick = "today();"><input type = "radio" name = "daytype" id = "today" value = "today">
				     					<label for = "today">오늘</label></td>
				     					<td onclick = "week();"><input type = "radio" name = "daytype" id = "week" value = "week">
				     					<label for = "week">1주일</label></td>
				     					<td onclick = "month1();"><input type = "radio" name = "daytype" id = "month1" value = "month1">
				     					<label for = "month1">1개월</label></td>
				     					<td onclick = "month3();"><input type = "radio" name = "daytype" id = "month3" value = "month3">
				     					<label for = "month3">3개월</label></td>
				     					<td onclick = "month6();"><input type = "radio" name = "daytype" id = "month6" value = "month6">
				     					<label for = "month6">6개월</label></td>
				     				</tr>
				     			</table></span>
				     		</td>
				     	</tr>
				     	
				     	<tr>
				     		<td>질문유형</td>
				     		<td>
	    					<input type = "checkbox" name = "Qtype" id = "prd" value = "상품문의">
	    					<label for = "prd">상품문의</label>&emsp;
	    					<input type = "checkbox" name = "Qtype" id = "deli" value = "배송문의">
	    					<label for = "deli">배송문의</label>&emsp;
	    					<input type = "checkbox" name = "Qtype" id = "paychk" value = "입금확인문의">
	    					<label for = "paychk">입금확인문의</label>&emsp;
	    					<input type = "checkbox" name = "Qtype" id = "refund" value = "주문/변경/취소/환불문의">
	    					<label for = "refund">주문/변경/취소/환불문의</label>&emsp;
	    					<input type = "checkbox" name = "Qtype" id = "ather" value = "기타문의">
	    					<label for = "ather">기타문의</label>
	    					</td>
	    				</tr>
	    				
	    				<tr>
							<td>검색어</td>
							<td>
							<select id = "type" name = "srhType" style = "padding:2px; vertical-align:middle;" onchange = "typeChange();">
								<option value = "0">전체</option>
								<option value = "1">제목</option>
								<option value = "2">내용</option>
								<option value = "3">작성자</option>
							</select>
							<input type = "hidden" name = "searchType" value = "0">
							<input type = "search" id = "srch" name = "srch" style = "vertical-align:middle;">
							</td>
						</tr>
					</table>
					
					<p id = "srhbtnp"><input type = "submit" value = "검색" id = "srhbtn"></p>
				</form>
				
				<div id = "result">
				<form action = "h_noticedeletePro.do" method = "post" name = "completeForm">
					<input type = "hidden" name = "pageNum" value = "${pageNum}">
					<table id = "resulttop">
						<td>검색 
						<b><span style = "color:blue;">${srhCnt}</span></b>개
						/ 총 <b><span style = "color:#ccc;">${cnt}</span></b>개</td>
						<td>
						<select id = "count" style = "padding:3px;">
							<option value = "">10개씩 출력</option>
							<option value = "">30개씩 출력</option>
							<option value = "">50개씩 출력</option>
						</select>
						</td>
					</table>
					
					<input type = "hidden" name = "choose" value = "1">
					<table id = "qnalist">
				    	<col style = "width:1%;">
				    	<col style = "width:10%;">
				    	<col style = "width:10%;">
				    	<col>
				    	<col style = "width:10%;">
				    	<col style = "width:10%;">
				    	<col style = "width:10%;">
				    	<tr id = "title">
				    		<td><input type = "checkbox" name = "prdcheck" id = "QnAallcheck"  value = "전체선택"></td>
				    		<td>번호</td>
				    		<td>질문유형</td>
				    		<td>제목</td>
				    		<td>작성자</td>
				    		<td>작성일</td>
				    		<td>답변상태</td>
				    	</tr>
				    	<!-- 게시글이 있으면 -->
						<c:if test="${srhCnt > 0}">
							<c:forEach var = "list" items = "${list}">
								<tr style = "text-align:center;" class = "qna">
									<td><input type = "checkbox" name = "QnAchecks" class = "qnacheck"  value = "${list.num}"></td>
									<td>${number}(${list.num})
									<c:set var = "number" value = "${number-1}"/>
									</td>
									<!-- 상세 페이지 -->
									<td>${list.state}</td>
									<td align = "left">
									<c:if test="${list.writer != sessionScope.memId}">
									<a href = "QnAForm.do?num=${list.num}&number=${number+1}&pageNum=${pageNum}&choose=${choose}">
									</c:if>
									<c:if test="${list.writer == sessionScope.memId}">
									<a href = "h_QnAupdate.do?num=${list.num}&number=${number+1}&pageNum=${pageNum}&choose=${choose}&ref=${list.ref}">
									</c:if>
									<c:if test="${list.ref_level > 0}">
										&nbsp;<img src = "ascloimage/re.png" border = "0" width = "20" height = "15">
									</c:if>
									${list.subject}
									</a></td>
									<td>${list.writer}</td>
									<td><fmt:formatDate type="both" pattern="yyyy-MM-dd HH:mm" value="${list.reg_date}"/></td>
									<td>
									${list.writestate}
									</td>
								</tr>
							</c:forEach>
						</c:if>
						<!-- 게시글이 없으면 -->
						<c:if test="${srhCnt <= 0}">
							<tr class = "faq">
								<td colspan = "5" align = "center">
									게시글이 없습니다. 글을 작성해주세요!!
								</td>
							</tr>
						</c:if>
				    </table>
				    
				    <table id = "buttons" style = "width:98%; padding: 10px 0px 0px; margin:0 auto;">
				    	<tr class = "prdbtn">
				    		<td>
				    		<input type = "submit" value = "선택삭제" id = "checkdel">
				    		</td>
				    	</tr>
				    	<tr>
				    		<td style = "text-align:center; padding: 15px;"><!-- 페이지 컨트롤 -->
								<table style = "width:1000px;" align = "center">
									<tr>
										<th align = "center">
											<!-- 게시글이 있으면 -->
											<c:if test="${cnt > 0}">
												<!-- 처음[◀◀] : ㅁ + 한자키 / 이전블록 [◀] -->
												<c:if test="${startPage > pageBlock}">
													<a href = "h_QnA.do?choose=${choose}">[◀◀]</a>
													<a href = "h_QnA.do?pageNum=${startPage - pageBlock}&choose=${choose}">[◀]</a>
												</c:if>
												
												<!-- 블럭내의 페이지 번호 -->
												<c:forEach var = "i" begin = "${startPage}" end = "${endPage}">
													<c:if test="${i == currentPage}">
														<span><b>[${i}]</b></span>				
													</c:if>
													<c:if test="${i != currentPage}">
														<span><a href = "h_QnA.do?pageNum=${i}&choose=${choose}">[${i}]</a></span>				
													</c:if>
												</c:forEach>
												
												<!-- 다음블럭 [▶] / 끝[▶▶] -->
												<c:if test="${pageCount > endPage}">
													<a href = "h_QnA.do?pageNum=${startPage + pageBlock}&choose=${choose}">[▶]</a>
													<a href = "h_QnA.do?pageNum=${pageCount}&choose=${choose}">[▶▶]</a>
												</c:if>
											</c:if>
										</th>
									</tr>
								</table>
							</td>
				    	</tr>
				    </table>
				</form>
				</div>
			</td>
		</tr>
	</table>
</section>
</body>
</html>