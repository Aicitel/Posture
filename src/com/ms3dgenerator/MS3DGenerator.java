package com.ms3dgenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.actioninstaller.ActionInstaller;
import com.ms3dstructure.SMs3dJointFrames;
import com.util.Constants;

public class MS3DGenerator {

	//Two buffers
	private static byte[] SourceBuffer;          //Buffer for static model
	private static byte[] OutBuffer;             //Buffer for output
	
	//Pointer in SourceBuffer
	private static int SourceBufferPtr = 0;
	
	//ActionInstaller
	private static ActionInstaller actionInstaller;

	//Constructor
	public MS3DGenerator(int JointsNumber,int mode) {
		try {
			actionInstaller = new ActionInstaller(JointsNumber,mode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//Load source file and generate a ms3d file
	public void LoadNGenerate(String staticModelFileName,
			String actionInfoFileName, String outputFileName, int JointsNumber) throws Exception {
		
		try {
			
			//Load static model file
			LoadStaticModel(staticModelFileName);
			
			//Load action info to actionInstaller
			actionInstaller.LoadActionInfo(actionInfoFileName,JointsNumber);
			
			//Assemble a new MS3DModel
			AssembleMS3D();
			
			//Write outBuffer to output file
			WriteToFile(outputFileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//=====================================================
	//Process functions in LoadNGenerate
	//=====================================================
	//Load a static ms3d model into sourceBuffer
	private static void LoadStaticModel(String staticModelFileName) throws Exception {
		
		InputStream in = null;
		
		try {
		    //Get size of the static model file
		    File f = new File(staticModelFileName);
		    if (!f.exists() || !f.isFile())
			    throw new Exception("File:"+staticModelFileName+" doesn't exist.");
		    long fileByteSize = f.length();
		
		    //New source buffer and inputstream
		    SourceBuffer = new byte[(int) fileByteSize];
		    in = new FileInputStream(staticModelFileName);
		    
		    //Read in
		    int bytes = in.read(SourceBuffer);
		    if (bytes != fileByteSize)
		    	throw new Exception("Error in loading static model file.\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//Assemble a new ms3d model by combining static model and action info
	private static void AssembleMS3D() {
	
		try {
			//Copy fixed part from SourceBuffer
			CopyFixedMS3D();
			
			//Install actions on joints
			actionInstaller.InstallAllJoints();
			
			//Write joints in OutBuffer
			WriteJoints();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//Copy fixed part of ms3d from sourceBuffer to outputBuffer(included in AssembleMS3D)
	private static void CopyFixedMS3D() throws Exception {
		
		//Function as a pointer in source buffer
		int arrayPos = 0;             
		
		//New outputBuffer and Copy MS3D Head
		OutBuffer = new byte[Constants.SMS3D_HEADER_SIZE];
		System.arraycopy(SourceBuffer, arrayPos,
				OutBuffer, 0, Constants.SMS3D_HEADER_SIZE);
		arrayPos += Constants.SMS3D_HEADER_SIZE;

		//Copy vertices
		short numVerts = byteArrayToShort(SourceBuffer, arrayPos);
		if (numVerts < 0) 
			throw new Exception("Wrong number of vertices");
		byte[] temp = new byte[2 + numVerts * Constants.SMS3D_VERTEX_SIZE];
		System.arraycopy(SourceBuffer, arrayPos,
				temp, 0, temp.length);
		OutBuffer = concat(OutBuffer, temp);
		arrayPos += 2 + numVerts * Constants.SMS3D_VERTEX_SIZE;

		//Copy triangles
		short numTriangles = byteArrayToShort(SourceBuffer, arrayPos);
		if (numTriangles < 0)
			throw new Exception("Wrong number of triangles");
		temp = new byte[2 + numTriangles * Constants.SMS3D_TRIANGLE_SIZE];
		System.arraycopy(SourceBuffer, arrayPos, temp, 0, temp.length);
		OutBuffer = concat(OutBuffer, temp);
		arrayPos += 2 + numTriangles * Constants.SMS3D_TRIANGLE_SIZE;
		//Copy mesh groups
		short numMeshes = byteArrayToShort(SourceBuffer, arrayPos);
		int meshSize = 2;
		int tempPtr = arrayPos;
		tempPtr += 2;
		for (int i = 0;i < numMeshes;i++)
		{
			meshSize += Constants.SMS3D_MESH_FIXED_SIZE;
			tempPtr += 33;
			short numTris = byteArrayToShort(SourceBuffer, tempPtr);
			meshSize += numTris * 2 + 1;
			tempPtr += 2 + numTris * 2 + 1;
		}
		temp = new byte[meshSize];
		System.arraycopy(SourceBuffer, arrayPos, temp, 0, temp.length);
		OutBuffer = concat(OutBuffer, temp);
		arrayPos += meshSize;
		
		//Copy material information
		short numMaterials = byteArrayToShort(SourceBuffer, arrayPos);
		temp = new byte[2 + numMaterials * Constants.SMS3D_MATERIAL_SIZE];
		System.arraycopy(SourceBuffer, arrayPos, temp, 0, temp.length);
		OutBuffer = concat(OutBuffer, temp);
		arrayPos += 2 + numMaterials * Constants.SMS3D_MATERIAL_SIZE;
		
		//Copy skipped data
		temp = new byte[Constants.SMS3D_SKIPPED_DATA_SIZE];
		System.arraycopy(SourceBuffer, arrayPos, temp, 0, temp.length);
		OutBuffer = concat(OutBuffer, temp);
		arrayPos += Constants.SMS3D_SKIPPED_DATA_SIZE;
		
		SourceBufferPtr = arrayPos;
	}
	//Write joints info to OutBuffer
	private static void WriteJoints() throws Exception {
		
		//Function as a pointer in source buffer
		int arrayPos = SourceBufferPtr;
		
		//Get number of joints and write to out buffer
		short numJoints = byteArrayToShort(SourceBuffer, arrayPos);
		System.out.println("HHHH "+numJoints + " " + actionInstaller.getM_iNumJoints());
		if (numJoints != actionInstaller.getM_iNumJoints())
			throw new Exception("Number of joints doesn't match");
		byte[] temp = new byte[2];
		System.arraycopy(SourceBuffer, arrayPos, temp, 0, temp.length);
		OutBuffer = concat(OutBuffer, temp);
        arrayPos += 2;
        
		//Write info of joints to out buffer
		for (int i = 0;i < numJoints;i++) {
			//Write static info of joints from source buffer
			//Origin
			temp = new byte[Constants.SMS3D_JOINTS_FIXED_SIZE];
			System.arraycopy(SourceBuffer, arrayPos, temp, 0, temp.length);
			OutBuffer = concat(OutBuffer, temp);
			arrayPos += Constants.SMS3D_JOINTS_FIXED_SIZE + 4 + 2 * 16;
			
			/*
			temp = new byte[89];
			System.arraycopy(SourceBuffer, arrayPos, temp, 0, 65);
			float zero = 0.0f;
			byte[] temp1 = new byte[0];
			for (int j = 0;j < 3;j++)
				temp1 = concat(temp1, floatToBytes(zero));
			System.arraycopy(temp1, 0, temp, 65, 12);
			System.arraycopy(SourceBuffer, arrayPos+77, temp, 77, 12);
			OutBuffer = concat(OutBuffer, temp);
			arrayPos += Constants.SMS3D_JOINTS_FIXED_SIZE + 4 + 2 * 16;
			*/
			//Write action info of joints
			temp = SMs3dJF2ByteArray(actionInstaller.getM_Joints()[i]);
			//System.out.println(i+" SMS3DJF2BYTEARRAY");
			OutBuffer = concat(OutBuffer, temp);
		}
	}
	//Write OutBuffer to outputfile
	private static void WriteToFile(String outputFileName) throws IOException {
		File file = new File(outputFileName);
		System.out.println(outputFileName+"want to write in ");
		if (!file.exists())
			file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(OutBuffer);
		fos.close();
	}
	//=====================================================
	//Assist functions in LoadNGenerate
	//=====================================================
	private static short byteArrayToShort(byte[] b, int offset) {
		short value = 0;
	       for (int i = 0; i < 2; i++) {
	           int shift= i * 8;
	           value += (b[i + offset] & 0x000000FF) << shift;
	       }
	    return value;
	}
	//Combine two arrays
	private static byte[] concat(byte[] outBuffer2, byte[] temp) {  
		byte[] result = Arrays.copyOf(outBuffer2, outBuffer2.length + temp.length);  
		System.arraycopy(temp, 0, result, outBuffer2.length, temp.length);  
		return result;  
	} 
	
    //Convert SMs3dJointFrames to byte array
	private static byte[] SMs3dJF2ByteArray(SMs3dJointFrames jointFrames) {
		/*if(jointFrames==null)
			System.out.println(jointFrames==null);
		else {
			System.out.println("joint frames not null "+jointFrames.m_RotKeyFrames.length);
		}*/
		byte result[] = new byte[4 + (jointFrames.m_sNumRotFrames + 
				jointFrames.m_sNumTransFrames) * Constants.SMS3D_KEY_FRAME_SIZE];
		int destPos = 0;
		System.arraycopy(shortToBytes(jointFrames.m_sNumRotFrames),
			    0, result, destPos, 2);
		destPos += 2;
		System.arraycopy(shortToBytes(jointFrames.m_sNumTransFrames),
			    0, result, destPos, 2);
		destPos += 2;
		for (int i = 0;i < jointFrames.m_sNumRotFrames;i++) {
			//if(jointFrames.m_RotKeyFrames[i]==null)
			//	System.out.println(jointFrames.m_RotKeyFrames[i]==null);
			System.arraycopy(floatToBytes(jointFrames.m_RotKeyFrames[i].m_fTime),
					0, result, destPos, 4);
			destPos += 4;
			System.arraycopy(floatToBytes(jointFrames.m_RotKeyFrames[i].m_XYZ.m_fParamX),
				0, result, destPos, 4);
			destPos += 4;
			System.arraycopy(floatToBytes(jointFrames.m_RotKeyFrames[i].m_XYZ.m_fParamY),
					0, result, destPos, 4);
			destPos += 4;
			System.arraycopy(floatToBytes(jointFrames.m_RotKeyFrames[i].m_XYZ.m_fParamZ),
					0, result, destPos, 4);
			destPos += 4;
		}
		for (int i = 0;i < jointFrames.m_sNumTransFrames;i++) {
			System.arraycopy(floatToBytes(jointFrames.m_TransKeyFrames[i].m_fTime),
					0, result, destPos, 4);
			destPos += 4;
			System.arraycopy(floatToBytes(jointFrames.m_TransKeyFrames[i].m_XYZ.m_fParamX),
				0, result, destPos, 4);
			destPos += 4;
			System.arraycopy(floatToBytes(jointFrames.m_TransKeyFrames[i].m_XYZ.m_fParamY),
					0, result, destPos, 4);
			destPos += 4;
			System.arraycopy(floatToBytes(jointFrames.m_TransKeyFrames[i].m_XYZ.m_fParamZ),
					0, result, destPos, 4);
			destPos += 4;
		}
		return result;
	}
	//Convert basic data type to byte array
	public static byte[] shortToBytes(short s) {  
        byte[] b = new byte[8];  
        b[1] = (byte) (s >>> 8);  
        b[0] = (byte) s;  
        return b;  
    }  
	public static byte[] intToBytes(int i) {  
        byte[] b = new byte[4];  
        b[3] = (byte) (i >>> 24);  
        b[2] = (byte) (i >>> 16);  
        b[1] = (byte) (i >>> 8);  
        b[0] = (byte) i;  
        return b;  
    }  
	public static byte[] floatToBytes(float f) {  
        return intToBytes(Float.floatToIntBits(f));  
    }  
}
