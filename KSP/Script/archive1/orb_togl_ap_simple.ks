@LAZYGLOBAL off.

wait 3.
switch to 0.

set ship:control:PILOTMAINTHROTTLE to 0.

set ship:control:MAINTHROTTLE to 1.
stage.

run lib_sac.

local t to HEADING3(90, 90, -90).

sac_start(t).

local MT to ship:maxthrust.
when MT > ship:maxthrust then {
    stage.
    set MT to ship:maxthrust.
    preserve.
}

local th to 100000. //40000

local a to 0.
local next to false.

until next {
  local v to ship:velocity:orbit:MAG.
  local vv to ship:VERTICALSPEED.
  
  set a to slow_turn(th, a).

  if cos(a) < vv / v set next to true.
  
  sac_target(HEADING3(90, 90 - a, -90)).
}

set th to 100000.

local thr to th + ship:body:RADIUS.
local ap0 to thr.
local ap1 to th * 0.9 + ship:body:RADIUS.
local r0 to ship:ALTITUDE + ship:body:RADIUS.
local r1 to ship:ALTITUDE * 1.1 + ship:body:RADIUS.

until ship:obt:APOAPSIS > th {
//until ship:obt:APOAPSIS + ship:body:RADIUS > ap1 {
  local pe to ship:obt:PERIAPSIS + ship:body:RADIUS.
  local r2 to newton(r1, r0, newton_start_toggle_apoapsis(ap1, pe, r1, thr), newton_start_toggle_apoapsis(ap1, pe, r0, thr)).
  if r2 > min(ap1, ap0) set r2 to (r1 + min(ap1, ap0)) / 2.
  set r0 to r1.
  set r1 to r2.
  //log1("r = "+(r1 - ship:body:RADIUS)).
  
  local ap2 to newton(ap1, ap0, newton_apoapsis(ap1, pe, r1, thr), newton_apoapsis(ap0, pe, r1, thr)).
  if ap2 < max(r1, r0) set ap2 to (ap1 + max(r1, r0)) / 2.
  set ap0 to ap1.
  set ap1 to ap2.
  //log1("ap = "+(ap1 - ship:body:RADIUS)).
  //log1(" ").
  
  sac_target(roll_dir(ship:prograde, -90)).
  //toggle_apoapsis_twr(th).
  //keep_throttle_to_apoapsis(th).
}

set ship:control:MAINTHROTTLE to 0.

set next to false.
until next {
  sac_target(roll_dir(ship:prograde, -90)).
  
  if min_thrust_time_to_orbit(th) > toggle_apoapsis_time(th) set next to true.
  //log1("ta = " + toggle_apoapsis_time(th)).
  //log1("tt = " + min_thrust_time_to_orbit(th)).
}

local tto to time:seconds.
local ctto to min_thrust_time_to_orbit(th).

set ship:control:MAINTHROTTLE to 1.

set next to false.
until next {
  local a to toggle_apoapsis(th).
  sac_target(heading3(90, a, -90)).
  keep_throttle_to_apoapsis(th).
  if th - ship:altitude < 1 or ship:velocity:orbit:MAG > orb_speed() set next to true.
  //if 180 - ship:obt:TRUEANOMALY < 0.5 set next to true.
}

local tta to time:seconds - tto.

until ship:velocity:orbit:MAG > orb_speed() {
  local a to keep_apoapsis(th).
  keep_throttle_to_apoapsis(th).
  sac_target(heading3(90, a, -90)).
}

set tto to time:seconds - tto.

log1("atto = " + tto).
log1("ctto = " + ctto).
log1("atta = " + tta).

sac_target(heading3(90, 0, -90)).

set ship:control:MAINTHROTTLE to 0.

wait until false.

function toggle_apoapsis_twr {
  parameter th.

  local pe to ship:obt:PERIAPSIS + ship:body:RADIUS.
  local ap to ship:obt:APOAPSIS + ship:body:RADIUS.
  local r to ship:ALTITUDE + ship:body:RADIUS.
  set th to th + ship:body:RADIUS.
  
  local vv to ship:VERTICALSPEED.
  local v to ship:velocity:orbit:MAG.

  //local a to (vv * v * (th - ap) / (ap - r)) / (th - r).
  //set ship:control:MAINTHROTTLE to a / max_thrust_acceleration().
  
  local av to (vv * vv * (th - ap) / (ap - r)) / (th - r).
  local a to vv * calc_orbit_dv(ap, pe, th) / (2 * (th - r)).
  
  if av/a > vv/v set ship:control:MAINTHROTTLE to 1.
  else set ship:control:MAINTHROTTLE to 0.
}

function newton_apoapsis {
  parameter ap.
  parameter pe.
  parameter r.
  parameter th.
  
  return calc_vertical_speed(ap, pe, r)/calc_speed(ap, pe, r) - calc_toggle_apoapsis_acceleration(ap, pe, r, th)/max_thrust_acceleration().
}

function newton_start_toggle_apoapsis {
  parameter ap.
  parameter pe.
  parameter r.
  parameter th.
  
  return 2 * (th - r) - calc_orbit_dv(ap, pe, th) * calc_vertical_speed(ap, pe, r)/max_thrust_acceleration().
}

function calc_vertical_speed {
  parameter ap.
  parameter pe.
  parameter r.
  
  local mu to ship:body:mu.
  return sqrt(2 * mu * (ap - r) * (r - pe) / (r * r * (ap + pe))).
}

