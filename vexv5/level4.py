screen_precision = 0
console_precision = 0
myVariable = 0
right1 = False
left1 = False
all1 = False
awaiting_all = False
all2 = False

def limit_switch_b_pressed_callback_0():
    global myVariable, right1, left1, all1, awaiting_all, all2, screen_precision, console_precision
    brain.screen.set_font(FontType.MONO20)
    brain.screen.next_row()
    if not right1:
        brain.screen.print("Running right motor at full")
        RedLED.on()
        right1 = True
        RightMotor.set_velocity(100, PERCENT)
        RightMotor.spin(FORWARD)
    else:
        if not left1:
            RedLED.off()
            GreenLED.on()
            brain.screen.print("Running left motor at full")
            left1 = True
            RightMotor.stop()
            LeftMotor.set_velocity(100, PERCENT)
            LeftMotor.spin(FORWARD)
        else:
            if not awaiting_all:
                GreenLED.off()
                awaiting_all = True
                LeftMotor.stop()
            else:
                if not all1:
                    GreenLED.off()
                    all1 = True
                else:
                    if not all2:
                        GreenLED.on()
                        RedLED.on()
                        brain.screen.print("Running all wheel motors at 1/4 speed")
                        all2 = True
                        RightMotor.set_velocity(25, PERCENT)
                        RightMotor.spin(FORWARD)
                        LeftMotor.set_velocity(25, PERCENT)
                        LeftMotor.spin(FORWARD)
                    else:
                        RedLED.off()
                        GreenLED.off()
                        brain.screen.print("Stopped all motors")
                        RightMotor.stop()
                        LeftMotor.stop()

def when_started1():
    global myVariable, right1, left1, all1, awaiting_all, all2, screen_precision, console_precision
    GreenLED.off()
    RedLED.off()

# system event handlers
limit_switch_b.pressed(limit_switch_b_pressed_callback_0)
# add 15ms delay to make sure events are registered correctly.
wait(15, MSEC)

when_started1()
