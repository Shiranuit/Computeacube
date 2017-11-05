package dan200.computercraft.core.apis;

import java.util.Random;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.lua.LuaException;

public class RandomJava implements ILuaObject {
	private Random rnd = new Random();
	@Override
	public String[] getMethodNames() {
		return new String[] {"seed","nextDouble","nextBoolean","nextFloat","nextBytes","nextGaussian","nextInt","nextLong"};
	}
	
	public RandomJava() {
		rnd = new Random();
	}

	@Override
	public Object[] callMethod(ILuaContext context, int method, Object[] arguments)
			throws LuaException, InterruptedException {
		switch(method) {
			case 0: {
				if (arguments.length >= 1) {
					if (arguments[0] instanceof Double) {
						rnd.setSeed(((Double)arguments[0]).longValue());
					}
				}
			}
			case 1: {
				return new Object[] {rnd.nextDouble()};
			}
			case 2: {
				return new Object[] {rnd.nextBoolean()};
			}
			case 3: {
				return new Object[] {rnd.nextFloat()};
			}
			case 4: {
				byte[] b = null;
				rnd.nextBytes(b);
				return new Object[] {b};
			}
			case 5: {
				return new Object[] {rnd.nextGaussian()};
			}
			case 6: {
				return new Object[] {rnd.nextInt()};
			}
			case 7: {
				return new Object[] {rnd.nextLong()};
			}
		}
		return new Object[] {};
	}
}
