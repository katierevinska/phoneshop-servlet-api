<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<meta http-equiv="refresh" content="5">

<jsp:useBean id="products" type="java.util.List" scope="request"/>
<jsp:useBean id="recentViewProducts" type="java.util.List" scope="request"/>
<tags:master pageTitle="Product List">
    <p>Welcome to Expert-Soft training!</p>

    <form>
        <input name="query" value="${param.query}">
        <button>Search</button>
    </form>

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
        </tr>
        </thead>
        <c:forEach var="product" items="${products}">
            <tr>
                <td>
                    <img class="product-tile" src="${product.imageUrl}" />
                </td>
                <td>
                    <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
                        ${product.description}
                    </a>
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
            </tr>
        </c:forEach>
    </table>
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
        document.addEventListener('DOMContentLoaded', function() {
            const priceLinks = document.querySelectorAll('.price-link');
            priceLinks.forEach(link => {
                link.addEventListener('click', function(e) {
                    e.preventDefault();
                    const productId = this.dataset.productId;
                    const modal = document.getElementById('modal-' + productId);
                    if (modal) {
                        modal.style.display = 'block';
                    }
                });
            });

            const closeButtons = document.querySelectorAll('.close-modal');
            closeButtons.forEach(btn => {
                btn.addEventListener('click', function() {
                    this.parentNode.style.display = 'none';
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