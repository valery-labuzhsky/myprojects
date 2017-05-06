switch to 0.

//run perf.

wait 3.

set ship:control:PILOTMAINTHROTTLE to 0.

run once lib_log("turn.log").
run once lib_sac.

set sac_yaw_debug to true.
sac_start(ship:facing * R(0, 160, 0)).

until false {
    wait 10.
    sac_target(ship:facing * R(0, 160, 0)).
}