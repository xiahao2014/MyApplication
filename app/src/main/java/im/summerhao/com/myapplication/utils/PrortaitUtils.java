package im.summerhao.com.myapplication.utils;


import im.summerhao.com.myapplication.R;

public class PrortaitUtils {
	private static int[] protraits=new int[]{R.drawable.portrait_0,
		R.drawable.portrait_1,R.drawable.portrait_2,R.drawable.portrait_3};

	public static int  conversionIdToRes(int id){
		if(isSystemProtraits(id)){
			return protraits[(int) id];
		}else{
			return protraits[0];
		}
	}

	public static boolean isSystemProtraits(int id){
		return id >= 0 && id < protraits.length;
	}
}
