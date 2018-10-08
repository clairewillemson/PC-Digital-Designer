
package LWJGLEngine;

import org.lwjgl.LWJGLException; 
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {
	
	private static final int Length = 1280;
	private static final int Height = 720;
	private static final int FPS = 60;

	public static void createDisplay() {		
		ContextAttribs attribs = new ContextAttribs(3, 2)
		.withForwardCompatible(true)
		.withProfileCore(true);
		
		try {
			Display.setDisplayMode(new DisplayMode(Length, Height));
			Display.create(new PixelFormat(), attribs);
			Display.setTitle("PC Digital Designer");
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		GL11.glViewport(0, 0, Length, Height);
	}
	
	public static void updateDisplay() {		
		Display.sync(FPS);
		Display.update();
	}
	
	public static void closeDisplay() {		
		Display.destroy();		
	}
	
}
