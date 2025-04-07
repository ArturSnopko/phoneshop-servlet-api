<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/priceHistory.css">

<jsp:useBean id="products" type="java.util.List" scope="request"/>
<tags:master pageTitle="Product List">
  <p>
    Welcome to Expert-Soft training!
  </p>
  <c:if test="${not empty param.message}">
    <div class="success">
        ${param.message}
    </div>
  </c:if>

  <c:if test="${not empty errors}">
    <div class="error">
      There were errors
    </div>
  </c:if>
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
        <td  class="quantity">
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
      <form method="post" action="${pageContext.servletContext.contextPath}/products">
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
            <c:set var="error" value="${errors[product.id]}"/>
            <input type="hidden" name="productId" value="${product.id}">
            <input type="number" name="quantity" min="1" value="1"  class="quantity">
            <c:if  test="${not empty error}">
              <div class="error">
                  ${error}
              </div>
            </c:if>

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
