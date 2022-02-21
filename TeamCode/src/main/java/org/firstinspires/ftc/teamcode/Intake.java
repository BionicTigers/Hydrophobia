package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;

public class Intake extends Mechanism {

    //Both of these variables control the direction of the intake
    public boolean goingIn;
    public boolean goingOut;

    /*
     * Creates, declares, and assigns a motor to the motors array list
     */
    public Intake(DcMotorEx intake) {
        super();
        motors.add(intake);
    }

    /*
     * Controls the robot during TeleOp and sends input to run
     */
    public void update(Gamepad gp1, Gamepad gp2) {
        goingIn = gp1.left_trigger >= .3;
        goingOut = gp1.right_trigger >= .3 || gp1.x;
    }

     //Controls the intake
    public void write() {
        run(goingIn, goingOut);
    }

     //Controls the intake during TeleOp using input from update
    public void run(boolean in, boolean out) {
        if (in) {
            motors.get(0).setPower(-1);
        } else if (out) {
            motors.get(0).setPower(1);
        } else {
            motors.get(0).setPower(0);
        }
    }
}

/*
    Pseudocode:
    When a button is pressed
    Turn of intake
    When another button is pressed
    Reverse intake
    (Optionally) Reverse intake when there is more than one cube in the robot
*/