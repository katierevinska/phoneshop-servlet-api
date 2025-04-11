<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:master pageTitle="Cart">
 <p>
     <a href="${pageContext.servletContext.contextPath}/products">
         Return to products list
     </a>
 </p>
  <p></p>
  <c:choose>
    <c:when test="${empty cart.items}">
         <p>
              Cart is empty
         </p>
    </c:when>
 <c:otherwise>
 <p>
     Cart:
 </p>
 <c:if test="${not empty message}">
      <div class="success">
          ${message}
      </div>
  </c:if>
  <c:if test="${not empty errors}">
      <div class="error">
          There was an error updating the cart
      </div>
  </c:if>
  <form id="cartForm" method="post" action="${pageContext.servletContext.contextPath}/cart">
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
        <c:forEach var="cartItem" items="${cart.items}" varStatus="status">
            <tr data-product-id="${cartItem.product.id}">
                <td>
                    <img class="product-tile" src="${cartItem.product.imageUrl}" />
                </td>
                <td>
                    <a href="${pageContext.servletContext.contextPath}/products/${cartItem.product.id}">
                        ${cartItem.product.description}
                    </a>
                </td>
                <td class="price">
                    <fmt:formatNumber
                        value="${cartItem.product.price}"
                        type="currency"
                        currencySymbol="${cartItem.product.currency.symbol}"/>
                </td>
                <td class="quantity">
                 <fmt:formatNumber value="${cartItem.quantity}" var="quantity"/>
                 <input name="quantity" value="${not empty errors[cartItem.product.id] ? paramValues['quantity'][status.index] : cartItem.quantity}">
                   <c:if test="${not empty errors[cartItem.product.id]}">
                       <div class="error">
                          ${errors[cartItem.product.id]}
                       </div>
                    </c:if>
                    <input type="hidden" name="productId" value="${cartItem.product.id}">
                </td>
                <td>
                    <button form="deleteCartItem" formaction="${pageContext.servletContext.contextPath}/cart/deleteCartItem/${cartItem.product.id}">Delete</button>
                </td>

            </tr>
        </c:forEach>
        <tr>
            <td></td>
            <td class="quantity">Total quantity: ${cart.totalQuantity}</td>
            <td class="price">
                Total cost:
                 <p>
                     <fmt:formatNumber value="${cart.cartPrice}" type="currency"
                        currencySymbol="${cart.currency.symbol}"/>
                 </p>
            </td>
            <td></td>
        </tr>
    </table>
    <button type="submit" id="updateCartBtn">Update cart</button>
  </form>
  <form id="deleteCartItem" method="post">
      <input type="hidden" name="method" value="delete">
  </form>
  <form action="${pageContext.servletContext.contextPath}/checkout">
        <button>Checkout</button>
  </form>
   <script>
      document.getElementById('cartForm').addEventListener('submit', function(event) {
        event.preventDefault();

        let cartData = [];
        document.querySelectorAll("tr").forEach(row => {
            const productIdInput = row.getAttribute('data-product-id');
            const quantityInput = row.querySelector('input[name="quantity"]');
            if (quantityInput && productIdInput) {
                cartData.push({
                    productId: productIdInput,
                    quantity: quantityInput.value
                });
            }
        });

        fetch(this.action, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(cartData),
            redirect: 'manual'
        })
        .then(response => {
            const redirectLocation = response.headers.get('Location');
            if (redirectLocation) {
                window.location.href = redirectLocation;
            } else {
                location.reload();
            }
        })
      });
    </script>
     </c:otherwise>
   </c:choose>
</tags:master>
