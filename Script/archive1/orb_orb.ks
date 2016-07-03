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
    log1("Stage").
    stage.
    set MT to ship:maxthrust.
    preserve.
}

local th to 100000.

local a to 0.
local next to false.

local tpelist to init_target_periapsis().

log1("Gravity turn: active").

local ath to 80000.

until next {
  if ship:altitude > 40000 set next to true.
  
  set a to grav_turn(ath, a).
  sac_target(HEADING3(90, 90-a, -90)).
  follow_target_periapsis(th, tpelist).
}

log1("Gravity turn: passive").

until ship:altitude > 30000 {
  sac_target(HEADING3(90, surf_angle(), -90)).
}

log1("Increase apoapsis").

set th to 100000.

set next to false.
until ship:obt:APOAPSIS > th {
  set a to follow_target_periapsis(th, tpelist).
  sac_target(heading3(90, a, -90)).
}

set ship:control:MAINTHROTTLE to 0.
set ship:control:PILOTMAINTHROTTLE to 0.

log1("Wait next burn").

set next to false.
until next {
  if min_thrust_time_to_orbit(th) + 2 > toggle_apoapsis_time(th) {
    sac_target(heading3(90, 0, -90)).
    set ship:control:MAINTHROTTLE to 0.
    if min_thrust_time_to_orbit(th) > toggle_apoapsis_time(th) set next to true.
  } else {
    keep_target_apoapsis(th).
    sac_target(roll_dir(ship:prograde, -90)).
  }
}

set ship:control:MAINTHROTTLE to 1.

log1("Burn to orbit").

set next to false.
until next {
  local a to toggle_apoapsis_2(th).
  sac_target(heading3(90, a, -90)).
  keep_throttle_to_apoapsis(th).
  if th - ship:altitude < 1 or ship:velocity:orbit:MAG > orb_speed() set next to true.
}

log1("Final burn").

until ship:velocity:orbit:MAG > orb_speed() {
  local a to keep_apoapsis(th).
  keep_throttle_to_apoapsis(th).
  sac_target(heading3(90, a, -90)).
}

log1("Orbit").

sac_target(heading3(90, 0, -90)).

set ship:control:MAINTHROTTLE to 0.
set ship:control:PILOTMAINTHROTTLE to 0.

wait until false.

function keep_target_apoapsis {
  parameter th.
  
  local ap to apoapsis().
  local pe to periapsis().
  local r to radius().
  set th to th + ship:body:radius.
  
  local c2r to ap*pe/(ap+pe-r).
  local pe2 to c2r*(th-r)/(th-c2r).
  
  local dv to calc_speed(th, pe2, r) - ship:velocity:orbit:mag.
  set ship:control:MAINTHROTTLE to dv/max_thrust_acceleration().
}

function follow_target_periapsis {
  parameter th.
  parameter nlist.
  
  local tap to th + ship:body:RADIUS.
  local r to ship:ALTITUDE + ship:body:RADIUS.
  local pe to periapsis().
  local ap to apoapsis().

  local tpe to newton_target_periapsis(tap, nlist).
  
  local vv to calc_vertical_speed(tap, tpe, r).
  local vh to calc_horizontal_speed(tap, tpe, r).
  local vv0 to calc_vertical_speed(ap, pe, r).
  local vh0 to calc_horizontal_speed(ap, pe, r).
  
  return arctan2(vv-vv0, vh-vh0).
}

function init_target_periapsis {
  return list(ship:obt:PERIAPSIS + ship:body:RADIUS, ship:obt:PERIAPSIS * 0.9 + ship:body:RADIUS).
}

function newton_target_periapsis{
  parameter tap.
  parameter nlist.
  
  local npe to newton(nlist[1], nlist[0], calc_dv_dpe(tap, nlist[1]), calc_dv_dpe(tap, nlist[0])).
  set nlist[0] to nlist[1].
  set nlist[1] to npe.
  return npe.
}

