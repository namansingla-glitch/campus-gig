package com.gigmarket.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Display helpers exposed to JSP pages through gigmarket.tld (prefix "g"). */
public final class Format {

    private static final Locale EN_IN = new Locale("en", "IN");

    private Format() {
    }

    /** 1500 -> "1,500"; 1500.5 -> "1,500.50". */
    public static String money(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        boolean whole = value.stripTrailingZeros().scale() <= 0;
        NumberFormat nf = NumberFormat.getNumberInstance(EN_IN);
        nf.setMinimumFractionDigits(whole ? 0 : 2);
        nf.setMaximumFractionDigits(whole ? 0 : 2);
        return nf.format(value);
    }

    public static String date(Date value) {
        return value == null ? "" : new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(value);
    }

    public static String dateTime(Date value) {
        return value == null ? "" : new SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.ENGLISH).format(value);
    }

    public static String ago(Date value) {
        if (value == null) {
            return "";
        }
        long diff = Math.max(0, System.currentTimeMillis() - value.getTime());
        long minutes = diff / 60_000L;
        if (minutes < 1) {
            return "just now";
        }
        if (minutes < 60) {
            return minutes + " min ago";
        }
        long hours = minutes / 60;
        if (hours < 24) {
            return hours + " h ago";
        }
        long days = hours / 24;
        if (days < 7) {
            return days == 1 ? "1 day ago" : days + " days ago";
        }
        return date(value);
    }

    public static String initial(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "?";
        }
        String trimmed = name.trim();
        return trimmed.substring(0, trimmed.offsetByCodePoints(0, 1)).toUpperCase(Locale.ROOT);
    }

    public static String statusLabel(String status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case "OPEN":
                return "Open";
            case "IN_PROGRESS":
                return "In progress";
            case "COMPLETED":
                return "Completed";
            case "PENDING":
                return "Pending";
            case "HIRED":
                return "Hired";
            case "REJECTED":
            case "NOT_SELECTED":
                return "Not selected";
            case "UNDER_REVIEW":
                return "Under review";
            default:
                return status;
        }
    }

    public static String statusClass(String status) {
        if (status == null) {
            return "bg-slate-100 text-slate-600 border-slate-200";
        }
        switch (status) {
            case "OPEN":
                return "bg-emerald-50 text-emerald-700 border-emerald-200";
            case "IN_PROGRESS":
            case "HIRED":
                return "bg-amber-50 text-amber-700 border-amber-200";
            case "COMPLETED":
                return "bg-indigo-50 text-indigo-700 border-indigo-200";
            case "PENDING":
            case "UNDER_REVIEW":
                return "bg-blue-50 text-blue-700 border-blue-200";
            default:
                return "bg-slate-100 text-slate-600 border-slate-200";
        }
    }

    public static String stars(double rating) {
        int filled = (int) Math.max(0, Math.min(5, Math.round(rating)));
        StringBuilder sb = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            sb.append(i < filled ? '★' : '☆');
        }
        return sb.toString();
    }

    public static String rating(double rating) {
        return String.format(Locale.ENGLISH, "%.1f", rating);
    }
}
