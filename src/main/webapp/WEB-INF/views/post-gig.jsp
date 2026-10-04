<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<t:layout title="Create a gig" active="post">
    <div class="mb-10 text-center lg:text-left">
        <h1 class="text-3xl sm:text-4xl font-extrabold text-slate-900 tracking-tight">Create a gig</h1>
        <p class="text-lg text-slate-500 mt-2">Post a micro-task and find the right student for the job.</p>
    </div>

    <script>
        // Server-provided values arrive through data-* attributes (HTML-escaped by the server),
        // so user text is only ever treated as a plain string here.
        function gigForm() {
            return {
                title: '', category: 'Development', budget: '', description: '',
                hasTimeline: false, appDeadline: '', compDeadline: '',
                init() {
                    const d = this.$el.dataset;
                    this.title = d.title || '';
                    this.category = d.category || 'Development';
                    this.budget = d.budget || '';
                    this.description = d.description || '';
                    this.hasTimeline = d.hasTimeline === 'true';
                    this.appDeadline = d.deadline || '';
                    this.compDeadline = d.completion || '';
                },
                get timelineInvalid() {
                    return this.hasTimeline && this.appDeadline && this.compDeadline && this.compDeadline <= this.appDeadline;
                },
                fmt(v) {
                    return v ? new Date(v + 'T00:00:00').toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' }) : '--';
                }
            };
        }
    </script>

    <div class="flex flex-col lg:flex-row gap-10" x-data="gigForm()"
         data-title="<c:out value='${form.title}'/>"
         data-category="<c:out value='${form.category}'/>"
         data-budget="<c:out value='${form.budget}'/>"
         data-description="<c:out value='${form.description}'/>"
         data-has-timeline="<c:out value='${form.hasTimeline}'/>"
         data-deadline="<c:out value='${form.deadline}'/>"
         data-completion="<c:out value='${form.completionDeadline}'/>">

        <!-- LEFT: form -->
        <div class="flex-1 max-w-2xl">
            <form action="${ctx}/post-gig" method="post" class="glass-card p-8 space-y-6">
                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">

                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div class="md:col-span-2">
                        <label for="title" class="block text-sm font-semibold text-slate-800 mb-2">Gig title</label>
                        <input id="title" type="text" name="title" required minlength="3" maxlength="200" x-model="title" class="field" placeholder="e.g. Build a student portal">
                    </div>

                    <div>
                        <label for="category" class="block text-sm font-semibold text-slate-800 mb-2">Category</label>
                        <select id="category" name="category" required x-model="category" class="field">
                            <c:forEach items="${categories}" var="cat">
                                <option value="<c:out value='${cat}'/>"><c:out value="${cat}"/></option>
                            </c:forEach>
                        </select>
                    </div>

                    <div>
                        <label for="budget" class="block text-sm font-semibold text-slate-800 mb-2">Budget (&#8377;)</label>
                        <input id="budget" type="number" name="budget" required min="1" max="1000000" step="0.01" x-model="budget" class="field" placeholder="0.00">
                    </div>
                </div>

                <div>
                    <label for="description" class="block text-sm font-semibold text-slate-800 mb-2">Description</label>
                    <textarea id="description" name="description" required minlength="10" maxlength="5000" rows="5" x-model="description" class="field resize-y"
                              placeholder="Describe the task, requirements and any skills needed..."></textarea>
                </div>

                <div class="bg-slate-50 border border-slate-100 rounded-xl p-5">
                    <label class="flex items-center gap-3 cursor-pointer mb-4">
                        <input type="checkbox" name="hasTimeline" value="true" x-model="hasTimeline" class="w-5 h-5 rounded border-slate-300 text-blue-600 focus:ring-blue-500">
                        <span class="text-sm font-semibold text-slate-800">Set an application &amp; completion timeline</span>
                    </label>

                    <div x-show="!hasTimeline" class="text-sm font-medium text-slate-500 bg-white border border-slate-100 px-4 py-3 rounded-lg shadow-sm">
                        Flexible timeline - deadlines are agreed with the hired student later.
                    </div>

                    <!-- x-if removes these inputs from the page when unticked, so their values are never submitted -->
                    <template x-if="hasTimeline">
                        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label for="deadline" class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2">Application deadline</label>
                                <input id="deadline" type="date" name="deadline" required min="<c:out value='${today}'/>" x-model="appDeadline" class="field">
                            </div>
                            <div>
                                <label for="completionDeadline" class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2">Completion deadline</label>
                                <input id="completionDeadline" type="date" name="completionDeadline" required min="<c:out value='${today}'/>" x-model="compDeadline" class="field">
                            </div>
                        </div>
                    </template>

                    <p x-show="timelineInvalid" x-cloak class="mt-3 text-xs font-bold text-red-600 bg-red-50 p-2 rounded-lg border border-red-100">
                        The completion deadline must be after the application deadline.
                    </p>
                </div>

                <div class="pt-6 border-t border-slate-100 flex justify-end">
                    <button type="submit" :disabled="timelineInvalid" class="btn-primary px-8 py-3.5 rounded-xl font-bold shadow-lg shadow-blue-500/20 w-full md:w-auto">Post gig</button>
                </div>
            </form>
        </div>

        <!-- RIGHT: live preview -->
        <div class="hidden lg:block w-80 shrink-0">
            <div class="sticky top-32">
                <h2 class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-4 ml-2">Live preview</h2>

                <div class="glass-card p-6 border-t-4 border-t-blue-500 shadow-xl shadow-slate-900/5">
                    <div class="mb-4">
                        <span class="inline-block px-2.5 py-1 bg-blue-50 text-blue-700 text-[10px] font-bold rounded uppercase tracking-wider" x-text="category"></span>
                    </div>
                    <h3 class="text-lg font-bold text-slate-900 leading-tight mb-3 break-words" x-text="title || 'Gig title'"></h3>
                    <p class="text-sm text-slate-500 line-clamp-3 mb-6 break-words" x-text="description || 'Describe the micro-task here...'"></p>

                    <div class="border-t border-slate-100 pt-5 pb-5">
                        <div class="text-3xl font-extrabold text-slate-900">&#8377;<span x-text="budget || '0'"></span></div>
                    </div>

                    <div class="bg-slate-50 rounded-xl p-4 border border-slate-100 text-sm">
                        <div x-show="!hasTimeline" class="font-medium text-slate-600 text-center">Flexible timeline</div>
                        <div x-show="hasTimeline" x-cloak class="space-y-3">
                            <div class="flex justify-between items-center pb-3 border-b border-slate-200">
                                <span class="text-slate-500 text-xs font-semibold uppercase">Apply by</span>
                                <span class="font-bold text-slate-800" x-text="fmt(appDeadline)"></span>
                            </div>
                            <div class="flex justify-between items-center">
                                <span class="text-slate-500 text-xs font-semibold uppercase">Finish by</span>
                                <span class="font-bold text-slate-800" x-text="fmt(compDeadline)"></span>
                            </div>
                        </div>
                    </div>

                    <div class="mt-4 flex justify-between items-center px-1">
                        <span class="text-xs font-semibold text-emerald-600">OPEN</span>
                        <span class="text-xs font-medium text-slate-400">Just now</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</t:layout>
