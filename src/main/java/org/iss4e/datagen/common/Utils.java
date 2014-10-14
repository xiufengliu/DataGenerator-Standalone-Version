package org.iss4e.datagen.common;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public final class Utils {
	public static void shuffleArray(int[] ar) {
		Random rnd = new Random();
		for (int i = ar.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			int a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
	}

	public static List<Integer> getShuffledNumbers(int num) {
		List<Integer> shuffledNums = new ArrayList<Integer>();
		for (int i = 0; i < num; i++) {
			shuffledNums.add(i);
		}
		Collections.shuffle(shuffledNums);
		return shuffledNums;
	}

	public static int getRandInt(int max) {
		return getRandIntRange(0, max);
	}

	public static int getRandIntRange(int min, int max) {
		return min + (int) (Math.random() * max);
	}

	public static double getNormalDistribution(double mean,
			double standardDeviation) {
		return standardDeviation * new Random().nextGaussian() + mean;
	}

	public static double[] getNormalDistributionArray(double[] seed,
			double standardDeviation) {
		double[] newArrays = new double[seed.length];
		for (int i = 0; i < seed.length; ++i) {
			newArrays[i] = standardDeviation * new Random().nextGaussian()
					+ seed[i];
		}
		return newArrays;
	}
    public static java.sql.Date toSqlDate(String dateStr) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        Date date = dateFormat.parse(dateStr);
        return new java.sql.Date(date.getTime());
    }

	public static boolean isWeekend(String dateStr) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			cal.setTime(sdf.parse(dateStr));
			int n = cal.get(Calendar.DAY_OF_WEEK);
			return n == Calendar.SATURDAY || n == Calendar.SUNDAY;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static int getPoisson(double lambda) {
		double L = Math.exp(-lambda);
		double p = 1.0;
		int k = 0;

		do {
			k++;
			p *= Math.random();
		} while (p > L);

		return k - 1;
	}

	public static boolean contains(char[] chars, char ch) {
		for (int c : chars) {
			if (ch == c)
				return true;
		}
		return false;
	}
	 
    public static String removeSpecialChar(String string, char[] chars) {
       if (string==null) return null;
       int len = string.length();
       StringBuffer buffer = new StringBuffer(len);
       boolean toremove = false;
       for (int i = 0; i < len; i++) {
           char c = string.charAt(i);
           toremove = false;
           for (char cc: chars){
               if (c==cc)  {
                   toremove = true;
                   break;
               }
           }
           if (!toremove)
               buffer.append(c);
       }
       return buffer.length()==0? null: buffer.toString();
     }
    
    
	public static String rtrimSpecialChar(String str, char[] chars) {
		if (str == null)
			return null;
		int i = str.length() - 1;
		for (; i >= 0; --i) {
			if (!contains(chars, str.charAt(i))) {
				break;
			}
		}
		return str.substring(0, i + 1);
	}

	public static String trimSpecialChar(String str, char[] chars) {
		if (str == null)
			return null;
		String subStr = null;
		for (int i = 0; i < str.length(); ++i) {
			if (contains(chars, str.charAt(i))) {
				continue;
			}
			subStr = str.substring(i);
			break;
		}
		return rtrimSpecialChar(subStr, chars);
	}

	public static boolean isWeekend(int startYear, int day) {
		Calendar cal = Calendar.getInstance();
		int actualStartYear = startYear + day / 365;
		int actualDayOfYear = day % 365;
		cal.set(Calendar.YEAR, actualStartYear);
		cal.set(Calendar.DAY_OF_YEAR, actualDayOfYear);
		int n = cal.get(Calendar.DAY_OF_WEEK);
		return n == Calendar.SATURDAY || n == Calendar.SUNDAY;
	}

	public static String getDate(int startYear, int day) {
		Calendar cal = Calendar.getInstance();
		int actualStartYear = startYear + day / 365;
		int actualDayOfYear = day % 365;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		cal.set(Calendar.YEAR, actualStartYear);
		cal.set(Calendar.DAY_OF_YEAR, actualDayOfYear);
		String date = format.format(cal.getTime());
		return date;
	}

	// getting the maximum value
	public static double getMaxValue(double[] array) {
		double maxValue = array[0];
		for (int i = 1; i < array.length; i++) {
			if (array[i] > maxValue) {
				maxValue = array[i];
			}
		}
		return maxValue;
	}

    public static void touch(String path) {
        try{
            File f = new File(path);
            f.createNewFile();
        }catch(Exception e){}

    }
	// getting the miniumum value
	public static double getMinValue(double[] array) {
		double minValue = array[0];
		for (int i = 1; i < array.length; i++) {
			if (array[i] < minValue) {
				minValue = array[i];
			}
		}
		return minValue;
	}

	public static void main(String args[]) {
		// Random ran = new Random();
		int total = 0;
		for (int i = 0; i < 1000; ++i) {
			System.out.println(getPoisson(4.5));
		}
		System.out.print(total / 1000.0);
	}
}
