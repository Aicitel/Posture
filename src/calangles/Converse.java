package calangles;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;


public class Converse {

	public static CalAngles cal = new CalAngles();

	public static float[] mds_time_axis;
	
	public static PXCPoint3DF32[][] Rot_axis_list ;
	public static PXCPoint3DF32[][] Rot_root_sub_list ;  
	public static PXCPoint3DF32[][] Pos_real_list ;
	public static PXCPoint3DF32[][] Pos_root_list ;

	public static void Init(int size){
		
		System.out.println("The size is "+size);
		
		Pos_real_list = new PXCPoint3DF32[size][42];
		for(int i = 0 ;i<size;i++){
			Pos_real_list[i]= new PXCPoint3DF32[42];
			for(int t=0; t<42; t++)
				Pos_real_list[i][t]= new PXCPoint3DF32();
		}
		
		Rot_axis_list = new PXCPoint3DF32[size][40];
		for(int i = 0 ;i<size;i++){
			Rot_axis_list[i]= new PXCPoint3DF32[40];
			for(int t=0; t<40; t++)
				Rot_axis_list[i][t]= new PXCPoint3DF32();
		}
		
		Pos_root_list = new PXCPoint3DF32[size][2];
		for(int i=0; i<size; i++){
			Pos_root_list[i] = new PXCPoint3DF32[2];
			for(int t=0; t<2; t++)
				Pos_root_list[i][t]= new PXCPoint3DF32();
		}

		Rot_root_sub_list = new PXCPoint3DF32[size][2];
		for(int i=0; i<size; i++){
			Rot_root_sub_list[i] = new PXCPoint3DF32[2];
			for(int t=0; t<2; t++)
				Rot_root_sub_list[i][t]= new PXCPoint3DF32();
		}
	}
	
	
	public static void Single_pos_set(String line_string,int time_axis,int index){
		float gv[] = String2Float(line_string);
		//System.out.println("Set "+time_axis+" "+index+" "+gv[1]);
		if(index == 0 ) {
			Pos_root_list[time_axis][0].x = gv[1];
			Pos_root_list[time_axis][0].y = gv[2];
			Pos_root_list[time_axis][0].z = gv[3];
		}
		if(index == 21 ) {
			Pos_root_list[time_axis][1].x = gv[1];
			Pos_root_list[time_axis][1].y = gv[2];
			Pos_root_list[time_axis][1].z = gv[3];
		}
		//Pos_real_list[time_axis][index] = new PXCPoint3DF32();
		Pos_real_list[time_axis][index].x=gv[1];
		Pos_real_list[time_axis][index].y=gv[2];
		Pos_real_list[time_axis][index].z=gv[3];
	}
	public static void Real_pos_set(String textFileRoute){

		File file = new File(textFileRoute);
		//File file = new File("E://javaIO/display/CatManager/CatManager/Real_Joints_Pos.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            tempString = reader.readLine();
            //System.out.println("Whole zhenshu is "+tempString);
            mds_time_axis=new float[Integer.parseInt(tempString)];
            Init(mds_time_axis.length);
            System.out.println(mds_time_axis.length+" length");
            
            for(int timeIndex=0;timeIndex<mds_time_axis.length;timeIndex++)
            {
	            tempString = reader.readLine();
	            mds_time_axis[timeIndex] = String2Float(tempString)[1];
	            for (int cnt_line = 0;cnt_line<=20;cnt_line++)
	            {
	            	tempString = reader.readLine();
	            	Single_pos_set(tempString,timeIndex,cnt_line);
	            }

	            for (int cnt_line = 21;cnt_line<=41;cnt_line++)
	            {
	            	tempString = reader.readLine();
	            	Single_pos_set(tempString,timeIndex,cnt_line);
	            }
            }
            reader.close();

            RS2OpenGL();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
	}
	private static void RS2OpenGL() {
		float corrected_value_y = 0.15f;
		float corrected_value_z = 0.25f;
		for (int i = 0;i < mds_time_axis.length;i++) {
			System.out.println("12-0 "+
				Math.sqrt(
						(
							(Pos_real_list[i][12].y-Pos_real_list[i][0].y)*(Pos_real_list[i][12].y-Pos_real_list[i][0].y)
							+(Pos_real_list[i][12].x-Pos_real_list[i][0].x)*(Pos_real_list[i][12].x-Pos_real_list[i][0].x)
							+(Pos_real_list[i][12].z-Pos_real_list[i][0].z)*(Pos_real_list[i][12].z-Pos_real_list[i][0].z)
						)
					)
			);
			System.out.println("21-0 "+
					Math.sqrt(
						(Pos_real_list[i][21].y-Pos_real_list[i][0].y)*(Pos_real_list[i][21].y-Pos_real_list[i][0].y)
						+(Pos_real_list[i][21].x-Pos_real_list[i][0].x)*(Pos_real_list[i][21].x-Pos_real_list[i][0].x)
						+(Pos_real_list[i][21].z-Pos_real_list[i][0].z)*(Pos_real_list[i][21].z-Pos_real_list[i][0].z)
						)
				);
		}
		for (int i = 0;i < mds_time_axis.length;i++) {
			for (int j = 0;j < 42;j++) {
				Pos_real_list[i][j].x *= 142;
				Pos_real_list[i][j].y += corrected_value_y;
				Pos_real_list[i][j].y *= 142;
				Pos_real_list[i][j].z = corrected_value_z - Pos_real_list[i][j].z;
				Pos_real_list[i][j].z *= 142;
			}
		}
		for (int i = 0;i < mds_time_axis.length;i++) {
			for (int j = 0;j < 2;j++) {
				Pos_root_list[i][j].x *= 142;
				Pos_root_list[i][j].y += corrected_value_y;
				Pos_root_list[i][j].y *= 142;
				Pos_root_list[i][j].z = corrected_value_z - Pos_root_list[i][j].z;
				Pos_root_list[i][j].z *= 142;
			}
		}
	}
	
