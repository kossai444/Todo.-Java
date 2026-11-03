import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * برنامج ادارة المهام TODO
 * - تحفظ المهام في ملف نصي tasks.txt بصيغة:  Status | Task
 * - Status اما TODO (حالية) او DONE (منجزة)
 *
 * الاستعمال:
 *   بدون وسائط  -> قائمة تفاعلية
 *   java Todo add "اشتري خبز"
 *   java Todo delete 2
 *   java Todo edit 2 "نص جديد"
 *   java Todo toggle 2
 *   java Todo all | done | current | help
 */
public class Todo {
    static final Path FILE = Paths.get("tasks.txt");
    static final String TODO = "TODO", DONE = "DONE";
    static List<String[]> tasks = new ArrayList<>(); // {status, task}

    // ---------- الملف ----------
    static void load() {
        tasks.clear();
        if (!Files.exists(FILE)) return;
        try {
            for (String line : Files.readAllLines(FILE, StandardCharsets.UTF_8)) {
                String[] p = line.split("\\s*\\|\\s*", 2);
                if (p.length == 2) tasks.add(new String[]{p[0].trim(), p[1].trim()});
            }
        } catch (IOException e) {
            System.out.println("خطأ في القراءة: " + e.getMessage());
        }
    }

    static void save() {
        List<String> lines = new ArrayList<>();
        for (String[] t : tasks) lines.add(t[0] + " | " + t[1]);
        try {
            Files.write(FILE, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("خطأ في الحفظ: " + e.getMessage());
        }
    }

    // ---------- العمليات ----------
    static void add(String text) {
        tasks.add(new String[]{TODO, text});
        save();
        System.out.println("تمت اضافة المهمة.");
    }

    static void delete(int n) {
        if (!valid(n)) return;
        tasks.remove(n - 1);
        save();
        System.out.println("تم حذف المهمة.");
    }

    static void edit(int n, String text) {
        if (!valid(n)) return;
        tasks.get(n - 1)[1] = text;
        save();
        System.out.println("تم تعديل المهمة.");
    }

    static void toggle(int n) {
        if (!valid(n)) return;
        String[] t = tasks.get(n - 1);
        t[0] = t[0].equals(DONE) ? TODO : DONE;
        save();
        System.out.println("الحالة الجديدة: " + t[0]);
    }

    /** filter = null => الكل */
    static void show(String filter) {
        boolean any = false;
        for (int i = 0; i < tasks.size(); i++) {
            String[] t = tasks.get(i);
            if (filter == null || t[0].equals(filter)) {
                System.out.printf("%d. [%s] %s%n", i + 1, t[0].equals(DONE) ? "x" : " ", t[1]);
                any = true;
            }
        }
        if (!any) System.out.println("لا توجد مهام.");
    }

    static boolean valid(int n) {
        if (n < 1 || n > tasks.size()) {
            System.out.println("رقم المهمة غير صحيح.");
            return false;
        }
        return true;
    }

    static void help() {
        System.out.println("add <مهمة> | delete <رقم> | edit <رقم> <نص> | toggle <رقم>");
        System.out.println("all | done | current | help");
    }

    // ---------- الوسائط (Arg) ----------
    static void runArgs(String[] a) {
        try {
            switch (a[0].toLowerCase()) {
                case "add":     add(String.join(" ", Arrays.copyOfRange(a, 1, a.length))); break;
                case "delete":  delete(Integer.parseInt(a[1])); break;
                case "edit":    edit(Integer.parseInt(a[1]),
                                     String.join(" ", Arrays.copyOfRange(a, 2, a.length))); break;
                case "toggle":  toggle(Integer.parseInt(a[1])); break;
                case "all":     show(null); break;
                case "done":    show(DONE); break;
                case "current": show(TODO); break;
                default:        help();
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("وسائط غير صحيحة.");
            help();
        }
    }

    // ---------- القائمة (Menu) ----------
    static void menu() {
        Scanner sc = new Scanner(System.in, "UTF-8");
        while (true) {
            System.out.println("\n===== برنامج ادارة المهام =====");
            System.out.println("1. اضافة مهمة");
            System.out.println("2. حذف مهمة");
            System.out.println("3. تعديل مهمة");
            System.out.println("4. تغيير حالة مهمة");
            System.out.println("5. عرض جميع المهام");
            System.out.println("6. عرض المهام المنجزة");
            System.out.println("7. عرض المهام الحالية");
            System.out.println("8. عرض قائمة الاوامر وشرحها");
            System.out.println("0. خروج");
            System.out.print("اختيارك: ");
            String c = sc.nextLine().trim();
            try {
                switch (c) {
                    case "1": System.out.print("المهمة: "); add(sc.nextLine().trim()); break;
                    case "2": System.out.print("الرقم: "); delete(Integer.parseInt(sc.nextLine().trim())); break;
                    case "3": System.out.print("الرقم: "); int n = Integer.parseInt(sc.nextLine().trim());
                              System.out.print("النص الجديد: "); edit(n, sc.nextLine().trim()); break;
                    case "4": System.out.print("الرقم: "); toggle(Integer.parseInt(sc.nextLine().trim())); break;
                    case "5": show(null); break;
                    case "6": show(DONE); break;
                    case "7": show(TODO); break;
                    case "8": help(); break;
                    case "0": return;
                    default:  System.out.println("خيار غير صحيح.");
                }
            } catch (NumberFormatException e) {
                System.out.println("ادخل رقما صحيحا.");
            }
        }
    }

    public static void main(String[] args) {
        load();
        if (args.length > 0) runArgs(args);
        else menu();
    }
}
