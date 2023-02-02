<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file = "setting.jsp" %>
<html>
<body>
<h2> 회원가입 - 처리페이지 </h2>
<c:if test="${icnt == 0}">
<script type = "text/javascript">
	errorAlert(insertError);
</script>
</c:if>
<c:if test="${icnt != 0}">
	<!-- cnt를 가지고 mainSuccess.do로 이동 -->
	<%-- response.sendRedirect("mainSuccess.do?cnt=" + cnt); --%>
	<script type="text/javascript">
		alert("글쓰기 완료!");
		window.location = "review.do?choose=${choose}";
	</script>
</c:if>
</body>
</html>