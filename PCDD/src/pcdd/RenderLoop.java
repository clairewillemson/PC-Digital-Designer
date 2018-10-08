package pcdd;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import LWJGLEngine.Camera;
import LWJGLEngine.DisplayManager;
import LWJGLEngine.Entity;
import LWJGLEngine.Light;
import LWJGLEngine.Loader;
import LWJGLEngine.ModelTexture;
import LWJGLEngine.OBJLoader;
import LWJGLEngine.RawModel;
import LWJGLEngine.Renderer;
import LWJGLEngine.StaticShader;
import LWJGLEngine.TexturedModel;

/**
 * Purpose : Initialize, run, and control the different components of the rendering loop.
 * @param potentialPartList : An ArrayList of entities (parts) that could possibly be added to the user's build.
 * @param activePartList : An ArrayList of entities (parts) that are currently being rendered.
 * @param Loader : class object responsible for configuring wavefront objs for use
 */
public class RenderLoop {
	
	private static ArrayList<Entity> potentialPartList = new ArrayList<Entity>();
	private static ArrayList<Entity> activePartList = new ArrayList<Entity>();
	public static Loader loader = new Loader();
	
	
	/**
	 * Purpose : Creates scene components other than the computer part objects, then loads all the components 
	 * 			 of the render on each tick, along with updating the mouse trajectory, until the user closes the GUI or viewport
	 * @param args not used
	 * @return void
	 */
	public void render() {
		int[][] tempTrajectory = new int[3][2];
		int[][] trueTrajectory = null;
		int index = 0;
		int cooldown = 0;
		
		StaticShader shader = new StaticShader();
		Renderer renderer = new Renderer(shader);	
		RawModel actorModel = OBJLoader.loadOBJModel("actor", loader);	
		TexturedModel actorStaticModel = new TexturedModel(actorModel, new ModelTexture(loader.loadTexture("gray")));
		Entity Actor = new Entity(actorStaticModel, new Vector3f(0, 10, 0), 0, 0, 0, 0, "actor");		
		Camera camera = new Camera(Actor);
		Light light = new Light(new Vector3f(0, 0, 0), new Vector3f(1,1,1));
		
		camera.move();
		boolean start = true;
		while(!Display.isCloseRequested()) {
			if(cooldown > 0)
				cooldown--;
			if(index == 3) {
				index = 0;
				trueTrajectory = setMouseLocs(tempTrajectory);
				start = false;
			}
			tempTrajectory = addMouseCoordinate(index, tempTrajectory);
			index++;
			
			if(cooldown == 0) {
				if(!start) 
					if(!resetCursor(trueTrajectory)) 
						camera.move();
					else
						cooldown = 2;
			}
			renderer.prepare();
			shader.start();
			renderer.render(Actor, shader);
			shader.loadLight(light);
			light.setPosition(camera.getPosition());
			shader.loadViewMatrix(camera);
			for (Entity entity  : activePartList) {
				renderer.render(entity, shader);
			}
			shader.start();
			DisplayManager.updateDisplay();
		}
		
		shader.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
		System.exit(1);
	}
	
	/**
	 * Purpose : Creates all PC components that could be used in a user's build, then adds 
	 * 			them to the Entity ArrayList named potentialPartList
	 * @param args not used
	 * @return void
	 */
	public void createParts() {
		
		RawModel caseModel = OBJLoader.loadOBJModel("case", loader);	
		TexturedModel caseStaticModel = new TexturedModel(caseModel, new ModelTexture(loader.loadTexture("gray")));
		potentialPartList.add(new Entity(caseStaticModel, new Vector3f(0, 0, 0), 0, 0, 0, 1, "case"));
		//(0, 0, 0)
		
		RawModel gpuModel = OBJLoader.loadOBJModel("gpu", loader);	
		TexturedModel gpuStaticModel = new TexturedModel(gpuModel, new ModelTexture(loader.loadTexture("gray")));
		potentialPartList.add(new Entity(gpuStaticModel, new Vector3f(0, 0, 0), 0, 0, 0, 1, "gpu"));
		//(-1.5f, 7, -5)

		RawModel powerSupplyModel = OBJLoader.loadOBJModel("PowerSupply", loader);	
		TexturedModel powerSupplyStaticModel = new TexturedModel(powerSupplyModel, new ModelTexture(loader.loadTexture("gray")));
		potentialPartList.add(new Entity(powerSupplyStaticModel, new Vector3f(0, 0, 0), 0, 0, 0, 1, "PowerSupply"));
		//(0, 0, -7)
		
		RawModel motherboardModel = OBJLoader.loadOBJModel("motherboard", loader);	
		TexturedModel motherboardStaticModel = new TexturedModel(motherboardModel, new ModelTexture(loader.loadTexture("gray")));
		potentialPartList.add(new Entity(motherboardStaticModel, new Vector3f(0, 0, 0), -90, 0, 0, 1, "motherboard"));
		//(5.14f, 17, 1)
		
		RawModel cpuModel = OBJLoader.loadOBJModel("cpu", loader);	
		TexturedModel cpuStaticModel = new TexturedModel(cpuModel, new ModelTexture(loader.loadTexture("gray")));
		potentialPartList.add(new Entity(cpuStaticModel, new Vector3f(0, 0, 0), -90, 0, 90, 1, "cpu"));
		//(5.5f, 14.1f, -4.2f)
		
		RawModel ramModel = OBJLoader.loadOBJModel("ram", loader);	
		TexturedModel ramStaticModel = new TexturedModel(ramModel, new ModelTexture(loader.loadTexture("gray")));
		potentialPartList.add(new Entity(ramStaticModel, new Vector3f(0, 0, 0), 0, 0, 90, 1, "ram"));
		//(4.2f, 14.4f, 0)
	}
	
