package fileUtils;

import com.ms3dgenerator.MS3DGenerator;

public class ModelCalThread implements Runnable  {
	private String modelRoute;
	private String actionRoute;
	private String ms3dRoute;
	private int JointsNumber;
	private int ModelMode;
	public ModelCalThread(String modelRoute, String actionRoute, String ms3dRoute, int JointsNumber,int mode){
		this.modelRoute = modelRoute;
		this.actionRoute = actionRoute;
		this.ms3dRoute = ms3dRoute;
		this.JointsNumber = JointsNumber;
		this.ModelMode = mode;
	}
	@Override
	public void run() {
	 	try {
	   		MS3DGenerator ms3dg = new MS3DGenerator(JointsNumber,ModelMode);
			ms3dg.LoadNGenerate(modelRoute,actionRoute,ms3dRoute,JointsNumber);
			System.out.println("Stored at "+ms3dRoute);
		} 
	 	catch (Exception e) {
			e.printStackTrace();
		}
	}
	    
}

