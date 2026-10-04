package com.gigmarket.util;

import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Stores uploaded portfolios outside the web root under random names.
 * Only .pdf, .doc and .docx are accepted, and the file's first bytes must match its extension.
 */
public final class FileStorage {

    public static final long MAX_BYTES = 5L * 1024 * 1024;

    private static final Pattern STORED_NAME = Pattern.compile("^[a-f0-9\\-]{36}\\.(pdf|doc|docx)$");

    private FileStorage() {
    }

    /** Result of a successful save. */
    public static final class StoredFile {
        private final String storedName;
        private final String originalName;

        StoredFile(String storedName, String originalName) {
            this.storedName = storedName;
            this.originalName = originalName;
        }

        public String getStoredName() {
            return storedName;
        }

        public String getOriginalName() {
            return originalName;
        }
    }

    public static StoredFile save(Part part) throws IOException {
        if (part == null || part.getSize() <= 0) {
            throw new BusinessException("Please attach your portfolio or resume.");
        }
        if (part.getSize() > MAX_BYTES) {
            throw new BusinessException("The file is too large. The limit is 5 MB.");
        }

        String originalName = cleanName(part.getSubmittedFileName());
        String ext = extensionOf(originalName);
        if (!ext.equals("pdf") && !ext.equals("doc") && !ext.equals("docx")) {
            throw new BusinessException("Only PDF, DOC or DOCX files are accepted.");
        }

        byte[] data;
        try (InputStream in = part.getInputStream()) {
            data = in.readAllBytes();
        }
        if (data.length == 0 || data.length > MAX_BYTES) {
            throw new BusinessException("The file is empty or larger than 5 MB.");
        }
        if (!looksLike(ext, data)) {
            throw new BusinessException("That file's contents do not match a ." + ext + " document.");
        }

        Path root = AppConfig.uploadDir();
        Files.createDirectories(root);
        String storedName = UUID.randomUUID() + "." + ext;
        Files.write(root.resolve(storedName), data);
        return new StoredFile(storedName, originalName);
    }

    /** Maps a stored name from the database to a safe path inside the upload folder, or null. */
    public static Path resolve(String storedName) {
        if (storedName == null || !STORED_NAME.matcher(storedName).matches()) {
            return null;
        }
        Path root = AppConfig.uploadDir();
        Path path = root.resolve(storedName).normalize();
        return path.startsWith(root) ? path : null;
    }

    public static void deleteQuietly(String storedName) {
        Path path = resolve(storedName);
        if (path != null) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException ignored) {
                // Nothing else to do; an orphaned file is harmless.
            }
        }
    }

    public static String contentTypeFor(String storedName) {
        String ext = extensionOf(storedName);
        switch (ext) {
            case "pdf":
                return "application/pdf";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            default:
                return "application/octet-stream";
        }
    }

    private static boolean looksLike(String ext, byte[] d) {
        switch (ext) {
            case "pdf":
                return d.length >= 4 && d[0] == '%' && d[1] == 'P' && d[2] == 'D' && d[3] == 'F';
            case "docx":
                return d.length >= 4 && d[0] == 'P' && d[1] == 'K' && d[2] == 3 && d[3] == 4;
            case "doc":
                return d.length >= 4 && (d[0] & 0xFF) == 0xD0 && (d[1] & 0xFF) == 0xCF
                        && (d[2] & 0xFF) == 0x11 && (d[3] & 0xFF) == 0xE0;
            default:
                return false;
        }
    }

    private static String extensionOf(String name) {
        if (name == null) {
            return "";
        }
        int dot = name.lastIndexOf('.');
        return dot < 0 ? "" : name.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    /** Keeps only the base name, drops control characters and quotes, and limits the length. */
    private static String cleanName(String submitted) {
        String name = submitted == null ? "" : submitted;
        int slash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        if (slash >= 0) {
            name = name.substring(slash + 1);
        }
        name = name.replaceAll("[\\p{Cntrl}\"<>|:*?]", "").trim();
        if (name.length() > 150) {
            String ext = extensionOf(name);
            name = name.substring(0, 140) + (ext.isEmpty() ? "" : "." + ext);
        }
        return name;
    }
}
