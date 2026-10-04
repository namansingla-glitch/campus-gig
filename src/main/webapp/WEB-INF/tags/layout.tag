<%@ tag description="CampusGig page shell: head, navigation, flash messages, footer" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="g" uri="http://gigmarket.com/functions" %>
<%@ attribute name="title" required="false" %>
<%@ attribute name="active" required="false" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="me" value="${sessionScope.user}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:if test="${not empty title}"><c:out value="${title}"/> - </c:if>CampusGig</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <script defer src="https://cdn.jsdelivr.net/npm/alpinejs@3.x.x/dist/cdn.min.js"></script>
    <style>
        [x-cloak] { display: none !important; }
        body {
            font-family: 'Inter', sans-serif;
            background-color: #fafafa;
            background-image:
                radial-gradient(circle at 0% 0%, rgba(59, 130, 246, 0.08), transparent 40%),
                radial-gradient(circle at 100% 100%, rgba(59, 130, 246, 0.05), transparent 40%);
            background-attachment: fixed;
            color: #334155;
            margin: 0;
            min-height: 100vh;
        }
        .floating-nav {
            background: rgba(255, 255, 255, 0.88);
            backdrop-filter: blur(16px);
            -webkit-backdrop-filter: blur(16px);
            border: 1px solid rgba(226, 232, 240, 0.8);
            border-radius: 20px;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -1px rgba(0, 0, 0, 0.03);
        }
        .nav-item { color: #64748b; font-weight: 500; transition: all 0.2s ease; white-space: nowrap; }
        .nav-item:hover { color: #0f172a; }
        .nav-item.active { color: #1d4ed8; background: #eff6ff; border-radius: 9999px; font-weight: 600; }
        .glass-card {
            background: rgba(255, 255, 255, 0.92);
            backdrop-filter: blur(20px);
            border: 1px solid rgba(15, 23, 42, 0.06);
            border-radius: 20px;
            box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.02);
        }
        .glass-card.hoverable { transition: transform 0.2s ease, box-shadow 0.2s ease; }
        .glass-card.hoverable:hover { transform: translateY(-2px); box-shadow: 0 12px 30px -5px rgba(0, 0, 0, 0.06); }
        .btn-primary { background-color: #2563eb; color: #fff; transition: all 0.2s ease; }
        .btn-primary:hover { background-color: #1d4ed8; transform: translateY(-1px); box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2); }
        .btn-primary:disabled { background-color: #94a3b8; cursor: not-allowed; transform: none; box-shadow: none; }
        .btn-secondary { background: rgba(255,255,255,0.9); color: #334155; border: 1px solid #e2e8f0; transition: all 0.2s ease; }
        .btn-secondary:hover { background: #fff; color: #0f172a; border-color: #cbd5e1; }
        .btn-danger { background: #fff; color: #b91c1c; border: 1px solid #fecaca; transition: all 0.2s ease; }
        .btn-danger:hover { background: #fef2f2; }
        .field {
            width: 100%; background: #fff; border: 1px solid #e2e8f0; border-radius: 12px;
            padding: 0.7rem 1rem; font-size: 0.875rem; outline: none; transition: box-shadow 0.15s ease, border-color 0.15s ease;
        }
        .field:focus { border-color: #3b82f6; box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.2); }
    </style>
</head>
<body class="antialiased flex flex-col items-center">

<header class="w-full max-w-6xl mx-auto px-4 mt-6 z-50 sticky top-4">
    <div class="floating-nav px-4 sm:px-6 py-3 flex flex-wrap items-center justify-between gap-x-6 gap-y-2">
        <a href="${ctx}/" class="flex items-center gap-2">
            <div class="w-8 h-8 rounded-full bg-blue-600 flex items-center justify-center shadow-sm">
                <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"></path></svg>
            </div>
            <span class="text-xl font-bold text-slate-900 tracking-tight">CampusGig</span>
        </a>

        <nav class="order-last basis-full md:basis-auto md:order-none md:flex-1 flex items-center gap-1 overflow-x-auto" aria-label="Main">
            <c:choose>
                <c:when test="${me.student}">
                    <a href="${ctx}/dashboard" class="nav-item px-4 py-2 text-sm ${active == 'dashboard' ? 'active' : ''}">Dashboard</a>
                    <a href="${ctx}/gigs" class="nav-item px-4 py-2 text-sm ${active == 'gigs' ? 'active' : ''}">Explore Gigs</a>
                    <a href="${ctx}/my-applications" class="nav-item px-4 py-2 text-sm ${active == 'applications' ? 'active' : ''}">My Applications</a>
                    <a href="${ctx}/profile" class="nav-item px-4 py-2 text-sm ${active == 'profile' ? 'active' : ''}">My Profile</a>
                </c:when>
                <c:when test="${me.poster}">
                    <a href="${ctx}/dashboard" class="nav-item px-4 py-2 text-sm ${active == 'dashboard' ? 'active' : ''}">Dashboard</a>
                    <a href="${ctx}/post-gig" class="nav-item px-4 py-2 text-sm ${active == 'post' ? 'active' : ''}">Create Gig</a>
                    <a href="${ctx}/my-gigs" class="nav-item px-4 py-2 text-sm ${active == 'mygigs' ? 'active' : ''}">My Gigs</a>
                    <a href="${ctx}/gigs" class="nav-item px-4 py-2 text-sm ${active == 'gigs' ? 'active' : ''}">Browse</a>
                </c:when>
                <c:otherwise>
                    <a href="${ctx}/gigs" class="nav-item px-4 py-2 text-sm ${active == 'gigs' ? 'active' : ''}">Explore Gigs</a>
                </c:otherwise>
            </c:choose>
        </nav>

        <div class="flex items-center gap-3">
            <c:choose>
                <c:when test="${empty me}">
                    <a href="${ctx}/login" class="text-sm font-medium text-slate-600 hover:text-slate-900 transition-colors">Log in</a>
                    <a href="${ctx}/register" class="btn-primary px-4 py-2 rounded-full text-sm font-semibold">Sign Up</a>
                </c:when>
                <c:otherwise>
                    <div class="flex items-center gap-3">
                        <div class="text-right hidden sm:block">
                            <div class="text-sm font-semibold text-slate-900 leading-none"><c:out value="${me.name}"/></div>
                            <div class="text-xs text-slate-500 mt-0.5 capitalize"><c:out value="${me.role}"/></div>
                        </div>
                        <div class="w-9 h-9 rounded-full bg-blue-100 text-blue-700 flex items-center justify-center font-bold shadow-sm"><c:out value="${g:initial(me.name)}"/></div>
                    </div>
                    <form action="${ctx}/logout" method="post" class="m-0">
                        <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                        <button type="submit" class="text-xs font-semibold text-slate-500 hover:text-red-600 transition-colors">Logout</button>
                    </form>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</header>

<main class="w-full max-w-6xl mx-auto px-4 mt-10 mb-20 flex-1">

    <c:if test="${not empty sessionScope.flashSuccess}">
        <div class="mb-8 p-4 rounded-xl bg-emerald-50 border border-emerald-100 text-emerald-700 text-sm font-medium max-w-2xl mx-auto shadow-sm" role="status">
            <c:out value="${sessionScope.flashSuccess}"/>
        </div>
        <c:remove var="flashSuccess" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.flashError}">
        <div class="mb-8 p-4 rounded-xl bg-red-50 border border-red-100 text-red-700 text-sm font-medium max-w-2xl mx-auto shadow-sm" role="alert">
            <c:out value="${sessionScope.flashError}"/>
        </div>
        <c:remove var="flashError" scope="session"/>
    </c:if>
    <c:if test="${not empty requestScope.error}">
        <div class="mb-8 p-4 rounded-xl bg-red-50 border border-red-100 text-red-700 text-sm font-medium max-w-2xl mx-auto shadow-sm" role="alert">
            <c:out value="${requestScope.error}"/>
        </div>
    </c:if>

    <jsp:doBody/>
</main>

<footer class="w-full py-8 mt-auto border-t border-slate-200 bg-white/60 backdrop-blur-md">
    <div class="max-w-6xl mx-auto px-4 flex flex-col sm:flex-row justify-between items-center gap-3 text-slate-500 text-sm">
        <div class="flex items-center gap-2">
            <div class="w-6 h-6 rounded-full bg-blue-600 flex items-center justify-center">
                <svg class="w-3 h-3 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"></path></svg>
            </div>
            <span class="font-bold text-slate-800">CampusGig</span>
            <span>&middot;</span>
            <span>Find. Work. Grow.</span>
        </div>
        <div>Short paid tasks, by students, for students.</div>
    </div>
</footer>

</body>
</html>
