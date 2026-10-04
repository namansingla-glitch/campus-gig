<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="g" uri="http://gigmarket.com/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<t:layout title="Dashboard" active="dashboard">
    <div class="mb-10 flex flex-col sm:flex-row sm:items-end justify-between gap-4">
        <div>
            <h1 class="text-3xl sm:text-4xl font-extrabold text-slate-900 tracking-tight">Poster dashboard</h1>
            <p class="text-lg text-slate-500 mt-2">Welcome back, <c:out value="${sessionScope.user.name}"/>.</p>
        </div>
        <a href="${ctx}/post-gig" class="btn-primary px-6 py-3 rounded-xl font-bold shadow-md text-center">Post a new gig</a>
    </div>

    <div class="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <div class="glass-card p-5"><p class="text-xs font-bold text-slate-400 uppercase tracking-wider">Total gigs</p><p class="text-3xl font-extrabold text-slate-900 mt-2"><c:out value="${stats.totalGigs}"/></p></div>
        <div class="glass-card p-5"><p class="text-xs font-bold text-slate-400 uppercase tracking-wider">Open</p><p class="text-3xl font-extrabold text-slate-900 mt-2"><c:out value="${stats.openGigs}"/></p></div>
        <div class="glass-card p-5"><p class="text-xs font-bold text-slate-400 uppercase tracking-wider">In progress</p><p class="text-3xl font-extrabold text-slate-900 mt-2"><c:out value="${stats.inProgressGigs}"/></p></div>
        <div class="glass-card p-5"><p class="text-xs font-bold text-slate-400 uppercase tracking-wider">Completed</p><p class="text-3xl font-extrabold text-slate-900 mt-2"><c:out value="${stats.completedGigs}"/></p></div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div class="lg:col-span-2 glass-card p-6">
            <div class="flex items-center justify-between mb-4">
                <h2 class="font-bold text-slate-900">Recent gigs</h2>
                <a href="${ctx}/my-gigs" class="text-sm font-semibold text-blue-600">View all</a>
            </div>
            <c:choose>
                <c:when test="${not empty recentGigs}">
                    <ul class="divide-y divide-slate-100">
                        <c:forEach items="${recentGigs}" var="gig">
                            <li class="py-3 flex items-center justify-between gap-3">
                                <div class="min-w-0">
                                    <a href="${ctx}/applicants?gigId=${gig.id}" class="font-semibold text-slate-800 hover:text-blue-600 block truncate"><c:out value="${gig.title}"/></a>
                                    <span class="text-xs text-slate-500"><c:out value="${gig.applicationCount}"/> application<c:if test="${gig.applicationCount != 1}">s</c:if></span>
                                </div>
                                <span class="inline-block px-2.5 py-1 text-[10px] font-bold rounded-full uppercase border shrink-0 ${g:statusClass(gig.status)}"><c:out value="${g:statusLabel(gig.status)}"/></span>
                            </li>
                        </c:forEach>
                    </ul>
                </c:when>
                <c:otherwise><p class="text-sm text-slate-500">No gigs yet. <a href="${ctx}/post-gig" class="text-blue-600 font-semibold">Post your first one</a>.</p></c:otherwise>
            </c:choose>
        </div>

        <div class="space-y-6">
            <div class="glass-card p-6">
                <h2 class="font-bold text-slate-900 mb-4">Applications</h2>
                <p class="text-3xl font-extrabold text-slate-900"><c:out value="${stats.totalApplications}"/></p>
                <p class="text-sm text-slate-500 mt-1"><c:out value="${stats.pendingApplications}"/> awaiting your review</p>
                <c:if test="${not empty stats.avgApplicationsPerGig}">
                    <p class="text-xs text-slate-400 mt-3">Average <c:out value="${g:rating(stats.avgApplicationsPerGig)}"/> per gig</p>
                </c:if>
            </div>

            <div class="glass-card p-6">
                <h2 class="font-bold text-slate-900 mb-4">Top categories</h2>
                <c:choose>
                    <c:when test="${not empty categoryShares}">
                        <div class="space-y-3">
                            <c:forEach items="${categoryShares}" var="s">
                                <div>
                                    <div class="flex justify-between text-sm mb-1"><span class="font-medium text-slate-700"><c:out value="${s.category}"/></span><span class="text-slate-500"><c:out value="${s.count}"/></span></div>
                                    <div class="w-full h-2 bg-slate-100 rounded-full overflow-hidden"><div class="h-full bg-blue-500" style="width: ${s.percent}%"></div></div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise><p class="text-sm text-slate-500">Post gigs to see a breakdown.</p></c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</t:layout>
