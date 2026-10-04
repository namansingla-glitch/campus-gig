<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="g" uri="http://gigmarket.com/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<t:layout title="My applications" active="applications">
    <div class="mb-10">
        <h1 class="text-3xl sm:text-4xl font-extrabold text-slate-900 tracking-tight">My applications</h1>
        <p class="text-lg text-slate-500 mt-2">Track every gig you've applied to.</p>
    </div>

    <c:choose>
        <c:when test="${not empty applications}">
            <div class="space-y-4">
                <c:forEach items="${applications}" var="a">
                    <div class="glass-card hoverable p-6 flex flex-col md:flex-row md:items-center justify-between gap-4">
                        <div class="min-w-0">
                            <h2 class="text-lg font-bold text-slate-900 break-words"><a href="${ctx}/gig?id=${a.gigId}" class="hover:text-blue-600"><c:out value="${a.gigTitle}"/></a></h2>
                            <p class="text-sm text-slate-500 mt-1">&#8377;<c:out value="${g:money(a.gigBudget)}"/> &middot; applied <c:out value="${g:ago(a.appliedAt)}"/>
                                <c:if test="${not empty a.gigCompletionDeadline and a.hired and a.gigStatus == 'IN_PROGRESS'}"> &middot; due <c:out value="${g:date(a.gigCompletionDeadline)}"/></c:if>
                            </p>
                            <c:if test="${not empty a.gigRating}">
                                <p class="text-sm mt-2"><span class="text-amber-500"><c:out value="${g:stars(a.gigRating)}"/></span> <span class="text-slate-500">rated by the poster</span></p>
                            </c:if>
                        </div>
                        <span class="inline-block px-3 py-1.5 text-xs font-bold rounded-full uppercase tracking-wide border shrink-0 ${g:statusClass(a.stage)}"><c:out value="${g:statusLabel(a.stage)}"/></span>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="glass-card p-16 text-center">
                <h2 class="text-xl font-bold text-slate-900 mb-2">No applications yet</h2>
                <p class="text-slate-500 mb-6">Browse open gigs and send your first pitch.</p>
                <a href="${ctx}/gigs" class="btn-primary px-6 py-2.5 rounded-full font-semibold">Explore gigs</a>
            </div>
        </c:otherwise>
    </c:choose>
</t:layout>
