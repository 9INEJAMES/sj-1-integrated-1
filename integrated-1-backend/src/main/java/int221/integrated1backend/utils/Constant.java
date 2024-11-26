package int221.integrated1backend.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constant {
    public final int TASK_TITLE_LIMIT = 100;
    public final int TASK_DESCRIPTION_LIMIT = 500;
    public final int TASK_ASSIGNEES_LIMIT = 30;
    public final int TASK_FILES_LIMIT = 10;
    public final int TASK_FILE_SIZE_LIMIT = 20 * 1024 * 1024; // 20MB

    public final int STATUS_NAME_LIMIT = 50;
    public final int STATUS_DESCRIPTION_LIMIT = 200;
    public final int BOARD_NAME_LIMIT = 120;

    public final String[] PUBLIC_ENDPOINTS = {
            "/login",
            "/login/oauth2/azure",
            "/login/oauth2/azure/callback",
            "/token"
    };

    public final String TASK_LIMIT_SETTING_KEY = "task_limit";
    public final String TASK_LIMIT_KEY = "limit";
    public final String TASK_UNLIMITED_STATUS_KEY = "task_unlimited_status";
}
