package org.hacker.module.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * DateHelper for DB operate and Simple Date tools
 * 	 
 * @author 	Mr.J.
 * 
 * @since	1.0
 * **/
public class DateKit {
	/**
	 * 这个函数将返回一个星期区间，根据参数d给出的时间为中轴
	 * 比如今天是2014-10-11,那么返回的就是2014-10-06和2014-10-12
	 * 
	 * @param	d :一个日期对象
	 * @param	pattern :日期的格式
	 * 
	 * @return	String[] :<p>String[0]是StarDate</p><p>String[1]是EndDate</p>
	 * **/
	public static String[] getWeekStarDateAndEndDate(Date d, String pattern){
		String[] results = new String[2];
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(d);
		c2.setTime(d);
		int week = c1.get(Calendar.DAY_OF_WEEK)-1;
		if(week < 0)
			week = 1;
		if(week == 0){
			c1.set(c1.get(Calendar.YEAR),c1.get(Calendar.MONTH),c1.get(Calendar.DATE)-6);
			results[0] = sdf.format(c1.getTime());
			results[1] = sdf.format(d);
		}else{
			c1.set(c1.get(Calendar.YEAR),c1.get(Calendar.MONTH),(c1.get(Calendar.DATE)-(c1.get(Calendar.DAY_OF_WEEK)-1)+1));
			results[0] = sdf.format(c1.getTime());
			c2.set(c2.get(Calendar.YEAR),c2.get(Calendar.MONTH),c2.get(Calendar.DATE)+7-(c2.get(Calendar.DAY_OF_WEEK)-1));
			results[1] = sdf.format(c2.getTime());
		}
		return results;
	}
	
	public static String format(Date d, String pattern){
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(d);
	}
	
