#include "AxisCamera.h" 
#include "BaeUtilities.h"
#include "FrcError.h" 
#include "PCVideoServer.h" 
#include "TrackAPI.h"

class Camera
{
public:
	Camera(bool); // parameter denotes to start a PC Server
	bool GetNewImage(); // returns true on new image, false on no new image
	
	Image *img;
	double imgTimestamp;
	double oldTimestamp;
	PCVideoServer *pcvs;

};
