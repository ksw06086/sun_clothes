<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file = "setting.jsp" %>
<html>
<body>
<h2> 회원가입 - 처리페이지 </h2>
<c:if test="${insertCnt == 0}">
<script type = "text/javascript">
	errorAlert(insertError);
</script>
</c:if>
<c:if test="${insertCnt != 0}">
	<!-- cnt를 가지고 mainSuccess.do로 이동 -->
	<%-- response.sendRedirect("mainSuccess.do?cnt=" + cnt); --%>
	<script type="text/javascript">
		window.location = "mainSuccess.do?id=${id}&names=${name}&email=${email}&member=${member}";
	</script>
</c:if>
</body>
</html>