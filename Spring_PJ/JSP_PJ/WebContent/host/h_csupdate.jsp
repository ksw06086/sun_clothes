<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file = "setting.jsp" %>
<link type = "text/css" rel = "stylesheet" href = "${project}cssall/h_productinput.css"/>
<html>
<script type="text/javascript">
function start(){
	if("${opart1}" != ""){
		document.productForm.color.value = "${opart1}";
	}
	if("${opart2}" != ""){
		document.productForm.size.value = "${opart2}";
	}
	if("${stock.state}" != ""){
		document.productForm.state.value = "${stockvo.state}";
	}
};
</script>
<body onload = "start();">
<%@ include file = "topmenu.jsp" %>

<section>
	<table id = "middle">
		<col style = "width:15%;">
		<col>
		<tr>
			<%@ include file = "h_productLeft.jsp" %>
			<td id = "tabright">
			<form method="post" name = "productForm">
				<input type = "hidden" name = "pageNum" value = "${pageNum}">
				<input type = "hidden" name = "num" value = "${num}">
				<div id = "righttop">
					<p><input type = "button" value = "상품리스트" id = "prdlist"></p>
					<p><b>color/size 재고 상품 등록</b></p>
					<p>
					<input type = "submit" value = "등록" id = "prdinput" onclick = "javascript: productForm.action = 'h_csupdatePro.do'">
					</p>
				</div>
				
				<div id = "product">
					<table id = "basetop">
						<tr>
							<td>
							<b>·상품정보</b>
							</td>
						</tr>
					</table>
					
					<table id = "base">
						<col style = "width:20%">
						<col>
						<tr>
			    			<td style = "text-align:center; padding: 10px 0px;">
			    			<div id = "image" style = "background-color:#ccc; width: 100px; height: 100px; line-height:100px; margin:0px auto;">
			    			<img src = 'fileready/${vo.mainfile}' width = '100px' height = '100px'></div></td>
			    			<td>${vo.name}
			    			</td>
			    		</tr>
						
						<tr>
							<td>color/size*</td>
							<td>
							<select style = "width:100%; padding:3px 0px;"  name = "color" onchange = "select_color();">
		     					<option value = "">-[필수] 색상 선택-</option>
								<!-- 게시글이 있으면 -->
								<c:if test="${colorsrhCnt > 0}">
									<c:forEach var = "list" items = "${colorlist}">
										<option value = "${list.colorcode}">${list.colorname}</option>
									</c:forEach>
								</c:if>
								<!-- 게시글이 없으면 -->
								<c:if test="${colorsrhCnt <= 0}">
								</c:if>
							</select>
							<select style = "width:100%; padding:3px 0px;" name = "size" onchange = "select_size();"> <!-- 일단 아우터것만 이거는 jQuery로 조건써서 갖다넣을려고함 -->
								<option value = "">-[필수] size 선택-</option>
								<!-- 게시글이 있으면 -->
								<c:if test="${sizesrhCnt > 0}">
									<c:forEach var = "list" items = "${sizelist}">
										<option value = "${list.sizecode}">${list.sizename}</option>
									</c:forEach>
								</c:if>
								<!-- 게시글이 없으면 -->
								<c:if test="${sizesrhCnt <= 0}">
								</c:if>
							</select>
							</td>
						</tr>
						
				     	<tr>
				     		<td>판매상태*</td>
				     		<td>
	    					<input type = "radio" name = "state" id = "saleing" value = "판매중" checked>
	    					<label for = "saleing">판매중</label>&emsp;
	    					<input type = "radio" name = "state" id = "notstock" value = "품절">
	    					<label for = "notstock">품절</label>&emsp;
	    					<input type = "radio" name = "state" id = "saleready" value = "판매대기">
	    					<label for = "saleready">판매대기</label>&emsp;
	    					<input type = "radio" name = "state" id = "salestop" value = "판매중지">
	    					<label for = "salestop">판매중지</label>&emsp;
	    					</td>
	    				</tr>
	    				
						<tr>
				     		<td>재고 *</td>
				     		<td><input type = "number" id = "stock" name = "stock" min = "1" value = "${stockvo.count}">ea
				     		</td>
				     	</tr>
				     	
						<tr>
							<td>최대구매수량</td>
							<td>
	    					최대 <input type = "number" id = "maxcount" name = "maxcount" min = "1" value = "${stockvo.maxcount}">개 까지 구매 가능
							</td>
						</tr>
					</table>
		    	</div>
		    	</form>
			</td>
		</tr>
	</table>
</section>
</body>
</html>