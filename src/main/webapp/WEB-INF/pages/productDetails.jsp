<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:master pageTitle="Product Details">
    <div>
        <h2>Product Details</h2>
        <p>Here you can see details of the chosen product:</p>
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
        </table>

        <img src="${product.imageUrl}" alt="${product.description}" />
    </div>
</tags:master>