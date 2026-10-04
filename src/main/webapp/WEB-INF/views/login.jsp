<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<t:layout title="Log in">
    <div class="max-w-md mx-auto mt-6">
        <div class="glass-card p-8">
            <h1 class="text-2xl font-extrabold text-slate-900 text-center mb-2 tracking-tight">Welcome back</h1>
            <p class="text-slate-500 text-center text-sm mb-8">Log in to find work or manage your gigs.</p>

            <c:if test="${param.loggedOut == '1'}">
                <div class="mb-6 p-3 rounded-xl bg-emerald-50 border border-emerald-100 text-emerald-700 text-sm font-semibold text-center">You have been logged out.</div>
            </c:if>

            <form action="${ctx}/login" method="post" class="space-y-5">
                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                <c:if test="${not empty next}">
                    <input type="hidden" name="next" value="<c:out value='${next}'/>">
                </c:if>

                <div>
                    <label for="email" class="block text-sm font-semibold text-slate-800 mb-1.5">Email address</label>
                    <input id="email" type="email" name="email" required autocomplete="email" autofocus
                           value="<c:out value='${email}'/>" class="field" placeholder="you@university.edu">
                </div>

                <div>
                    <label for="password" class="block text-sm font-semibold text-slate-800 mb-1.5">Password</label>
                    <input id="password" type="password" name="password" required autocomplete="current-password"
                           class="field" placeholder="Your password">
                </div>

                <button type="submit" class="w-full btn-primary py-3 rounded-xl font-bold shadow-md">Log in</button>
            </form>

            <p class="text-center text-sm text-slate-500 mt-8 font-medium">
                New to CampusGig? <a href="${ctx}/register" class="text-blue-600 font-bold hover:underline">Create an account</a>
            </p>
        </div>
    </div>
</t:layout>