	/**
	 * Purpose : Scales a part on the XYZ plane then adds that part to the current render.
	 * @param partID : Used to determine which part is to be added to the activePartList from the potentialPartList.
	 * @param ScaleX/ScaleY/ScaleZ : Used to determine X Y and Z scale of object.
	 * @return void
	 */
	public void spawnPart(int partID, float ScaleX, float ScaleY, float ScaleZ) {
		potentialPartList.get(partID).setScaleX(ScaleX);
		potentialPartList.get(partID).setScaleY(ScaleY);
		potentialPartList.get(partID).setScaleZ(ScaleZ);
		activePartList.add(potentialPartList.get(partID));
	}
	
	/**
	 * Purpose : Removes a part from the current render.
	 * @param partName : Used to determine which part is to be removed from the activePartList.
	 * @return void
	 */
	public void removePart(String partName) {
		int x = 0;
		for (Entity part : activePartList) {
			if(part.getName().equals(partName)) {
				activePartList.remove(x);
				return;
			}
			x++;
		}
	}
	
	/**
	 * Purpose : Creates a viewing window where the rendering can be seen.
	 * @param args not used
	 * @return void
	 */
	public void createDisplay() {
		DisplayManager.createDisplay();
	}
	
	/**
	 * Purpose : get a single item from the activePartList
	 * @param partName : specifies the part required from the activePartList
	 * @return An entity representing the specified part from the activePartList
	 */
	public Entity getPart(String partName) {
		for (Entity part  : activePartList) {
			if (part.getName().equals(partName))
				return part;
		}
		return null;
	}
	
	/**
	 * Purpose : checks if a single item is in the activePartList
	 * @param partName  : specifies the part being searched for in the activePartList
	 * @return true if part is in the activePartList, false if not
	 */
	public boolean containsPart(String partName) {
		for (Entity part  : activePartList) {
			if (part.getName().equals(partName))
				return true;
		}
		return false;
	}
	
	/**
	 * Purpose : receive an overview of parts that have already been chosen.
	 * @param no args used
	 * @return an ArrayList of entities representing individual computer parts currently being rendered.
	 */
	public ArrayList<Entity> getactivePartList() {
		return activePartList;
	}
	
	/**
	 * Purpose : Keeps the cursor inside the rendering window when moving the camera.
	 * @param mouseLocs  : a 2D int array representing the trajectory of the cursor in the form of its last 3 coordinates.
	 * @return true if the cursor is reset, false if otherwise.
	 */
	public boolean resetCursor(int mouseLocs[][]) {
		int rise = mouseLocs[2][1] - mouseLocs[0][1];
		int run = mouseLocs[2][0] - mouseLocs[0][0];
		int X = mouseLocs[2][0];
		int Y = mouseLocs[2][1];
				
		if(Keyboard.isKeyDown(Keyboard.KEY_GRAVE) && !Mouse.isInsideWindow()) {
			if(run > 0 && rise > 0) {
				while(X > 0 && Y > 0) {
					X -= run;
					Y -= rise;
				}
				Mouse.setCursorPosition(X, Y);
				return true;
			}
			
			else if(run > 0 && rise < 0) {
				while(X > 0 && Y < 720) {
					X -= run;
					Y -= rise;
				}
				Mouse.setCursorPosition(X, Y);
				return true;
			}
			
			else if(run < 0 && rise > 0) {
				while(X < 1279 && Y > 0) {
					X -= run;
					Y -= rise;
				}
				Mouse.setCursorPosition(X, Y);
				return true;
			}
			
			else if(run < 0 && rise < 0) {
				while(X < 1279 && Y < 720) {
					X -= run;
					Y -= rise;
				}
				Mouse.setCursorPosition(X, Y);
				return true;
			}
			
			else if(run == 0 && rise > 0) {
				Mouse.setCursorPosition(X, 0);
				return true;
			}
			
			else if(run == 0 && rise < 0) {
				Mouse.setCursorPosition(X, 719);
				return true;
			}
			
			else if(run > 0 && rise == 0) {
				Mouse.setCursorPosition(0, Y);
				return true;
			}
			
			else if(run < 0 && rise == 0) {
				Mouse.setCursorPosition(1279, Y);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Purpose : Determines a cursor coordinate to add to the temporary trajectory array
	 * @param index  : represents the row of the 2D array where the calculated trajectory is being added,
	 * 				   so the coordinates can be kept in chronological order.
	 * @param trajectory  : 2D int coordinate array representing the current trajectory of the cursor
	 * @return updated 2D array of the current trajectory of the cursor in the form of integer coordinates.
	 */
	public int[][] addMouseCoordinate(int index, int[][] trajectory) {
		trajectory[index][0] = Mouse.getX();
		trajectory[index][1] = Mouse.getY();
		return trajectory;
	}
	
	/**
	 * Purpose : Copies the temporary trajectory array to a complete 3 coordinate trajectory array
	 * @param temp  : the filled temporary 2D integer coordinate array
	 * @return a 2D integer array outlining the true trajectory of the user's cursor in the form of exactly 3 coordinates
	 */
	public int[][] setMouseLocs(int[][] temp) {
		int[][] mouseLocs = new int[3][2];
		for (int i = 0; i < 3; i++) {
			for(int k = 0; k < 2; k++) {
				mouseLocs[i][k] = temp[i][k];
			}
		}
		return mouseLocs;
	}
	
}