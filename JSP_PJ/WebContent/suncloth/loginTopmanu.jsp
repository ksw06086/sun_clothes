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
		<a href = "main.do"><img src = "./images/12장/kr.png" width = "18px" height = "13px" style = "border: 1px solid #000;"></a>
		<a href = "ch_main.do"><img src = "./images/12장/ch.png"></a>
		<a href = "jp_main.do"><img src = "./images/12장/jp.png"></a>
		<a href = "us_main.do"><img src = "./images/12장/us.png"></a>
	</div>
	<div id = "search">
		<input type = "text" id = "search_box" class = "searchall">
		<a href = "search.do"><button id = "search_btn" class = "searchall"></button></a>
	</div>
	
	<div id = "mainname">
		<h2><a href = "main.do">suncloth</a></h2>
	</div>
	
	<div id = "bottomleft" class = "bottom">
			<table>
				<tr>
					<td><a href = "logout.do">로그아웃</a></td>
					<td><a href = "modifyView.do">회원수정</a></td>
					<td><a href = "cart.do">장바구니</a></td>
					<td><a href = "order.do">주문관리</a></td>
					<td><a href = "mypage.do">마이페이지</a></td>
				</tr>
			</table>
		</div>
	<div id = "bottomright" class = "bottom">
		<table>
			<tr>
				<td><a href = "notice.do">공지</a></td>
				<td><a href = "FAQ.do">FAQ</a></td>
				<td><a href = "QnA.do">Q&A</a></td>
				<td><a href = "review.do">후기</a></td>
				<td><a href = "bookmark.do">bookmark</a></td>
				<td><a href = "https://www.doortodoor.co.kr/parcel/pa_004.jsp">delivery</a></td>
				<td><a href = "h_login.do">host</a></td>
				<td><a href = "https://www.facebook.com/profile.php?id=100008360707255.html"><img src="./ascloimage/facebook-logo.png" width = "10px" height = "10px"></a></td>
				<td><a href = "https://www.instagram.com/seonu1109/?hl=ko"><img src="./ascloimage/instagram.png" width = "10px" height = "10px"></a></td>
			</tr>
		</table>
	</div>
</header>

<nav>
	<div class="lnb">
        <ul>
            <li><a href="only_asclo.do" alt="">only asclo</a></li>
            <li><a href="best.do" alt="">best</a></li>
            <li><a href="outer.do" alt="">outer</a></li>
            <li><a href="suit.do" alt="">suit</a></li>
            <li><a href="top.do" alt="">top</a></li>
            <li><a href="shirt.do" alt="">shirt</a></li>
            <li><a href="knit.do" alt="">knit</a></li>
            <li><a href="bottom.do" alt="">bottom</a></li>
            <li><a href="shoes.do" alt="">shoes</a></li>
            <li><a href="acc.do" alt="">acc</a></li>
        </ul>
     </div>
    </nav>
</body>
</html>