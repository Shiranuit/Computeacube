package dan200.computercraft.core.apis;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.core.terminal.TextBuffer;
import dan200.computercraft.shared.computer.blocks.TileComputer;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.turtle.blocks.TileTurtle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class ComputerUtils implements ILuaAPI {
	
	IAPIEnvironment computer;
	public ComputerUtils(IAPIEnvironment c) {
		computer = c;
	}
	
	@Override
	public String[] getMethodNames() {
		return new String[] {"wrap","getComputer","openComputerGUI","openTurtleGUI"};
	}

	@Override
	public Object[] callMethod(ILuaContext context, int method, Object[] arguments)
			throws LuaException, InterruptedException {
		switch(method) {
			case 0: { //wrap
				if (arguments.length >=5) {
					if (arguments[0] instanceof Double && arguments[1] instanceof Double && arguments[2] instanceof Double && arguments[3] instanceof Double && arguments[4] instanceof Double) {
						World w = DimensionManager.getWorld(((Double)arguments[0]).intValue());
						int x = ((Double)arguments[1]).intValue();
						int y = ((Double)arguments[2]).intValue();
						int z = ((Double)arguments[3]).intValue();
						int side = ((Double)arguments[4]).intValue();
						IPeripheral periph = ComputerCraft.getPeripheralAt(w, new BlockPos(x,y,z), EnumFacing.getFront(side));
						System.out.println(periph);
						if (periph != null) {
							return new Object[] {new Peripherique(periph, computer)};
						}
					}
				}
				return new Object[] {false};
			}
			case 1: {
				if (arguments.length >=1) {
					if (arguments[0] instanceof Double) {
						return new Object[] {new CC(Intv(arguments[0]))};
					}
				}
				return new Object[] {false};
			}
			case 2: {
				if (arguments.length >=2) {
					if (arguments[0] instanceof String && arguments[1] instanceof Double) {
						ServerComputer pc = CCUtils.getComputer(Intv(arguments[1]));
						if (pc != null) {
							TileComputer tile = (TileComputer)pc.getWorld().getTileEntity(pc.getPosition());
							if (tile != null) {
								EntityPlayer pl = CCUtils.getPlayer(arguments[0].toString());
								if (pl != null) {
									ComputerCraft.openComputerGUI(pl, tile);
									return new Object[] {true};	
								}
							}
						}
					}
				}
				return new Object[] {false};
			}
			case 3: {
				if (arguments.length >=2) {
					if (arguments[0] instanceof String && arguments[1] instanceof Double) {
						ServerComputer pc = CCUtils.getComputer(Intv(arguments[1]));
						if (pc != null) {
							TileTurtle tile = (TileTurtle)pc.getWorld().getTileEntity(pc.getPosition());
							if (tile != null) {
								EntityPlayer pl = CCUtils.getPlayer(arguments[0].toString());
								if (pl != null) {
									ComputerCraft.openTurtleGUI(pl, tile);
									return new Object[] {true};
								}
							}
						}
					}
				}
				return new Object[] {false};
			}

		}
		return null;
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
	private class CTextBuffer implements ILuaObject {
		TextBuffer buff;
		public CTextBuffer(TextBuffer t) {
			buff = t;
		}
		
		@Override
		public String[] getMethodNames() {
			// TODO Auto-generated method stub
			return new String[] {"fill","read","write"};
		}

		@Override
		public Object[] callMethod(ILuaContext context, int method, Object[] arguments)
				throws LuaException, InterruptedException {
			// TODO Auto-generated method stub
			if (buff != null) {
				switch(method) {
					case 0: {
						if (arguments.length == 1) {
							if (arguments[0] instanceof String) {
								buff.fill(arguments[0].toString());
								return new Object[] {true};
							}
						}
						if (arguments.length >= 2) {
							if (arguments[0] instanceof String && arguments[1] instanceof Double) {
								buff.fill(arguments[0].toString(), Intv(arguments[1]));
								return new Object[] {true};
							}
						}
						return new Object[] {false};
					}
					case 1: {
						if (arguments.length == 0) {
						return new Object[] {buff.read()};
						}
						if (arguments.length == 1) {
							if (arguments[0] instanceof Double) {
								return new Object[] {buff.read(Intv(arguments[0]))};
							}
						}
						if (arguments.length >= 2) {
							if (arguments[0] instanceof Double && arguments[1] instanceof Double) {
								return new Object[] {buff.read(Intv(arguments[0]),Intv(arguments[1]))};
							}
						}
						return new Object[] {false};
					}
					case 2: {
						if (arguments.length >= 1) {
							if (arguments[0] instanceof String) {
								buff.write(arguments[0].toString());
								return new Object[] {true};
							}
						}
						return new Object[] {false};
					}
				}
			}
			return null;
		}
		
	}
	
	public int Intv(Object a) {
		return ((Double)a).intValue();
	}
	
	public boolean Boolv(Object a) {
		return (Boolean)a;
	}
	
	private class CCTerminal implements ILuaObject {
		Terminal term;
		boolean isColored = false;
		public CCTerminal(Terminal t, boolean isColor) {
			term = t;
			isColored = isColor;
		}
		@Override
		public String[] getMethodNames() {
			// TODO Auto-generated method stub
			return new String[] {"blit","clear","clearChanged","clearLine","getBackgroundColour","getBackgroundColourLine","getChanged","getCursorBlink","getCursorX","getCursorY","getTextColourLine","getHeight","getLine","getTextColour","getWidth","reset","resize","scroll","setBackgroundColour","setCursorBlink","setCursorPos","setLine","setTextColour"};
		}

		@Override
		public Object[] callMethod(ILuaContext context, int method, Object[] arguments)
				throws LuaException, InterruptedException {
			// TODO Auto-generated method stub
			if (term != null) {
				switch(method) {
					case 0: {
						if (arguments.length >= 3) {
							if (arguments[0] instanceof String && arguments[1] instanceof String && arguments[2] instanceof String) {
								term.blit(arguments[0].toString(), arguments[1].toString(), arguments[2].toString());
								return new Object[] {true};
							}
						}
						return new Object[] {false};
					}
					case 1: {
						term.clear();
						return new Object[] {true};
					}
					case 2: {
						term.clearChanged();
						return new Object[] {true};
					}
					case 3: {
						term.clearLine();
						return new Object[] {true};
					}
					case 4: {
						return new Object[] {term.getBackgroundColour()};
					}
					case 5: {
						if (arguments.length >= 1) {
							if (arguments[0] instanceof Double) {
								return new Object[] {new CTextBuffer(term.getBackgroundColourLine(Intv(arguments[0])))};
							}
						}
						return new Object[] {false};
					}
					case 6: {
						return new Object[] {term.getChanged()};
					}
					case 7: {
						return new Object[] {term.getCursorBlink()};
					}
					case 8: {
						return new Object[] {term.getCursorX()};
					}
					case 9: {
						return new Object[] {term.getCursorY()};
					}
					case 10: {
						if (arguments.length >= 1) {
							if (arguments[0] instanceof Double) {
								return new Object[] {new CTextBuffer(term.getTextColourLine(Intv(arguments[0])))};
							}
						}
						return new Object[] {false};
					}
					case 11: {
						return new Object[] {term.getHeight()};
					}
					case 12: {
						if (arguments.length >= 1) {
							if (arguments[0] instanceof Double) {
								return new Object[] {new CTextBuffer(term.getLine(Intv(arguments[0])))};
							}
						}
						return new Object[] {false};
					}
					case 13: {
						return new Object[] {term.getTextColour()};
					}
					case 14: {
						return new Object[] {term.getWidth()};
					}
					case 15: {
						term.reset();
						return new Object[] {true};
					}
					case 16: {
						if (arguments.length >= 2) {
							if (arguments[0] instanceof Double && arguments[1] instanceof Double) {
								term.resize(Intv(arguments[0]), Intv(arguments[1]));
								return new Object[] {true};
							}
						}
						return new Object[] {false};
					}
					case 17: {
						if (arguments.length >= 1) {
							if (arguments[0] instanceof Double) {
								term.scroll(Intv(arguments[0]));
								return new Object[] {true};
							}
						}
						return new Object[] {false};
					}
					case 18: {
						if (arguments.length >= 1) {
							if (arguments[0] instanceof Double) {
								term.setBackgroundColour(CCUtils.parseColour(arguments, isColored));
								return new Object[] {true};
							}
						}
						return new Object[] {false};
					}
					case 19: {
						if (arguments.length >= 1) {
							if (arguments[0] instanceof Boolean) {
								term.setCursorBlink(Boolv(arguments[0]));
								return new Object[] {true};
							}
						}
						return new Object[] {false};
					}
					case 20: {
						if (arguments.length >= 2) {
							if (arguments[0] instanceof Double && arguments[1] instanceof Double) {
								term.setCursorPos(Intv(arguments[0]), Intv(arguments[1]));
								return new Object[] {true};
							}
						}
						return new Object[] {false};
					}
					case 21: {
						if (arguments.length >= 4) {
							if (arguments[0] instanceof Double && arguments[1] instanceof String && arguments[2] instanceof String && arguments[3] instanceof String) {
								term.setLine(Intv(arguments[0]), arguments[1].toString(), arguments[2].toString(), arguments[3].toString());
								return new Object[] {true};
							}
						}
						return new Object[] {false};
					}
					case 22: {
						if (arguments.length >= 1) {
							if (arguments[0] instanceof Double) {
								term.setTextColour(CCUtils.parseColour(arguments, isColored));
								return new Object[] {true};
							}
						}
						return new Object[] {false};
					}
					
				}
			}
			return null;
		}
		
	}
	private class CC implements ILuaObject {
		ServerComputer computer;
		public CC(int id) {
			computer = CCUtils.getComputer(id);
			
		}
		
		@Override
		public String[] getMethodNames() {
			// TODO Auto-generated method stub
			return new String[] {"getID","getDay","getInstanceID","getLabel","getBundledRedstoneOutput","getPeripheral","getPosition","getTimeOfDay","hasOutputChanged","hasTerminalChanged","hasTimedOut","isColour","isCursorDisplayed","isOn","queueEvent","reboot","resize","setID","setLabel","setRedstoneInput","shutdown","turnOn","getTerm","getFS","getOS","getRS"};
		}

		@Override
		public Object[] callMethod(ILuaContext context, int method, Object[] arguments)
				throws LuaException, InterruptedException {
			// TODO Auto-generated method stub
			if (computer != null) {
				switch(method) {
					case 0: {
						return new Object[] {computer.getID()};
					}
					case 1: {
						return new Object[] {computer.getDay()};
					}
					case 2: {
						return new Object[] {computer.getInstanceID()};
					}
					case 3: {
						return new Object[] {computer.getLabel()};
					}
					case 4: {
						return new Object[] {computer.getBundledRedstoneOutput(CCUtils.parseSide(arguments))};
					}
					case 5: {
						return new Object[] {computer.getPeripheral(CCUtils.parseSide(arguments))};
					}
					case 6: {
						return new Object[] {computer.getPosition().getX(),computer.getPosition().getY(),computer.getPosition().getZ()};
					}
					case 7: {
						return new Object[] {computer.getTimeOfDay()};
					}
					case 8: {
						return new Object[] {computer.hasOutputChanged()};
					}
					case 9: {
						return new Object[] {computer.hasTerminalChanged()};
					}
					case 10: {
						return new Object[] {computer.hasTimedOut()};
					}
					case 11: {
						return new Object[] {computer.isColour()};
					}
					case 12: {
						return new Object[] {computer.isCursorDisplayed()};
					}
					case 13: {
						return new Object[] {computer.isOn()};
					}
					case 14: {
						if (arguments[0] instanceof String) {
							if (arguments.length == 1) {
								computer.queueEvent(arguments[0].toString());
								return new Object[] {true};
							} else if (arguments.length >= 2) {
								Object[] arg = {};
								for (int i=1; i<arguments.length; i++) {
									arg[i] = arguments[i];
								}
								computer.queueEvent(arguments[0].toString(),arg);
								return new Object[] {true};
							}
						}
						return new Object[] {false};
					}
					case 15: {
						computer.reboot();
						return new Object[] {true};
					}
					case 16: {
						if (arguments.length >= 2) {
							if (arguments[0] instanceof Double && arguments[1] instanceof Double) {
								computer.resize(Intv(arguments[0]), Intv(arguments[1]));
								return new Object[] {true};
							}
						}
						return new Object[] {false};
					}
					case 17: {
						if (arguments[0] instanceof Double) {
							computer.setID(Intv(arguments[0]));
							return new Object[] {true};
						}
						return new Object[] {false};			
					}
					case 18: {
						if (arguments[0] instanceof String) {
							computer.setLabel(arguments[0].toString());
							return new Object[] {true};
						}
						return new Object[] {false};
					}
					case 19: {
						if (arguments[0] instanceof String && arguments[1] instanceof Double) {
							computer.setRedstoneInput(CCUtils.parseSide(arguments), Intv(arguments[1]));
							return new Object[] {true};
						}
						return new Object[] {false};
					}
					case 20: {
						computer.shutdown();
						return new Object[] {true};
					}
					case 21: {
						computer.turnOn();
						return new Object[] {true};
					}
					case 22: {
						return new Object[] {this.computer.m_computer.m_apis.get(0)};
					}
					case 23: {
						return new Object[] {new FSAPI(this.computer.getAPIEnvironment(), this.computer.getAPIEnvironment().getFileSystem())};
					}
					case 24: {
						return new Object[] {new OSAPI(this.computer.getAPIEnvironment())};
					}
					case 25: {
						return new Object[] {new RedstoneAPI(this.computer.getAPIEnvironment())};
					}
				}
			}
			return null;
		}
		
		
	}

	@Override
	public String[] getNames() {
		// TODO Auto-generated method stub
		return new String[]{"computer"};
	}
}
