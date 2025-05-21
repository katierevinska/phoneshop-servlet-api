<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.List" scope="request" />

<tags:master pageTitle="Advanced Product Search">
    <p>Advanced Product Search</p>

    <form method="GET">
        Query: <input name="query" value="${param.query}">

        <select name="searchMode">
            <option value="any" ${param.searchMode == 'any' || empty param.searchMode ? 'selected' : ''}>any words</option>
            <option value="all" ${param.searchMode == 'all' ? 'selected' : ''}>all words</option>
        </select>
        <br/><br/>

        Min Price: <input name="minPrice" value="${param.minPrice}" type="number" step="0.01">
        <c:if test="${not empty errorMinPrice}">
            <span style="color:red;">${errorMinPrice}</span>
        </c:if>
        <br/><br/>
        Max Price: <input name="maxPrice" value="${param.maxPrice}" type="number" step="0.01">
        <c:if test="${not empty errorMaxPrice}">
            <span style="color:red;">${errorMaxPrice}</span>
        </c:if>
        <br/><br/>
        <c:if test="${not empty errorPriceRange}">
            <div style="color:red;">${errorPriceRange}</div>
        </c:if>
        <button type="submit">Search</button>
    </form>
    <hr/>

    <c:if test="${not empty error}">
        <div class="error" style="color:red;">
            There was an error updating the product list: ${error}
        </div>
    </c:if>

    <c:choose>
        <c:when test="${not empty products}">
            <table>
                <thead>
                <tr>
                    <th>Image</th>
                    <th>Description</th>
                    <th class="price">Price</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                    <c:forEach var="product" items="${products}" varStatus="status">
                        <tr>
                            <td>
                                <c:if test="${not empty product.imageUrl}">
                                    <img class="product-tile" src="${pageContext.request.contextPath}/images/${product.imageUrl}" alt="${product.description}" width="50"/>
                                </c:if>
                            </td>
                            <td>
                                <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
                                        ${product.description}
                                </a>
                            </td>
                            <td class="price">
                                <c:if test="${not empty product.price && not empty product.currency}">
                                    <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
                                </c:if>
                            </td>
                            <td>

                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:when>
    </c:choose>
</tags:master>