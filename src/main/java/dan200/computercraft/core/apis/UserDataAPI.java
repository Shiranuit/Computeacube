package dan200.computercraft.core.apis;

import javax.swing.JFrame;

import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.LuajavaLib;
import org.luaj.vm2.lua2java.Lua2Java;
import org.luaj.vm2.luajc.LuaJC;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;

public class UserDataAPI  implements ILuaAPI {

	@Override
	public String[] getMethodNames() {
		// TODO Auto-generated method stub
		return new String[] {
				"new"
		};
	}

	@Override
	public Object[] callMethod(ILuaContext context, int method, Object[] arguments)
			throws LuaException, InterruptedException {
		// TODO Auto-generated method stub
		switch (method) {
		case 0: {
			return new Object[] {new LuaObject(arguments[0].toString())};
		}

		default:
			break;
		}
		
		return null;
	}

	@Override
	public String[] getNames() {
		// TODO Auto-generated method stub
		return new String[] {"userdata"};
	}

	@Override
	public void startup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void advance(double _dt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

}
