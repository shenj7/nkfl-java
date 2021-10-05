
public class CommonMethods {
	public static int[] copyArray(int[] source)
	{
		int[] cpy = new int[source.length];
		for(int i = 0; i < source.length; i++)
		{
			cpy[i] = source[i];
		}
		return cpy;
	}
	
	public static int[] randomIntArray(int length, int maxValue)
	{
		int[] arr = new int[length];
		for(int i = 0; i < length; i++)
		{
			arr[i] = Math.abs(SeededRandom.rnd.nextInt() % (maxValue+1));
		}
		return arr;
	}
	
	public static String arrayAsString(int[] array)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(int i : array)
		{
			sb.append(i);
			sb.append(",");
		}
		return sb.toString().substring(0, sb.length()-1) + "]";
	}
}