	public static PXCPoint3DF32 Pos_sub(PXCPoint3DF32 child ,PXCPoint3DF32 parent){
		PXCPoint3DF32 resultPos = new PXCPoint3DF32();
		resultPos.x = child.x-parent.x;
		resultPos.y = child.y-parent.y;
		resultPos.z = child.z-parent.z;
		return resultPos;
	}
	
	public static void RotSet(int time_axis,int jointIndex, int parentIndex, int childIndex,int targetIndex){
		Rot_axis_list[time_axis][targetIndex] = new PXCPoint3DF32();
		//System.out.println("In RotSet "+time_axis+" "+jointIndex+" "+childIndex);
		Rot_axis_list[time_axis][targetIndex] = cal.calAngles(
				Pos_sub(Pos_real_list[time_axis][jointIndex],
						Pos_real_list[time_axis][parentIndex]),
				Pos_sub(Pos_real_list[time_axis][childIndex],
						Pos_real_list[time_axis][jointIndex]));
		Rot_axis_list[time_axis][targetIndex].z=0;
		//if(jointIndex==6)
		//	System.out.println("???"+Rot_axis_list[time_axis][jointIndex].x);
	}
	
	public static void RotSetFinger(int time_axis,int jointIndex, int parentIndex, int childIndex,int targetIndex,int helpIndex,float inia){
		Rot_axis_list[time_axis][targetIndex] = new PXCPoint3DF32();
		//System.out.println("In RotSet "+time_axis+" "+jointIndex+" "+childIndex);
		Rot_axis_list[time_axis][targetIndex] = cal.calAngles(
				Pos_sub(Pos_real_list[time_axis][jointIndex],
						Pos_real_list[time_axis][parentIndex]),
				Pos_sub(Pos_real_list[time_axis][childIndex],
						Pos_real_list[time_axis][jointIndex]));
		PXCPoint3DF32 child=new PXCPoint3DF32();
		child =Pos_sub(Pos_real_list[time_axis][childIndex],
				Pos_real_list[time_axis][jointIndex]);
		PXCPoint3DF32 help=new PXCPoint3DF32();
		help =Pos_sub(Pos_real_list[time_axis][helpIndex],
				Pos_real_list[time_axis][childIndex]);
		float cosang=(float) ((child.x*help.x+child.y*help.y+child.z*help.z)/((Math.sqrt(child.x*child.x+child.y*child.y+child.z*child.z))*Math.sqrt(help.x*help.x+help.y*help.y+help.z*help.z)));
		if(cosang>=1.0f)
			cosang = 1.0f;
		float angle=(float) Math.acos(cosang);
		System.out.println(cosang+" cosang "+angle+" "+jointIndex);
		Rot_axis_list[time_axis][targetIndex].z=angle-inia;
		System.out.println(Rot_axis_list[time_axis][targetIndex].z+" hahah");
		//Rot_axis_list[time_axis][targetIndex].z = 0;
		//if(jointIndex==6)
		//	System.out.println("???"+Rot_axis_list[time_axis][jointIndex].x);
	}
	/*
	public static void RotSet(int time_axis,int jointIndex, int parentIndex, int childIndex,int targetIndex){
		Rot_axis_list[time_axis][targetIndex] = new PXCPoint3DF32();
		//System.out.println("In RotSet "+time_axis+" "+jointIndex+" "+childIndex);
		PXCPoint3DF32 c1=cal.calAngles(
				Pos_sub(Pos_real_list[time_axis][jointIndex],
						Pos_real_list[time_axis][parentIndex]),
				Pos_sub(Pos_real_list[time_axis][childIndex],
						Pos_real_list[time_axis][jointIndex]));
		PXCPoint3DF32 inix=new PXCPoint3DF32();
		inix.x=0.0f;
		inix.y=0.0f;
		inix.z=-1.0f;
		PXCPoint3DF32 c2=cal.calAngles(inix
				,
				Pos_sub(Pos_real_list[time_axis][jointIndex],
						Pos_real_list[time_axis][parentIndex]));
		PXCPoint3DF32 result=new PXCPoint3DF32();
		result.x=c1.x+c2.x;
		result.y=c1.y+c2.y;
		result.z=c1.z+c2.z;
		Rot_axis_list[time_axis][targetIndex] = result;
		
		//if(jointIndex==6)
		//	System.out.println("???"+Rot_axis_list[time_axis][jointIndex].x);
	}*/
	public static PXCPoint3DF32 Linit18= new PXCPoint3DF32();
	public static PXCPoint3DF32 Linit6= new PXCPoint3DF32();
	public static PXCPoint3DF32 Lroot= new PXCPoint3DF32();
	public static void RootRotSetLeft(int time_axis){
		
		if(time_axis==0)
		{
		//PXCPoint3DF32 init18= new PXCPoint3DF32();
		Linit18.x=(float) 6.92513;
		Linit18.y=(float) 15.1452;
		Linit18.z=(float) 0.120883;
		//PXCPoint3DF32 init6= new PXCPoint3DF32();
		Linit6.x=(float) -3.82466;
		Linit6.y=(float) 15.7071;
		Linit6.z=(float) -0.666846;
		//PXCPoint3DF32 root= new PXCPoint3DF32();
		Lroot.x=(float) 0.0;
		Lroot.y=(float) 0.0;
		Lroot.z=(float) 0.0;
		}

		PXCPoint3DF32 caolulu2= new PXCPoint3DF32();
		PXCPoint3DF32 caolulu1= new PXCPoint3DF32();
		
		PXCPoint3DF32 caolulu0= new PXCPoint3DF32();
		
		//System.out.println(time_axis+" "+test.x+" "+test.y+" "+test.z);
		caolulu1=cal.calAngles(
				Pos_sub(Pos_real_list[time_axis][17],
						Pos_real_list[time_axis][0]),
				Pos_sub(Linit18,
						Lroot)
				);
		caolulu2=cal.calAngles(
				Pos_sub(Pos_real_list[time_axis][5],
						Pos_real_list[time_axis][0]),
				Pos_sub(Linit6,
						Lroot)
				);
		caolulu0=cal.calAngles(
				Pos_sub(Pos_real_list[time_axis][17],
						Pos_real_list[time_axis][5]),
				Pos_sub(Linit18,
						Linit6)
				);
		Linit18.x=Pos_real_list[time_axis][17].x;
		Linit18.y=Pos_real_list[time_axis][17].y;
		Linit18.z=Pos_real_list[time_axis][17].z;
		
		Linit6.x=Pos_real_list[time_axis][5].x;
		Linit6.y=Pos_real_list[time_axis][5].y;
		Linit6.z=Pos_real_list[time_axis][5].z;
		
		Lroot.x=Pos_real_list[time_axis][0].x;
		Lroot.y=Pos_real_list[time_axis][0].y;
		Lroot.z=Pos_real_list[time_axis][0].z;
		//Rot_axis_list[time_axis][0]= new PXCPoint3DF32();
		
		//Rot_axis_list[time_axis][0].x=(caolulu1.x+caolulu2.x)/2-((caolulu1.z+caolulu2.z)/2-caolulu0.z)/2;
		//Rot_axis_list[time_axis][0].z=-caolulu0.y+(-caolulu0.z-caolulu0.x+(caolulu1.x+caolulu2.x)/2+(caolulu1.z+caolulu2.z)/2)/2;
		//Rot_axis_list[time_axis][0].y=(caolulu1.z+caolulu2.z)/2;
		
		Rot_root_sub_list[time_axis][0].x=(caolulu1.x+caolulu2.x)/2-((caolulu1.z+caolulu2.z)/2-caolulu0.z)/2;
		Rot_root_sub_list[time_axis][0].z=-caolulu0.y+(-caolulu0.z-caolulu0.x+(caolulu1.x+caolulu2.x)/2+(caolulu1.z+caolulu2.z)/2)/2;
		//Rot_root_sub_list[time_axis][0].z = -Rot_root_sub_list[time_axis][0].z;
		Rot_root_sub_list[time_axis][0].y=(caolulu1.z+caolulu2.z)/2;
		
	}
	public static PXCPoint3DF32 Rinit18= new PXCPoint3DF32();
	public static PXCPoint3DF32 Rinit6= new PXCPoint3DF32();
	public static PXCPoint3DF32 Rroot= new PXCPoint3DF32();
	public static void RootRotSetRight(int time_axis){
		
		if(time_axis==0)
		{
			Rinit18.x=(float) -6.92511;
			Rinit18.y=(float) 15.1452;
			Rinit18.z=(float) 0.120883;
			
			Rinit6.x=(float) 3.82466;
			Rinit6.y=(float) 15.7071;
			Rinit6.z=(float) -0.666846;
			
			Rroot.x=(float) 0;
			Rroot.y=(float) 0;
			Rroot.z=(float) 0;
		}
		PXCPoint3DF32 caolulu2= new PXCPoint3DF32();
		PXCPoint3DF32 caolulu1= new PXCPoint3DF32();
		
		PXCPoint3DF32 caolulu0= new PXCPoint3DF32();
		caolulu1=cal.calAngles(
				Pos_sub(Pos_real_list[time_axis][38],
						Pos_real_list[time_axis][21]),
				Pos_sub(Rinit18,
						Rroot)
				);
		caolulu2=cal.calAngles(
				Pos_sub(Pos_real_list[time_axis][26],
						Pos_real_list[time_axis][21]),
				Pos_sub(Rinit6,
						Rroot)
				);
		//System.out.println(caolulu2.x*180.0f/3.14f+" "+
				//caolulu2.y*180.0f/3.14f+" "+caolulu2.z*180.0f/3.14f);
		caolulu0=cal.calAngles(
				Pos_sub(Pos_real_list[time_axis][38],
						Pos_real_list[time_axis][26]),
				Pos_sub(Rinit18,
						Rinit6)
				);
		
		Rinit18.x=Pos_real_list[time_axis][38].x;
		Rinit18.y=Pos_real_list[time_axis][38].y;
		Rinit18.z=Pos_real_list[time_axis][38].z;
		
		Rinit6.x=Pos_real_list[time_axis][26].x;
		Rinit6.y=Pos_real_list[time_axis][26].y;
		Rinit6.z=Pos_real_list[time_axis][26].z;
		
		Rroot.x=Pos_real_list[time_axis][21].x;
		Rroot.y=Pos_real_list[time_axis][21].y;
		Rroot.z=Pos_real_list[time_axis][21].z;
		
		//Rot_axis_list[time_axis][20]= new PXCPoint3DF32();
		//Rot_axis_list[time_axis][20].x=(caolulu1.x+caolulu2.x)/2+((caolulu1.z+caolulu2.z)/2-caolulu0.z)/2;
		
		//Rot_axis_list[time_axis][20].z=-caolulu0.y+(-caolulu0.z-caolulu0.x+(caolulu1.x+caolulu2.x)/2+(caolulu1.z+caolulu2.z)/2)/2;;
		//Rot_axis_list[time_axis][20].y=(caolulu1.z+caolulu2.z)/2;
		Rot_root_sub_list[time_axis][1].x=(caolulu1.x+caolulu2.x)/2+((caolulu1.z+caolulu2.z)/2-caolulu0.z)/2;
		Rot_root_sub_list[time_axis][1].z=-caolulu0.y+(-caolulu0.z-caolulu0.x+(caolulu1.x+caolulu2.x)/2+(caolulu1.z+caolulu2.z)/2)/2;
		Rot_root_sub_list[time_axis][1].y=(caolulu1.z+caolulu2.z)/2;
		
	}
	
