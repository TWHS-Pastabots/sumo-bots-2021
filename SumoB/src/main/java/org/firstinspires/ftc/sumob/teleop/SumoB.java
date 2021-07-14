package org.firstinspires.ftc.sumob.teleop;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

@Config
@TeleOp(name = "Sumo B")
public class SumoB extends OpMode {
    // Lift servo position constants
    public static double LIFT_SERVO_HIGH = 1;
    public static double LIFT_SERVO_LOW = 0;
    public static double LIFT_SERVO_UP = 0.5;
    public static double LIFT_SERVO_DOWN = 0.25;

    // Drive train motors
    private DcMotorEx frontLeftMotor;
    private DcMotorEx backLeftMotor;
    private DcMotorEx frontRightMotor;
    private DcMotorEx backRightMotor;

    // Lift servo
    private Servo liftServo;

    // Lift button state memory
    private boolean liftState = false;
    private boolean previousLiftButton = false;

    @Override
    public void init() {
        // Get drive train motors
        frontLeftMotor = hardwareMap.get(DcMotorEx.class, "frontLeft");
        backLeftMotor = hardwareMap.get(DcMotorEx.class, "backLeft");
        frontRightMotor = hardwareMap.get(DcMotorEx.class, "frontRight");
        backRightMotor = hardwareMap.get(DcMotorEx.class, "backRight");

        // Get lift servo
        liftServo = hardwareMap.get(Servo.class, "liftServo");

        // Set zero power behavior for drive train motors
        frontLeftMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        backLeftMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        frontRightMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        backRightMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        // Set correct direction for drive train motors
        frontLeftMotor.setDirection(DcMotorEx.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorEx.Direction.REVERSE);
        frontRightMotor.setDirection(DcMotorEx.Direction.FORWARD);
        backRightMotor.setDirection(DcMotorEx.Direction.FORWARD);

        // Set drive train to zero power
        frontLeftMotor.setPower(0);
        backLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backRightMotor.setPower(0);

        // Set correct direction for lift servo
        liftServo.setDirection(Servo.Direction.FORWARD);

        // Set starting position for lift servo
        liftServo.setPosition(LIFT_SERVO_HIGH);
    }

    @Override
    public void loop() {
        // Get positioning inputs from the gamepad
        double y = -gamepad1.left_stick_y; // Remember, this is reversed!
        double x = gamepad1.left_stick_x; // Counteract imperfect strafing
        double rx = gamepad1.right_stick_x;

        // Calculate the power levels for all the motors
        double frontLeftPower = y + x + rx;
        double backLeftPower = y - x + rx;
        double frontRightPower = y - x - rx;
        double backRightPower = y + x - rx;

        // Check if the magnitude of any of the motors is greater than 1. This
        // makes sure all of the motors power levels are scaled in the
        // range of [-1, 1]
        if (Math.abs(frontLeftPower) > 1 || Math.abs(backLeftPower) > 1 ||
                Math.abs(frontRightPower) > 1 || Math.abs(backRightPower) > 1 ) {
            // Find the largest power
            double max;
            max = Math.max(Math.abs(frontLeftPower), Math.abs(backLeftPower));
            max = Math.max(Math.abs(frontRightPower), max);
            max = Math.max(Math.abs(backRightPower), max);

            // Divide everything by max (it's positive so we don't need to worry
            // about signs)
            frontLeftPower /= max;
            backLeftPower /= max;
            frontRightPower /= max;
            backRightPower /= max;
        }

        // Set the power level of all of the motors
        frontLeftMotor.setPower(frontLeftPower);
        backLeftMotor.setPower(backLeftPower);
        frontRightMotor.setPower(frontRightPower);
        backRightMotor.setPower(backRightPower);

        // Check if the lift button is pressed
        boolean liftButton = gamepad1.a;

        // Check if the button was just pressed this update
        if (liftButton != previousLiftButton) {
            // Check if the lift button is currently down
            if (liftButton) {
                // If it is down, flip the lift servo state
                liftState = !liftState;
            }

            // Remember the previous state of the lift button so we can check if it was pressed the
            // last update
            previousLiftButton = liftButton;
        }

        // Check if the lift servo should be up or down
        if (liftState) {
            // Move the lift servo up
            liftServo.setPosition(LIFT_SERVO_UP);
        } else {
            // Move the lift servo down
            liftServo.setPosition(LIFT_SERVO_DOWN);
        }
    }
}
