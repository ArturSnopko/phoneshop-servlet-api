<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/priceHistory.css">

<jsp:useBean id="cart" type="com.es.phoneshop.model.cart.Cart" scope="request"/>
<tags:master pageTitle="Cart">
  <table>
    <thead>
      <tr>
        <td>Image</td>
        <td>
          Description
        </td>
        <td class="price">
          Price
        </td>
      </tr>
    </thead>
    <c:forEach var="cartItem" items="${cart.items}">
      <tr>
        <td>
          <img class="product-tile" src="${cartItem.product.imageUrl}">
        </td>
        <td>
          <a href = "${pageContext.servletContext.contextPath}/products/${cartItem.product.id}">
            ${cartItem.product.description}
          </a>
        </td>
        <td class="price-container">
          <span class="price">
            <fmt:formatNumber value="${cartItem.product.price}" type="currency" currencySymbol="${cartItem.product.currency.symbol}"/>
          </span>
        </td>
      </tr>
    </c:forEach>
  </table>

</tags:master>