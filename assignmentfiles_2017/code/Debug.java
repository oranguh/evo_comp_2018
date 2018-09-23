
public class Debug {
	public static boolean isOutputEnabled = false;

	public static void print (String str) {
		if (isOutputEnabled) {
			System.out.print(str);
		}
	}

	public static void println (String str) {
		if (isOutputEnabled) {
			System.out.println(str);
		}
	}

	public static void printf (String str, Object... args) {
		if (isOutputEnabled) {
			System.out.printf(str, args);
		}
	}
}