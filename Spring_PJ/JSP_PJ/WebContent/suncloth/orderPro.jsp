<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file = "setting.jsp" %>
<html>
<body>
<c:if test="${ucnt == 0}">
<script type = "text/javascript">
	errorAlert("재고 수정에 문제가 생겼습니다.");
</script>
</c:if>
<c:if test="${icnt == 0}">
<script type = "text/javascript">
	errorAlert("수정처리가 안됐습니다.");
</script>
</c:if>
<c:if test="${icnt != 0}">
	<!-- cnt를 가지고 mainSuccess.do로 이동 -->
	<%-- response.sendRedirect("mainSuccess.do?cnt=" + cnt); --%>
	<script type="text/javascript">
		window.location = "order.do";
	</script>
</c:if>
</body>
</html>