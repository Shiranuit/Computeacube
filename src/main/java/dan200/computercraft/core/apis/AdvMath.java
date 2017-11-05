package dan200.computercraft.core.apis;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;


public class AdvMath implements ILuaAPI {

    @Override
    public String[] getNames()
    {
        return new String[] {"advMath"};
    }
	
	@Override
	public String[] getMethodNames() {
		return new String[] {"round","cbrt","getExponent","nextAfter","scalb","atan2","cosh","expm1","nextDown","nextUp","sinh","tanh","ulp","log1p","randomJV","randomSecure","map"};
	}

	@Override
	public Object[] callMethod(ILuaContext context, int method, Object[] arguments)
			throws LuaException, InterruptedException {
		switch(method) {
			case 0: {
				if (arguments.length >= 1) {
					if (arguments[0] instanceof Double) {
						return new Object[] {Math.round((Double)arguments[0])};
					}
				}
			}
			case 1: {
				if (arguments.length >= 1) {
					if (arguments[0] instanceof Double) {
						return new Object[] {Math.cbrt((Double)arguments[0])};
					}
				}
			}
			case 2: {
				if (arguments.length >= 1) {
					if (arguments[0] instanceof Double) {
						return new Object[] {Math.getExponent((Double)arguments[0])};
					}
				}
			}
			case 3: {
				if (arguments.length >= 2) {
					if (arguments[0] instanceof Double && arguments[1] instanceof Double) {
						return new Object[] {Math.nextAfter((Double)arguments[0],(Double)arguments[1])};
					}
				}
			}
			case 4: {
				if (arguments.length >= 2) {
					if (arguments[0] instanceof Double && arguments[1] instanceof Double) {
						return new Object[] {Math.scalb((Double)arguments[0],((Double)arguments[1]).intValue())};
					}
				}
			}
			case 5: {
				if (arguments.length >= 2) {
					if (arguments[0] instanceof Double && arguments[1] instanceof Double) {
						return new Object[] {Math.atan2((Double)arguments[0],(Double)arguments[1])};
					}
				}
			}
			case 6: {
				if (arguments.length >= 1) {
					if (arguments[0] instanceof Double) {
						return new Object[] {Math.cosh((Double)arguments[0])};
					}
				}
			}
			case 7: {
				if (arguments.length >= 1) {
					if (arguments[0] instanceof Double) {
						return new Object[] {Math.expm1((Double)arguments[0])};
					}
				}
			}
			case 8: {
				if (arguments.length >= 1) {
					if (arguments[0] instanceof Double) {
						return new Object[] {Math.nextDown((Double)arguments[0])};
					}
				}
			}
			case 9: {
				if (arguments.length >= 1) {
					if (arguments[0] instanceof Double) {
						return new Object[] {Math.nextUp((Double)arguments[0])};
					}
				}
			}
			case 10: {
				if (arguments.length >= 1) {
					if (arguments[0] instanceof Double) {
						return new Object[] {Math.sinh((Double)arguments[0])};
					}
				}
			}
			case 11: {
				if (arguments.length >= 1) {
					if (arguments[0] instanceof Double) {
						return new Object[] {Math.tanh((Double)arguments[0])};
					}
				}
			}
			case 12: {
				if (arguments.length >= 1) {
					if (arguments[0] instanceof Double) {
						return new Object[] {Math.ulp((Double)arguments[0])};
					}
				}
			}
			case 13: {
				if (arguments.length >= 1) {
					if (arguments[0] instanceof Double) {
						return new Object[] {Math.log1p((Double)arguments[0])};
					}
				}
			}
			case 14: {
				return new Object[] {new RandomJava()};
			}
			case 15: {
				return new Object[] {new SecureRandomJava()};
			}
			case 16: {
				if (arguments.length >= 5) {
					if (arguments[0] instanceof Double && arguments[1] instanceof Double && arguments[2] instanceof Double && arguments[3] instanceof Double && arguments[4] instanceof Double) {
						Double val = (Double)arguments[0], min1 = (Double)arguments[2], max1 = (Double)arguments[2], min2 = (Double)arguments[3], max2 = (Double)arguments[4];
						return new Object[] {min2+(max2-min2) * ((val - min1)/(max1-min1))};
					}
				}
			}
		}
		return new Object[] {};
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
