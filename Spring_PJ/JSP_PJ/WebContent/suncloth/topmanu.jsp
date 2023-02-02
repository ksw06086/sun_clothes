<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<header id = "header">
	<div id = "langage">
	</div>
	<form method = "post" name = "search">
	<div id = "search">
		<input type = "search" id = "srch" name = "srch" id = "search_box" class = "searchall">
		<button id = "search_btn" class = "searchall" onclick = "javascript: search.action = 'searchlist.do'"></button>
	</div>
	</form>
	
	<div id = "mainname">
		<h2><a href = "main.do">suncloth</a></h2>
	</div>
	
	<div id = "bottomleft" class = "bottom">
		<table>
			<tr>
			<c:if test="${sessionScope.memId == null}"> <!-- 로그인 안함 -->
				<td><a href = "login.do?member=0">로그인</a></td>
				<td><a href = "inputForm.do">회원가입</a></td>
				<td><a href = "login.do?member=0">장바구니</a></td>
				<td><a href = "login.do?member=0">주문관리</a></td>
				<td><a href = "login.do?member=0">마이페이지</a></td>
			</c:if>
			<c:if test="${sessionScope.memId != null}"> <!-- 로그인 성공 -->
				<td><a href = "logout.do">로그아웃</a></td>
				<td><a href = "modifyView.do">회원수정</a></td>
				<td><a href = "cart.do">장바구니</a></td>
				<td><a href = "order.do">주문관리</a></td>
				<td><a href = "mypage.do">마이페이지</a></td>
			</c:if>
			</tr>
		</table>
	</div>
	<div id = "bottomright" class = "bottom">
		<table>
			<tr>
				<td><a href = "notice.do?choose=1">공지</a></td>
				<td><a href = "FAQ.do?choose=2">FAQ</a></td>
				<td><a href = "QnA.do?choose=3">Q&A</a></td>
				<td><a href = "review.do?choose=4">후기</a></td>
				<td><a href = "bookmark.do">bookmark</a></td>
				<td><a href = "https://www.doortodoor.co.kr/parcel/pa_004.jsp">delivery</a></td>
				<td><a href = "login.do?member=1">host</a></td>
				<td><a href = "https://www.facebook.com/profile.php?id=100008360707255.html"><img src="./ascloimage/facebook-logo.png" width = "10px" height = "10px"></a></td>
				<td><a href = "https://www.instagram.com/seonu1109/?hl=ko"><img src="./ascloimage/instagram.png" width = "10px" height = "10px"></a></td>
			</tr>
		</table>
	</div>
</header>

<nav>
	<div class="lnb" style = "background-color:rgb(191,57,46);">
        <ul>
            <li><a href="menulist.do?name=only asclo" alt="" style = "color:#fff;">only asclo</a></li>
            <li><a href="menulist.do?name=best" alt="" style = "color:#fff;">best</a></li>
            <li><a href="menulist.do?name=outer" alt="" style = "color:#fff;">outer</a></li>
            <li><a href="menulist.do?name=suit" alt="" style = "color:#fff;">suit</a></li>
            <li><a href="menulist.do?name=top" alt="" style = "color:#fff;">top</a></li>
            <li><a href="menulist.do?name=shirt" alt="" style = "color:#fff;">shirt</a></li>
            <li><a href="menulist.do?name=knit" alt="" style = "color:#fff;">knit</a></li>
            <li><a href="menulist.do?name=bottom" alt="" style = "color:#fff;">bottom</a></li>
            <li><a href="menulist.do?name=shoes" alt="" style = "color:#fff;">shoes</a></li>
            <li><a href="menulist.do?name=acc" alt="" style = "color:#fff;">acc</a></li>
        </ul>
     </div>
    </nav>
</body>
</html>