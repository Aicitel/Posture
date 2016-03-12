package calangles;
public class CalAngles {
	float[][] rotation=new float[3][3];
	public float dotProduct(PXCPoint3DF32 A,PXCPoint3DF32 B)
	{
		float result=A.x*B.x+A.y*B.y+A.z*B.z;
		return result;
	}
	public float angleBetween(PXCPoint3DF32 vectorA,PXCPoint3DF32 vectorB)
	{
		float dotResult=dotProduct(vectorA, vectorB)/(abs(vectorA)*abs(vectorB));
		if(dotResult>1)
			dotResult=1;
		float angle=(float) Math.acos(dotResult);
		return angle;
	}
	private float abs(PXCPoint3DF32 A) {
		float result = (float)Math.sqrt(A.x*A.x + A.y*A.y + A.z*A.z);
		return result;
	}
	public PXCPoint3DF32 calAngles(PXCPoint3DF32 oldN,PXCPoint3DF32 newN)
	{
		PXCPoint3DF32 oldNormal=oldN, newNormal=newN;
		float angle = angleBetween(oldNormal, newNormal);
		
		PXCPoint3DF32 rotationAxis;
		rotationAxis=crossProduct(oldNormal, newNormal);
		rotationAxis=normalize(rotationAxis);
		
		RotationMatrix(rotationAxis, angle);
		float xAngle, yAngle, zAngle;
		
		if (rotation[2][0] != 1 && rotation[2][0] != -1)
		{
			yAngle = (float) -Math.asin(rotation[2][0]);
			xAngle = (float) Math.atan2(rotation[2][1] / Math.cos(yAngle), rotation[2][2] / Math.cos(yAngle));
			zAngle = (float) Math.atan2(rotation[1][0] / Math.cos(yAngle), rotation[0][0] / Math.cos(yAngle));
		}
		else
		{
			zAngle = 0;
			if (rotation[2][0] == -1)
			{
				yAngle = (float) 90.0;
				xAngle = (float) (zAngle + Math.atan2(rotation[0][1], rotation[0][2]));
			}
			else
			{
				yAngle = (float) -90.0;
				xAngle = (float) (-zAngle + Math.atan2(-rotation[0][1], -rotation[0][2]));
			}
		}
		//result.x = (pxcF32)r2d(xAngle);
		//result.y = (pxcF32)r2d(yAngle);
		//result.z = (pxcF32)r2d(zAngle);
		PXCPoint3DF32 result= new PXCPoint3DF32();
		result.x = (xAngle);
		result.y = (yAngle);
		result.z = (zAngle);
		return result;
	}
	public void RotationMatrix(PXCPoint3DF32 rotationAxis, float angle)
	{
		//Rodrigues' rotation formula
		rotation[0][0] = (float) (Math.cos(angle) + rotationAxis.x * rotationAxis.x * (1 - Math.cos(angle)));
		rotation[0][1] = (float) (rotationAxis.x*rotationAxis.y*(1 - Math.cos(angle)) - rotationAxis.z*Math.sin(angle));
		rotation[0][2] = (float) (rotationAxis.y*Math.sin(angle) + rotationAxis.x*rotationAxis.z*(1 - Math.cos(angle)));
		rotation[1][0] = (float) (rotationAxis.z*Math.sin(angle) + rotationAxis.x*rotationAxis.y*(1 - Math.cos(angle)));
		rotation[1][1] = (float) (Math.cos(angle) + rotationAxis.y*rotationAxis.y*(1 - Math.cos(angle)));
		rotation[1][2] = (float) (-rotationAxis.x*Math.sin(angle) + rotationAxis.y*rotationAxis.z*(1 - Math.cos(angle)));
		rotation[2][0] = (float) (-rotationAxis.y*Math.sin(angle) + rotationAxis.x*rotationAxis.z*(1 - Math.cos(angle)));
		rotation[2][1] = (float) (rotationAxis.x*Math.sin(angle) + rotationAxis.y*rotationAxis.z*(1 - Math.cos(angle)));
		rotation[2][2] = (float) (Math.cos(angle) + rotationAxis.z*rotationAxis.z*(1 - Math.cos(angle)));
	}
	private PXCPoint3DF32 normalize(PXCPoint3DF32 A) {
		PXCPoint3DF32 result= new PXCPoint3DF32();
		float tmp = abs(A);
		result.x = A.x / tmp;
		result.y = A.y / tmp;
		result.z = A.z / tmp;
		return result;
	}
	private PXCPoint3DF32 crossProduct(PXCPoint3DF32 vectorAB, PXCPoint3DF32 vectorAC) {
		PXCPoint3DF32 result= new PXCPoint3DF32();
		result.x = vectorAB.y*vectorAC.z - vectorAB.z*vectorAC.y;
		result.y = vectorAB.z*vectorAC.x - vectorAB.x*vectorAC.z;
		result.z = vectorAB.x*vectorAC.y - vectorAB.y*vectorAC.x;
		return result;
	}
}
