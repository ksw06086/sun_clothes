<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file = "setting.jsp" %>
<html>
<body>
<c:if test="${cnt == 0}">
<script type = "text/javascript">
	errorAlert(idError);
</script>
</c:if>

<c:if test="${cnt == -1}">
<script type = "text/javascript">
	errorAlert(passwdError);
</script>
</c:if>

<c:if test="${cnt == 1}">
	<!-- cnt를 가지고 mainSuccess.do로 이동 -->
	<%-- response.sendRedirect("mainSuccess.do?cnt=" + cnt); --%>
	<script type="text/javascript">
		alert("${sessionScope.memId}님 환영합니다!!");
		if(${member} == 0){
			window.location = "main.do";
		} else {
			window.location = "hostmain.do";
		}
	</script>
</c:if>
</body>
</html>