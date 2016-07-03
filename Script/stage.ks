@LAZYGLOBAL off.

run lib_misc.
run lib_log("stage.log").
run lib_sac.
run lib_math.
run lib_ctrl.
run lib_calc.
run lib_gt.
run once lib_desc.

local base to LATLNG(-0.097208003409217, -74.5576396864113).

//when ship:altitude > 20000 then warp_stop().
wait until ship:altitude > 30000.

set_throttle(0).

open_terminal().
log_log("Stage").

wait until ship:velocity:surface:MAG > 0.

KUniverse:FORCESETACTIVEVESSEL(ship).

set sac_yaw_debug to true.

sac_start(heading3(-90, 0, 90)).

wait 3.

local sa to V(0, 0, 0).
local sat to time:seconds.
local sav to V(0, 0, 0).

local va to V(0, 0, 0).
when false then {
    CLEARVECDRAWS().
    set sat to time:seconds - sat.

    set sa to (ship:velocity:surface - sav)/sat.
    draw(sa, rgb(1, 0, 0)).

    local gv to gt_g() * heading(0, -90):vector.
    draw(gv, rgb(1, 1, 0)).

    set sa to sa - gv.
    draw(sa, rgb(0, 1, 0)).

    set sa to VECTOREXCLUDE(sav,sa).
    draw(sa, rgb(0, 0, 1)).

    set sav to ship:velocity:surface:vec.
    set sat to time:seconds.
    preserve.
}

sac_target(heading3(-90, 0, 90)).

local bdv to 1.
until bdv < 0 {
    local tar to lookdirup(heading(base:heading+get_a(), 0):vector, heading(180, 0):vector).
    sac_target(tar).
    set bdv to base_dv().
    local tt to bdv/calc_max_twr().
    if tt > 1 set tt to 1.
    if tt < 0 set tt to 0.
    local ac to tar:vector * ship:facing:vector.
    if ac > 1 set ac to 1.
    set_throttle((1-arccos(ac)/180)^2*sqrt(tt)).
}
set_throttle(0).

//warp_phys(1).
//when ship:altitude < 10000 then warp_stop().

local alt to ship:altitude.
local tt to time:seconds.
local last_v to ship:GROUNDSPEED.

local twr to calc_max_twr().

until twr > calc_max_twr() {
    sac_target(srfretrograde(R(fall_right()*10, fall_up()*20, 0))).
    if ship:verticalspeed < 0 {
        if alt - ship:altitude > 100 {
            set tt to time:seconds - tt.
            local g to ship:body:mu / (ship:body:radius + ship:altitude)^2.
            local vh to ship:GROUNDSPEED.
            local vv to ship:verticalspeed.
            set vv to vv + g*tt.
            local v to sqrt(vh^2 + vv^2).
            local f to (last_v - v)/tt.

        //log_log(ship:altitude + " " + ship:velocity:surface:mag).

            set alt to ship:altitude.
            set tt to time:seconds.
            set last_v to ship:velocity:surface:mag.
        }
    } else {
        set alt to ship:altitude.
        set tt to time:seconds.
    }
//log_log("twr = "+ (twr/calc_max_twr())).
    set twr to gt_twr(twr).
//log_log("a = "+calc_srf_ang()).
}

log "poehali" to "stage.csv".
delete "stage.csv".
log "v, a, c, t, y" to "stage.csv".

until base_alt() < 10 {
    set twr to gt_twr(twr).
    set_throttle(((twr)*1.1/calc_max_twr())*10-9).

    //local d to heading3(-90, 0, 90):VECTOR *(ship:geoposition:altitudeposition(base:TERRAINHEIGHT) - base:position).
    local d to vxcl(ship:velocity:surface, heading(90, 90):vector):normalized * (ship:geoposition:altitudeposition(base:TERRAINHEIGHT) - base:position).
    local g to gt_dist(twr).

    log_log("d = " + (g + d)).

    local san to -steer_south().
    local ca to calc_ang_2_vacc((g+d)*0.1).

    local cc to (ship:velocity:surface:mag - 300)/50.
    if cc > 1 set cc to 1.
    else if cc < -1 set cc to -1.
    set san to cc*san.
    set ca to cc*ca.


    local srf to srfretrograde(r(0,0,0)).
    //log_log("v = " + ship:velocity:surface:mag).
    //log_log("a = " + normX((ship:facing:inverse*srf):yaw, 0)).
    //log_log("c = "+(ca)).
    //log_log("t = "+normX((sac_cur_target:inverse*srf):yaw, 0)).
    //log_log(" ").
    log ship:velocity:surface:mag+", "+ normX((ship:facing:inverse*srf):yaw, 0)+", "+
            ca+", "+normX((sac_cur_target:inverse*srf):yaw, 0) + ", " +(SHIP:CONTROL:YAW*10) to "stage.csv".

    sac_target(srfretrograde(R(san, -ca, 0))).
}

