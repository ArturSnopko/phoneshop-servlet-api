<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/priceHistory.css">

<jsp:useBean id="order" type="com.es.phoneshop.model.order.Order" scope="request"/>
<tags:master pageTitle="Checkout">
  <p>
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
  </p>
  <form method="post" action="${pageContext.servletContext.contextPath}/checkout">
    <table>
      <thead>
        <tr>
          <td>Image</td>
          <td>
            Description
          </td>
          <td class="quantity">
            Quantity
          </td>
          <td class="price">
            Price
          </td>
        </tr>
      </thead>

      <c:forEach var="cartItem" items="${order.items}" varStatus="status">
        <tr>
          <td>
            <img class="product-tile" src="${cartItem.product.imageUrl}">
          </td>
          <td>
            <a href = "${pageContext.servletContext.contextPath}/products/${cartItem.product.id}">
              ${cartItem.product.description}
            </a>
          </td>
          <td class="quantity">
            <fmt:formatNumber value = "${cartItem.quantity}" var = "quantity"/>
            ${cartItem.quantity}
          </td>
          <td class="price-container">
            <span class="price">
              <fmt:formatNumber value="${cartItem.product.price}" type="currency" currencySymbol="${cartItem.product.currency.symbol}"/>
            </span>
          </td>

        </tr>
      </c:forEach>

      <tr>
        <td></td>
        <td></td>
        <td>Subtotal price: </td>
        <td>${order.subtotal}</td>
      </tr>

      <tr>
        <td></td>
        <td></td>
        <td>Delivery cost: </td>
        <td>${order.deliveryCost}</td>
      </tr>

      <tr>
        <td></td>
        <td></td>
        <td>Total cost: </td>
        <td>${order.totalCost}</td>
      </tr>
    </table>

    <h2>Your details</h2>
    <table>
      <tags:orderFormRow name="firstName" label="First Name" order="${order}" errors="${errors}" expectedFormat="Name"></tags:orderFormRow>
      <tags:orderFormRow name="lastName" label="Last Name" order="${order}" errors="${errors}" expectedFormat="Name"></tags:orderFormRow>
      <tags:orderFormRow name="phone" label="Phone" order="${order}" errors="${errors}" expectedFormat="+375xxxxxxxxx"></tags:orderFormRow>
      <tags:orderFormRow name="deliveryAddress" label="Delivery Address" order="${order}" errors="${errors}" expectedFormat=""></tags:orderFormRow>
      <tr>
        <td>Delivery Date <span style="color:red">*</span></td>
        <td>
          <c:set var="formattedDate" value="${order.deliveryDate}" />
          <input type="date" name="deliveryDate"
                 value="${formattedDate}" />
          <c:if test="${not empty errors['deliveryDate']}">
            <div class="error">${errors['deliveryDate']}</div>
          </c:if>
        </td>
      </tr>
      <tr>
        <td>Payment method <span style ="color:red">*</span></td>
        <td>
          <select name = paymentMethod>
            <c:set var="error" value="${errors['paymentMethod']}"/>
            <option></option>
            <c:forEach var = "paymentMeth" items ="${paymentMethods}">
              <option>${paymentMeth}</option>
            </c:forEach>
          </select>
          <c:if  test="${not empty error}">
            <div class="error">
                ${error}
            </div>
          </c:if>
        </td>
      </tr>
    </table>

    <p>
      <button>PlaceOrder</button>
    </p>
  </form>
  <form id ="deleteCartItem" method="post"> </form>
</tags:master>