function calc_dv_dpe {
  parameter tap.
  parameter tpe.
  
  local mu to ship:body:mu.
  local r to ship:ALTITUDE + ship:body:RADIUS.
  local ap to apoapsis().
  local pe to periapsis().
  
  local vv0 to calc_vertical_speed(ap, pe, r).
  local vh0 to calc_horizontal_speed(ap, pe, r).
  local vv1 to calc_vertical_speed(tap, tpe, r).
  local vh1 to calc_horizontal_speed(tap, tpe, r).
  
  //local dvh1dr to tap * vh1/(2 * tpe * (tap + tpe)).
  //local dvv1dr to -(tap + r)*vv1/(2 * (r - tpe) * (tap + tpe)).
  //local dv1 to sqrt((vv1 - vv0)^2 + (vh1 - vh0)^2).
  //local dv1dr to ((vv1 - vv0)*dvv1dr + (vh1 - vh0)*dvh1dr) / dv1.
  //local dv2dr to -sqrt(mu * tap / (2 * tpe * (tap + tpe)^3)).

  local dvh1dr to tap * vh1/(2 * tpe).
  local dvv1dr to -(tap + r)*vv1/(2 * (r - tpe)).
  local dv1 to sqrt((vv1 - vv0)^2 + (vh1 - vh0)^2).
  local dv1dr to ((vv1 - vv0)*dvv1dr + (vh1 - vh0)*dvh1dr) / dv1.
  local dv2dr to -sqrt(mu * tap / (2 * tpe * (tap + tpe))).
  
  return dv1dr + dv2dr.
}

function calc_horizontal_speed {
  parameter ap.
  parameter pe.
  parameter r.
    
  local mu to ship:body:mu.
  local v2 to 2 * mu * ap * pe / (r * r * (ap + pe)).
  if v2 < 0 set v2 to 0.
  return sqrt(v2).
}

function calc_vertical_speed {
  parameter ap.
  parameter pe.
  parameter r.
  
  local mu to ship:body:mu.
  local v2 to 2 * mu * (ap - r) * (r - pe) / (r * r * (ap + pe)).
  if v2 < 0 set v2 to 0.
  return sqrt(v2).
}

function calc_speed {
  parameter ap.
  parameter pe.
  parameter r.
  
  local mu to ship:body:mu.
  local v2 to 2 * mu * (ap + pe - r) / (r * (ap + pe)).
  if v2 < 0 set v2 to 0.
  return sqrt(v2).
}

function calc_orbit_dv {
  parameter ap.
  parameter pe.
  parameter th.
  
  local mu to ship:body:mu.

  return sqrt(mu / ap) * (sqrt(2 - ap/th) - sqrt(2*pe/(ap + pe))).
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
  
  log1("F = "+f1).
  
  return x1 - f1 * dxf.
}