	public static Date format(String dateStr, String pattern){
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			return sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 该函数返回提前或者是推后的天数的日期的字符串
	 * 如今天是2014-10-13,number:2;b_or_l:before,则返回2014-10-11的Date对象
	 * <p>(当然日期的格式还需要自己调用格式的函数)</p>
	 * 
	 * @param	d :给定的Date对象
	 * @param	number :提前或者是推后的天数
	 * @param 	b_or_l :参数如果是"before" 则返回的是提前的天数，否则"later"返回推后的天数
	 * @throws StringParamException 
	 * **/
	public static Date getBeforeOrLaterDate(Date d,int number, String b_or_l) throws Exception{
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		if(b_or_l.equals("before")){
			c.set(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DATE)-number);
		}else if(b_or_l.equals("later")){
			c.set(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DATE)+number);
		}else{
			throw new Exception("b_or_l param异常,请检查你的参数是否正确--->\"before\" of \"later\"");
		}
    	c.set(Calendar.HOUR_OF_DAY,0);
    	c.set(Calendar.MINUTE,0);
    	c.set(Calendar.SECOND,0);
    	c.set(Calendar.MILLISECOND,0);
		return c.getTime();
	}
	
	/**
	 * 提前或者推后月份并且day都为1
	 * @param d
	 * @param number
	 * @param b_or_l
	 * @return
	 * @throws Exception
	 */
	public static Date getBeforeOrLaterMonth(Date d,int number, String b_or_l) throws Exception{
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		if(b_or_l.equals("before")){
			c.set(c.get(Calendar.YEAR),c.get(Calendar.MONTH) - number, 1);
		}else if(b_or_l.equals("later")){
			c.set(c.get(Calendar.YEAR),c.get(Calendar.MONTH) + number, 1);
		}else{
			throw new Exception("b_or_l param异常,请检查你的参数是否正确--->\"before\" of \"later\"");
		}
    	c.set(Calendar.HOUR_OF_DAY,0);
    	c.set(Calendar.MINUTE,0);
    	c.set(Calendar.SECOND,0);
    	c.set(Calendar.MILLISECOND,0);
		return c.getTime();
	}
	
	/**
	 * 这个函数将返回一个月区间，根据参数d给出的时间为中轴
	 * 比如今天是2014-10-11,那么返回的就是2014-10-01和2014-10-31
	 * 
	 * @param	d :一个日期对象
	 * @param	pattern :日期的格式
	 * 
	 * @return	String[] :<p>String[0]是StarDate</p><p>String[1]是EndDate</p>
	 * **/
	public static String[] getMonthStarDateAndEndDate(Date d, String pattern){
		String[] results = new String[2];
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(d);
		c2.setTime(d);
		c1.set(c1.get(Calendar.YEAR),c1.get(Calendar.MONTH),1);
		results[0] = sdf.format(c1.getTime());
		int last = getMonthLastDay(c2.get(Calendar.YEAR),c1.get(Calendar.MONTH) + 1);
		c2.set(c2.get(Calendar.YEAR),c2.get(Calendar.MONTH),last);
		results[1] = sdf.format(c2.getTime());
		return results;
	}
	
	/**
	 * 取得当月天数
	 * */
	public static int getCurrentMonthLastDay()
	{
		Calendar a = Calendar.getInstance();
		a.set(Calendar.DATE, 1);//把日期设置为当月第一天
		a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
		int maxDate = a.get(Calendar.DATE);
		return maxDate;
	}

	/**
	 * 得到指定月的天数
	 * */
	public static int getMonthLastDay(int year, int month)
	{
		Calendar a = Calendar.getInstance();
		a.set(Calendar.YEAR, year);
		a.set(Calendar.MONTH, month - 1);
		a.set(Calendar.DATE, 1);//把日期设置为当月第一天
		a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
		int maxDate = a.get(Calendar.DATE);
		return maxDate;
	}
	
	/** 
     * 获取某年第一天日期 
     * @param year 年份 
     * 
     * @return Date 
     */  
    public static Date getYearFirstDay(int year){  
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);  
        Date currYearFirst = calendar.getTime();  
        return currYearFirst;
    }
    
    /**
     * 获取当年的第一天
     * @return
     */
    public static Date getCurrentYearFirstDay(){
    	Calendar calendar = Calendar.getInstance();
    	return getYearFirstDay(calendar.get(Calendar.YEAR));
    }
    
    /**
     * 获取当年的最后一天(最后的时刻23:59:59)
     * @return
     */
    public static Date getCurrentYearLastDay(){
    	Calendar calendar = Calendar.getInstance();  
    	calendar.set(Calendar.MONTH, calendar.getActualMaximum(Calendar.MONTH));
    	calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    	calendar.set(Calendar.HOUR_OF_DAY,23);
    	calendar.set(Calendar.SECOND,59);
    	calendar.set(Calendar.MINUTE,59);
    	calendar.set(Calendar.MILLISECOND,0);
    	return calendar.getTime();  
    }
    
    /**
     * 获取当月的第一天(最开始的时刻00:00:00)
     * @return
     */
    public static Date getCurrentMonth(){
    	Calendar calendar = Calendar.getInstance();
    	calendar.set(Calendar.DAY_OF_MONTH, 1);
    	calendar.set(Calendar.HOUR_OF_DAY,0);
    	calendar.set(Calendar.SECOND,0);
    	calendar.set(Calendar.MINUTE,0);
    	calendar.set(Calendar.MILLISECOND,0);
    	return calendar.getTime();
    }
    
    /**
     * 获取上个月的第一天(最开始的时刻00:00:00)
     * @return
     */
    public static Date getLastMonth(){
    	Calendar calendar = Calendar.getInstance();
    	calendar.add(Calendar.MONTH, -1);
    	calendar.set(Calendar.DAY_OF_MONTH, 1);
    	calendar.set(Calendar.HOUR_OF_DAY,0);
    	calendar.set(Calendar.SECOND,0);
    	calendar.set(Calendar.MINUTE,0);
    	calendar.set(Calendar.MILLISECOND,0);
    	return calendar.getTime();
    }
    
    /**
     * 获取下个月的第一天(最开始的时刻00:00:00)
     * @return
     */
    public static Date getNextMonth(){
    	Calendar calendar = Calendar.getInstance();
    	calendar.add(Calendar.MONTH, 1);
    	calendar.set(Calendar.DAY_OF_MONTH, 1);
    	calendar.set(Calendar.HOUR_OF_DAY,0);
    	calendar.set(Calendar.SECOND,0);
    	calendar.set(Calendar.MINUTE,0);
    	calendar.set(Calendar.MILLISECOND,0);
    	return calendar.getTime();
    }
    
    /**
     * 获取上个月的第一天(最开始的时刻00:00:00)
     * @return
     */
    public static Date getLastMonthFirstDay(){
    	Calendar calendar = Calendar.getInstance();
    	calendar.add(Calendar.MONTH, -1);
    	calendar.set(Calendar.DAY_OF_MONTH, 1);
    	calendar.set(Calendar.HOUR_OF_DAY,0);
    	calendar.set(Calendar.SECOND,0);
    	calendar.set(Calendar.MINUTE,0);
    	calendar.set(Calendar.MILLISECOND,0);
    	return calendar.getTime();
    }
    
    /**
     * 获取上个月的最后第一天(最后的时刻23:59:59)
     * @return
     */
    public static Date getLastMonthLastDay(){
    	Calendar calendar = Calendar.getInstance();  
    	int month = calendar.get(Calendar.MONTH);
    	calendar.set(Calendar.MONTH, month - 1);
    	calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    	calendar.set(Calendar.HOUR_OF_DAY,23);
    	calendar.set(Calendar.SECOND,59);
    	calendar.set(Calendar.MINUTE,59);
    	calendar.set(Calendar.MILLISECOND,0);
    	return calendar.getTime();  
    }
    
    /**
     * 获取昨天
     * @return
     */
    public static Date getYesterday(){
    	Calendar calendar = Calendar.getInstance();  
    	int day = calendar.get(Calendar.DAY_OF_MONTH);
    	calendar.set(Calendar.DAY_OF_MONTH, day - 1);
    	calendar.set(Calendar.HOUR_OF_DAY,0);
    	calendar.set(Calendar.SECOND,0);
    	calendar.set(Calendar.MINUTE,0);
    	calendar.set(Calendar.MILLISECOND,0);
    	return calendar.getTime();
    }
    
    /**
     * 获取明天
     * @return
     */
    public static Date getTomorrow(){
    	Calendar calendar = Calendar.getInstance();  
    	int day = calendar.get(Calendar.DAY_OF_MONTH);
    	calendar.set(Calendar.DAY_OF_MONTH, day + 1);
    	calendar.set(Calendar.HOUR_OF_DAY,0);
    	calendar.set(Calendar.SECOND,0);
    	calendar.set(Calendar.MINUTE,0);
    	calendar.set(Calendar.MILLISECOND,0);
    	return calendar.getTime();
    }
    
    /**
     * 获取当年的数值
     * @return
     */
    public static int getYear(){
    	Calendar calendar = Calendar.getInstance(); 
    	return calendar.get(Calendar.YEAR);
    }
    
    /**
     * 获取当年的数值
     * @return
     */
    public static int getYear(Date date){
    	Calendar calendar = Calendar.getInstance(); 
    	calendar.setTime(date);
    	return calendar.get(Calendar.YEAR);
    }
    
    /**
     * 获取当月的数值
     * @return
     */
    public static int getMonth(){
    	Calendar calendar = Calendar.getInstance(); 
    	return calendar.get(Calendar.MONTH) + 1;
    }
    
    /**
     * 获取当月的数值
     * @return
     */
    public static int getDayOfMonth(Date date){
    	Calendar calendar = Calendar.getInstance(); 
    	calendar.setTime(date);
    	return calendar.get(Calendar.DAY_OF_MONTH) + 1;
    }
    
    /**
     * 获取当年的数值
     * @return
     */
    public static int getMonthOfYear(Date date){
    	Calendar calendar = Calendar.getInstance(); 
    	calendar.setTime(date);
    	return calendar.get(Calendar.MONTH) + 1;
    }
    
    /**
     * 返回2个日期相差多少个月
     * 
     * @param start_date
     * @param end_date
     * @return
     */
    public static int subtractAndReturnMonth(Date start_date, Date end_date) {
    	if(start_date.getTime() - end_date.getTime() > 0) throw new IllegalArgumentException("Oop! start_date > end_date");
    	
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(start_date);
    	int s_year = calendar.get(Calendar.YEAR);
    	int s_month = calendar.get(Calendar.MONTH) + 1;
    	
    	calendar.setTime(end_date);
    	int e_year = calendar.get(Calendar.YEAR);
    	int e_month = calendar.get(Calendar.MONTH) + 1;
    	
    	int subtract_year = e_year - s_year;
    	// 同一年的情况下
    	if(subtract_year == 0) {
    		// 同一年的情况不会出现e_month < s_month
    		return e_month - s_month;
    	// 不同年的情况下
    	}else {
    		// 月份有小于大于等于3种情况
    		int subtract_month = e_month - s_month;
    		if(subtract_month == 0) return subtract_year * 12;
    		else if(subtract_month < 0) return subtract_year * 12 - Math.abs(subtract_month);
    		else return subtract_year * 12 + Math.abs(subtract_month);
    	}
    }
    
    /**
     * 返回2个日期相差多少天
     * 格式必须为yyyy-MM-dd
     * 
     * @param start_date 
     * @param end_date
     * @return
     */
    public static int subtractAndReturnDay(Date start_date, Date end_date) {
    	long subtract = end_date.getTime() - start_date.getTime();
    	if(subtract < 0) throw new IllegalArgumentException("Oop! start_date > end_date");
    	long result = subtract / (24 * 60 * 60 * 1000);
    	return Integer.parseInt(result + "");
    }
    
    /**
     * 获取到date月份的第一天
     * example: 
     * assert getMonthFirstDay("2016-09-21") == "2016-09-01"
     * @param date
     * @return
     */
    public static String getMonthFirstDay(Date date) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	calendar.set(Calendar.DAY_OF_MONTH, 1);
    	return format(calendar.getTime(), "yyyy-MM-dd");
    }
    
    /**
     * 获取到date月份的第一天
     * example: 
     * assert getMonthFirstDay("2016-09-21") == "2016-09-01"
     * @param date
     * @return
     */
    public static String getMonthFirstDay(String date) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(format(date, "yyyy-MM-dd"));
    	calendar.set(Calendar.DAY_OF_MONTH, 1);
    	return format(calendar.getTime(), "yyyy-MM-dd");
    }
    
    /**
     * 获取到date月份的最后一天
     * example: 
     * assert getMonthFirstDay("2016-09-21") == "2016-09-30"
     * @param date
     * @return
     */
    public static String getMonthLastDay(Date date) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    	return format(calendar.getTime(), "yyyy-MM-dd");
    }
}	
