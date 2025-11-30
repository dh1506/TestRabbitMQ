package com.se445g.SE_445_G_ETL.transformer;

import java.util.HashMap;
import java.util.Map;

public class DataTransformer {

    private static final Map<String, String> GENDER_MAP = new HashMap<>();
    private static final Map<String, String> EDUCATION_MAP = new HashMap<>();
    private static final Map<String, String> POSITION_MAP = new HashMap<>();

    static {
        // Giới tính
        GENDER_MAP.put("Nam", "M");
        GENDER_MAP.put("Nữ", "F");
        GENDER_MAP.put("Khác", "O");

        // Trình độ học vấn
        EDUCATION_MAP.put("Cử Nhân", "Bachelor");
        EDUCATION_MAP.put("Thạc Sĩ", "Master");
        EDUCATION_MAP.put("Tiến Sĩ", "Doctorate");
        EDUCATION_MAP.put("Trung cấp", "Intermediate");
        EDUCATION_MAP.put("Cao đẳng", "College");

        // Chức vụ
        POSITION_MAP.put("Chuyên viên", "Specialist");
        POSITION_MAP.put("Chuyên viên phân tích", "Analyst");
        POSITION_MAP.put("Nhân viên văn phòng", "Office Staff");
        POSITION_MAP.put("Trưởng nhóm", "Team Leader");
        POSITION_MAP.put("Kỹ sư", "Engineer");
        POSITION_MAP.put("Thực tập sinh", "Intern");
        POSITION_MAP.put("Quản lý", "Manager");
    }

    public static String normalizeGender(String gender) {
        return GENDER_MAP.getOrDefault(gender.trim(), "Other");
    }

    public static String normalizeEducation(String education) {
        return EDUCATION_MAP.getOrDefault(education.trim(), "Other");
    }

    public static String normalizePosition(String position) {
        return POSITION_MAP.getOrDefault(position.trim(), "Other");
    }

    public static String normalizeEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        return email.replace("example.com", "gmail.com").toLowerCase();
    }

    public static String normalizeStatus(String status) {
        if (status == null) {
            return null;
        }

        String s = status.trim().toLowerCase();
        return switch (s) {
            case "đang làm", "active" ->
                "Active";
            case "nghỉ việc", "left" ->
                "Inactive";
            default ->
                "Unknown";
        };
    }

    // ------------------ PHONE VALIDATE ------------------
    public static boolean isValidPhone(String phone) {
        if (phone == null) {
            return false;
        }

        // Chỉ nhận số 10 chữ số
        return phone.matches("\\d{10}");
    }
}
