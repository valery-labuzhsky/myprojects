lock steering to HEADING(90,90).
wait 2.
lock throttle to 1.
stage.

set MT to ship:maxthrust.
when MT > ship:maxthrust then {
    stage.
    set MT to ship:maxthrust.
    preserve.
}

wait until SHIP:ALTITUDE > 8000.
LOCK STEERING TO HEADING(90,45).

wait until ship:obt:apoapsis > 100000.
lock throttle to 0.
LOCK STEERING TO HEADING(90,0).

list engines in es.
for e in es {
    set eng to e.
    break.
}

print "wait".

wait 1.
print eng:fuelflow.
lock throttle to 0.1.
wait 1.
print eng:fuelflow.
lock throttle to 1.
wait 1.
print eng:fuelflow.
lock throttle to 0.

set ta to (ship:obt:period / 2) - (ship:obt:MEANANOMALYATEPOCH / (2 * constant():PI / ship:obt:period)).
//set ta to (ship:obt:MEANANOMALYATEPOCH / (2 * constant():PI / ship:obt:period)).
print ta.
wait ta.
lock throttle to 1.

wait until false.