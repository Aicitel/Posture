package com.actioninstaller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import calangles.Converse;

import com.ms3dstructure.SMs3dJointFrames;
import com.ms3dstructure.SMs3dKeyFrame;
import com.ms3dstructure.TimeNSpace;

public class ActionInstaller {

	//Members  
	private int m_iNumJoints;                  //Number of joints
	private int m_iNumJointsModel;
	private int m_iMode;
	private int m_iNumFrames;                  //Number of frames
	private String textFileRouteString;
	private SMs3dJointFrames[] m_JointsFrames; //Joints' frames
	private TimeNSpace[][] m_JointsPos;        //Position of all joints in every frame
	
	//Getter
	public SMs3dJointFrames[] getM_Joints() {
		return m_JointsFrames;
	}
	public TimeNSpace[][] getM_JointsPos() {
		return m_JointsPos;
	}
	public int getM_iNumJoints() {
		return m_iNumJointsModel;
	}
	
	//Constructor
	public ActionInstaller(int numJoints,int mode) throws Exception {
		//Get the number of joints
		if (numJoints < 0)
			throw new Exception("Negative number of joints");
		if(numJoints == 21)
			m_iNumJointsModel = 20;
		if(numJoints == 42)
			m_iNumJointsModel = 40;
		m_iNumJoints = numJoints;
		
		m_iMode = mode;
		//New joints
		m_JointsFrames = new SMs3dJointFrames[numJoints];
	}
	
