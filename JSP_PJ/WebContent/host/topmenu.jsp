<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<header>
	<table>
		<col style = "width:20%;">
		<col>
		<col>
		<tr>
			<td>
			안녕하세요. 관리자님
			</td>
			<td>
			관리자메뉴
			<span style = "background-color:#fff; padding:5px 0px;">
			<input type = "search" id = "search" name = "srh" style = "border:none;">
			<a href = "search.html"><button id = "search_btn" class = "searchall"></button>
			</a></span>
			</td>
			<td>
			<a href = "mymain.do">내 쇼핑몰 가기</a>&emsp;
			<a href = "logout.do">로그아웃</a>
			</td>
		</tr>
	</table>
</header>

<nav>
	<table>
		<col style = "width:20%;">
		<col style = "width:8%;">
		<col style = "width:8%;">
		<col style = "width:8%;">
		<col style = "width:8%;">
		<col style = "width:8%;">
		<col style = "width:8%;">
		<col style = "width:8%;">
		<col>
		<tr>
			<td><a href = "hostmain.do"><b>suncloth</b></a></td>
			<td><a href = "hostmain.do">홈</a></td>
			<td><a href = "h_product.do">상품</a></td>
			<td><a href = "h_order.do">주문</a></td>
			<td><a href = "h_member.do">회원</a></td>
			<td><a href = "boardView.do">운영</a></td>
			<td><a href = "h_clicktotal.do">통계</a></td>
			<td></td>
		</tr>
	</table>
</nav>
</body>
</html>