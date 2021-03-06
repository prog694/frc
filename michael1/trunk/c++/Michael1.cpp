#include "Michael1.h"
#include "Ports.h"


/*
 void Michael1::Turntoshoot(){
 while(cam->TrackTarget() && cam->oktoshoot()!= 3){
 if(cam->oktoshoot() < 3){
 dt->SetMotors(.05, .05);
 }else if(cam->oktoshoot() >3){
 dt->SetMotors(-.05, -.05);
 }
 }
 }
 */

Michael1::Michael1()
{
	// We're Alive!
	printf("Hello!\n\n\n");
	
	// Driver's Station Inputs
	left_stick = new Joystick(LEFT_DRIVE_JOYSTICK);
	right_stick = new Joystick(RIGHT_DRIVE_JOYSTICK);
	shooter_stick = new Joystick(SHOOTER_JOYSTICK);
	
	// Robot Inputs
	autonswitch = new BinarySwitch(4, AUTON_SELECTOR_4, AUTON_SELECTOR_3, AUTON_SELECTOR_2, AUTON_SELECTOR_1);
	alliance_selector = new DigitalInput(4,ALLIANCE_SELECTOR);
	// Robot Outputs
	ariels_light = new DigitalOutput(ARIELS_LIGHT);
	intake = new Victor(INTAKE_ROLLER);
	shooter = new Victor(SHOOTER_ROLLER);
	servo_1 = new Servo(SERVO1);
	servo_2 = new Servo(SERVO2);
	
	// Helper Objects
	dt = new DriveTrain();
	cam = new Michael1Camera(false);
	
	ds = DriverStation::GetInstance();
	
	// System-Wide timer. Never to be reset.
	time = new Timer();
	time->Start();
	
	// WPILib crap
	GetWatchdog().SetExpiration(100);
}

void Michael1::Autonomous(void)
{
	GetWatchdog().SetEnabled(false);
	
	/*	switch(autonswitch->Get()){
	 case 2: //arc to the right
	 dt->SetMotors(0.5,0.1);
	 break;
	 case 3: //arc to the left
	 dt->SetMotors(0.1,0.5);
	 break;
	 default: //arc to the right
	 dt->SetMotors(1.0,1.0);
	 } */
	
	
	if (autonswitch->Get() == 3)  //arc to the left
		dt->SetMotors(0.1, 0.5);   
	else                          // arc to the right
		dt->SetMotors(0.5,0.1);  
}





/* void Michael1::RunScript(Command* scpt){
 bool finished = false;
 
 while (IsAutonomous())
 {
 switch(scpt->cmd){
 case TURN:
 dt->Turn(scpt->param1,14.5 - time->Get());
 break;
 case JSTK:
 dt->SetMotors(scpt->param1, scpt->param2);
 Wait(14.5 - time->Get());
 break;
 case WAIT:
 dt->SetMotors(0,0);
 Wait(scpt->param1);
 break;
 case FWD:
 dt->GoDistance(scpt->param1,14.5 - time->Get());
 break;
 default:
 dt->SetMotors(0,0);
 finished = true;
 }
 if (finished){
 break;
 }
 scpt++;
 }
 }
 */
void Michael1::OperatorControl(void)
{
	printf("\n\n\tStart Teleop:\n\n");
	double oldTime = 0;
	GetWatchdog().SetEnabled(false);
	ds->SetDigitalOut(3,false);
	dt->gyro->Reset();
	
	
	while (IsOperatorControl())
	{	
		double newTime = time->Get();
		if(newTime - oldTime >= 0.1){
			dt->UpdateSensors();
			oldTime = newTime;	
			printf("%f \n", dt->gyro->GetAngle());
			//ShowActivity("Center_mass_x %f, Center_mass_y %f Height %f, Width %f", cam->par1.center_mass_x, cam->par1.center_mass_y, cam->par1.imageHeight, cam->par1.imageWidth);
		}
		
		
		//dan's goggles
		cam->TrackTarget();
		bool pin[5];
		pin[0] = pin[1] = pin[2] = pin[3] = pin[4] = false;
		if(cam->TrackTarget())
			pin[cam->oktoshoot()-1] = true;
		ds->SetDigitalOut(1, pin[0]);
		ds->SetDigitalOut(2, pin[1]);
		ds->SetDigitalOut(3, pin[2]);
		ds->SetDigitalOut(4, pin[3]);
		ds->SetDigitalOut(5, pin[4]);
		//joystick motor control
		//if (ds->GetDigitalIn(1)){
		if (left_stick->GetTrigger() || right_stick->GetTrigger()){
			//dt->slipMode = true;
			dt->TankDrive(left_stick, right_stick);
		} else {
			//dt->slipMode = false;
			dt->SetMotors(-(left_stick->GetY() / 2), -(right_stick->GetY() / 2));
		}
		/*} else {
		 dt->TankDrive(left_stick, right_stick);
		 }*/
		
		
		//brakes
		if (left_stick->GetRawButton(2) || right_stick->GetRawButton(2)){
			dt->coast->Set(0);
		} else {
			dt->coast->Set(1);
		}
		
		//shooter
		
		
		
		if (shooter_stick->GetTrigger())
		{
			shooter->Set(-1);
		} 
		else 
		{
			shooter->Set(shooter_stick->GetY() * 0.5);
		}
		
		
		/* OI switch box
		 * Left 1u, 2d
		 * mid  5u, 3d
		 * rig	6u, 4d*/
		
		//intake
		if (ds->GetDigitalIn(6) || fabs(shooter->Get()) > 0)
			intake->Set(1);
		else if (ds->GetDigitalIn(4))
			intake->Set(-1);
		if (shooter_stick->GetRawButton(3))
		{
			intake->Set(-1);
		}
		else
			intake->Set(0);		
		
		
		//Servos
		/*if(shooter_stick->GetRawButton(6)){
		 servo_1->Set(0);
		 servo_2->Set(1);
		 }
		 else if (shooter_stick->GetRawButton(7)){
		 servo_1->Set(1);
		 servo_2->Set(0);
		 }
		 else
		 {*/
		servo_1->Set(0);
		servo_2->Set(1);
		//}
	}
}



START_ROBOT_CLASS(Michael1);
