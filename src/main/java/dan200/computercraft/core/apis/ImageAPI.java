package dan200.computercraft.core.apis;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.shared.computer.core.ServerComputer;

public class ImageAPI implements ILuaAPI {
	
		
	
		IAPIEnvironment cc;
	public ImageAPI(IAPIEnvironment computer) {
		this.cc = computer;
	}
	
	@Override
	public String[] getMethodNames() {
		// TODO Auto-generated method stub
		return new String[] {"load","create","colorFromInt","colorWithAlphaFromInt","colorFromRGB","colorFromARGB"};
	}

	@Override
	public Object[] callMethod(ILuaContext context, int method, Object[] arguments)
			throws LuaException, InterruptedException {
		switch(method) {
			case 0: {
				if (arguments.length >= 1) {
					if (arguments[0] instanceof String) {
						ServerComputer pc = ComputerCraft.serverComputerRegistry.lookup(cc.getComputerID());
						File worldDir = ComputerCraft.getWorldDir(pc.getWorld());
						File computerDir = new File(worldDir.getPath() + File.separatorChar + "computer");
						if (computerDir.exists()) {
							File computerIDDir = new File(computerDir.getPath() + File.separatorChar + cc.getComputerID());
							if (!computerIDDir.exists()) {
								computerIDDir.mkdirs();
							}
							File file = new File(computerIDDir.getPath() + File.separatorChar + arguments[0].toString());
							try {
								if (file.exists()) {
									BufferedImage image = ImageIO.read(file);
									return new Object[] {true, new CCImage(image)};
								} else {
									return new Object[] {false, "File not found"};
								}
							} catch (IOException e) {
								return new Object[] {false, e.getMessage(), e.getStackTrace(), e.getCause()};
							}
						} else {
							return new Object[] {false, "Computer folder not found"};
						}
					}
				}
				return new Object[] {false};
			}
			case 1: {
				if (arguments.length >= 2) {
					if (arguments[0] instanceof Double && arguments[1] instanceof Double ) {
						ServerComputer pc = ComputerCraft.serverComputerRegistry.lookup(cc.getComputerID());
						File worldDir = ComputerCraft.getWorldDir(pc.getWorld());
						File computerDir = new File(worldDir.getPath() + File.separatorChar + "computer");
						if (computerDir.exists()) {
							File computerIDDir = new File(computerDir.getPath() + File.separatorChar + cc.getComputerID());
							if (!computerIDDir.exists()) {
								computerIDDir.mkdirs();
							}
							int w = Double.valueOf(arguments[0].toString()).intValue();
							int h = Double.valueOf(arguments[1].toString()).intValue();
							try {
								BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
								return new Object[] {true, new CCImage(image)};
							} catch (Exception e) {
								return new Object[] {false, e.getMessage(), e.getStackTrace(), e.getCause()};
							}					
						} else {
							return new Object[] {false, "Computer folder not found"};
						}
					}
				}
			}
			case 2: {
				if (arguments.length >= 1) {
					if (arguments[0] instanceof Double) {
						int c = Double.valueOf(arguments[0].toString()).intValue();
						try {
							Color color = new Color(c,true);
							return new Object[] {new CCColor(color)};
						} catch (Exception e) {
							return new Object[] {false, e.getMessage(), e.getStackTrace(), e.getCause()};
						}
					}
				}
			}
			case 3: {
				if (arguments.length >= 1) {
					if (arguments[0] instanceof Double) {
						int c = Double.valueOf(arguments[0].toString()).intValue();
						try {
							Color color = new Color(c);
							return new Object[] {new CCColor(color)};
						} catch (Exception e) {
							return new Object[] {false, e.getMessage(), e.getStackTrace(), e.getCause()};
						}
					}
				}
			}
			case 4: {
				if (arguments.length >= 3) {
					if (arguments[0] instanceof Double && arguments[1] instanceof Double && arguments[2] instanceof Double) {
						int r = Double.valueOf(arguments[0].toString()).intValue();
						int g = Double.valueOf(arguments[1].toString()).intValue();
						int b = Double.valueOf(arguments[2].toString()).intValue();
						try {
							Color color = new Color(r,g,b);
							return new Object[] {new CCColor(color)};
						} catch (Exception e) {
							return new Object[] {false, e.getMessage(), e.getStackTrace(), e.getCause()};
						}
					}
				}
			}
			case 5: {
				if (arguments.length >= 4) {
					if (arguments[0] instanceof Double && arguments[1] instanceof Double && arguments[2] instanceof Double && arguments[3] instanceof Double) {
						int a = Double.valueOf(arguments[0].toString()).intValue();
						int r = Double.valueOf(arguments[1].toString()).intValue();
						int g = Double.valueOf(arguments[2].toString()).intValue();
						int b = Double.valueOf(arguments[3].toString()).intValue();
						try {
							Color color = new Color(r,g,b,a);
							return new Object[] {new CCColor(color)};
						} catch (Exception e) {
							return new Object[] {false, e.getMessage(), e.getStackTrace(), e.getCause()};
						}
					}
				}
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
	private class CCColor implements ILuaObject {
		Color color;
		public CCColor(Color c) {
			this.color = c;
		}
		
		@Override
		public String[] getMethodNames() {
			// TODO Auto-generated method stub
			return new String[] {"R","G","B","A","brighter","RGB","darker","transparency"};
		}

		@Override
		public Object[] callMethod(ILuaContext context, int method, Object[] arguments)
				throws LuaException, InterruptedException {
			switch(method) {
				case 0: {
					return new Object[] {this.color.getRed()};
				}
				case 1: {
					return new Object[] {this.color.getGreen()};
				}
				case 2: {
					return new Object[] {this.color.getBlue()};
				}
				case 3: {
					return new Object[] {this.color.getAlpha()};
				}
				case 4: {
					return new Object[] {new CCColor(this.color.brighter())};
				}
				case 5: {
					return new Object[] {this.color.getRGB()};
				}
				case 6: {
					return new Object[] {new CCColor(this.color.darker())};
				}
				case 7: {
					return new Object[] {this.color.getTransparency()};
				}
			}
			return null;
		}
		
	}
	
	private class CCImage implements ILuaObject {
		
		BufferedImage image;
		public CCImage(BufferedImage img) {
			this.image=img;
		}
		@Override
		public String[] getMethodNames() {
			// TODO Auto-generated method stub
			return new String[] {"getWidth","getHeight","getPixel","getPixelsTable","setPixel","getSubimage","flush","save"};
		}

		@Override
		public Object[] callMethod(ILuaContext context, int method, Object[] arguments)
				throws LuaException, InterruptedException {
			switch(method) {
				case 0: {
					return new Object[] {this.image.getWidth()};
				}
				case 1: {
					return new Object[] {this.image.getHeight()};
				}
				case 2: {
					if (arguments.length >= 2) {
						if (arguments[0] instanceof Double && arguments[1] instanceof Double) {
							int x = Double.valueOf(arguments[0].toString()).intValue();
							int y = Double.valueOf(arguments[1].toString()).intValue();
							try {
								int rgb = this.image.getRGB(x, y);
								return new Object[] {true, new CCColor(new Color(rgb,true))};
							} catch (Exception e) {
								return new Object[] {false, e.getMessage(), e.getStackTrace(), e.getCause()};
							}
						}
					} 
					return null;
				}
				case 3: {
					int[] rgb = this.image.getRGB(0, 0, this.image.getWidth(), this.image.getHeight(), null, 0, this.image.getWidth());
					HashMap<Object, Object> dt = new HashMap<Object, Object>();
					for (int i=0; i<rgb.length; i++) {
						dt.put(i, new CCColor(new Color(rgb[i],true)));
					}
					return new Object[] {dt};
				}
				case 4: {
					if (arguments.length >= 3) {
						if (arguments[0] instanceof Double && arguments[1] instanceof Double && arguments[2] instanceof Double) {
							int x = Double.valueOf(arguments[0].toString()).intValue();
							int y = Double.valueOf(arguments[1].toString()).intValue();
							int rgb = Double.valueOf(arguments[2].toString()).intValue();
							try {
							this.image.setRGB(x, y, rgb);
							} catch(Exception e) {
								return new Object[] {false, e.getMessage(), e.getStackTrace(), e.getCause()};
							}
							return new Object[] {true};
						}
					}
				}
				case 5: {
					if (arguments.length >= 4) {
						if (arguments[0] instanceof Double && arguments[1] instanceof Double && arguments[2] instanceof Double && arguments[3] instanceof Double) {
							int x = Double.valueOf(arguments[0].toString()).intValue();
							int y = Double.valueOf(arguments[1].toString()).intValue();
							int w = Double.valueOf(arguments[2].toString()).intValue();
							int h = Double.valueOf(arguments[3].toString()).intValue();
							try {
								BufferedImage nimg = this.image.getSubimage(x, y, w, h);
								return new Object[] {true, new CCImage(nimg)};
							} catch (Exception e) {
								return new Object[] {false, e.getMessage(), e.getStackTrace(), e.getCause()};
							}
						}
					}
				}
				case 6: {
					this.image.flush();
					return new Object[] {true};
				}
				case 7: {
					if (arguments.length == 1) {
						if (arguments[0] instanceof String) {
							ServerComputer pc = ComputerCraft.serverComputerRegistry.lookup(cc.getComputerID());
							File worldDir = ComputerCraft.getWorldDir(pc.getWorld());
							File computerDir = new File(worldDir.getPath() + File.separatorChar + "computer");
							if (computerDir.exists()) {
								File computerIDDir = new File(computerDir.getPath() + File.separatorChar + cc.getComputerID());
								if (!computerIDDir.exists()) {
									computerIDDir.mkdirs();
								}
								File file = new File(computerIDDir.getPath() + File.separatorChar + arguments[0].toString());
								try {
								    ImageIO.write(this.image, "png", file);
								    return new Object[] {true};
								} catch (IOException e) {
								    return new Object[] {false, e.getMessage(), e.getStackTrace(), e.getCause()};
								}
							}
						}
					} else {
						if (arguments.length >= 2) {
							if (arguments[0] instanceof String && arguments[1] instanceof String) {
								String format = arguments[1].toString().toLowerCase();
								if (!format.equals("png") && !format.equals("jpg") && !format.equals("gif") && !format.equals("jpeg") && !format.equals("bmp") && !format.equals("wbmp")) {
									format = "png";
								}
								ServerComputer pc = ComputerCraft.serverComputerRegistry.lookup(cc.getComputerID());
								File worldDir = ComputerCraft.getWorldDir(pc.getWorld());
								File computerDir = new File(worldDir.getPath() + File.separatorChar + "computer");
								if (computerDir.exists()) {
									File computerIDDir = new File(computerDir.getPath() + File.separatorChar + cc.getComputerID());
									if (!computerIDDir.exists()) {
										computerIDDir.mkdirs();
									}
									File file = new File(computerIDDir.getPath() + File.separatorChar + arguments[0].toString());
									try {
									    ImageIO.write(this.image, format, file);
									    return new Object[] {true};
									} catch (IOException e) {
									    return new Object[] {false, e.getMessage(), e.getStackTrace(), e.getCause()};
									}
								}
							}
						}
					}
				}
			}
			return null;
		}
		
	}

	@Override
	public String[] getNames() {
		// TODO Auto-generated method stub
		return new String[] {"imageUtils"};
	}

}
