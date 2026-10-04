<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="g" uri="http://gigmarket.com/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<t:layout title="My gigs" active="mygigs">
    <div class="flex flex-col sm:flex-row sm:items-end justify-between gap-4 mb-10">
        <div>
            <h1 class="text-3xl sm:text-4xl font-extrabold text-slate-900 tracking-tight">My gigs</h1>
            <p class="text-lg text-slate-500 mt-2">Manage your postings and review applicants.</p>
        </div>
        <a href="${ctx}/post-gig" class="btn-primary px-6 py-3 rounded-xl font-bold shadow-md text-center">Post a new gig</a>
    </div>

    <c:choose>
        <c:when test="${not empty gigs}">
            <div class="space-y-4">
                <c:forEach items="${gigs}" var="gig">
                    <div class="glass-card hoverable p-6 flex flex-col md:flex-row md:items-center justify-between gap-4">
                        <div class="min-w-0">
                            <div class="flex flex-wrap items-center gap-2 mb-2">
                                <span class="inline-block px-2.5 py-1 text-[10px] font-bold rounded-full uppercase tracking-wider border ${g:statusClass(gig.status)}"><c:out value="${g:statusLabel(gig.status)}"/></span>
                                <span class="text-xs font-semibold text-slate-400"><c:out value="${gig.category}"/> &middot; <c:out value="${g:ago(gig.createdAt)}"/></span>
                            </div>
                            <h2 class="text-lg font-bold text-slate-900 break-words"><a href="${ctx}/gig?id=${gig.id}" class="hover:text-blue-600"><c:out value="${gig.title}"/></a></h2>
                            <p class="text-sm text-slate-500 mt-1">
                                &#8377;<c:out value="${g:money(gig.budget)}"/> &middot;
                                <c:out value="${gig.applicationCount}"/> application<c:if test="${gig.applicationCount != 1}">s</c:if>
                                <c:if test="${gig.pendingCount > 0}">(<c:out value="${gig.pendingCount}"/> pending)</c:if>
                            </p>
                        </div>
                        <a href="${ctx}/applicants?gigId=${gig.id}" class="btn-secondary px-5 py-2.5 rounded-xl text-sm font-semibold text-center shrink-0">
                            <c:choose><c:when test="${gig.completed}">View details</c:when><c:otherwise>Manage</c:otherwise></c:choose>
                        </a>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="glass-card p-16 text-center">
                <h2 class="text-xl font-bold text-slate-900 mb-2">You haven't posted any gigs yet</h2>
                <p class="text-slate-500 mb-6">Post your first micro-task and start receiving applications.</p>
                <a href="${ctx}/post-gig" class="btn-primary px-6 py-2.5 rounded-full font-semibold">Post a gig</a>
            </div>
        </c:otherwise>
    </c:choose>
</t:layout>
