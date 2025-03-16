<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.List" scope="request"/>
<tags:master pageTitle="Product List">
  <p>
    Welcome to Expert-Soft training!
  </p>
  <form>
    <label>
      <input name = "query" value = "${param.query}">
      <button>Search</button>
    </label>
  </form>
  <table>
    <thead>
      <tr>
        <td>Image</td>
        <td>
          Description
          <tags:sortLink sort="description" order="asc" text="▲"/>
          <tags:sortLink sort="description" order="desc" text="▼"/>
        </td>
        <td class="price">
          Price
          <tags:sortLink sort="price" order="asc" text="▲"/>
          <tags:sortLink sort="price" order="desc" text="▼"/>
        </td>
      </tr>
    </thead>
    <c:forEach var="product" items="${products}">
      <tr>
        <td>
          <img class="product-tile" src="${product.imageUrl}">
        </td>
        <td>
          <a href = "${pageContext.servletContext.contextPath}/products/${product.id}">
            ${product.description}
          </a>
        </td>
        <td class="price">
          <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
        </td>
      </tr>
    </c:forEach>
  </table>
</tags:master>