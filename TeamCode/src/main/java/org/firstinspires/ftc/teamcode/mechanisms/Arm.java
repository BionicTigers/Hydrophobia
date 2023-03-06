package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.hardware.lynx.LynxDcMotorController;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.system.Deadline;
import org.firstinspires.ftc.teamcode.teleop.Robot;
import org.firstinspires.ftc.teamcode.util.ControlHub;
import org.firstinspires.ftc.teamcode.util.Mechanism;
import org.firstinspires.ftc.teamcode.util.PID;

import java.util.concurrent.TimeUnit;

public class Arm extends Mechanism {
    public Telemetry telemetry;

    public enum State {
        IDLE,
        MOVING
    }
    public State currentState;

    private int targetTicks = 0;

    private int junkTicks;
    public PID pid;
    public static final double kP = 2.75, kI = 0, kD = 0;


    public ControlHub controlHub;
    public int encoderPos = 0;

    public CRServo left;
    public CRServo right;

    public double setPosition = 26;

    public Arm (CRServo l, CRServo r, HardwareMap hardwareMap, Telemetry T) {
        super();
        //Declares the left motor
        crServos.add(l);
        left = l;
        //Declares the right motor
        crServos.add(r);
        right = r;

        //Flips the right servo so it rotates the right way
        right.setDirection(DcMotorSimple.Direction.REVERSE);

        //Declares telemetry
        telemetry = T;

        //Declares Control Hub
        controlHub = new ControlHub(hardwareMap, hardwareMap.get(LynxDcMotorController.class, "Control Hub"));

        controlHub.refreshBulkData();
        junkTicks = controlHub.getEncoderTicks(3);

//        move(26);

        pid = new PID(kP, kI, kD, 0, 5848, -1, 1);
    }

    @Override
    public void update(Gamepad gp1, Gamepad gp2) {

        //PID CODE

        //Resets lift encoder position
        encoderPos = controlHub.getEncoderTicks(3);

        //Moves the arm upward slowly
        if (gp2.right_bumper) {
            setPosition += 2.5;
        }
        //Moves the arm downward slowly
        if (gp2.left_bumper) {
            setPosition -= 2.5;
        }
        //Moves the arm upward quickly
        if (gp2.dpad_up || gp2.dpad_left || gp2.dpad_down) {
            setPosition = 257;
        }
        //Moves the arm downward quickly
        if (gp2.dpad_right) {
            setPosition = 0;
        }

        setPosition = Math.max(-30, Math.min(260, setPosition));

        move(setPosition);

        //NON PID CODE
//
//        if (gp2.dpad_up || gp2.dpad_left || gp2.dpad_down || gp2.right_bumper) {
//            left.setPower(-1);
//            right.setPower(-1);
//        } else if (gp2.dpad_right || gp2.left_bumper) {
//            left.setPower(1);
//            right.setPower(1);
//        } else {
//            left.setPower(0);
//            right.setPower(0);
//        }
    }

    @Override
    public void write() {

        //PID CODE

        controlHub.refreshBulkData();
        int currentTicks = controlHub.getEncoderTicks(3) - junkTicks;
        double output = pid.calculate(targetTicks, currentTicks) + .2;

        int ticksError = Math.abs(currentTicks - targetTicks);
        telemetry.addData("error", Math.abs(ticksError));

        if (ticksError < 400) {
            currentState = State.IDLE;
        }

        if ((currentTicks < 300 && targetTicks < 300) || (currentTicks > 5348 && targetTicks > 5348)) {
            output = 0;
        }

        //Moves the servos
        left.setPower(-output);
        right.setPower(-output);

        telemetry.addData("Arm Ticks", currentTicks);
        telemetry.addData("Set position", setPosition);
        telemetry.addData("Arm degrees", currentTicks/(8192/360));
        telemetry.addData("PID: ", output);
        telemetry.update();

        //NON PID CODE
        //None lmao
    }

    //Moves the lift forward
    public void move(double degrees) {
        //0 degrees = storing position; 150 = scoring position
        //8192 ticks per revolution; 8192 / 360 = 22.756
        targetTicks = (int) Math.round(degrees * (8192 / 360));
        currentState = State.MOVING;
    }
}