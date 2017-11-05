package dan200.computercraft.core.apis;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;

import java.io.*;
import java.util.HashMap;
import java.util.regex.*;

public class RegexAPI implements ILuaAPI {

	@Override
	public String[] getNames() {
		return new String[]{"regex"};
	}
	
	@Override
	public String[] getMethodNames() {
		// TODO Auto-generated method stub
		return new String[] {"match","find","split","replaceFirst","replaceAll"};
	}

	@Override
	public Object[] callMethod(ILuaContext context, int method, Object[] arguments)
			throws LuaException, InterruptedException {
		try {
		switch(method) {
			case 0: {
				if (arguments.length == 2) {
					if (arguments[0] instanceof String && arguments[1] instanceof String) {
						String txt = arguments[0].toString();
						String pat = arguments[1].toString();
						Pattern pattern = Pattern.compile(pat);
						Matcher matcher = pattern.matcher(txt);
						if (matcher.find()) {
							Object[] data = new Object[matcher.groupCount()+1];
								for (int i = 0; i<data.length; i++) {
									data[i]=matcher.group(i);
								}
							return data;
						}
						return null;
					}
				} else {
					if (arguments.length == 3) {
						if (arguments[0] instanceof String && arguments[1] instanceof String && arguments[2] instanceof Double) {
							String txt = arguments[0].toString();
							String pat = arguments[1].toString();
							Pattern pattern = Pattern.compile(pat);
							int start = Double.valueOf(arguments[2].toString()).intValue()-1;
							if (start > -1 && start < txt.length()) {
								Matcher matcher = pattern.matcher(txt);
								if (matcher.find(start)) {
									Object[] data = new Object[matcher.groupCount()+1];
										for (int i = 0; i<data.length; i++) {
											data[i]=matcher.group(i);
										}
									
									return data;
								}
								return null;
							}
						}
					} else {
						if (arguments.length >= 4) {
							if (arguments[0] instanceof String && arguments[1] instanceof String && arguments[2] instanceof Double && arguments[3] instanceof Double) {
								String txt = arguments[0].toString();
								String pat = arguments[1].toString();
								Pattern pattern = Pattern.compile(pat);
								int start = Double.valueOf(arguments[2].toString()).intValue()-1;
								int end = Double.valueOf(arguments[3].toString()).intValue();
								if (start > -1 && start < txt.length() && end > -1 && end <= txt.length()) {
									Matcher matcher = pattern.matcher(txt);
									matcher = matcher.region(start, end);
									if (matcher.find()) {
										Object[] data = new Object[matcher.groupCount()+1];
											for (int i = 0; i<data.length; i++) {
												data[i]=matcher.group(i);
											}
										
										return data;
									}
									return null;
								}
							}
						}
					}
				}
				return null;
			}
			case 1: {
				if (arguments.length == 2) {
					if (arguments[0] instanceof String && arguments[1] instanceof String) {
						String txt = arguments[0].toString();
						String pat = arguments[1].toString();
						Pattern pattern = Pattern.compile(pat);
						
						Matcher matcher = pattern.matcher(txt);
						if (matcher.find()) {
							
							return new Object[] {matcher.start()+1, matcher.end()};
						}
						return null;
					}
				} else {
					if (arguments.length == 3) {
						if (arguments[0] instanceof String && arguments[1] instanceof String && arguments[2] instanceof Double) {
							String txt = arguments[0].toString();
							String pat = arguments[1].toString();
							Pattern pattern = Pattern.compile(pat);
							Matcher matcher = pattern.matcher(txt);
							int start = Double.valueOf(arguments[2].toString()).intValue()-1;
							if (matcher.find(start)) {		
								return new Object[] {matcher.start()+1, matcher.end()};
							}
							return null;
						}
					} else {
						if (arguments.length >= 4) {
							if (arguments[0] instanceof String && arguments[1] instanceof String && arguments[2] instanceof Double && arguments[3] instanceof Double) {
								String txt = arguments[0].toString();
								String pat = arguments[1].toString();
								Pattern pattern = Pattern.compile(pat);
								Matcher matcher = pattern.matcher(txt);
								int start = Double.valueOf(arguments[2].toString()).intValue()-1;
								int end = Double.valueOf(arguments[3].toString()).intValue();
								matcher = matcher.region(start, end);
								if (matcher.find(start)) {		
									return new Object[] {matcher.start()+1, matcher.end()};
								}
								return null;
							}
						} 
					}
				}
			}
			case 2: {
				if (arguments.length >= 2) {
					if (arguments[0] instanceof String && arguments[1] instanceof String) {			
						String txt = arguments[0].toString();
						String pat = arguments[1].toString();
						Pattern pattern = Pattern.compile(pat);
						String[] result = pattern.split(txt);
						HashMap data = new HashMap();
						for (int i=0; i<result.length; i++) {
							data.put(i+1, result[i]);
						}
						return new Object[] {data};
					}
				}
				return null;
			}
			case 3: {
				if (arguments.length >= 3) {
					if (arguments[0] instanceof String && arguments[1] instanceof String && arguments[2] instanceof String) {
						String txt = arguments[0].toString();
						String pat = arguments[1].toString();
						String rep = arguments[2].toString();
						Pattern pattern = Pattern.compile(pat);
						Matcher matcher = pattern.matcher(txt);
						return new Object[] {matcher.replaceFirst(rep)};
					}
				}
				return null;
			}
			case 4: {
				if (arguments.length >= 3) {
					if (arguments[0] instanceof String && arguments[1] instanceof String && arguments[2] instanceof String) {
						String txt = arguments[0].toString();
						String pat = arguments[1].toString();
						String rep = arguments[2].toString();
						Pattern pattern = Pattern.compile(pat);
						Matcher matcher = pattern.matcher(txt);
						return new Object[] {matcher.replaceAll(rep)};
					}
				}
				return null;
			}
		}
		return null;
		} catch (Exception ex) {
			return null;
		}
	}

	@Override
	public void advance(double arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startup() {
		// TODO Auto-generated method stub
		
	}

}
