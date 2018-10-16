
public class Debug {
	public static boolean isOutputEnabled = false;

	public static void print (String str) {
		if (isOutputEnabled) {
			System.out.print(str);
		}
	}
	public static void print (Object obj) {
		if (isOutputEnabled) {
			Debug.print(obj.toString());
		}
	}

	public static void println (String str) {
		if (isOutputEnabled) {
			System.out.println(str);
		}
	}
	public static void println (Object obj) {
		if (isOutputEnabled) {
			Debug.println(obj.toString());
		}
	}

	public static void printf (String str, Object... args) {
		if (isOutputEnabled) {
			System.out.printf(str, args);
		}
	}
}