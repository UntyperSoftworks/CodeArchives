myVariable = 0
all1 = False
avail1 = False

def run():
    global myVariable, all1, avail1
    Claw.set_velocity(10, PERCENT)
    Claw.spin(REVERSE)
    wait(1.5, SECONDS)
    Claw.stop()
    wait(3, SECONDS)
    Claw.spin(FORWARD)
    wait(2, SECONDS)
    Claw.stop()

def limit_switch_b_pressed_callback_0():
    global myVariable, all1, avail1
    if not all1:
        all1 = True
        GreenLED.off()
        RedLED.off()
        RightMotor.set_velocity(100, PERCENT)
        LeftMotor.set_velocity(100, PERCENT)
        RightMotor.spin(FORWARD)
        LeftMotor.spin(FORWARD)
        run()
        avail1 = True
    else:
        if avail1:
            RightMotor.stop()
            LeftMotor.stop()
            Claw.stop()
            RedLED.on()
            GreenLED.off()

def when_started1():
    global myVariable, all1, avail1
    RedLED.off()

# system event handlers
limit_switch_b.pressed(limit_switch_b_pressed_callback_0)
# add 15ms delay to make sure events are registered correctly.
wait(15, MSEC)

when_started1()
