package android.app;

public class ActivityThread {

    public static ActivityThread systemMain() {
        throw new RuntimeException();
    }

    public static ActivityThread currentActivityThread() {
        throw new RuntimeException();
    }

    public Application getApplication() {
        throw new RuntimeException();
    }

    public ContextImpl getSystemContext() {
        throw new RuntimeException();
    }
    public final LoadedApk peekPackageInfo(String packageName, boolean includeCode) {
        throw new RuntimeException();
        }
}
