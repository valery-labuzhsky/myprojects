@LAZYGLOBAL off.

run once compile(list("lib_sac", "stage", "turn", "perf")).

run lib_misc.
run lib_ctrl.
run lib_log("main.log").
//run lib_sac.
run lib_math.
run lib_calc.
run lib_asc.
run lib_funct.
run lib_gt.

open_terminal().

if ship:altitude < 90000 {
    asc_start(100000).
}
