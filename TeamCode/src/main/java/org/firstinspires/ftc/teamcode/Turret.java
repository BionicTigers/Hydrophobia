package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.internal.system.Deadline;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.concurrent.TimeUnit;

public class Turret extends Mechanism{

    public boolean forward;
    public boolean left;
    public boolean backward;
    public boolean right;
    public int position = 1;

    public boolean altMode = false;
    public int spinTrim = 0;
    public int spinLocation = 0;
    public int verticalTrim = 0;
    public double horizontalTrim = 0;
    public boolean extend = false;
    public boolean middle = false; //middle is for extension, do not confuse with mid
    public boolean retract = false;

    //public String position = "Mid";
    public Telemetry telemetry;
    public DcMotorEx motor;
    private boolean up;
    private boolean mid; //mid is for height, do not confuse with middle
    private boolean liftOverride;
    private boolean down;
    public boolean scorepos;
    public boolean retractOnEnd;
    Deadline wait = new Deadline (400, TimeUnit.MILLISECONDS);
    public boolean sharedHubExtend;
    public boolean sharedHubExtendleft;


    Deadline wait2retract = new Deadline (100, TimeUnit.MILLISECONDS);

    public Turret(DcMotorEx turret, DcMotorEx turretRise, Servo turretL, Servo turretR, Telemetry T){
        super();
        telemetry = T;
        motors.add(turret);
        motors.add(turretRise);
        getServos().add(turretL);
        getServos().add(turretR);
        motors.get(0).setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motors.get(0).setTargetPosition(0);
        motors.get(0).setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motors.get(1).setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motors.get(1).setTargetPosition(0);
        motors.get(1).setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void update(Gamepad gp1, Gamepad gp2){
        if(gp2.dpad_up){
            forward = true;
            spinTrim = 0;
        } else if(gp2.dpad_left && !gp2.left_bumper){
            right = true;
            spinTrim = 0;
        } else if(gp2.dpad_down){
            backward = true;
            spinTrim = 0;
        } else if(gp2.dpad_right && !gp2.left_bumper){
            left = true;
            spinTrim = 0;
        } else {
            forward = false;
            left = false;
            backward = false;
            right = false;
        }
        if(gp2.left_bumper && gp2.dpad_right){
            mid = true; up = false; down = false;
            middle = false; extend = false; retract = true;
            sharedHubExtend = true;
        }
        else if (gp2.left_bumper && gp2.dpad_left){
            mid = true; up = false; down = false;
            middle = true; extend = false; retract = true;
            sharedHubExtendleft = true;
        }
        if (gp1.right_bumper && gp1.dpad_up) {
            altMode = true;
        }
        if (gp1.right_bumper && gp1.dpad_down) {
            altMode = false;
        }

        if ((altMode && gp1.left_stick_x >= 0.3) || gp2.left_stick_x >=.3) {
            spinTrim = spinTrim - 10;
        } else if ((altMode && gp1.left_stick_x <= -0.3) || gp2.left_stick_x <= -.3) {
            spinTrim = spinTrim + 10;
        }
        if (gp2.right_bumper) {
            extend = true;
            retract = false;
            middle = false;
        } else if (gp2.right_stick_y >= 0.5) {
            extend = false;
            retract = true;
            middle = false;
        } else if (gp2.right_stick_y <= -0.5) {
            middle = true;
            extend = false;
            retract = false;
        }
        if(gp2.y)
        {
            up = true;
            mid = false;
            down = false;
            liftOverride = false;
        }
        else if(gp2.b && !gp2.start)
        {
            mid = true;
            up = false;
            down = false;
            liftOverride = false;
        }
        else if (gp2.a) {
            mid = false;
            up = false;
            liftOverride = false;

            forward = true; left = false; right = false; backward = false;
            wait2retract.reset();
            retract = true; middle = false; extend = false;
            spinTrim = 0;
            retractOnEnd = true;
        }

        if(gp2.start && gp2.back){
            liftOverride = true;
        }
        if(gp2.right_stick_button && gp2.left_stick_button){
            unfold();
        }

        if (gp2.left_stick_y <= -0.5) {
            verticalTrim = verticalTrim - 15;
        }
        if (gp2.left_stick_y >= 0.5) {
            verticalTrim = verticalTrim + 15;
        }
//        if (gp2.right_trigger >= 0.5) {
//            verticalTrim = verticalTrim + 5;
//        }
//        if (gp2.left_trigger >= 0.5) {
//            verticalTrim = verticalTrim - 5;
//        }
        if(gp2.x)
        {
            unfold();
            spinTrim = 0;
        }

        if(wait2retract.hasExpired() && retractOnEnd && ((motors.get(0).getCurrentPosition() < 300 && motors.get(0).getCurrentPosition() > 0) || (motors.get(0).getCurrentPosition() > -300 && motors.get(0).getCurrentPosition() < 0)))
        {
            retractOnEnd = false;
            down = true;
            retract = true; middle = false; extend = false;
        }
        if(!wait.hasExpired())
        {
            forward = false;
            left = false;
            backward = false;
            up = true;
            scorepos = true;
            mid = false;
            down = false;
        }
        if(sharedHubExtend && motors.get(1).getCurrentPosition() < -800){
            left = true; right = false; forward = false; backward = false;
            sharedHubExtend = false;
        }
        if(sharedHubExtendleft && motors.get(1).getCurrentPosition() < -800){
            left = false; right = true; forward = false; backward = false;
            sharedHubExtendleft = false;
        }
        if(scorepos && wait.hasExpired() && motors.get(1).getCurrentPosition() < -800)
        {
            right = true;
            scorepos = false;
            extend = false;
            retract = false;
            middle = true;
        }
        if (gp2.right_stick_button){
            motors.get(1).setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motors.get(1).setTargetPosition(0);
            motors.get(1).setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }
        telemetry.addData("mid:", mid);
        telemetry.addData("down:", down);
        telemetry.addData("override:", liftOverride);
        telemetry.addData("lift position: ", motors.get(0).getCurrentPosition() );
        telemetry.addData("lift power: ", motors.get(0).getPower() );
    }

    public void write(){
        motors.get(0).setPower(100);
        motors.get(0).setTargetPosition(spinLocation + spinTrim);
        if(forward){
            spinLocation = 0;
            spinTrim = 0;
        } else if(left && !(retract && down)){
            spinLocation = 588-2590;
        } else if(backward && !down){
            spinLocation = -1600-2590;
        } else if(right && !(retract && down)){
            spinLocation = 4557-2590;
        }
        if (extend) {
            servos.get(0).setPosition(0.856 + horizontalTrim); //0.08
            servos.get(1).setPosition(0.2 - horizontalTrim); //0.58
        } else if (retract) {
            servos.get(0).setPosition(0.456); //0.32
            servos.get(1).setPosition(0.60); //0.35
        } else if (middle){
            servos.get(0).setPosition(0.606 + horizontalTrim);
            servos.get(1).setPosition(0.45 - horizontalTrim);
        }
        if(!liftOverride)
        {
            if(down){
                motors.get(0).setPower(100);
            }
            else{
                motors.get(1).setPower(100);
            }

        }
        else motors.get(1).setPower(0);

        if (up) {
            motors.get(1).setTargetPosition(-2700 + verticalTrim);
        } else if (mid) {
            motors.get(1).setTargetPosition(-900 + verticalTrim);
        } else if (down) {
            motors.get(1).setTargetPosition(-50 + verticalTrim);
        }
    }
    public void unfold(){
        wait.reset();
    }
}