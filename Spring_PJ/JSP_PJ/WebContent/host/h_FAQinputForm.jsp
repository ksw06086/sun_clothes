<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file = "setting.jsp" %>
<link type = "text/css" rel = "stylesheet" href = "${project}cssall/h_noticewrite.css"/>
<html>
<body>
<%@ include file = "topmenu.jsp" %>

<section>
<form action = "h_FAQPro.do" method = "post" name = "h_noticeform">
	<input type = "hidden" name = "choose" value = "${choose}">
	<input type = "hidden" name = "num" value = "${num}">
	<input type = "hidden" name = "ref" value = "${ref}">
	<input type = "hidden" name = "ref_step" value = "${ref_step}">
	<input type = "hidden" name = "ref_level" value = "${ref_level}">
	<table id = "middle">
		<col style = "width:15%;">
		<col>
		<tr>
			<%@ include file = "h_boardLeft.jsp" %>
			<td id = "tabright">
				<div id = "righttop">
					<p><input type = "button" value = "게시글 리스트" id = "boardlist" onclick = "window.location = 'h_notice.do?choose=${choose}'"></p>
					<p><b>[FAQ] 게시글 등록</b></p>
					<p><input type = "submit" value = "저장" id = "save"></p>
				</div>
				
				<div id = "product">
					<table id = "faq">
						<col style = "width:20%">
						<col>
						<tr>
							<td>작성자</td>
							<td>
							관리자(${sessionScope.memId})
							</td>
						</tr>
						
						<tr>
				     		<td>판매상태</td>
				     		<td>
	    					<input type = "radio" name = "state" id = "memberrank" value = "회원등급" checked>
	    					<label for = "memberrank">회원 등급</label>&emsp;
	    					<input type = "radio" name = "state" id = "prdinput" value = "상품등록">
	    					<label for = "prdinput">상품 등록</label>&emsp;
	    					<input type = "radio" name = "state" id = "order_pay" value = "주문/결제">
	    					<label for = "order_pay">주문/결제</label>&emsp;
	    					<input type = "radio" name = "state" id = "deli" value = "배송">
	    					<label for = "deli">배송</label>&emsp;
	    					<input type = "radio" name = "state" id = "refund" value = "환불">
	    					<label for = "refund">환불</label>&emsp;
	    					<input type = "radio" name = "state" id = "pluspay" value = "적립금">
	    					<label for = "pluspay">적립금</label>&emsp;
	    					<input type = "radio" name = "state" id = "other" value = "기타">
	    					<label for = "other">기타</label>
	    					</td>
	    				</tr>
						
						<tr>
							<td>제목</td>
							<td>
							<input type = "text" id = "question" name = "subject" 
							placeholder = "제목을 입력하세요!" maxlength = "400" style = "width:300px;" required autofocus>
							</td>
						</tr>
				     	
				     	<tr>
							<td>내용</td>
				     		<td>
				     		<textarea class = "Box" rows="30" cols="80" maxlength = "4000"
			    			style="resize: vertical; width:100%;" name = "content" 
			    			required word-break:break-all placeholder = "글내용을 입력하세요!"></textarea>
				     		</td>
				     	</tr>
					</table>
		    	</div>
			</td>
		</tr>
	</table>
</form>
</section>
</body>
</html>