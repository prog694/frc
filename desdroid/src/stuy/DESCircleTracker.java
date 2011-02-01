/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stuy;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.camera.*;
import edu.wpi.first.wpilibj.image.*;

/**
 *
 * @author Ginkgo
 */
public class DESCircleTracker implements Constants, PIDOutput {

    double kScoreThreshold = .001;
    AxisCamera cam;
    DESTrackerDashboard trackerDashboard;
    DESTarget mainTarget;
    PIDController strafeController;
    DESdroid des;

    public DESCircleTracker(DESdroid d) {
        des = d;

        cam = AxisCamera.getInstance();
        cam.writeResolution(AxisCamera.ResolutionT.k320x240);
        cam.writeBrightness(0);

        mainTarget = new DESTarget();
        mainTarget.m_xPos = 0;

        strafeController = new PIDController(PVAL, IVAL, DVAL, mainTarget, this);
        strafeController.disable();
    }

    public void doCamera() {
        des.halogen_a.set(true);
        try {

            
            if (cam.freshImage()) {// && turnController.onTarget()) {

                ColorImage image = cam.getImage();
                Thread.yield();
                DESTarget[] targets = DESTarget.findCircularTargets(image);
                Thread.yield();
                image.free();
                if (targets.length == 0 || targets[0].m_score < kScoreThreshold) {
                    if (targets.length > 0) {
                        System.out.println(targets[0].m_score);
                    }
                    // System.out.println("No target found");

                    DESTarget[] newTargets = new DESTarget[targets.length + 1];
                    newTargets[0] = new DESTarget();
                    newTargets[0].m_majorRadius = 0;
                    newTargets[0].m_minorRadius = 0;
                    newTargets[0].m_score = 0;
                    for (int i = 0; i < targets.length; i++) {
                        newTargets[i + 1] = targets[i];
                    }
                    mainTarget = newTargets[0];
                    System.out.println("Target not found");
                    // trackerDashboard.updateVisionDashboard(0.0, 0.0, 0.0, 0.0, newTargets);

                } else {
                    //  System.out.println(targets[0]);
                    System.out.println("Target Angle: " + targets[0].getHorizontalAngle());
                    System.out.println("Target m_xPos: " + targets[0].m_xPos);
                    mainTarget = targets[0];
                }
            }
        } catch (NIVisionException ex) {
            ex.printStackTrace();
        } catch (AxisCameraException ex) {
            ex.printStackTrace();
        }

        des.halogen_a.set(false);
    }

    public void pidWrite(double output) {
        des.drive.mecanumDrive_Cartesian(output, 0, 0, 0);
    }

    public void startAligning() {
        strafeController.enable();
        strafeController.setSetpoint(PID_SETPOINT); // the ideal value of m_xPos
    }
    public void stopAligning() {
        strafeController.disable();
    }
}