	public static void ZeroRotSet(int time_axis, int index){
		Rot_axis_list[time_axis][index]= new PXCPoint3DF32();
		Rot_axis_list[time_axis][index].x=0;
		Rot_axis_list[time_axis][index].y=0;
		Rot_axis_list[time_axis][index].z=0;
	}
	
	public static void SumRotSet(){

		Rot_axis_list[0][0] = Rot_root_sub_list[0][0];
		Rot_axis_list[0][20] = Rot_root_sub_list[0][1];
		for(int time_axis=1;time_axis<mds_time_axis.length;time_axis++){
			Rot_axis_list[time_axis][0].x = Rot_axis_list[time_axis-1][0].x+Rot_root_sub_list[time_axis][0].x;
			Rot_axis_list[time_axis][0].y = Rot_axis_list[time_axis-1][0].y+Rot_root_sub_list[time_axis][0].y;
			Rot_axis_list[time_axis][0].z = Rot_axis_list[time_axis-1][0].z+Rot_root_sub_list[time_axis][0].z;
			
			Rot_axis_list[time_axis][20].x = Rot_axis_list[time_axis-1][20].x+Rot_root_sub_list[time_axis][1].x;
			Rot_axis_list[time_axis][20].y = Rot_axis_list[time_axis-1][20].y+Rot_root_sub_list[time_axis][1].y;
			Rot_axis_list[time_axis][20].z = Rot_axis_list[time_axis-1][20].z+Rot_root_sub_list[time_axis][1].z;
		}
		
		//Turn Down Hands in Legend Mode
		/*
		for(int time_axis=0;time_axis<mds_time_axis.length;time_axis++)
		{
			Rot_axis_list[time_axis][0].x += Math.PI;
			Rot_axis_list[time_axis][20].x += Math.PI;
		}*/
	}
	
