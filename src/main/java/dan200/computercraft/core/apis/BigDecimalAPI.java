package dan200.computercraft.core.apis;

import java.math.BigDecimal;


import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;


public class BigDecimalAPI implements ILuaAPI {

	@Override
	public String[] getMethodNames() {
		return new String[]{"unm", "add", "sub", "mul", "mod", "pow", "div", "compare", "tostring", "tonumber", "signum","plus","max","min","byteValue","abs","movePointRight","movePointLeft"};
	}

	@Override
	public String[] getNames() {
		return new String[]{"bigdecimal"};
	}
	
	@Override
	public Object[] callMethod(ILuaContext context, int method, Object[] arguments)
			throws LuaException, InterruptedException {
		switch(method) {
			case 0: {
				if (arguments.length >= 1) {
					return new Object[] {getValue(arguments[0]).negate().toPlainString()};
				}
			}
			case 1: {
				if (arguments.length >= 2) {
					return new Object[] {getValue(arguments[0]).add(getValue(arguments[1])).toPlainString()};
				}
			}
			case 2: {
				if (arguments.length >= 2) {
					return new Object[] {getValue(arguments[0]).subtract(getValue(arguments[1])).toPlainString()};
				}
			}
			case 3: {
				if (arguments.length >= 2) {
					return new Object[] {getValue(arguments[0]).multiply(getValue(arguments[1])).toPlainString()};
				}
			}
			case 4: {
				if (arguments.length >= 2) {
					if (arguments[1] instanceof Double) {
						return new Object[] {getValue(arguments[0]).remainder(getValue(arguments[1])).toPlainString()};
					}
				}
			}
			case 5: {
				if (arguments.length >= 2) {
					return new Object[] {getValue(arguments[0]).pow(((Double)arguments[1]).intValue()).toPlainString()};
				}
			}
			case 6: {
				if (arguments.length >= 2) {
					return new Object[] {getValue(arguments[0]).divide(getValue(arguments[1])).toPlainString()};
				}
			}
			case 7: {
				if (arguments.length >= 2) {
					return new Object[] {Double.valueOf(getValue(arguments[0]).compareTo(getValue(arguments[1])))};
				}
			}
			case 8: {
				if (arguments.length >= 1) {
					return new Object[] {getValue(arguments[0]).toString()};
				}
			}
			case 9: {
				if (arguments.length >= 1) {
					return new Object[] {Double.valueOf(getValue(arguments[0]).doubleValue())};
				}
			}
			case 10: {
				if (arguments.length >= 1) {
					return new Object[] {Double.valueOf(getValue(arguments[0]).signum())};
				}
			}
			case 11: {
				if (arguments.length >= 1) {
					return new Object[] {getValue(arguments[0]).plus().toPlainString()};
				}
			}
			case 12: {
				if (arguments.length >= 2) {
					return new Object[] {getValue(arguments[0]).max(getValue(arguments[1])).toPlainString()};
				}
			}
			case 13: {
				if (arguments.length >= 2) {
					return new Object[] {getValue(arguments[0]).min(getValue(arguments[1])).toPlainString()};
				}
			}
			case 14: {
				if (arguments.length >= 1) {
					return new Object[] {Double.valueOf(getValue(arguments[0]).toPlainString())};
				}
			}
			case 15: {
				if (arguments.length >= 1) {
					return new Object[] {getValue(arguments[0]).abs().toPlainString()};
				}
			}
			case 16: {
				if (arguments.length >= 2) {
					return new Object[] {getValue(arguments[0]).movePointRight(((Double)arguments[1]).intValue()).toPlainString()};
				}
			}
			case 17: {
				if (arguments.length >= 2) {
					return new Object[] {getValue(arguments[0]).movePointLeft(((Double)arguments[1]).intValue()).toPlainString()};
				}
			}
		}
		return new Object[] {};
	}

	private BigDecimal getValue(Object val) {
		return new BigDecimal(val.toString());
	}
	
	@Override
	public void advance(double arg0) {
		
	}

	@Override
	public void shutdown() {
		
	}

	@Override
	public void startup() {
		
	}
}
