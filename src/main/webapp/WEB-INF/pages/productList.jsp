<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.List" scope="request"/>
<jsp:useBean id="recentViewProducts" type="java.util.List" scope="request"/>
<tags:master pageTitle="Product List">
    <p>Welcome to Expert-Soft training!</p>

    <form>
        <input name="query" value="${param.query}">
        <button>Search</button>
    </form>
    <p>
         Cart:
     </p>
     <p>${cart}</p>
     <c:if test="${not empty message}">
          <div class="success">
              ${message}
          </div>
      </c:if>
      <c:if test="${not empty error}">
          <div class="error">
              There was an error updating the cart
          </div>
      </c:if>
    <form id="listForm" method="post" action="${pageContext.servletContext.contextPath}/products">
    <table>
        <thead>
        <tr>
            <td>Image</td>
            <td>
                Description
                <c:choose>
                    <c:when test="${not empty param.query and param.query.trim() != ''}">
                        <a href="?query=${param.query}&sort=description&order=asc">asc</a>
                    </c:when>
                    <c:otherwise>
                        <a href="?sort=description&order=asc">asc</a>
                    </c:otherwise>
                </c:choose>

                <c:choose>
                    <c:when test="${not empty param.query and param.query.trim() != ''}">
                        <a href="?query=${param.query}&sort=description&order=desc">desc</a>
                    </c:when>
                    <c:otherwise>
                        <a href="?sort=description&order=desc">desc</a>
                    </c:otherwise>
                </c:choose>
            </td>
            <td>Quantity</td>
            <td class="price">
                Price
                <c:choose>
                    <c:when test="${not empty param.query and param.query.trim() != ''}">
                        <a href="?query=${param.query}&sort=price&order=asc">asc</a>
                    </c:when>
                    <c:otherwise>
                        <a href="?sort=price&order=asc">asc</a>
                    </c:otherwise>
                </c:choose>

                <c:choose>
                    <c:when test="${not empty param.query and param.query.trim() != ''}">
                        <a href="?query=${param.query}&sort=price&order=desc">desc</a>
                    </c:when>
                    <c:otherwise>
                        <a href="?sort=price&order=desc">desc</a>
                    </c:otherwise>
                </c:choose>
            </td>
            <td>
            </td>
        </tr>
        </thead>
        <c:forEach var="product" items="${products}" varStatus="status">
            <tr data-product-id="${product.id}">
                <td>
                    <img class="product-tile" src="${product.imageUrl}" />
                </td>
                <td>
                    <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
                        ${product.description}
                    </a>
                </td>
                <td class="quantity">
                <input name="quantity" value="${not empty quantity and productId == product.id ? quantity : '1'}" >
                    <c:if test="${not empty error and productId == product.id}">
                        <div class="error">
                            ${error}
                        </div>
                    </c:if>
                </td>
                <td class="price">
                    <a href="#"
                       class="price-link"
                       data-product-id="${product.id}">
                        <fmt:formatNumber
                                value="${product.price}"
                                type="currency"
                                currencySymbol="${product.currency.symbol}"/>
                    </a>

                    <div class="price-history-modal"
                         id="modal-${product.id}"
                         style="display: none;">
                        <h2>Price history: ${product.description}</h2>
                        <table>
                            <thead>
                            <tr>
                                <th>Date</th>
                                <th>Price</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="historyItem" items="${product.priceHistory}">
                                <tr>
                                    <td>
                                        <fmt:formatDate value="${historyItem.dateFrom}"
                                                        pattern="dd MMM yyyy"/>
                                    </td>
                                    <td>
                                        <fmt:formatNumber
                                                value="${historyItem.price}"
                                                type="currency"
                                                currencySymbol="${historyItem.currency.symbol}"/>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                        <button class="close-modal">Close</button>
                    </div>
                </td>
                <td>
                    <button type="submit">Add to cart</button>
                </td>
            </tr>
        </c:forEach>
    </table>
    </form>
    <br />
    <table>
        <tr>
            <c:forEach var="recentViewProduct" items="${recentViewProducts}">
            <td>
                <img class="product-tile" src="${recentViewProduct.imageUrl}" /><br />
                <a href="${pageContext.servletContext.contextPath}/products/${recentViewProduct.id}">
                   ${recentViewProduct.description}
                </a><br />
                <fmt:formatNumber value="${recentViewProduct.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
            </td>
            </c:forEach>
        </tr>
    </table>

    <script>
document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll("#listForm button[type='submit']").forEach(button => {
        button.addEventListener("click", function (event) {
            event.preventDefault();

            let row = this.closest("tr");
            let productId = row.getAttribute('data-product-id');
            let quantityInput = row.querySelector("input[name='quantity']");
            let requestData = { productId: productId, quantity: quantityInput.value };

            fetch(document.getElementById("listForm").action, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(requestData),
                redirect: 'manual'
            })
            .then(response => {
                const redirectLocation = response.headers.get('Location');
                if (redirectLocation) {
                    window.location.href = redirectLocation;
                } else {
                    location.reload();
                }
            });
        });
    });
});

        document.addEventListener('DOMContentLoaded', function () {
            document.querySelectorAll('.quantity-input').forEach(input => {
                input.addEventListener('input', function () {
                    const productId = this.dataset.productId;
                    const quantity = this.value;
                    const button = document.querySelector(`.add-to-cart[data-product-id='${productId}']`);
                    if (button) {
                        button.setAttribute("formaction",
                            `${button.getAttribute("formaction").split('&quantity=')[0]}&quantity=${quantity}`
                        );
                    }
                });
            });
        });
        document.addEventListener("DOMContentLoaded", function () {
            document.querySelectorAll('.price-link').forEach(link => {
                link.addEventListener('click', function (e) {
                    e.preventDefault();
                    const productId = this.dataset.productId;
                    const modal = document.getElementById('modal-' + productId);
                    if (modal) {
                        modal.style.display = 'block';
                    }
                });
            });

            document.querySelectorAll('.close-modal').forEach(btn => {
                btn.addEventListener('click', function (e) {
                    e.preventDefault();
                    e.stopPropagation();
                    this.closest(".price-history-modal").style.display = "none";
                });
            });
        });
    </script>

    <style>
        .price-history-modal {
            position: fixed;
            top: 50%; left: 50%;
            transform: translate(-50%, -50%);
            background-color: #fff;
            border: 1px solid #ccc;
            padding: 1rem;
            z-index: 1000;
        }
    </style>
</tags:master>