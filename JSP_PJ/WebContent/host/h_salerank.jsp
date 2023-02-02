<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file = "setting.jsp" %>
<link type = "text/css" rel = "stylesheet" href = "${project}cssall/h_salerank.css"/>
<html>
<body>
<%@ include file = "topmenu.jsp" %>

<section>
	<table id = "middle">
		<col style = "width:15%;">
		<col>
		<tr>
			<%@ include file = "h_clicktotalLeft.jsp" %>
			<td id = "tabright">
				<div id = "righttop">
					<p><b>상품 판매순위 분석</b></p>
				</div>
				<table id = "searchifs">
					<col style = "width:15%;">
					<col>
					<tr>
						<td>기간검색</td>
			     		<td>
			     		<select id = "year" name = "yearname" style = "padding:2px; vertical-align:middle;">
							<option value = "2019">2019년</option>
							<option value = "2018">2018년</option>
							<option value = "2017">2017년</option>
							<option value = "2016">2016년</option>
							<option value = "2015">2015년</option>
						</select>
			     		</td>
			     	</tr>
				</table>
				
				<p id = "srhbtnp"><input type = "button" value = "검색" id = "srhbtn"></p>
				
				<c:set var = "cntsum" value = "0"/>
				<c:set var = "pricesum" value = "0"/>
				<c:set var = "stocksum" value = "0"/>
				<div id = "monthresult">
					<table id = "subtop">
						<tr>
							<td>
							<b>·월별 상품 판매 순위 현황</b>
							</td>
						</tr>
					</table>
					
					<table id = "clicklist">
				    	<col style = "width:20%;">
				    	<col style = "width:16%;">
				    	<col style = "width:16%;">
				    	<col style = "width:16%;">
				    	<col style = "width:16%;">
				    	<col style = "width:16%;">
				    	<tr id = "title">
				    		<td>순위</td> <!-- 10위까지만 -->
				    		<td>상품명</td>
				    		<td>판매수량</td>
				    		<td>판매금액</td>
				    		<td>재고</td>
				    		<td>리뷰</td>
				    	</tr>
				    	<c:if test="${list != null}">
					    	<c:forEach var="list" items="${list}">
					    		<tr class = "click">
						    		<td>${number}
									<c:set var = "number" value = "${number+1}"/>
									<c:set var = "cntsum" value = "${cntsum+list.cnttotal}"/>
									<c:set var = "pricesum" value = "${pricesum+list.pricetotal}"/>
									<c:set var = "stocksum" value = "${stocksum+list.stock}"/>
									</td>
						    		<td>${list.prdname}</td>
						    		<td>${list.cnttotal}</td>
						    		<td>${list.pricetotal}</td>
						    		<td>${list.stock}</td>
						    		<td>15</td>
						    	</tr>
					    	</c:forEach>
				    	</c:if>
				    	<tr class = "click" id = "foot">
				    		<td>합계</td>
				    		<td></td>
				    		<td>${cntsum}</td>
				    		<td>${pricesum}원</td>
				    		<td>${stocksum}</td>
				    		<td>27</td>
				    	</tr>
				    </table>
				</div>
			</td>
		</tr>
	</table>
</section>
</body>
</html>