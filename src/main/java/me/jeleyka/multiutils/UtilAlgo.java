package me.jeleyka.multiutils;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;

@UtilityClass
public class UtilAlgo {

	public String getTimeBySeconds(int seconds) {
		return (seconds / 3600) + " ч. " + (seconds % 3600 / 60) + " мин. " + (seconds % 60) + " сек.";
	}

	public boolean isInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Object getPrivateField(Class<?> clazz, String name, Object obj) {
		Object o = null;
		try {
			Field f = clazz.getDeclaredField(name);
			f.setAccessible(true);
			o = f.get(obj);
			f.setAccessible(false);
		} catch (NoSuchFieldException | IllegalAccessException ex) {
			ex.printStackTrace();
		}
		return o;
	}

}
