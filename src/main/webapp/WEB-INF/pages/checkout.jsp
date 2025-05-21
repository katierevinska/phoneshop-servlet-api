<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="order" type="com.es.phoneshop.model.order.Order" scope="request"/>
<jsp:useBean id="paymentMethods" type="java.util.List" scope="request"/>

<tags:master pageTitle="Checkout">
 <h2>
     Cart:
 </h2>

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
    <c:if test="${not empty param.message}">
          <div class="success">
              ${param.message}
          </div>
      </c:if>
      <c:if test="${not empty errors}">
      </br>
          <div class="error">
              There was an error during placing order
          </div>
      </c:if>
      <h2>Your details</h2>
     <form method="post" action="${pageContext.servletContext.contextPath}/checkout">
    <table>
        <tags:orderFormRow name="firstName" label="First name" order="${order}" errors="${errors}"></tags:orderFormRow>
        <tags:orderFormRow name="lastName" label="Last name" order="${order}" errors="${errors}"></tags:orderFormRow>
        <tags:orderFormRow name="deliveryAddress" label="Delivery Address" order="${order}" errors="${errors}"></tags:orderFormRow>
         <tr>
              <td>
                   Phone:<span style="color:red">*</span>
              </td>
              <td>
              <input type="tel"
                         name="phone"
                         placeholder="+375 29 355-58-89"
                         pattern="^\+\d[\d\s\-\(\)]{9,20}$"
                         value="${param['phone']}"
                         required/>
              <c:if test="${not empty errors['phone']}">
                   <div class="error">
                         ${errors['phone']}
                   </div>
              </c:if>
              </td>
        </tr>
        <tr>
             <td>
                  Delivery Date<span style="color:red">*</span>
             </td>
             <td>
                <input type="date"
                            name="deliveryDate"
                            value="${param['deliveryDate']}"
                            required/>
                <c:if test="${not empty errors['deliveryDate']}">
                    <div class="error">
                        ${errors['deliveryDate']}
                    </div>
                </c:if>
            </td>
        </tr>
        <tr>
            <td>
                Payment method<span style="color:red">*</span>
            </td>
            <td>
                <select name="paymentMethod">
                    <option></option>
                    <c:forEach var="paymentMethod" items="${paymentMethods}">
                        <option <c:if test="${param.paymentMethod == paymentMethod}"> selected </c:if> >
                            ${paymentMethod}
                        </option>
                    </c:forEach>
                </select>
                <c:if test="${not empty errors['paymentMethod']}">
                     <div class="error">
                         ${errors['paymentMethod']}
                     </div>
                </c:if>
            </td>
        </tr>
    </table>
    <button>Checkout</button>
  </form>

</tags:master>
