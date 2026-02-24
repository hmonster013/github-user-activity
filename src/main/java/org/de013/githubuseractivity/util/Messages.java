package org.de013.githubuseractivity.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Messages {
    private static final Path CONFIG_FILE = Paths.get("language.config");
    public static Language currentLanguage = Language.EN;

    public enum Language {
        VI, EN
    }

    private static final Map<String, Map<Language, String>> messages = new HashMap<>();

    static {
        loadLanguagePreference();
        initMessages();
    }

    private static void initMessages() {
        // Error messages
        addMessage("error.parsing_failed", "Command parsing failed. Please check your input.", "Phân tích lệnh thất bại. Vui lòng kiểm tra lại đầu vào.");

        // Default command
        addMessage("error.default.missing_username", "Error: Missing Username. Usage: github-activity <username>", "Lỗi: Thiếu Username. Cách dùng: github-activity <username>");

        // HTTP errors
        addMessage("error.http.not_found", "Error: User '{0}' not found.", "Lỗi: Không tìm thấy người dùng '{0}'.");
        addMessage("error.http.rate_limit", "Error: GitHub API rate limit exceeded. Please try again later.", "Lỗi: Đã vượt quá giới hạn GitHub API. Vui lòng thử lại sau.");
        addMessage("error.http.generic", "Error: GitHub API returned status {0}.", "Lỗi: GitHub API trả về trạng thái {0}.");
        addMessage("error.http.connection", "Error: Could not connect to GitHub API. Check your internet connection.", "Lỗi: Không thể kết nối GitHub API. Kiểm tra kết nối mạng.");

        // Language command
        addMessage("language.switched", "Language switched to English.", "Đã chuyển ngôn ngữ sang Tiếng Việt.");
        addMessage("error.language.invalid", "Error: Invalid language '{0}'. Use 'en' or 'vi'.", "Lỗi: Ngôn ngữ '{0}' không hợp lệ. Dùng 'en' hoặc 'vi'.");
        addMessage("error.language.missing_arg", "Error: Missing language argument. Usage: github-activity --language <en|vi>", "Lỗi: Thiếu tham số ngôn ngữ. Cách dùng: github-activity --language <en|vi>");

        // Help command
        addMessage("help.text",
                "Usage: github-activity <command> [options]\n\n" +
                "Commands:\n" +
                "  <username>                         Fetch recent GitHub activity for a user\n\n" +
                "Options:\n" +
                "  -h, --help                         Show this help message\n" +
                "  -l, --language <en|vi>             Switch display language (persisted)\n" +
                "  -f, --filter <type>                Filter activity by event type\n\n" +
                "Event types for --filter:\n" +
                "  push, watch, fork, create, delete,\n" +
                "  issues, pullrequest, release,\n" +
                "  issuecomment, commitcomment\n\n" +
                "Examples:\n" +
                "  github-activity kamranahmedse\n" +
                "  github-activity kamranahmedse --filter push\n" +
                "  github-activity --language vi\n" +
                "  github-activity --help\n",
                "Cách dùng: github-activity <lệnh> [tùy chọn]\n\n" +
                "Lệnh:\n" +
                "  <username>                         Lấy hoạt động GitHub gần đây của người dùng\n\n" +
                "Tùy chọn:\n" +
                "  -h, --help                         Hiển thị trợ giúp này\n" +
                "  -l, --language <en|vi>             Chuyển đổi ngôn ngữ hiển thị (được lưu lại)\n" +
                "  -f, --filter <type>                Lọc hoạt động theo loại sự kiện\n\n" +
                "Loại sự kiện cho --filter:\n" +
                "  push, watch, fork, create, delete,\n" +
                "  issues, pullrequest, release,\n" +
                "  issuecomment, commitcomment\n\n" +
                "Ví dụ:\n" +
                "  github-activity kamranahmedse\n" +
                "  github-activity kamranahmedse --filter push\n" +
                "  github-activity --language vi\n" +
                "  github-activity --help\n");

        // Event messages
        addMessage("event.push", "Pushed {0} commit(s) to {1}", "Đã đẩy {0} commit lên {1}");
        addMessage("event.watch", "Starred {0}", "Đã gắn sao {0}");
        addMessage("event.fork", "Forked {0}", "Đã fork {0}");
        addMessage("event.create", "Created {0} in {1}", "Đã tạo {0} trong {1}");
        addMessage("event.delete", "Deleted {0} in {1}", "Đã xóa {0} trong {1}");
        addMessage("event.issues.opened", "Opened an issue in {0}", "Đã mở issue trong {0}");
        addMessage("event.issues.closed", "Closed an issue in {0}", "Đã đóng issue trong {0}");
        addMessage("event.issues.other", "Updated an issue in {0}", "Đã cập nhật issue trong {0}");
        addMessage("event.pull_request.opened", "Opened a pull request in {0}", "Đã mở pull request trong {0}");
        addMessage("event.pull_request.closed", "Closed a pull request in {0}", "Đã đóng pull request trong {0}");
        addMessage("event.pull_request.other", "Updated a pull request in {0}", "Đã cập nhật pull request trong {0}");
        addMessage("event.release", "Published a release in {0}", "Đã phát hành release trong {0}");
        addMessage("event.issue_comment", "Commented on an issue in {0}", "Đã bình luận issue trong {0}");
        addMessage("event.commit_comment", "Commented on a commit in {0}", "Đã bình luận commit trong {0}");
        addMessage("event.member", "Became a collaborator on {0}", "Đã trở thành cộng tác viên trong {0}");
        addMessage("event.public", "Made {0} public", "Đã công khai {0}");
        addMessage("event.unknown", "Did something in {0}", "Đã thực hiện hành động trong {0}");

        // Info messages
        addMessage("info.no_activity", "No recent activity found for '{0}'.", "Không tìm thấy hoạt động gần đây của '{0}'.");
    }

    private static void addMessage(String key, String en, String vi) {
        Map<Language, String> translations = new HashMap<>();
        translations.put(Language.EN, en);
        translations.put(Language.VI, vi);
        messages.put(key, translations);
    }

    public static String get(String key, Object... params) {
        Map<Language, String> translations = messages.get(key);
        if (translations == null) {
            return key;
        }

        String message = translations.get(currentLanguage);
        if (message == null) {
            return key;
        }

        // Replace placeholders {0}, {1}, etc
        for (int i = 0; i < params.length; i++) {
            message = message.replace("{" + i + "}", String.valueOf(params[i]));
        }

        return message;
    }

    public static void setLanguage(Language language) {
        currentLanguage = language;
        saveLanguagePreference();
    }

    public static Language getCurrentLanguage() { return currentLanguage; }

    private static void loadLanguagePreference() {
        File file = new File(CONFIG_FILE.toString());
        if (!file.exists()) {
            return;
        }

        try {
            String lang = Files.readString(file.toPath()).trim();
            if ("VI".equalsIgnoreCase(lang)) {
                currentLanguage = Language.VI;
            } else if ("EN".equalsIgnoreCase(lang)) {
                currentLanguage = Language.EN;
            }
        } catch (IOException e) {
            // Use default language
        }
    }

    private static void saveLanguagePreference() {
        try {
            Files.writeString(CONFIG_FILE, currentLanguage.name());
        } catch (IOException e) {
            System.err.println("Cannot save language preference: " + e.getMessage());
        }
    }
}
