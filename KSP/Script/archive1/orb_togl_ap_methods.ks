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

print target_vert_speed(th).

until next {
  local v to ship:velocity:orbit:MAG.
  local vv to ship:VERTICALSPEED.
  
  set a to slow_turn(th, a).

  if cos(a) < vv / v set next to true.
  
  sac_target(HEADING3(90, 90 - a, -90)).
}

set th to 100000.

until ship:obt:APOAPSIS > th {
  sac_target(roll_dir(ship:prograde, -90)).
}

set ship:control:MAINTHROTTLE to 0.

set next to false.
until next {
  sac_target(heading3(90, ship:obt:TRUEANOMALY - 180, -90)).
  
  if thrust_time_to_orbit() > time_to_apoapsis_3(th) * 2 set next to true.
  log1("t = " + time_to_apoapsis_2(th)).
  //log1("t = " + target_vert_speed(th)).
  //log1("v = " + ship:VERTICALSPEED).
  //log1(" ").

  //if target_vert_speed(th) > ship:VERTICALSPEED set next to true.
}

local tto to time:seconds.
local ctto to thrust_time_to_orbit().

set ship:control:MAINTHROTTLE to 1.

set next to false.
until next {
  local a to toggle_apoapsis_2(th).
  //set a to 0.
  sac_target(heading3(90, a, -90)).
  if th - ship:altitude < 10 or ship:velocity:orbit:MAG > orb_speed() set next to true.
  //if 180 - ship:obt:TRUEANOMALY < 0.5 set next to true.
}

local tta to time:seconds - tto.

until ship:velocity:orbit:MAG > orb_speed() {
  local a to keep_apoapsis(th).
  sac_target(heading3(90, a, -90)).
}

set tto to time:seconds - tto.

log1("atto = " + tto).
log1("ctto = " + ctto).
log1("atta = " + tta).

sac_target(heading3(90, 0, -90)).

set ship:control:MAINTHROTTLE to 0.

wait until false.

function toggle_apoapsis_3 {
  parameter th.

  local mu to ship:body:mu.
  local ap to ship:obt:APOAPSIS + ship:body:RADIUS.
  local x0 to ship:ALTITUDE + ship:body:RADIUS.
  set th to th + ship:body:RADIUS.
  
  local vv to ship:VERTICALSPEED.
  
  local dth to th - x0.
  return 2 * dth / vv.
}

function time_to_apoapsis_2 {
  parameter th.

  local mu to ship:body:mu.
  local ap to ship:obt:APOAPSIS + ship:body:RADIUS.
  local r to ship:ALTITUDE + ship:body:RADIUS.
  local v to ship:velocity:orbit:MAG.
  
  local ta to 180 - ship:obt:TRUEANOMALY.
  local sa to ship:VERTICALSPEED/v.
  local ca to sqrt(1 - sa ^ 2).
  local san to sa * cos(ta) + ca * sin(ta).
  
  local vv to v * san.
  
  local x0 to (ship:body:RADIUS + ship:ALTITUDE) * cos(ta).
  
  local dth to ship:body:RADIUS + th - x0.
  
  return 2 * dth / vv.
}

function target_vert_speed {
  parameter th.
 
  set th to th + ship:body:RADIUS.
  local pe to ship:obt:PERIAPSIS + ship:body:RADIUS.
  local mu to ship:body:mu.
  local v to sqrt(2 * mu * pe / ((th + pe) * th)).
  local t to thrust_time_to_orbit().
  local a to ship:maxthrust/ship:mass.
  //set th to ship:altitude + ship:body:RADIUS.
  //set v to sqrt(ship:velocity:orbit:MAG ^ 2 - ship:VERTICALSPEED).
  set t to (sqrt(mu / th) - v) * ship:mass/ship:maxthrust.
  return (t / th) * (mu / th - v*v - v*a*t - a*a*t*t/3).
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

function toggle_apoapsis_2 {
  parameter th.

  local mu to ship:body:mu.
  local ap to ship:obt:APOAPSIS + ship:body:RADIUS.
  local x0 to ship:ALTITUDE + ship:body:RADIUS.
  set th to th + ship:body:RADIUS.
  
  local vv to ship:VERTICALSPEED.
  
  local dth to th - x0.
  local dap to ap - x0.
  
  local a to 0.
  //if true {
  if dth>1 and dap>1 and vv>0 {
    return a + calc_angle(vv * vv * (((th - ap)/dap)/dth)/2).
  } else {
    return a.
  }
}

function toggle_apoapsis {
  parameter th.

  local mu to ship:body:mu.
  local ap to ship:obt:APOAPSIS + ship:body:RADIUS.
  local r to ship:ALTITUDE + ship:body:RADIUS.
  local v to ship:velocity:orbit:MAG.
  
  local ta to 180 - ship:obt:TRUEANOMALY.
  local sa to ship:VERTICALSPEED/v.
  local ca to sqrt(1 - sa ^ 2).
  local san to sa * cos(ta) + ca * sin(ta).
  
  local vv to v * san.
  local vh to v * ca.
  
  local x0 to (ship:body:RADIUS + ship:ALTITUDE) * cos(ta).
  
  local dth to ship:body:RADIUS + th - x0.
  local dap to ap - x0.
  
  local a to -ta.
  //if true {
  if dth>10 and dap>10 and vv>0 {
    return a + calc_angle(vv * vv * (1/dap - 1/dth)/2).
  } else {
    return a.
  }
}

function calc_angle {
  parameter ta.

  local ma to ship:maxthrust/ship:mass.
  
  local a to 90.
  if -ta > ma {
    set a to -90.
  } else if ta < ma {
    set a to arcsin(ta / ma).
  }
  return a.
}

function thrust_time_to_orbit {
  local mu to ship:body:mu.
  local pe to ship:obt:PERIAPSIS + ship:body:RADIUS.
  local ap to ship:obt:APOAPSIS + ship:body:RADIUS.
  local dv to sqrt(mu / ap) * (1 - sqrt(2 * pe / (pe + ap))).
  return 72.
  return dv * ship:mass/ship:maxthrust.
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