function keep_throttle_to_apoapsis {
  parameter th.

  local tat to toggle_apoapsis_time(th).
  if tat > 1 {
    set ship:control:MAINTHROTTLE to (min_thrust_time_to_orbit(th) / tat) * 10 - 9.
  } else {
    set ship:control:MAINTHROTTLE to min_thrust_time_to_orbit(th) / 2.
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

function surf_angle {
  local sina to ship:VERTICALSPEED / ship:velocity:surface:MAG.
  if sina > 1 set sina to 1.
  if sina < -1 set sina to -1.
  return arcsin(sina).
}

function keep_apoapsis {
  parameter th.
  
  local w to 0.5 * ship:control:MAINTHROTTLE.
  local r to ship:ALTITUDE + ship:body:RADIUS.
  local vv to ship:VERTICALSPEED.
  local v2 to ship:velocity:orbit:SQRMAGNITUDE.
  local vh2 to v2 - vv ^ 2.
  local a0 to ship:body:mu / (r ^ 2) - vh2 / r.
  
  local ap to apoapsis().
  local pe to periapsis().
  set vv to calc_vertical_speed(ap, pe, r).
  if ship:obt:MEANANOMALYATEPOCH > 180 set vv to -vv.
  set a0 to -calc_vertical_acc(ap, pe, r).
  
  return calc_angle(a0 - 2 * w * vv - w * w * (ship:ALTITUDE - th)).
}

function calc_vertical_acc {
  parameter ap.
  parameter pe.
  parameter r.
  
  return ship:body:mu * (pe * (ap - r) - ap * (r - pe)) / (r^3 * (ap + pe)).
}

function apoapsis {
  return ship:obt:APOAPSIS + ship:body:RADIUS.
}

function periapsis {
  return ship:obt:PERIAPSIS + ship:body:RADIUS.
}

function radius {
  return ship:ALTITUDE + ship:body:RADIUS.
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

function toggle_apoapsis_2 {
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
  if dth>1 and dap>1 and vv>0 {
    return a + calc_angle(vv * vv * (((ship:body:RADIUS + th - ap)/dap)/dth)/2).
  } else {
    return a.
  }
}

function toggle_apoapsis_dv {
  parameter th.
  
  set th to th + ship:body:radius.
  local ap to apoapsis().
  local pe to periapsis().
  local r to radius().
  
  local vv to calc_vertical_speed(th, pe, r) - calc_vertical_speed(ap, pe, r).
  local vh to calc_horizontal_speed(th, th, r) - calc_horizontal_speed(ap, pe, r).
  return v(vv, vh, 0).
}

function toggle_apoapsis_3 {
  parameter th.
  
  local dv to toggle_apoapsis_dv(th).
  return arctan2(dv:x, dv:y).
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
  
  //set f to dh * (mu - rv * v) * sin(a) - rad * (90 - a) * r * rv * v * cos(a).
  //set df to dh * (mu - rv * v) * cos(a) + rad * r * rv * v * cos(a) + rad * (90 - a) * r * rv * v * sin(a).
  
  set a to a - f / df.
  
  return a.
}

function slow_turn_2 {
  parameter th.
  parameter a.
  
  local v to ship:velocity:orbit:MAG.
  local r to ship:ALTITUDE + ship:body:RADIUS.
  local mu to ship:body:mu.
  local dh to th - ship:ALTITUDE.
  local rv to r * v.
  local am to max_thrust_acceleration().
  local a0 to calc_vertical_acc(apoapsis(), periapsis(), r).
  local rad to constant():pi / 180.
  
  local vv to v * cos(a).
  local dvv to -v*sin(a).
  
  local vh to v * sin(a).
  local dvh to v *cos(a).

  local g to am*cos(a) + a0.
  local dg to -am*sin(a).
  
  local m to mu*sin(a) - rv*vh.
  local dm to mu*cos(a) - rv*dvh.
  
  local tm to rad*(90-a)*r*rv.
  local tm2 to tm^2.
  local dtm to -rad*r*rv.
  
  local f to g*tm2/2 + vv*tm*m - dh*m^2.
  local df to dg*tm2/2 + g*tm*dtm + vv*(dtm*m + tm*dm) +dvv*tm*m - dh*2*m*dm.

  local fdf to f / df.
  if a - fdf < 0 set a to a + fdf.
  else
  set a to a - (f / df).
  
  log1("f = "+f).
  log1("df = "+df).
  log1("fdf = "+(f/df)).

  if (a<0) set a to -a.
  
  return a.
}

function grav_turn {
  parameter th.
  parameter a.
  
  local z to tan(a/2).
  
  local v2 to ship:velocity:surface:SQRMAGNITUDE.
  local g to -calc_vertical_acc(apoapsis(), periapsis(), ship:ALTITUDE + ship:body:radius).
  local n to 1.3*max_thrust_acceleration()/g.
  local b to v2/(g*(n^2-1)).
  local xt to th - ship:ALTITUDE.
  
  if v2 < 1 return 0.
  if z <= 0 set z to sqrt(b/xt).
  
  local y to (z^(n-1))*(1 + z^2).
  local f to b-xt*y^2.
  local df to -xt*2*y*(z^(n-2))*(n-1 + (n+1)*z^2).
  local fdf to f/df.
  
  if fdf < z {
    set z to z - fdf.
  } else {
    set z to z/2.
  }
  
  if z < 0 return 0.
  
  return 2*arctan(z).
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
