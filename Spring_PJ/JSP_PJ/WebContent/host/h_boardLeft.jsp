<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<link type = "text/css" rel = "stylesheet" href = "${project}cssall/h_boardLeft.css"/>
<html>
<body>
<td id = "tableft">
	<div id = "left">
		<table id = "menu">
			<tr>
				<td><b>·운영</b></td>
			</tr>
			<tr>
				<td>&emsp;<a href = "boardView.do">게시판 리스트</a></td>
			</tr>
			<tr>
				<td>&emsp;&emsp;<a href = "h_notice.do?choose=1">공지사항</a></td>
			</tr>
			<tr>
				<td>&emsp;&emsp;<a href = "h_FAQ.do?choose=2">FAQ</a></td>
			</tr>
			<tr>
				<td>&emsp;&emsp;<a href = "h_QnA.do?choose=3">Q&A</a></td>
			</tr>
			<tr>
				<td>&emsp;&emsp;<a href = "h_review.do?choose=4">후기</a></td>
			</tr>
			<tr>
				<td>&emsp;<a href = "h_notwritechar.do">금칙어관리</a></td>
			</tr>
		</table>
	</div>
</td>
</body>
</html>