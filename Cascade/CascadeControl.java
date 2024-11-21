package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "Cascade Controller", group = "Robot")
public class CascadeControl extends LinearOpMode {
    private DcMotor frontRightMotor = null;
    private DcMotor frontLeftMotor = null;
    private DcMotor backRightMotor = null;
    private DcMotor backLeftMotor = null;
    private DcMotor armMotor = null;
    private DcMotor slideMotor = null;
    private CRServo intakeMotorL = null;
    private CRServo intakeMotorR = null;
    private static double HOLD_POWER = 0.27;
    private static double BRAKE_POWER = -1;

    private boolean isGameOver = false;
    private boolean isExtendMove = false;
    @Override
    public void runOpMode() {

        frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");

        frontRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        slideMotor = hardwareMap.dcMotor.get("slideMotor");
        slideMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        armMotor = hardwareMap.dcMotor.get("armMotor");
        armMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        intakeMotorL = hardwareMap.get(CRServo.class, "intakeMotorLeft");
        intakeMotorR = hardwareMap.get(CRServo.class, "intakeMotorRight");
        intakeMotorL.setDirection(DcMotorSimple.Direction.FORWARD);
        intakeMotorR.setDirection(DcMotorSimple.Direction.FORWARD);

        armMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addData("Status", "Ready");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // Drive control
            double y = -gamepad1.left_stick_y; // Forward/backward
            double x = gamepad1.left_stick_x; // Left/right
            double rotation = gamepad1.right_stick_x; // Rotation

            double frontLeftPower = y + x + rotation;
            double frontRightPower = y - x - rotation;
            double backLeftPower = y - x + rotation;
            double backRightPower = y + x - rotation;

            double maxPower = Math.max(Math.abs(frontLeftPower), Math.max(Math.abs(frontRightPower),
                    Math.max(Math.abs(backLeftPower), Math.abs(backRightPower))));
            if (maxPower > 1.0) {
                frontLeftPower /= maxPower;
                frontRightPower /= maxPower;
                backLeftPower /= maxPower;
                backRightPower /= maxPower;
            }

            frontLeftMotor.setPower(frontLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backLeftMotor.setPower(backLeftPower);
            backRightMotor.setPower(backRightPower);

            if(gamepad2.dpad_down){
                isGameOver = true;
            }

            if (gamepad2.left_bumper) {
                isExtendMove = true;
            }
            else {
                isExtendMove = false;
            }

            if (isGameOver) {
                //telemetry.addData("GAME IS OVER, APPLYING BRAKE");
                armMotor.setTargetPosition(-271);
                armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
            // Arm control
            if (gamepad2.right_stick_y != 0 && !isGameOver) {
                if (isExtendMove) {
                    armMotor.setPower(gamepad2.right_stick_y);
                }
                else {


                    if (gamepad2.right_stick_y < 0) {
                        armMotor.setPower(gamepad2.right_stick_y / 5); // Move down
                    }
                    else {
                        if (armMotor.getCurrentPosition() < 2000) {
                            armMotor.setPower(gamepad2.right_stick_y / 1.5); // Move up
                        }
                        else {
                            armMotor.setPower(0);

                        }
                    }
                }
            } else if (!isGameOver) {
                armMotor.setPower(0);
                if (armMotor.getCurrentPosition() < 2000) {
                    armMotor.setPower(HOLD_POWER);
                }
                if (armMotor.getCurrentPosition() == 0) {
                    armMotor.setPower(0);
                }
                // Stop
            }



            // Control the slide with gamepad2 triggers
            double slidePower = gamepad2.left_trigger - gamepad2.right_trigger;
            slideMotor.setPower(slidePower);

            // Use gamepad buttons to control the intake
            if (gamepad2.y) {
                intakeMotorR.setPower(1.0); // Run intake forward
                intakeMotorL.setPower(-1.0); // Run intake forward
            } else if (gamepad2.x) {
                intakeMotorL.setPower(1.0); // Run intake reverse
                intakeMotorR.setPower(-1.0); // Run intake reverse
            } else {
                intakeMotorR.setPower(0); // Stop intake
                intakeMotorL.setPower(0); // Stop intake
            }

            // Update telemetry
            telemetry.addData("Front Left Power", frontLeftPower);
            telemetry.addData("Front Right Power", frontRightPower);
            telemetry.addData("Back Left Power", backLeftPower);
            telemetry.addData("Back Right Power", backRightPower);
            telemetry.addData("Arm Power", armMotor.getPower());
            telemetry.addData("Arm Position", armMotor.getCurrentPosition());
            telemetry.addData("Slide Position", slideMotor.getCurrentPosition());
            telemetry.addData("Slide Power", slidePower);
            telemetry.addData("Intake Left Power", intakeMotorL.getPower());
            telemetry.addData("Intake Right Power", intakeMotorR.getPower());

            telemetry.addData("Can Extend?: ", isExtendMove);
            telemetry.addData("arm control", gamepad2.right_stick_y);
            telemetry.update();
        }
    }
}
