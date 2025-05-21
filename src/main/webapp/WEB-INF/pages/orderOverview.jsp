<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="order" type="com.es.phoneshop.model.order.Order" scope="request"/>

<tags:master pageTitle="Order overview">
 <p>
     Cart:
 </p>
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
            <td>
               Quantity
            </td>
        </tr>
        </thead>
        <c:forEach var="item" items="${order.items}" varStatus="status">
            <tr data-product-id="${item.product.id}">
                <td>
                    <img class="product-tile" src="${item.product.imageUrl}" />
                </td>
                <td>
                    <a href="${pageContext.servletContext.contextPath}/products/${item.product.id}">
                        ${item.product.description}
                    </a>
                </td>
                <td class="price">
                    <fmt:formatNumber
                        value="${item.product.price}"
                        type="currency"
                        currencySymbol="${item.product.currency.symbol}"/>
                </td>
                <td class="quantity">
                 ${item.quantity}
                </td>
            </tr>
        </c:forEach>
         <tr>
            <td class="price">
                Subtotal:
                <p>
                    <fmt:formatNumber value="${order.cartPrice}" type="currency"
                       currencySymbol="${order.currency.symbol}"/>
                </p>
            </td>
             <td class="price">
                 Delivery cost:
                 <p>
                    <fmt:formatNumber value="${order.deliveryCosts}" type="currency"
                       currencySymbol="${order.currency.symbol}"/>
                 </p>
             </td>
             <td class="price">
                Total cost:
                <p>
                     <fmt:formatNumber value="${order.orderTotalPrice}" type="currency"
                         currencySymbol="${order.currency.symbol}"/>
                </p>
             </td>
         </tr>
    </table>
      <h2>Your details</h2>
    <table>
        <tr><td>First name</td><td>${order.firstName}</td></tr>
        <tr><td>Last name</td><td>${order.lastName}</td></tr>
        <tr><td>Phone</td><td>${order.phone}</td></tr>
        <tr><td>Delivery Date</td><td>${order.deliveryDate}</td></tr>
        <tr><td>Delivery Address</td><td>${order.deliveryAddress}</td></tr>
        <tr><td>Payment method</td><td>${order.paymentMethod}</td></tr>
    </table>

</tags:master>
