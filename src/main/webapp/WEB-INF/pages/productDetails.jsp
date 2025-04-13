<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:master pageTitle="Product Details">
<p>
    Cart:
</p>
<p>${cart}</p>
 <p>
     <a href="${pageContext.servletContext.contextPath}/products">
         Return to products list
     </a>
 </p>
 <c:if test="${not empty param.message}">
    <div class="success">
        ${param.message}
    </div>
</c:if>
<c:if test="${not empty param.error}">
    <div class="error">
        There was an error adding to the cart
    </div>
</c:if>
    <div>
        <h2>Product Details</h2>
        <p>Here you can see details of the chosen product:</p>
        <form method="post">
        <table>
            <tr>
                <td>
                    <h5>Product Code:</h5>
                </td>
                <td>
                    <p>${product.code}</p>
                </td>
            </tr>
            <tr>
                <td>
                    <h5>Description:</h5>
                </td>
                <td>
                    <p>${product.description}</p>
                </td>
            </tr>
            <tr>
                <td>
                    <h5>Stock:</h5>
                </td>
                <td>
                    <p>${product.stock}</p>
                </td>
            </tr>
            <tr>
                <td>
                    <h5>Price:</h5>
                </td>
                <td class="price">
                    <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}" />
                </td>
            </tr>
             <tr>
                <td>
                    <h5>Quantity:</h5>
                </td>
                <c:set var="quantityTmp" value="${param.quantity != null && !param.quantity.isEmpty() ? param.quantity : param.productQuantity}" />
                <c:set var="finalQuantity" value="${quantityTmp != null && !quantityTmp.isEmpty() ? quantityTmp : '1'}" />

                <td class="quantity">
                    <input name="quantity" value="${finalQuantity}">
                    <c:if test="${not empty param.error}">
                       <div class="error">
                          ${param.error}
                       </div>
                    </c:if>
                </td>
             </tr>
        </table>
        <button>Add to cart</button>
        </form>

        <img src="${product.imageUrl}" alt="${product.description}" />
    </div>
</tags:master>