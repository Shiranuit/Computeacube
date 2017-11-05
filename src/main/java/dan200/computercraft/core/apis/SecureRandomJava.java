package dan200.computercraft.core.apis;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.lua.LuaException;

public class SecureRandomJava implements ILuaObject{
	private SecureRandom rnd = new SecureRandom();
	
	@Override
	public String[] getMethodNames() {
		return new String[] {"seed","nextDouble","nextBoolean","nextFloat","nextBytes","nextGaussian","nextInt","nextLong","generateSeed","getAlgorithm","getSeed","setInstance"};
	}
	
	public SecureRandomJava() {
		 rnd = new SecureRandom();
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
			case 8: {
				if (arguments.length >= 1) {
					if (arguments[0] instanceof Double) {
						return new Object[] {rnd.generateSeed(((Double)arguments[0]).intValue())};
					}
				}
			}
			case 9: {
				return new Object[] {rnd.getAlgorithm()};
			}
			case 10: {
				if (arguments.length >= 1) {
					if (arguments[0] instanceof Double) {
						return new Object[] {rnd.getSeed(((Double)arguments[0]).intValue())};
					}
				}
			}
			case 11: {
				if (arguments.length >= 1) {
					if (arguments[0] instanceof String) {
						try {
							this.rnd = SecureRandom.getInstance((String)arguments[0]);
						} catch (NoSuchAlgorithmException e) {
							rnd = new SecureRandom();
						}
						
					}
				}
			}
		}
		return new Object[] {};
	}
}
