<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="g" uri="http://gigmarket.com/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<t:layout title="Dashboard" active="dashboard">
    <div class="mb-10">
        <h1 class="text-3xl sm:text-4xl font-extrabold text-slate-900 tracking-tight"><c:out value="${greeting}"/>, <c:out value="${sessionScope.user.name}"/></h1>
        <p class="text-lg text-slate-500 mt-2">Here's how your gigs are going.</p>
    </div>

    <div class="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <div class="glass-card p-5"><p class="text-xs font-bold text-slate-400 uppercase tracking-wider">Applied</p><p class="text-3xl font-extrabold text-slate-900 mt-2"><c:out value="${stats.applied}"/></p></div>
        <div class="glass-card p-5"><p class="text-xs font-bold text-slate-400 uppercase tracking-wider">In progress</p><p class="text-3xl font-extrabold text-slate-900 mt-2"><c:out value="${stats.inProgress}"/></p></div>
        <div class="glass-card p-5"><p class="text-xs font-bold text-slate-400 uppercase tracking-wider">Completed</p><p class="text-3xl font-extrabold text-slate-900 mt-2"><c:out value="${stats.completed}"/></p></div>
        <div class="glass-card p-5"><p class="text-xs font-bold text-slate-400 uppercase tracking-wider">Earnings</p><p class="text-3xl font-extrabold text-slate-900 mt-2">&#8377;<c:out value="${g:money(stats.earnings)}"/></p></div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div class="lg:col-span-2 space-y-6">
            <div class="glass-card p-6">
                <h2 class="font-bold text-slate-900 mb-6">Earnings, last 6 months</h2>
                <div class="flex items-end gap-3 h-44">
                    <c:forEach items="${months}" var="m">
                        <div class="flex-1 flex flex-col items-center justify-end h-full">
                            <span class="text-[10px] font-semibold text-slate-500 mb-1">&#8377;<c:out value="${g:money(m.amount)}"/></span>
                            <div class="w-full bg-blue-500 rounded-t-lg" style="height: ${m.percent}%; min-height: 4px;"></div>
                            <span class="text-xs font-medium text-slate-400 mt-2"><c:out value="${m.label}"/></span>
                        </div>
                    </c:forEach>
                </div>
            </div>

            <div class="glass-card p-6">
                <div class="flex items-center justify-between mb-4">
                    <h2 class="font-bold text-slate-900">Recent applications</h2>
                    <a href="${ctx}/my-applications" class="text-sm font-semibold text-blue-600">View all</a>
                </div>
                <c:choose>
                    <c:when test="${not empty recentApplications}">
                        <ul class="divide-y divide-slate-100">
                            <c:forEach items="${recentApplications}" var="a">
                                <li class="py-3 flex items-center justify-between gap-3">
                                    <a href="${ctx}/gig?id=${a.gigId}" class="font-semibold text-slate-800 hover:text-blue-600 truncate"><c:out value="${a.gigTitle}"/></a>
                                    <span class="inline-block px-2.5 py-1 text-[10px] font-bold rounded-full uppercase border shrink-0 ${g:statusClass(a.stage)}"><c:out value="${g:statusLabel(a.stage)}"/></span>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:when>
                    <c:otherwise><p class="text-sm text-slate-500">Nothing yet. <a href="${ctx}/gigs" class="text-blue-600 font-semibold">Find a gig</a>.</p></c:otherwise>
                </c:choose>
            </div>
        </div>

        <div class="space-y-6">
            <div class="glass-card p-6">
                <h2 class="font-bold text-slate-900 mb-4">Rewards</h2>
                <p class="text-3xl font-extrabold text-slate-900"><c:out value="${stats.points}"/> <span class="text-sm font-semibold text-slate-400">pts</span></p>
                <p class="text-sm font-semibold text-blue-700 mt-1"><c:out value="${stats.tier}"/> tier</p>
                <div class="w-full h-2 bg-slate-100 rounded-full mt-4 overflow-hidden"><div class="h-full bg-blue-500" style="width: ${stats.progressPercent}%"></div></div>
                <p class="text-xs text-slate-500 mt-2">
                    <c:choose>
                        <c:when test="${not empty stats.nextTier}"><c:out value="${stats.pointsToNext}"/> pts to <c:out value="${stats.nextTier}"/></c:when>
                        <c:otherwise>Top tier reached</c:otherwise>
                    </c:choose>
                </p>
            </div>

            <div class="glass-card p-6">
                <h2 class="font-bold text-slate-900 mb-4">Upcoming deadlines</h2>
                <c:choose>
                    <c:when test="${not empty deadlines}">
                        <ul class="space-y-3">
                            <c:forEach items="${deadlines}" var="d">
                                <li class="flex items-center justify-between gap-3 text-sm">
                                    <a href="${ctx}/gig?id=${d.gigId}" class="font-semibold text-slate-800 hover:text-blue-600 truncate"><c:out value="${d.gigTitle}"/></a>
                                    <span class="text-slate-500 shrink-0"><c:out value="${g:date(d.gigCompletionDeadline)}"/></span>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:when>
                    <c:otherwise><p class="text-sm text-slate-500">No upcoming deadlines.</p></c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</t:layout>
