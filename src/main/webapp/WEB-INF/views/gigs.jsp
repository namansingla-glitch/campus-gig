<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="g" uri="http://gigmarket.com/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<t:layout title="Explore gigs" active="gigs">
    <div class="mb-10 text-center">
        <h1 class="text-3xl sm:text-4xl font-extrabold text-slate-900 tracking-tight mb-2">Explore Gigs</h1>
        <p class="text-lg text-slate-500">Discover paid micro-tasks and projects across campus.</p>
    </div>

    <div class="flex flex-col lg:flex-row gap-8">

        <!-- Filters -->
        <div class="w-full lg:w-72 shrink-0">
            <form action="${ctx}/gigs" method="get" class="glass-card p-6 lg:sticky lg:top-32 space-y-5">
                <h2 class="font-bold text-slate-900">Filters</h2>

                <div>
                    <label for="q" class="block text-sm font-semibold text-slate-700 mb-2">Search</label>
                    <input id="q" type="search" name="q" maxlength="100" value="<c:out value='${q}'/>" class="field" placeholder="Title or keywords">
                </div>

                <div>
                    <label for="category" class="block text-sm font-semibold text-slate-700 mb-2">Category</label>
                    <select id="category" name="category" class="field">
                        <option value="">All categories</option>
                        <c:forEach items="${categories}" var="cat">
                            <option value="<c:out value='${cat}'/>" ${cat == category ? 'selected' : ''}><c:out value="${cat}"/></option>
                        </c:forEach>
                    </select>
                </div>

                <div>
                    <label for="minBudget" class="block text-sm font-semibold text-slate-700 mb-2">Minimum budget (&#8377;)</label>
                    <input id="minBudget" type="number" name="minBudget" min="0" step="1" value="<c:out value='${minBudget}'/>" class="field" placeholder="e.g. 500">
                </div>

                <div>
                    <button type="submit" class="w-full btn-primary py-2.5 rounded-xl font-semibold shadow-md">Apply filters</button>
                    <a href="${ctx}/gigs" class="block text-center w-full mt-3 text-sm font-medium text-slate-500 hover:text-slate-800 transition-colors">Clear filters</a>
                </div>
            </form>
        </div>

        <!-- Results -->
        <div class="flex-1">
            <p class="text-sm font-medium text-slate-500 mb-4">
                <c:out value="${result.total}"/> open gig<c:if test="${result.total != 1}">s</c:if> found
            </p>

            <c:choose>
                <c:when test="${not empty result.items}">
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <c:forEach items="${result.items}" var="gig">
                            <div class="glass-card hoverable p-6 flex flex-col justify-between">
                                <div>
                                    <div class="flex justify-between items-start mb-4">
                                        <span class="inline-block px-2.5 py-1 bg-slate-100 text-slate-600 text-[10px] font-bold rounded uppercase tracking-wider"><c:out value="${gig.category}"/></span>
                                        <span class="text-xs font-semibold text-slate-400">Posted <c:out value="${g:ago(gig.createdAt)}"/></span>
                                    </div>
                                    <h2 class="text-xl font-bold text-slate-900 mb-2 leading-tight">
                                        <a href="${ctx}/gig?id=${gig.id}" class="hover:text-blue-600 transition-colors"><c:out value="${gig.title}"/></a>
                                    </h2>
                                    <p class="text-sm text-slate-600 line-clamp-2 mb-2 leading-relaxed"><c:out value="${gig.description}"/></p>
                                    <p class="text-xs text-slate-400 mb-6">by <c:out value="${gig.posterName}"/></p>
                                </div>

                                <div class="mt-auto border-t border-slate-100 pt-5">
                                    <div class="flex items-center justify-between mb-5">
                                        <span class="text-2xl font-extrabold text-slate-900 tracking-tight">&#8377;<c:out value="${g:money(gig.budget)}"/></span>
                                        <div class="text-right">
                                            <p class="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-0.5">Timeline</p>
                                            <p class="text-sm font-bold text-slate-700">
                                                <c:choose>
                                                    <c:when test="${not empty gig.deadlineNote}"><c:out value="${gig.deadlineNote}"/></c:when>
                                                    <c:otherwise>Flexible</c:otherwise>
                                                </c:choose>
                                            </p>
                                        </div>
                                    </div>
                                    <a href="${ctx}/gig?id=${gig.id}" class="btn-primary block w-full text-center py-2.5 rounded-xl font-semibold shadow-md">View gig</a>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <c:if test="${result.totalPages > 1}">
                        <div class="flex items-center justify-between mt-10">
                            <c:choose>
                                <c:when test="${result.hasPrevious}">
                                    <a href="${ctx}/gigs?${baseQuery}page=${result.page - 1}" class="btn-secondary px-5 py-2 rounded-full text-sm font-semibold">&larr; Previous</a>
                                </c:when>
                                <c:otherwise><span></span></c:otherwise>
                            </c:choose>
                            <span class="text-sm font-medium text-slate-500">Page <c:out value="${result.page}"/> of <c:out value="${result.totalPages}"/></span>
                            <c:choose>
                                <c:when test="${result.hasNext}">
                                    <a href="${ctx}/gigs?${baseQuery}page=${result.page + 1}" class="btn-secondary px-5 py-2 rounded-full text-sm font-semibold">Next &rarr;</a>
                                </c:when>
                                <c:otherwise><span></span></c:otherwise>
                            </c:choose>
                        </div>
                    </c:if>
                </c:when>
                <c:otherwise>
                    <div class="glass-card p-16 text-center flex flex-col items-center justify-center">
                        <div class="w-20 h-20 bg-slate-50 rounded-full flex items-center justify-center mb-6 shadow-inner">
                            <svg class="w-10 h-10 text-slate-300" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path></svg>
                        </div>
                        <h2 class="text-xl font-bold text-slate-900 mb-2">No gigs found</h2>
                        <p class="text-slate-500 mb-6">Try adjusting your filters or check back later.</p>
                        <a href="${ctx}/gigs" class="btn-secondary px-6 py-2.5 rounded-full font-semibold shadow-sm">Clear filters</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</t:layout>