function calc_orbit_dv {
  parameter ap.
  parameter pe.
  parameter th.
  
  local mu to ship:body:mu.

  return sqrt(mu / ap) * (sqrt(2 - ap/th) - sqrt(2*pe/(ap + pe))).
}

function calc_speed {
  parameter ap.
  parameter pe.
  parameter r.

  local mu to ship:body:mu.
  return sqrt(mu * (1/ r - 1/(ap + pe))).
}

function newton {
  parameter x1.
  parameter x0.
  parameter f1.
  parameter f0.
  
  local dx to x1 - x0.
  local df to f1 - f0.
  local dxf to 1.
  if abs(dx) >= abs(df) {
    if dx * df < 0 set dxf to -1.
  } else {
    set dxf to dx / df.
  }
  return x1 - f1 * dxf.
}

function keep_throttle_to_apoapsis {
  parameter th.

  local tat to toggle_apoapsis_time(th).
  if tat > 1 {
    set ship:control:MAINTHROTTLE to min_thrust_time_to_orbit(th) / tat.
  } else {
    set ship:control:MAINTHROTTLE to min_thrust_time_to_orbit(th).
  }
}

function toggle_apoapsis_time {
  parameter th.

  local mu to ship:body:mu.
  local x0 to ship:ALTITUDE + ship:body:RADIUS.
  set th to th + ship:body:RADIUS.
  
  local vv to ship:VERTICALSPEED.
  
  local dth to th - x0.
  if vv < 1 return 0.
  return 2 * dth / vv.
}

function orb_speed {
  return sqrt(ship:body:mu/(ship:body:radius + ship:altitude)).
}

function orb_angle {
  return arcsin(ship:VERTICALSPEED / ship:velocity:orbit:MAG).
}

function keep_apoapsis {
  parameter th.
  
  local w to 0.5.
  local r to ship:ALTITUDE + ship:body:RADIUS.
  local vv to ship:VERTICALSPEED.
  local v2 to ship:velocity:orbit:SQRMAGNITUDE.
  local vh2 to v2 - vv ^ 2.
  local a0 to ship:body:mu / (r ^ 2) - vh2 / r.
  
  return calc_angle(a0 - 2 * w * vv - w * w * (ship:ALTITUDE - th)).
}

function calc_toggle_apoapsis_acceleration {
  parameter ap.
  parameter pe.
  parameter r.
  parameter th.

  local mu to ship:body:mu.
  
  local vv to calc_vertical_speed(ap, pe, r).
  
  local dth to th - r.
  local dap to ap - r.
  
  return vv * vv * (((th - ap)/dap)/dth)/2.
}

function toggle_apoapsis {
  parameter th.

  local mu to ship:body:mu.
  local ap to ship:obt:APOAPSIS + ship:body:RADIUS.
  local x0 to ship:ALTITUDE + ship:body:RADIUS.
  set th to th + ship:body:RADIUS.
  
  local vv to ship:VERTICALSPEED.
  
  local dth to th - x0.
  local dap to ap - x0.
  
  local a to 0.
  //local a to ship:obt:MEANANOMALYATEPOCH - 180.
  if dth>1 and dap>1 and vv>0 {
    return a + calc_angle(vv * vv * (((th - ap)/dap)/dth)/2).
  } else {
    return a.
  }
}

function max_thrust_acceleration {
  return ship:maxthrust/ship:mass.
}

function thrust_acceleration {
  return ship:control:MAINTHROTTLE * max_thrust_acceleration().
}

function calc_angle {
  parameter ta.

  local ma to thrust_acceleration().
  
  local a to 90.
  if -ta > ma {
    set a to -90.
  } else if ta < ma {
    set a to arcsin(ta / ma).
  }
  return a.
}

function thrust_time_to_orbit {
  parameter th.

  return min_thrust_time_to_orbit(th) / ship:control:MAINTHROTTLE.
}

function min_thrust_time_to_orbit {
  parameter th.

  local mu to ship:body:mu.
  local pe to ship:obt:PERIAPSIS + ship:body:RADIUS.
  local ap to ship:obt:APOAPSIS + ship:body:RADIUS.
  //local dv to sqrt(mu / ap) * (1 - sqrt(2 * pe / (pe + ap))).
  local dv to calc_orbit_dv(ap, pe, th + ship:body:RADIUS).
  return dv / max_thrust_acceleration().
}

function time_to_apoapsis {
  return ship:obt:period * (1 - ship:obt:MEANANOMALYATEPOCH/180) / 2.
}

function slow_turn {
  parameter th.
  parameter a.

  local v to ship:velocity:orbit:MAG.
  local r to ship:ALTITUDE + ship:body:RADIUS.
  local mu to ship:body:mu.
  local dh to th - ship:ALTITUDE.
  local rv to r * v.
  local vv to ship:VERTICALSPEED.
  local rad to constant():pi / 180.
  local f to dh * (mu - rv * v) * sin(a) - rad * (90 - a) * r * rv * vv.
  local df to dh * (mu - rv * v) * cos(a) + rad * r * rv * vv.
  
  set a to a - f / df.
  
  return a.
}

function heading3{
  parameter dir.
  parameter pitch.
  parameter roll.
  local h to heading(dir, pitch).
  return roll_dir(h, roll).
}

function roll_dir{
  parameter dir.
  parameter roll.
  local r to R(0, 0, roll).
  return dir * r.
}
