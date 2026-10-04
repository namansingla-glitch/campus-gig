<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="g" uri="http://gigmarket.com/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<t:layout title="My profile" active="profile">
    <div class="glass-card p-8 mb-8 flex flex-col sm:flex-row items-center sm:items-start gap-6">
        <div class="w-20 h-20 rounded-full bg-blue-100 text-blue-700 flex items-center justify-center text-3xl font-extrabold shrink-0"><c:out value="${g:initial(account.name)}"/></div>
        <div class="text-center sm:text-left min-w-0">
            <h1 class="text-2xl sm:text-3xl font-extrabold text-slate-900 break-words"><c:out value="${account.name}"/></h1>
            <p class="text-slate-500 break-all"><c:out value="${account.email}"/></p>
            <p class="text-sm text-slate-400 mt-1">Member since <c:out value="${g:date(account.createdAt)}"/></p>
            <div class="mt-3 flex flex-wrap justify-center sm:justify-start gap-2">
                <span class="px-3 py-1 rounded-full text-xs font-bold bg-blue-50 text-blue-700 border border-blue-200"><c:out value="${stats.tier}"/> tier</span>
                <c:if test="${myRank > 0}"><span class="px-3 py-1 rounded-full text-xs font-bold bg-slate-100 text-slate-600 border border-slate-200">Rank #<c:out value="${myRank}"/> of <c:out value="${studentCount}"/></span></c:if>
            </div>
        </div>
    </div>

    <div class="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <div class="glass-card p-5"><p class="text-xs font-bold text-slate-400 uppercase tracking-wider">Points</p><p class="text-3xl font-extrabold text-slate-900 mt-2"><c:out value="${stats.points}"/></p></div>
        <div class="glass-card p-5"><p class="text-xs font-bold text-slate-400 uppercase tracking-wider">Completed</p><p class="text-3xl font-extrabold text-slate-900 mt-2"><c:out value="${stats.completed}"/></p></div>
        <div class="glass-card p-5"><p class="text-xs font-bold text-slate-400 uppercase tracking-wider">Earnings</p><p class="text-3xl font-extrabold text-slate-900 mt-2">&#8377;<c:out value="${g:money(stats.earnings)}"/></p></div>
        <div class="glass-card p-5"><p class="text-xs font-bold text-slate-400 uppercase tracking-wider">Rating</p>
            <p class="text-3xl font-extrabold text-slate-900 mt-2">
                <c:choose><c:when test="${stats.reviewCount > 0}"><c:out value="${g:rating(stats.avgRating)}"/></c:when><c:otherwise>&ndash;</c:otherwise></c:choose>
            </p>
            <p class="text-xs text-slate-400"><c:out value="${stats.reviewCount}"/> review<c:if test="${stats.reviewCount != 1}">s</c:if></p>
        </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div class="glass-card p-6">
            <h2 class="font-bold text-slate-900 mb-4">Achievements (<c:out value="${unlockedCount}"/>/<c:out value="${achievements.size()}"/>)</h2>
            <ul class="space-y-3">
                <c:forEach items="${achievements}" var="ach">
                    <li class="flex items-start gap-3 ${ach.unlocked ? '' : 'opacity-50'}">
                        <span class="mt-0.5 w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold ${ach.unlocked ? 'bg-emerald-100 text-emerald-700' : 'bg-slate-100 text-slate-400'}">${ach.unlocked ? '&#10003;' : '&middot;'}</span>
                        <div><p class="font-semibold text-slate-800 text-sm"><c:out value="${ach.title}"/></p><p class="text-xs text-slate-500"><c:out value="${ach.description}"/></p></div>
                    </li>
                </c:forEach>
            </ul>
        </div>

        <div class="glass-card p-6">
            <h2 class="font-bold text-slate-900 mb-4">Leaderboard</h2>
            <ol class="divide-y divide-slate-100">
                <c:forEach items="${leaderboard}" var="e">
                    <li class="py-3 flex items-center justify-between text-sm ${e.userId == account.id ? 'font-bold text-blue-700' : 'text-slate-700'}">
                        <span>#<c:out value="${e.rank}"/> &nbsp;<c:out value="${e.name}"/></span>
                        <span><c:out value="${e.points}"/> pts</span>
                    </li>
                </c:forEach>
            </ol>
        </div>

        <div class="glass-card p-6 lg:col-span-2">
            <h2 class="font-bold text-slate-900 mb-4">Recent reviews</h2>
            <c:choose>
                <c:when test="${not empty reviews}">
                    <ul class="divide-y divide-slate-100">
                        <c:forEach items="${reviews}" var="r">
                            <li class="py-4">
                                <p class="text-amber-500"><c:out value="${g:stars(r.rating)}"/></p>
                                <c:if test="${not empty r.comment}"><p class="text-sm text-slate-700 mt-1 break-words">&ldquo;<c:out value="${r.comment}"/>&rdquo;</p></c:if>
                                <p class="text-xs text-slate-400 mt-1"><c:out value="${r.reviewerName}"/> &middot; <c:out value="${r.gigTitle}"/> &middot; <c:out value="${g:ago(r.createdAt)}"/></p>
                            </li>
                        </c:forEach>
                    </ul>
                </c:when>
                <c:otherwise><p class="text-sm text-slate-500">No reviews yet. Complete a gig to receive one.</p></c:otherwise>
            </c:choose>
        </div>
    </div>
</t:layout>
