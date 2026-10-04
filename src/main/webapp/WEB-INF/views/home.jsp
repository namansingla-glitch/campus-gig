<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="g" uri="http://gigmarket.com/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="me" value="${sessionScope.user}"/>

<t:layout>
    <div class="flex flex-col items-center text-center w-full relative pt-6" x-data="{ loaded: false }" x-init="setTimeout(() => loaded = true, 80)">

        <div class="absolute top-0 left-1/4 w-96 h-96 bg-blue-400/10 rounded-full blur-3xl -z-10 pointer-events-none"></div>
        <div class="absolute bottom-0 right-1/4 w-[28rem] h-[28rem] bg-pink-400/10 rounded-full blur-3xl -z-10 pointer-events-none"></div>

        <div class="mb-8 transition-all duration-700 transform" :class="loaded ? 'translate-y-0 opacity-100' : '-translate-y-4 opacity-0'">
            <span class="inline-flex items-center gap-2 px-4 py-1.5 rounded-full bg-blue-50 border border-blue-200 text-blue-700 text-sm font-semibold shadow-sm">
                <span class="w-2 h-2 rounded-full bg-blue-600"></span>
                The campus marketplace for micro-tasks
            </span>
        </div>

        <h1 class="text-5xl md:text-7xl font-extrabold text-slate-900 tracking-tight mb-6 transition-all duration-700 delay-100 transform" :class="loaded ? 'translate-y-0 opacity-100' : 'translate-y-4 opacity-0'">
            Find. <span class="text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-indigo-600">Work.</span> Grow.
        </h1>

        <p class="text-lg md:text-xl text-slate-500 max-w-2xl mx-auto mb-10 leading-relaxed transition-all duration-700 delay-200 transform" :class="loaded ? 'translate-y-0 opacity-100' : 'translate-y-4 opacity-0'">
            Discover paid micro-tasks, build your portfolio and collaborate on campus - or post a gig and hire a talented student.
        </p>

        <div class="flex flex-col sm:flex-row gap-4 mb-20 transition-all duration-700 delay-300 transform" :class="loaded ? 'translate-y-0 opacity-100' : 'translate-y-4 opacity-0'">
            <a href="${ctx}/gigs" class="btn-primary px-8 py-3.5 rounded-full text-base font-semibold shadow-lg shadow-blue-500/20">Explore Gigs</a>
            <c:choose>
                <c:when test="${empty me}">
                    <a href="${ctx}/register" class="btn-secondary px-8 py-3.5 rounded-full text-base font-semibold">Create an account</a>
                </c:when>
                <c:when test="${me.poster}">
                    <a href="${ctx}/post-gig" class="btn-secondary px-8 py-3.5 rounded-full text-base font-semibold">Post a Gig</a>
                </c:when>
                <c:otherwise>
                    <a href="${ctx}/dashboard" class="btn-secondary px-8 py-3.5 rounded-full text-base font-semibold">Go to my dashboard</a>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="w-full max-w-5xl text-left transition-all duration-1000 delay-500 transform relative z-10" :class="loaded ? 'translate-y-0 opacity-100' : 'translate-y-8 opacity-0'">
            <div class="flex items-end justify-between mb-5">
                <h2 class="text-lg font-bold text-slate-900 tracking-tight">Latest gigs</h2>
                <a href="${ctx}/gigs" class="text-sm font-semibold text-blue-600 hover:text-blue-800">See all &rarr;</a>
            </div>

            <c:choose>
                <c:when test="${not empty latestGigs}">
                    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
                        <c:forEach items="${latestGigs}" var="gig">
                            <a href="${ctx}/gig?id=${gig.id}" class="glass-card hoverable p-6 flex flex-col justify-between">
                                <div>
                                    <div class="text-[10px] font-bold tracking-wider text-blue-600 mb-2 uppercase"><c:out value="${gig.category}"/></div>
                                    <h3 class="text-lg font-bold text-slate-900 leading-tight mb-4"><c:out value="${gig.title}"/></h3>
                                </div>
                                <div class="flex justify-between items-end">
                                    <div class="text-xl font-extrabold text-slate-800">&#8377;<c:out value="${g:money(gig.budget)}"/></div>
                                    <div class="text-xs font-medium text-slate-500">
                                        <c:choose>
                                            <c:when test="${not empty gig.deadlineNote}"><c:out value="${gig.deadlineNote}"/></c:when>
                                            <c:otherwise>Flexible timeline</c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </a>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="glass-card p-10 text-center text-slate-500">
                        No open gigs yet. Be the first to post one!
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</t:layout>
