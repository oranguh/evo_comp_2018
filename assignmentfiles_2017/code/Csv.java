import java.util.Arrays;
import java.util.StringJoiner;

public class Csv {
	public static boolean isOutputEnabled = true;

	public static void printHeader (String... headers) {
		if (isOutputEnabled) {
			System.out.println(String.join(";", headers));
		}
	}
	public static void printData (Object... data) {
		if (isOutputEnabled) {
			StringJoiner sj = new StringJoiner(";");
			Arrays.stream(data).forEach(x -> sj.add(x.toString()));
			System.out.println(sj.toString());
		}
	}
}