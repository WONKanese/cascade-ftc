package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous(name = "Auto Code", group = "Autonomous")
public class Auto extends LinearOpMode {
    private DcMotor frontLeftMotor = null;
    private DcMotor frontRightMotor = null;
    private DcMotor backRightMotor = null;
    private DcMotor backLeftMotor = null;
    private DcMotor slideMotor = null;
    private DcMotor armMotor = null;

    private CRServo intakeMotorL = null;
    private CRServo intakeMotorR = null;

    public static final int TARGET_POSITION = 3000;
    public static boolean movingArm = false;
    @Override
    public void runOpMode() {

        frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        slideMotor = hardwareMap.dcMotor.get("slideMotor");
        armMotor = hardwareMap.dcMotor.get("armMotor");

        armMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        // Setup directions
        frontRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        slideMotor.setDirection(DcMotorSimple.Direction.FORWARD);


        intakeMotorL = hardwareMap.get(CRServo.class, "intakeMotorLeft");
        intakeMotorR = hardwareMap.get(CRServo.class, "intakeMotorRight");
        intakeMotorL.setDirection(DcMotorSimple.Direction.FORWARD);
        intakeMotorR.setDirection(DcMotorSimple.Direction.FORWARD);

        armMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addData("Status", "Ready");
        telemetry.update();

        waitForStart();
        Thread constantArm = new Thread(() -> armConstant());

        Auto();

        telemetry.addData("Auto phase", "Done");
        telemetry.update();
    }
    public void armConstant() {
        while (true) {
            if (!movingArm) {
                armMotor.setPower(0);
                if (armMotor.getCurrentPosition() < 2000) {
                    armMotor.setPower(0.105);
                }
                if (armMotor.getCurrentPosition() == 0) {
                    armMotor.setPower(0);
                }
            }
        }
    }
    public void Auto() {
        move(150, 0.25);
        rotate(-33);
        Arm(30, 0.25);
        Slide(30, 0.25);
        Intake(30, -1);
        Slide(-30, 0.25);
        Arm(-29, 0.25);
        rotate(33);
    }
    public void Intake(double movement, double speed) {
        telemetry.addData("Current Status", "Intake");
        telemetry.update();
        // Use gamepad buttons to control the intake
        if (speed == 1) {
            intakeMotorR.setPower(1.0); // Run intake forward
            intakeMotorL.setPower(-1.0); // Run intake forward
        } else if (speed == -1) {
            intakeMotorL.setPower(1.0); // Run intake reverse
            intakeMotorR.setPower(-1.0); // Run intake reverse
        } else {
            intakeMotorR.setPower(0); // Stop intake
            intakeMotorL.setPower(0); // Stop intake
        }

        sleep(movement);
        intakeMotorR.setPower(0); // Stop intake
        intakeMotorL.setPower(0); // Stop intake
    }

    public void Slide(double movement, double speed) {
        telemetry.addData("Current Status", "Slide");
        telemetry.update();
        double slidePower;

        slidePower = speed;

        slideMotor.setPower(slidePower);

        sleep(movement);
        slideMotor.setPower(0);
    }
    public void Arm(double movement, double speed) {
        telemetry.addData("Current Status", "Arm");
        telemetry.addData("Current arm motor mode", armMotor.getMode());
        telemetry.update();
        movingArm = true;
        double movementI = 0;
        while (movementI < movement) {
            movementI++;
            if (movement < 0) {
                armMotor.setPower(speed / 5); // Move down
            } else {
                if (armMotor.getCurrentPosition() < 2000) {
                    armMotor.setPower(speed / 1.5); // Move up
                } else {
                        /*
                        if (isExtendMove) {
                            armMotor.setPower(gamepad2.right_stick_y / 2);
                        }
                        */

                }
            }
            sleep(10);
        }
        armMotor.setPower(0);
        movingArm = false;
    }
    public void rotate(double degrees) {
        telemetry.addData("Current Status", "Rotate");
        telemetry.update();
        double frontLeftPower;
        double backLeftPower;
        double frontRightPower;
        double backRightPower;

        double direction;
        if (degrees > 0) {
            direction = 1;
        }
        else {
            direction = -1;
        }
        frontLeftPower = direction;
        backLeftPower = direction;
        frontRightPower = -direction;
        backRightPower = -direction;

        frontLeftMotor.setPower(frontLeftPower);
        backLeftMotor.setPower(backLeftPower);
        frontRightMotor.setPower(frontRightPower);
        backRightMotor.setPower(backRightPower);

        sleep(degreesI);
        frontLeftMotor.setPower(0);
        backLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backRightMotor.setPower(0);

    }
    public void move(double movement, double speed) {
        telemetry.addData("Current Status", "Moving");
        telemetry.update();
        double frontLeftPower;
        double backLeftPower;
        double frontRightPower;
        double backRightPower;

        frontLeftPower = speed;
        backLeftPower = speed;
        frontRightPower = speed;
        backRightPower = speed;

        frontLeftMotor.setPower(frontLeftPower);
        backLeftMotor.setPower(backLeftPower);
        frontRightMotor.setPower(frontRightPower);
        backRightMotor.setPower(backRightPower);

        sleep(movement);
        frontLeftMotor.setPower(0);
        backLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backRightMotor.setPower(0);
    }
}
