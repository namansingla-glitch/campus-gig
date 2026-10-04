<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<t:layout title="Sign up">
    <div class="max-w-md mx-auto mt-6">
        <div class="glass-card p-8">
            <h1 class="text-2xl font-extrabold text-slate-900 text-center mb-2 tracking-tight">Create your account</h1>
            <p class="text-slate-500 text-center text-sm mb-8">Join the campus marketplace in under a minute.</p>

            <form action="${ctx}/register" method="post" class="space-y-4">
                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">

                <div>
                    <label for="name" class="block text-sm font-semibold text-slate-800 mb-1.5">Full name</label>
                    <input id="name" type="text" name="name" required minlength="2" maxlength="100" autocomplete="name"
                           value="<c:out value='${form.name}'/>" class="field" placeholder="Your name">
                </div>

                <div>
                    <label for="email" class="block text-sm font-semibold text-slate-800 mb-1.5">Email address</label>
                    <input id="email" type="email" name="email" required maxlength="190" autocomplete="email"
                           value="<c:out value='${form.email}'/>" class="field" placeholder="you@university.edu">
                </div>

                <div>
                    <label for="password" class="block text-sm font-semibold text-slate-800 mb-1.5">Password</label>
                    <input id="password" type="password" name="password" required minlength="8" maxlength="72" autocomplete="new-password"
                           class="field" placeholder="At least 8 characters">
                </div>

                <div>
                    <label for="confirmPassword" class="block text-sm font-semibold text-slate-800 mb-1.5">Confirm password</label>
                    <input id="confirmPassword" type="password" name="confirmPassword" required minlength="8" maxlength="72" autocomplete="new-password"
                           class="field" placeholder="Type it again">
                </div>

                <div>
                    <span class="block text-sm font-semibold text-slate-800 mb-1.5">I want to...</span>
                    <div class="grid grid-cols-2 gap-3 mt-1">
                        <label class="relative flex items-center justify-center p-3 border border-slate-200 rounded-xl cursor-pointer hover:bg-slate-50 transition-colors has-[:checked]:bg-blue-50 has-[:checked]:border-blue-500 has-[:checked]:text-blue-700">
                            <input type="radio" name="role" value="student" class="absolute opacity-0" ${form.role != 'poster' ? 'checked' : ''}>
                            <span class="text-sm font-bold">Find work</span>
                        </label>
                        <label class="relative flex items-center justify-center p-3 border border-slate-200 rounded-xl cursor-pointer hover:bg-slate-50 transition-colors has-[:checked]:bg-blue-50 has-[:checked]:border-blue-500 has-[:checked]:text-blue-700">
                            <input type="radio" name="role" value="poster" class="absolute opacity-0" ${form.role == 'poster' ? 'checked' : ''}>
                            <span class="text-sm font-bold">Post gigs</span>
                        </label>
                    </div>
                </div>

                <button type="submit" class="w-full btn-primary py-3 rounded-xl font-bold shadow-md mt-2">Create account</button>
            </form>

            <p class="text-center text-sm text-slate-500 mt-8 font-medium">
                Already have an account? <a href="${ctx}/login" class="text-blue-600 font-bold hover:underline">Log in</a>
            </p>
        </div>
    </div>
</t:layout>
