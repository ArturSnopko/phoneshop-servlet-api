<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/priceHistory.css">

<jsp:useBean id="products" type="java.util.List" scope="request"/>
<tags:master pageTitle="Product List">
  <iframe name="hidden-frame" style="display: none;"></iframe>

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
        <td>
          Quantity
        </td>
        <td class="price">
          Price
          <tags:sortLink sort="price" order="asc" text="▲"/>
          <tags:sortLink sort="price" order="desc" text="▼"/>
        </td>
        <td>
        </td>
      </tr>
    </thead>
    <c:forEach var="product" items="${products}">
      <form method="post" action="${pageContext.servletContext.contextPath}/products/${product.id}" target="hidden-frame">
        <tr>
          <td>
            <img class="product-tile" src="${product.imageUrl}">
          </td>
          <td>
            <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
                ${product.description}
            </a>
          </td>
          <td class="quantity">
            <input type="hidden" name="productId" value="${product.id}">
            <input type="number" name="quantity" min="1" value="1">
          </td>
          <td class="price-container">
        <span class="price">
          <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
        </span>
            <div class="modal">
              <h2>Price History for ${product.description}</h2>
              <table>
                <thead>
                <tr>
                  <th>Date</th>
                  <th>Price</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="history" items="${product.historyList}">
                  <tr>
                    <td>${history.date}</td>
                    <td><fmt:formatNumber value="${history.price}" type="currency" currencySymbol="${product.currency.symbol}"/></td>
                  </tr>
                </c:forEach>
                </tbody>
              </table>
            </div>
          </td>
          <td>
            <button type="submit">Add to Cart</button>
          </td>
        </tr>
      </form>
    </c:forEach>
  </table>

</tags:master>