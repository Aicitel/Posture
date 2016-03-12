package com.ms3dstructure;

public class SMs3dKeyFrame {

	public float m_fTime;
	public TripleFloat m_XYZ;
	
	//Constructor
	public SMs3dKeyFrame() {
		m_XYZ = new TripleFloat();
	}
	
	public void FillTXYZ(float t, float x, float y, float z) {
		m_fTime = t;
		m_XYZ.setXYZ(x, y, z);
	}
	
}

