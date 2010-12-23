<%@ taglib uri="http://www.eclipse.org/virgo/snaps" prefix="snaps" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<snaps:snaps>
<c:forEach var="snap" items="${snaps}">
${snap.name}
</c:forEach>
</snaps:snaps>