	public static void RotRecursive(int time_axis){
		
		RootRotSetLeft(time_axis);
		RotSetFinger(time_axis,2,0,3,1,5,1.6985219f);
		//RotSet(time_axis,2,0,3,1);
		RotSet(time_axis,3,2,4,6);
		ZeroRotSet(time_axis,7);
		//RotSet(time_axis,5,0,6,2);
		RotSetFinger(time_axis,5,0,6,2,9,2.673468f);
		RotSet(time_axis,6,5,7,8);
		RotSet(time_axis,7,6,8,9);
		ZeroRotSet(time_axis,10);
		//RotSet(time_axis,9,0,10,3);
		RotSetFinger(time_axis,9,0,10,3,13,2.7769709f);
		RotSet(time_axis,10,9,11,11);
		RotSet(time_axis,11,10,12,12);
		ZeroRotSet(time_axis,13);
		//RotSet(time_axis,13,0,14,4);
		RotSetFinger(time_axis,13,0,14,4,17,2.6994295f);
		RotSet(time_axis,14,13,15,14);
		RotSet(time_axis,15,14,16,15);
		ZeroRotSet(time_axis,16);
		RotSetFinger(time_axis,17,0,18,5,13,2.4123836f);
		//RotSet(time_axis,17,0,18,5);
		RotSet(time_axis,18,17,19,17);
		RotSet(time_axis,19,18,20,18);
		ZeroRotSet(time_axis,19);
		
		RootRotSetRight(time_axis);
		RotSetFinger(time_axis,23,21,24,21,26,1.6985219f);
		//RotSet(time_axis,23,21,24,21);
		RotSet(time_axis,24,23,25,26);
		ZeroRotSet(time_axis,27);
		//RotSet(time_axis,26,21,27,22);
		RotSetFinger(time_axis,26,21,27,22,30,2.673468f);
		RotSet(time_axis,27,26,28,28);
		RotSet(time_axis,28,27,29,29);
		ZeroRotSet(time_axis,30);
		//RotSet(time_axis,30,21,31,23);
		RotSetFinger(time_axis,30,21,31,23,34,2.7769709f);
		RotSet(time_axis,31,30,32,31);
		RotSet(time_axis,32,31,33,32);
		ZeroRotSet(time_axis,33);
		//RotSet(time_axis,34,21,35,24);
		RotSetFinger(time_axis,34,21,35,24,38,2.6994295f);
		RotSet(time_axis,35,34,36,34);
		RotSet(time_axis,36,35,37,35);
		ZeroRotSet(time_axis,36);
		RotSet(time_axis,38,21,39,25);
		RotSetFinger(time_axis,38,21,39,25,34,2.4123836f);
		RotSet(time_axis,39,38,40,37);
		RotSet(time_axis,40,39,41,38);
		ZeroRotSet(time_axis,39);
		
		SumRotSet();   
	}

	public static float[] String2Float(String goodsVolume){
		float gv[];
		int i = 0; 
		StringTokenizer tokenizer = new StringTokenizer(goodsVolume, " ");
		gv = new float[tokenizer.countTokens()];
		while (tokenizer.hasMoreTokens()) {
		    String d = tokenizer.nextToken();
		    gv[i] = Float.valueOf(d).floatValue();
		    i++;
		}
		return gv;
	}

}
