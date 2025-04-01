<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="pageTitle" required="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script>
  history.scrollRestoration = "manual";
  window.onpageshow = function(event) {
    if (event.persisted) {
      location.reload();
    }
  };
</script>

<html>
<head>
  <title>${pageTitle}</title>
  <link href='http://fonts.googleapis.com/css?family=Lobster+Two' rel='stylesheet' type='text/css'>
  <link rel="stylesheet" href="${pageContext.servletContext.contextPath}/styles/main.css">
</head>
<body class="product-list">
  <header>
    <a href="${pageContext.servletContext.contextPath}">
      <img src="${pageContext.servletContext.contextPath}/images/logo.svg"/>
      PhoneShop
    </a>
    <jsp:include page="/cart/minicart"/>
  </header>
  <main>
    <jsp:doBody/>
  </main>
  <p> Recently viewed</p>
  <c:if test="${not empty sessionScope.recentlyVisited}">
    <c:set var="recentlyVisitedList" value="${sessionScope.recentlyVisited.items.reversed()}" />
    <tr>
      <c:forEach var="pr" items="${recentlyVisitedList}">
        <td>
            <img src="${pr.imageUrl}">
            <p>
              <a href="${pageContext.servletContext.contextPath}/products/${pr.id}">
                  ${pr.description}
              </a>
            </p>
            <p>
              <fmt:formatNumber value="${pr.price}" type="currency" currencySymbol="${pr.currency.symbol}"/>
            </p>
        </td>
      </c:forEach>
    </tr>
  </c:if>
  <p>
    (c) Expert-soft
  </p>
</body>
</html>
