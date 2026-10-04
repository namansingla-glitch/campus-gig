<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="code" value="${pageContext.errorData.statusCode}"/>

<t:layout title="Something went wrong">
    <div class="glass-card p-16 text-center max-w-2xl mx-auto">
        <p class="text-6xl font-extrabold text-blue-600 mb-4"><c:out value="${code == 0 ? 500 : code}"/></p>
        <h1 class="text-2xl font-bold text-slate-900 mb-2">
            <c:choose>
                <c:when test="${code == 404}">Page not found</c:when>
                <c:when test="${code == 403}">Access denied</c:when>
                <c:otherwise>Something went wrong</c:otherwise>
            </c:choose>
        </h1>
        <p class="text-slate-500 mb-8">
            <c:choose>
                <c:when test="${code == 404}">We couldn't find what you were looking for.</c:when>
                <c:when test="${code == 403}">
                    <c:choose>
                        <c:when test="${not empty requestScope['javax.servlet.error.message']}"><c:out value="${requestScope['javax.servlet.error.message']}"/></c:when>
                        <c:otherwise>You don't have permission to view this page.</c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>An unexpected error occurred. Please try again in a moment.</c:otherwise>
            </c:choose>
        </p>
        <a href="${ctx}/" class="btn-primary px-6 py-2.5 rounded-full font-semibold">Back to home</a>
    </div>
</t:layout>
