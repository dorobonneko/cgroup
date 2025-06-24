package android.content.pm;

public class UserInfo {

    public static final int FLAG_MANAGED_PROFILE = 0x00000020;

    public int id;
    public String name;
    public int flags;

    public String userType;
}
