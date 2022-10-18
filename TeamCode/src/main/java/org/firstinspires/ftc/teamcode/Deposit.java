package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * The deposit uses a servo to deposit cones picked up from the transfer.
 * @author Jack Gerber
 */
public class Deposit extends Mechanism {
    public Servo servo;
    /**
     * Adds values to the variables
     * @param s imported servo
     */
    public Deposit (Servo s) {
        super();
        servo = s;
        servos.add(servo);
    }
    /**
     * Takes inputs from controllers and translates them into robot movement
     * @param gp1 the gamepad controlling the drivetrain
     * @param gp2 the gamepad controlling the lift
     */
    public void update(Gamepad gp1, Gamepad gp2) {
        if(gp2.a) {
            servos.get(0).setPosition(0.625);
        }
        else if(gp2.b){
            servos.get(0).setPosition(0.5);
        }
    }

    public void write() {

    }
}
