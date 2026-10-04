<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="g" uri="http://gigmarket.com/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<t:layout title="Applicants" active="mygigs">
    <div class="mb-6">
        <a href="${ctx}/my-gigs" class="inline-flex items-center text-sm font-semibold text-slate-500 hover:text-slate-800">&larr; Back to my gigs</a>
    </div>

    <div class="glass-card p-8 mb-8">
        <div class="flex flex-col md:flex-row md:items-start justify-between gap-6">
            <div class="min-w-0">
                <span class="inline-block px-3 py-1 text-[10px] font-bold rounded-full uppercase tracking-wider border ${g:statusClass(gig.status)}"><c:out value="${g:statusLabel(gig.status)}"/></span>
                <h1 class="text-2xl sm:text-3xl font-extrabold text-slate-900 tracking-tight mt-3 break-words"><c:out value="${gig.title}"/></h1>
                <p class="text-sm text-slate-500 mt-2"><c:out value="${gig.category}"/> &middot; &#8377;<c:out value="${g:money(gig.budget)}"/> &middot; <a class="text-blue-600 font-semibold" href="${ctx}/gig?id=${gig.id}">View public page</a></p>
            </div>

            <c:if test="${gig.inProgress}">
                <form action="${ctx}/gig/action" method="post" onsubmit="return confirm('Mark this gig as completed?');">
                    <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="gigId" value="${gig.id}">
                    <input type="hidden" name="action" value="complete">
                    <button type="submit" class="btn-primary px-6 py-3 rounded-xl font-bold shadow-md">Mark as completed</button>
                </form>
            </c:if>
        </div>

        <c:if test="${gig.completed and not empty hired}">
            <div class="mt-8 pt-6 border-t border-slate-100">
                <c:choose>
                    <c:when test="${not empty review}">
                        <h2 class="text-sm font-bold text-slate-400 uppercase tracking-wider mb-2">Your rating of <c:out value="${hired.applicantName}"/></h2>
                        <p class="text-xl text-amber-500"><c:out value="${g:stars(review.rating)}"/></p>
                        <c:if test="${not empty review.comment}"><p class="text-sm text-slate-600 mt-1 break-words">&ldquo;<c:out value="${review.comment}"/>&rdquo;</p></c:if>
                    </c:when>
                    <c:otherwise>
                        <h2 class="text-lg font-bold text-slate-900 mb-4">Rate <c:out value="${hired.applicantName}"/></h2>
                        <form action="${ctx}/gig/action" method="post" class="space-y-4 max-w-md">
                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                            <input type="hidden" name="gigId" value="${gig.id}">
                            <input type="hidden" name="action" value="rate">
                            <select name="rating" required class="field">
                                <option value="">Choose a rating</option>
                                <option value="5">5 - Excellent</option>
                                <option value="4">4 - Good</option>
                                <option value="3">3 - Okay</option>
                                <option value="2">2 - Poor</option>
                                <option value="1">1 - Very poor</option>
                            </select>
                            <textarea name="comment" maxlength="500" rows="3" class="field resize-y" placeholder="Optional comment"></textarea>
                            <button type="submit" class="btn-primary px-6 py-2.5 rounded-xl font-bold">Submit rating</button>
                        </form>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>
    </div>

    <h2 class="text-xl font-bold text-slate-900 mb-4">Applicants (<c:out value="${applications.size()}"/>)</h2>

    <c:choose>
        <c:when test="${not empty applications}">
            <div class="space-y-4">
                <c:forEach items="${applications}" var="a">
                    <div class="glass-card p-6">
                        <div class="flex flex-col md:flex-row md:items-start justify-between gap-4">
                            <div class="flex items-start gap-4 min-w-0">
                                <div class="w-11 h-11 rounded-full bg-blue-100 text-blue-700 flex items-center justify-center font-bold shrink-0"><c:out value="${g:initial(a.applicantName)}"/></div>
                                <div class="min-w-0">
                                    <p class="font-bold text-slate-900"><c:out value="${a.applicantName}"/></p>
                                    <p class="text-xs text-slate-500">
                                        <c:choose>
                                            <c:when test="${a.applicantReviewCount > 0}"><span class="text-amber-500"><c:out value="${g:stars(a.applicantAvgRating)}"/></span> <c:out value="${g:rating(a.applicantAvgRating)}"/> (<c:out value="${a.applicantReviewCount}"/>)</c:when>
                                            <c:otherwise>No ratings yet</c:otherwise>
                                        </c:choose>
                                        &middot; applied <c:out value="${g:ago(a.appliedAt)}"/>
                                    </p>
                                    <p class="text-sm text-slate-600 mt-3 whitespace-pre-line break-words"><c:out value="${a.pitchText}"/></p>
                                    <c:if test="${a.hasPortfolio}">
                                        <a href="${ctx}/portfolio?applicationId=${a.id}" class="inline-block mt-3 text-sm font-semibold text-blue-600 hover:text-blue-800">Download <c:out value="${a.portfolioName}"/></a>
                                    </c:if>
                                </div>
                            </div>

                            <div class="flex flex-col items-start md:items-end gap-3 shrink-0">
                                <span class="inline-block px-3 py-1 text-[10px] font-bold rounded-full uppercase tracking-wider border ${g:statusClass(a.status)}"><c:out value="${g:statusLabel(a.status)}"/></span>
                                <c:if test="${a.pending and gig.open}">
                                    <div class="flex gap-2">
                                        <form action="${ctx}/application/action" method="post" onsubmit="return confirm('Hire this applicant? Other applicants will be notified as not selected.');">
                                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                                            <input type="hidden" name="applicationId" value="${a.id}">
                                            <input type="hidden" name="action" value="hire">
                                            <button type="submit" class="btn-primary px-4 py-2 rounded-lg text-sm font-semibold">Hire</button>
                                        </form>
                                        <form action="${ctx}/application/action" method="post">
                                            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                                            <input type="hidden" name="applicationId" value="${a.id}">
                                            <input type="hidden" name="action" value="reject">
                                            <button type="submit" class="btn-danger px-4 py-2 rounded-lg text-sm font-semibold">Reject</button>
                                        </form>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="glass-card p-12 text-center text-slate-500">No applications yet. Check back soon.</div>
        </c:otherwise>
    </c:choose>
</t:layout>
