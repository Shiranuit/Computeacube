package dan200.computercraft.core.apis;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

public class Peripherique implements ILuaObject{
	IPeripheral peripherique;
	IAPIEnvironment computer;
	public Peripherique(IPeripheral p, IAPIEnvironment computer2) {
		peripherique = p;
		computer = computer2;
	}
	@Override
	public String[] getMethodNames() {
		return peripherique.getMethodNames();
	}

	@Override
	public Object[] callMethod(ILuaContext context, int method, Object[] arguments)
			throws LuaException, InterruptedException {
		return peripherique.callMethod((IComputerAccess)computer, context, method, arguments);
	}

}
