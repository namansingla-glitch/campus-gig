<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="g" uri="http://gigmarket.com/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="me" value="${sessionScope.user}"/>

<t:layout title="${gig.title}" active="gigs">
    <div class="mb-6">
        <a href="${ctx}/gigs" class="inline-flex items-center text-sm font-semibold text-slate-500 hover:text-slate-800 transition-colors">
            <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path></svg>
            Back to Explore
        </a>
    </div>

    <div class="flex flex-col lg:flex-row gap-8 items-start">

        <!-- LEFT: details and application -->
        <div class="flex-1 w-full space-y-8 min-w-0">

            <div class="glass-card p-8">
                <span class="inline-block px-3 py-1.5 bg-blue-50 text-blue-700 text-[10px] font-bold rounded-full uppercase tracking-wider mb-4"><c:out value="${gig.category}"/></span>
                <h1 class="text-3xl sm:text-4xl font-extrabold text-slate-900 tracking-tight mb-4 leading-tight break-words"><c:out value="${gig.title}"/></h1>

                <div class="text-sm font-medium text-slate-500 mb-8 border-b border-slate-100 pb-6">
                    Posted by <span class="font-semibold text-slate-700"><c:out value="${gig.posterName}"/></span>
                    &middot; <c:out value="${g:ago(gig.createdAt)}"/>
                </div>

                <h2 class="text-lg font-bold text-slate-900 mb-3">Project description</h2>
                <p class="mb-8 text-slate-600 leading-relaxed whitespace-pre-line break-words"><c:out value="${gig.description}"/></p>

                <h2 class="text-lg font-bold text-slate-900 mb-4">Timeline</h2>
                <div class="bg-slate-50 border border-slate-100 rounded-2xl p-6">
                    <c:choose>
                        <c:when test="${not empty gig.applicationDeadline or not empty gig.completionDeadline}">
                            <dl class="grid grid-cols-1 sm:grid-cols-3 gap-4 text-sm">
                                <div>
                                    <dt class="text-xs font-bold text-slate-400 uppercase tracking-wider">Posted</dt>
                                    <dd class="font-bold text-slate-800 mt-1"><c:out value="${g:date(gig.createdAt)}"/></dd>
                                </div>
                                <c:if test="${not empty gig.applicationDeadline}">
                                    <div>
                                        <dt class="text-xs font-bold text-slate-400 uppercase tracking-wider">Apply by</dt>
                                        <dd class="font-bold text-slate-800 mt-1"><c:out value="${g:date(gig.applicationDeadline)}"/></dd>
                                    </div>
                                </c:if>
                                <c:if test="${not empty gig.completionDeadline}">
                                    <div>
                                        <dt class="text-xs font-bold text-slate-400 uppercase tracking-wider">Finish by</dt>
                                        <dd class="font-bold text-slate-800 mt-1"><c:out value="${g:date(gig.completionDeadline)}"/></dd>
                                    </div>
                                </c:if>
                            </dl>
                        </c:when>
                        <c:otherwise>
                            <div class="text-sm">
                                <span class="font-bold text-slate-800">Flexible timeline</span>
                                <p class="text-xs text-slate-500 mt-1">Deadlines are agreed with the poster after hiring.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Application area -->
            <div id="apply-section">
                <c:choose>
                    <c:when test="${empty me}">
                        <div class="glass-card p-8 border-t-4 border-t-blue-600 text-center">
                            <h2 class="text-xl font-bold text-slate-900 mb-2">Want to apply?</h2>
                            <p class="text-slate-500 mb-6 text-sm">Log in with a student account to send your pitch and portfolio.</p>
                            <div class="flex justify-center gap-3">
                                <a href="${ctx}/login?next=%2Fgig%3Fid%3D${gig.id}" class="btn-primary px-6 py-2.5 rounded-full font-semibold">Log in</a>
                                <a href="${ctx}/register" class="btn-secondary px-6 py-2.5 rounded-full font-semibold">Create account</a>
                            </div>
                        </div>
                    </c:when>

                    <c:when test="${isOwner}">
                        <div class="glass-card p-8 border-t-4 border-t-blue-600 flex flex-col sm:flex-row items-center justify-between gap-4">
                            <div>
                                <h2 class="text-xl font-bold text-slate-900">This is your gig</h2>
                                <p class="text-slate-500 text-sm mt-1"><c:out value="${gig.applicationCount}"/> application<c:if test="${gig.applicationCount != 1}">s</c:if> so far.</p>
                            </div>
                            <a href="${ctx}/applicants?gigId=${gig.id}" class="btn-primary px-6 py-3 rounded-xl font-bold shadow-md">Review applicants</a>
                        </div>
                    </c:when>

                    <c:when test="${me.poster}">
                        <div class="glass-card p-8 text-center text-slate-500 text-sm">
                            Poster accounts can't apply to gigs. Log in with a student account to apply.
                        </div>
                    </c:when>

                    <c:when test="${not empty myApplication}">
                        <div class="glass-card p-8 border-t-4 border-t-blue-600">
                            <h2 class="text-xl font-bold text-slate-900 mb-2">You applied to this gig</h2>
                            <p class="text-sm text-slate-500 mb-4">Submitted <c:out value="${g:dateTime(myApplication.appliedAt)}"/>.</p>
                            <span class="inline-flex items-center rounded-full px-3 py-1 text-xs font-bold border uppercase tracking-wide ${g:statusClass(myApplication.status)}"><c:out value="${g:statusLabel(myApplication.status)}"/></span>
                            <a href="${ctx}/my-applications" class="ml-3 text-sm font-semibold text-blue-600 hover:text-blue-800">Track my applications &rarr;</a>
                        </div>
                    </c:when>

                    <c:when test="${gig.acceptingApplications}">
                        <div class="glass-card p-8 border-t-4 border-t-blue-600 shadow-xl shadow-blue-900/5">
                            <h2 class="text-2xl font-bold text-slate-900 mb-6 tracking-tight">Submit your application</h2>

                            <form action="${ctx}/gig/apply" method="post" enctype="multipart/form-data" class="space-y-6"
                                  x-data="{ fileName: '', fileError: '',
                                            pick(e) {
                                                const f = e.target.files[0];
                                                if (f && f.size > 5 * 1024 * 1024) { this.fileError = 'That file is larger than 5 MB.'; this.fileName = ''; e.target.value = ''; }
                                                else { this.fileError = ''; this.fileName = f ? f.name : ''; }
                                            } }">
                                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                                <input type="hidden" name="gigId" value="${gig.id}">

                                <div>
                                    <label for="pitchText" class="block text-sm font-semibold text-slate-800 mb-1">Your pitch <span class="text-blue-500">*</span></label>
                                    <p class="text-xs text-slate-500 mb-3">Explain why you're the best fit for this task (20-2000 characters).</p>
                                    <textarea id="pitchText" name="pitchText" required minlength="20" maxlength="2000" rows="5" class="field resize-y" placeholder="Hi! I have experience with..."></textarea>
                                </div>

                                <div>
                                    <label for="portfolioFile" class="block text-sm font-semibold text-slate-800 mb-1">Portfolio or resume <span class="text-blue-500">*</span></label>
                                    <p class="text-xs text-slate-500 mb-3">PDF, DOC or DOCX, up to 5 MB.</p>
                                    <div class="flex flex-wrap items-center gap-3 p-4 border-2 border-dashed border-slate-200 rounded-2xl bg-slate-50/50">
                                        <input id="portfolioFile" type="file" name="portfolioFile" required accept=".pdf,.doc,.docx" @change="pick($event)"
                                               class="text-sm text-slate-600 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border file:border-slate-200 file:bg-white file:text-sm file:font-semibold file:text-slate-700 hover:file:text-blue-600">
                                        <span x-show="fileName" x-cloak class="text-sm font-semibold text-blue-700" x-text="fileName"></span>
                                    </div>
                                    <p x-show="fileError" x-cloak class="text-sm font-semibold text-red-600 mt-2" x-text="fileError"></p>
                                </div>

                                <div class="pt-4 border-t border-slate-100">
                                    <button type="submit" :disabled="fileError !== ''" class="w-full btn-primary py-3.5 rounded-xl text-base font-bold shadow-lg shadow-blue-500/20">Submit application</button>
                                </div>
                            </form>
                        </div>
                    </c:when>

                    <c:otherwise>
                        <div class="glass-card p-8 text-center text-slate-500 text-sm">
                            This gig is no longer accepting applications.
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- RIGHT: summary -->
        <div class="w-full lg:w-80 shrink-0 lg:sticky lg:top-32">
            <div class="glass-card p-6 shadow-xl shadow-slate-900/5 border-t-4 border-t-blue-600">
                <div class="text-center pb-6 border-b border-slate-100 mb-6">
                    <p class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">Budget</p>
                    <div class="text-4xl font-extrabold text-slate-900">&#8377;<c:out value="${g:money(gig.budget)}"/></div>
                </div>

                <div class="space-y-4 text-sm">
                    <div class="flex justify-between items-center">
                        <span class="font-medium text-slate-500">Status</span>
                        <span class="inline-block px-2.5 py-1 text-xs font-bold rounded-full uppercase tracking-wider border ${g:statusClass(gig.status)}"><c:out value="${g:statusLabel(gig.status)}"/></span>
                    </div>
                    <div class="flex justify-between items-center">
                        <span class="font-medium text-slate-500">Applications</span>
                        <span class="font-bold text-slate-800"><c:out value="${gig.applicationCount}"/></span>
                    </div>
                    <c:if test="${not empty gig.deadlineNote}">
                        <div class="flex justify-between items-center">
                            <span class="font-medium text-slate-500">Apply by</span>
                            <span class="font-bold text-slate-800"><c:out value="${g:date(gig.applicationDeadline)}"/></span>
                        </div>
                    </c:if>
                    <c:if test="${not empty gig.completionDeadline}">
                        <div class="flex justify-between items-center">
                            <span class="font-medium text-slate-500">Finish by</span>
                            <span class="font-bold text-slate-800"><c:out value="${g:date(gig.completionDeadline)}"/></span>
                        </div>
                    </c:if>
                    <c:if test="${empty gig.applicationDeadline and empty gig.completionDeadline}">
                        <div class="flex justify-between items-center">
                            <span class="font-medium text-slate-500">Timeline</span>
                            <span class="font-bold text-slate-800">Flexible</span>
                        </div>
                    </c:if>
                </div>

                <c:if test="${gig.acceptingApplications and not isOwner and (empty me or me.student) and empty myApplication}">
                    <a href="#apply-section" class="btn-primary block w-full text-center py-3 rounded-xl font-bold shadow-md mt-8">Apply now</a>
                </c:if>
            </div>
        </div>
    </div>
</t:layout>