function normX {
    parameter x.
    parameter ox.

    until x - ox < 180 {
        set x to x - 360.
    }
    until x - ox > -180 {
        set x to x + 360.
    }
    return x.
}

local h to base_alt().
until h < 1 {
    set h to base_alt().
    local v to ship:verticalspeed.
    local v0 to -1.
    local a to (v^2 - v0^2) / (2*h).
    set twr to a + gt_g().
    set_throttle(twr/calc_max_twr()).
    sac_target(HEADING3(90, 90, -90)*R(steer_south(), -steer_west(), 0)).
}

until ship:verticalspeed >= 0 {
    set twr to gt_g() - ship:verticalspeed + 5.
    set_throttle(twr/calc_max_twr()).
    sac_target(HEADING3(90, 90, -90)*R(steer_south(), -steer_west(), 0)).
}

set_throttle(0).

log_log("Fuel left "+SHIP:LIQUIDFUEL).

until ship:ANGULARMOMENTUM:mag < 0.1 and arccos(HEADING3(90, 90, -90):vector * ship:facing:vector) < 0.5 {
    sac_target(HEADING3(90, 90, -90)).
}

sac_stop().
set ship:control:PILOTMAINTHROTTLE to 0.

function srfretrograde {
    parameter r.
    return lookdirup(ship:SRFRETROGRADE:vector, heading(180, 0):vector) * r.
}

function draw {
    parameter vec.
    parameter c.

    VECDRAW(V(0,0,0), vec, c, "", 1.0, TRUE).
}

function gt_twr {
    parameter twr.
    local dh to base_alt()-5.

    if dh < 1 return gt_g() - ship:verticalspeed - 1.

    local a to 90 + calc_srf_ang().
    if a > 90 return twr.
    local v to ship:velocity:surface:mag.
    return gt_nt_twr(twr, dh, a, v).
}

function gt_dist {
    parameter twr.

    local g to gt_g().
//local n to gt_n(calc_twr(), g).
    local n to gt_n(twr, g).
    local z to gt_z(90+calc_srf_ang()).
    if z = 0 return 0.
    local c to gt_c(n, ship:velocity:surface:mag, z).
    return gt_dist_0(g, n, c, z).
}

function get_a {
    local w to 0.5 * get_throttle().
    local vv to heading3(base:heading+90, 0, 90):VECTOR * ship:velocity:surface.
    return calc_ang_2_vacc(- 2 * w * vv).
}

function fall_t {
//return desc_fall_t_simple().

    local v to ship:verticalspeed.
    local g to ship:body:mu / (ship:body:radius + ship:altitude)^2.
//local g to ship:body:mu / (ship:body:radius)^2.
    local x to base_alt().

    if x < 0 return 1.

    return (sqrt(v^2+2*g*x) + v)/g.
}

function base_dv {
    local hv to heading3(base:heading, 0, 90):VECTOR * ship:velocity:surface.
//local hv to heading3(base:heading, 0, 90):VECTOR * ground_speed().
    local d to (base:position - ship:geoposition:altitudeposition(base:TERRAINHEIGHT)):mag.
    local t to fall_t().
    local dv to d/t - hv.
    set dv to base_dist()/t.
    return dv.
}

function ground_speed {
    return (ship:velocity:surface - ship:velocity:orbit)*ship:body:radius/calc_abs_alt() + ship:velocity:orbit.
}

function base_dist {
    return desc_dist(base, 0, 0) + 3.
}

function fall_up {
    local dd to base_dist().

    log_log("d = "+dd).
    log_log(" ").

    local a to arctan2(dd, base:distance).
    return a.
}

function fall_right {
    local vv to heading3(base:heading+90, 0, 90):VECTOR * ship:velocity:surface.
    local t to fall_t().
    local dd to vv*t.
    local a to arctan2(dd, base:distance).
    return a.
}

function steer_west {
    local hv to heading3(-90, 0, 90):VECTOR * ship:velocity:surface.
    local d to heading3(-90, 0, 90):VECTOR *(ship:geoposition:altitudeposition(base:TERRAINHEIGHT) - base:position).
    local w to 0.5.
    local a to -2*w*hv-w^2*d.
    return steer(hv, d).
}

function steer_south {
    local vv to heading3(0, 0, 90):VECTOR * ship:velocity:surface.
    local d to heading3(0, 0, 90):VECTOR *(ship:geoposition:altitudeposition(base:TERRAINHEIGHT) - base:position).

    //log_log("y = "+d).

    return steer(vv, d).
}

function steer {
    parameter v.
    parameter d.
    local w to 0.5.
    local x to base_alt().
    if x < 100 set d to 0.
    return calc_ang_2_vacc(-2*w*v-w^2*d).
}

function base_alt {
    return ship:altitude - base:TERRAINHEIGHT - 10.
}
