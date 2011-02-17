package stuy;
/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

import edu.wpi.first.wpilibj.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class DESdroid extends SimpleRobot implements Constants {

    // Robot hardware
    VictorSpeed driveFrontLeft, driveRearLeft, driveFrontRight, driveRearRight;
//    Victor driveFrontLeft, driveRearLeft, driveFrontRight, driveRearRight;
    Arm arm;
    VictorSpeed dummyFLeft, dummyRLeft, dummyFRight, dummyRRight;
    Grabber grabber;
    DigitalInput leftSensor, middleSensor, rightSensor;
    // Driver controls
    Joystick leftStick;
    Joystick rightStick;
    Joystick armStick;

    OperatorInterface oi;
    DriveTrain drive;
    // Autonomous class
    Autonomous auton;

    /**
     * DESdroid constructor.
     */
    public DESdroid() {
        //oi = new OperatorInterface(this);

        arm = new Arm(this);
        grabber = new Grabber();
        leftSensor = new DigitalInput(LINE_SENSOR_LEFT_CHANNEL);
        middleSensor = new DigitalInput(LINE_SENSOR_MIDDLE_CHANNEL);
        rightSensor = new DigitalInput(LINE_SENSOR_RIGHT_CHANNEL);

        driveFrontLeft = new VictorSpeed(CHANNEL_FRONT_LEFT, CHANNEL_FRONT_LEFT_ENC_A, CHANNEL_FRONT_LEFT_ENC_B, true);
        dummyFLeft = new VictorSpeed(CHANNEL_FRONT_LEFT_ENC_A, CHANNEL_FRONT_LEFT_ENC_B, true);
        driveFrontRight = new VictorSpeed(CHANNEL_FRONT_RIGHT, CHANNEL_FRONT_RIGHT_ENC_A, CHANNEL_FRONT_RIGHT_ENC_B, false);
        dummyFRight = new VictorSpeed(CHANNEL_FRONT_RIGHT_ENC_A, CHANNEL_FRONT_RIGHT_ENC_B, false);
        driveRearLeft = new VictorSpeed(CHANNEL_REAR_LEFT, CHANNEL_REAR_LEFT_ENC_A, CHANNEL_REAR_LEFT_ENC_B, true);
        dummyRLeft = new VictorSpeed(CHANNEL_REAR_LEFT_ENC_A, CHANNEL_REAR_LEFT_ENC_B, true);
        dummyRRight = new VictorSpeed(CHANNEL_REAR_RIGHT_ENC_A, CHANNEL_REAR_RIGHT_ENC_B, true);
        driveRearRight = new VictorSpeed(CHANNEL_REAR_RIGHT, CHANNEL_REAR_RIGHT_ENC_A, CHANNEL_REAR_RIGHT_ENC_B, true);

        leftStick = new Joystick(PORT_LEFT_STICK);
        rightStick = new Joystick(PORT_RIGHT_STICK);
        armStick = new Joystick(PORT_ARM_STICK);

        updatePID();

        drive = new DriveTrain(driveFrontLeft,
                driveRearLeft,
                driveFrontRight,
                driveRearRight);

        drive = new DriveTrain(driveFrontLeft,
                driveRearLeft,
                driveFrontRight,
                driveRearRight);

        auton = new Autonomous(this);
    }

    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    public void autonomous() {
        getWatchdog().setEnabled(false);

//        auton.run(oi.getAutonSetting(this));
        auton.lineTrack(false, false);
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() {
        getWatchdog().setEnabled(false);

        updatePID();

        drive.updateWeightGains();

        driveFrontLeft.e.reset();
        driveFrontRight.e.reset();
        driveRearLeft.e.reset();
        driveRearRight.e.reset();

        int i = 0;

        while (isEnabled() && isOperatorControl()) {
            drive.mecanumDrive_Cartesian(
                    leftStick.getX(),   // X translation (horizontal strafe)
                    leftStick.getY(), // Y translation (straight forward)
                    rightStick.getX(), // rotation (clockwise?)
                    0,             // use gyro for field-oriented drive
                    true);
        
            /*if (i == 1000) {
            System.out.println(leftStick.getX() + "  " + leftStick.getY() + "  " + rightStick.getX());
            System.out.println(DriveTrain.scaleInput(leftStick.getX()) + "  " + DriveTrain.scaleInput(leftStick.getY()) + "  " + DriveTrain.scaleInput(rightStick.getX()));

            i = 0;
            }*/
            /*if (leftStick.getRawButton(3)) {
                drive.mecanumDrive_Cartesian(0, -0.25, 0, 0);
            } else if (leftStick.getRawButton(2)) {
                drive.mecanumDrive_Cartesian(0, 0.25, 0, 0);
            } else if (leftStick.getRawButton(4)) {
                drive.mecanumDrive_Cartesian(-0.25, 0, 0, 0);
            } else if (leftStick.getRawButton(5)) {
                drive.mecanumDrive_Cartesian(0.25, 0, 0, 0);
            } else {
                drive.mecanumDrive_Cartesian(0, 0, 0, 0);
            }*/

            if (leftStick.getRawButton(7)) {
                updatePID();
            }
            if (rightStick.getRawButton(6)) {
                System.out.println("front left:" + " getRate: " + driveFrontLeft.e.getRate() + "Output num:" + driveFrontLeft.v.get());
            }

            if (rightStick.getRawButton(11)) {
                System.out.println("front right:" + " getRate: " + driveFrontRight.e.getRate() + "Output num:" + driveFrontRight.v.get());
            }

            if (rightStick.getRawButton(7)) {
                System.out.println("rear left:" + " getRate: " + driveRearLeft.e.getRate() + "Output num:" + driveRearLeft.v.get());
            }

            if (rightStick.getRawButton(10)) {
                System.out.println("rear right:" + " getRate: " + driveRearRight.e.getRate() + "Output num:" + driveRearRight.v.get());
            }

            if (rightStick.getTrigger()) {
                driveFrontLeft.e.reset();
                driveFrontRight.e.reset();
                driveRearLeft.e.reset();
                driveRearRight.e.reset();
            }


            // Arm control
            arm.rotate(armStick.getY());

            // Grabber control
            if (armStick.getTrigger()) {
                grabber.in();
            } else if (armStick.getRawButton(2)) {
                grabber.out();
            } else if (armStick.getRawButton(6)) {
                grabber.rotateUp();
            } else if (armStick.getRawButton(7)) {
                grabber.rotateDown();
            } else {
                grabber.stop();
            }

            Timer.delay(.05);
            i++;
        }
    }

// update PID values.  uses a text file drive_PID_values.txt that must be
// uploaded to the cRIO via ftp://10.6.94.2/ in the root directory.
    public void updatePID() {
        double drivePID[];


        try {
            drivePID = FileIO.getArray("drive_PID_values.txt");


        } catch (Exception e) {
            e.printStackTrace();
            drivePID = new double[3];
            drivePID[0] = PDRIVE;
            drivePID[1] = IDRIVE;
            drivePID[2] = DDRIVE;


        }

        System.out.println("PID:  " + drivePID[0] + "  " + drivePID[1] + "  " + drivePID[2]);


        try {
            driveFrontLeft.c.disable();
            driveFrontRight.c.disable();
            driveRearLeft.disable();
            driveRearRight.disable();

            driveFrontLeft.c.setPID(drivePID[0], drivePID[1], drivePID[2]);
            driveFrontRight.c.setPID(drivePID[0], drivePID[1], drivePID[2]);
            driveRearLeft.c.setPID(drivePID[0], drivePID[1], drivePID[2]);
            driveRearRight.c.setPID(drivePID[0], drivePID[1], drivePID[2]);

            driveFrontLeft.c.enable();
            driveFrontRight.c.enable();
            driveRearLeft.c.enable();
            driveRearRight.c.enable();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