	//Load action info file
	public void LoadActionInfo(String fileName, int JointsNumber) throws Exception {

		File file = new File(fileName);
		textFileRouteString = fileName;
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			
			//Read Number of frames
			if ((line = reader.readLine()) == null)
			    throw new Exception("Wrong action info file.\n");
			int frameNum = Integer.parseInt(line);
			if (frameNum < 0)
				throw new Exception("The numbers of frame of this file is negative\n");
			else{
				//System.out.println(frameNum+"framenum");
				m_iNumFrames = frameNum;
			}
			
			m_JointsPos = new TimeNSpace[m_iNumJoints][m_iNumFrames];
			
			//Read info 
			float frameTime;
			for (int i = 0;i < m_iNumFrames;i++)
			{
				//Read head of a frame
				if ((line = reader.readLine()) == null)
				    throw new Exception("Wrong action info file.\n");
				//System.out.println(line+" "+i);
				String temp[] = line.split(" ");
				if (temp.length != 2)
					throw new Exception("Wrong action info file.\n");
				//frameIndex = Integer.parseInt(temp[0]);
				frameTime = Float.parseFloat(temp[1]);
				//Read body of a frame
				for (int j = 0;j < JointsNumber;j++)
				{
					if ((line = reader.readLine()) == null)
					    throw new Exception("Wrong action info file.\n");
                        if (j < 1) {
						    m_JointsPos[j][i] = new TimeNSpace();
						    m_JointsPos[j][i].FillTXYZ(frameTime, 0, 0, 0);
                        } else if (1 < j && j < 22) {
                        	m_JointsPos[j-1][i] = new TimeNSpace();
						    m_JointsPos[j-1][i].FillTXYZ(frameTime, 0, 0, 0);
                        } else if (22 < j && j < 42) {
                        	m_JointsPos[j-2][i] = new TimeNSpace();
						    m_JointsPos[j-2][i].FillTXYZ(frameTime, 0, 0, 0);
                        }
	
				}
			}
			File cardInfoFile = new File(fileName+".cif");
		    if (!cardInfoFile.exists()) {
		    	cardInfoFile.createNewFile();  
		    }
			if ((line = reader.readLine()) != null){
				line = reader.readLine();
			    FileWriter fw = new FileWriter(cardInfoFile.getAbsoluteFile());
			    BufferedWriter bw = new BufferedWriter(fw);
			   
			    bw.write(line);
			    while((line = reader.readLine()) != null)
			    	bw.write(line);
			    
			    bw.close();
			    
			}
			
			
			reader.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null)
				reader.close();
		}
	}

    //Install Joints
	public void InstallAllJoints() {
		//New jointsframes
		for (int i = 0;i < m_iNumJointsModel;i++)
			m_JointsFrames[i] = new SMs3dJointFrames();
		InstallAllJoint();
	}
	private void InstallAllJoint(){
		Converse.Real_pos_set(textFileRouteString);
		for(int axis_index=0; axis_index<Converse.mds_time_axis.length;axis_index++){
			//Converse.PosRecursive();
			Converse.RotRecursive(axis_index);
		}
		//InstallRoot();
		for(int index=0; index < m_iNumJointsModel; index++){
			InstallJoint(index);
			//System.out.println(index+" Finish");
		}
	}
	
	private void InstallJoint(int index) {
		int mode = m_iMode;
		int reverse = -1;
		int d_verse = 0;
		if(mode == 2){
			reverse = 1;
			d_verse = 1;
		}
		
		int real_index = index;
		m_JointsFrames[index].m_sNumRotFrames = (short) m_iNumFrames;
		m_JointsFrames[index].m_sNumTransFrames = (short) m_iNumFrames;
		m_JointsFrames[index].m_RotKeyFrames = new SMs3dKeyFrame[m_iNumFrames];
		//System.out.println("minumframe"+m_iNumFrames );
		m_JointsFrames[index].m_TransKeyFrames = new SMs3dKeyFrame[m_iNumFrames];
		//Install rotate key frames
		for (int i = 0; i < m_iNumFrames;i++) {
			if(index==0 ){
				//System.out.println(i+" here inumframesI");
				m_JointsFrames[index].m_RotKeyFrames[i] = new SMs3dKeyFrame(); 
				m_JointsFrames[index].m_RotKeyFrames[i].FillTXYZ(m_JointsPos[index][i].m_fTime,
					-Converse.Rot_axis_list[i][real_index].x+d_verse*3.14f,//to your face
					Converse.Rot_axis_list[i][real_index].z,//roll
					-Converse.Rot_axis_list[i][real_index].y//wave your hand
					//	0,0,0
					);
			} 
			else if(index==20 ){
				m_JointsFrames[index].m_RotKeyFrames[i] = new SMs3dKeyFrame(); 
				m_JointsFrames[index].m_RotKeyFrames[i].FillTXYZ(m_JointsPos[index][i].m_fTime,
					-Converse.Rot_axis_list[i][real_index].x+d_verse*3.14f,
					Converse.Rot_axis_list[i][real_index].z,//roll
					-Converse.Rot_axis_list[i][real_index].y
					//	0,0,0
					);
			} 
			else if(index>0 && index<20){
				
				m_JointsFrames[index].m_RotKeyFrames[i] = new SMs3dKeyFrame(); 
				m_JointsFrames[index].m_RotKeyFrames[i].FillTXYZ(m_JointsPos[index][i].m_fTime,
					-Converse.Rot_axis_list[i][real_index].z,
					//-rotX,0
					reverse*Math.abs(Converse.Rot_axis_list[i][real_index].x),0
					//	0,0,0
					);
			}
			else if(index>20 && index<40){
				
				m_JointsFrames[index].m_RotKeyFrames[i] = new SMs3dKeyFrame(); 
				m_JointsFrames[index].m_RotKeyFrames[i].FillTXYZ(m_JointsPos[index][i].m_fTime,
					-Converse.Rot_axis_list[i][real_index].z,
					//-rotX,0
					reverse*Math.abs(Converse.Rot_axis_list[i][real_index].x),0
					//	0,0,0
					);
			}
		}
		//Install translate key frames
		if(index==0){
			for (int i = 0;i < m_iNumFrames;i++) {
				m_JointsFrames[index].m_TransKeyFrames[i] = new SMs3dKeyFrame();
				m_JointsFrames[index].m_TransKeyFrames[i].FillTXYZ(m_JointsPos[index][i].m_fTime,
						//0,0,0);
						Converse.Pos_root_list[i][0].x ,
						Converse.Pos_root_list[i][0].y+mode*10 ,
						Converse.Pos_root_list[i][0].z 
						);
			}
		}
		else if(index==20){
			for (int i = 0;i < m_iNumFrames;i++) {
				m_JointsFrames[index].m_TransKeyFrames[i] = new SMs3dKeyFrame();
				m_JointsFrames[index].m_TransKeyFrames[i].FillTXYZ(m_JointsPos[index][i].m_fTime,
						//0,100,0);
						Converse.Pos_root_list[i][1].x ,
						Converse.Pos_root_list[i][1].y+mode*10 ,
						Converse.Pos_root_list[i][1].z
						);
			}
		}
		else {
			for (int i = 0;i < m_iNumFrames;i++) {
				m_JointsFrames[index].m_TransKeyFrames[i] = new SMs3dKeyFrame();
				m_JointsFrames[index].m_TransKeyFrames[i].FillTXYZ(m_JointsPos[index][i].m_fTime,
						0,0,0);
			}
		}
	}
	
}
