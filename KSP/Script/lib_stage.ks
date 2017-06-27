@LAZYGLOBAL off.

run once lib_misc.
run lib_log("stage.log").
run once lib_sac.
run once lib_math.
run once lib_ctrl.
run once lib_calc.
run once lib_gt.
run once lib_desc.
run once lib_desc_.
run once lib_msmnt.
run once lib_stat.
run once conf(false).

local base to LATLNG(-0.097208003409217, -74.5576396864113).

log_log("main "+main_proc).

if main_proc<>0 {
    wait until ship:altitude > stage_alt.

    set_throttle(0).

    log_log("change vessel").
    //if switch_active KUniverse:FORCESETACTIVEVESSEL(ship). // it not always works
    //KUniverse:activevessel <> ship
}

log_log(KUniverse:activevessel).
log_log(ship).
log_log(KUniverse:activevessel = ship).

//open_terminal().
log_log("Stage").

conf_stage().

wait until ship:unpacked.
wait until ship:velocity:surface:MAG > 0.

log_log(KUniverse:activevessel).
log_log(ship).
log_log(KUniverse:activevessel = ship).

if conf_switch_active {
    until KUniverse:activevessel = ship KUniverse:FORCESETACTIVEVESSEL(ship).
}

set ship:control:neutralize to true.
set ship:control:PILOTMAINTHROTTLE to 0.
set ship:control:MAINTHROTTLE to 0.

// TODO measure distances again
set ship:LOADDISTANCE:FLYING:UNLOAD to 622034.705598598.
wait 0.001.
set ship:LOADDISTANCE:FLYING:PACK to 604593.202303569.

//set sac_roll_debug to true.

sac_start(lookdirup(heading(-90, 0):vector, heading(180, 0):vector)).

wait 1.

sac_follow({return heading(-90, 0):vector.}).
sac_start_following().

if conf_switch_active hud_text("Flip maneuver").

//if true {
if not conf_switch_active {
    set sac_manual_ac to true.
    when true then {
        local thr to get_throttle().
        local ac_yaw to get_ac(thr, 8, 50).
        local ac_roll to get_ac(thr, 200, 5000).
        sac_ac(ac_yaw, ac_yaw, ac_roll).
        preserve.
    }
}

function get_ac {
    parameter thr.
    parameter min.
    parameter max.

    return min + (max - min)*thr.
}

if vectorangle(ship:facing:vector, ship:SRFRETROGRADE:vector) > vectorangle(ship:facing:vector, heading(-90, 0):vector) {
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

    start_throttle_dv().

    local bdv to 1.
    local ba to 180.
    local dvz to 0.
    until bdv < 0 {
        sac_toggle(r(arctan2(dvz, bdv), (180-ba), 0)).
        set_throttle_dv(bdv).

        desc_dist_newton3(base, bdv, torad(ba), {
            parameter ndv.
            parameter nda.
            parameter ndvz.

            set bdv to ndv.
            set ba to todeg(nda).
            set dvz to ndvz.
        }).
        //log_log("a = "+(180-ba)).
        //log_log("dv = "+bdv).
        //log_log("dvz = "+dvz).
        //log_log(" ").

        local tt to bdv/calc_max_twr().
        if tt > 1 set tt to 1.
        if tt < 0 set tt to 0.
        local ac to sac_new_target:vector * ship:facing:vector.
        if ac > 1 set ac to 1.
        if ac > 0.8 {
            if conf_switch_active hud_text("Boostback burn").
        }
        set_throttle((1-arccos(ac)/180)^4).
    }
    set_throttle(0).
    stop_throttle_dv().
}

log_log("Fall").

set ship:control:PILOTMAINTHROTTLE to 0.
set ship:control:MAINTHROTTLE to 0.

//if false {
if ship:altitude > 10000 and conf_speedup {
    warp_phys(1).
    when ship:altitude < 10000 then warp_stop().
}

when ship:altitude < stage_alt then if conf_switch_active hud_text("Aerodynamic guidance").

//msmnt_acc_start().

local dragr to 120.
local liftr to 3. // 2
local ldr to 0.5.

local degt to 5. // 1
set degt to 1.

local dv to 0.
local ddv to 1.
local zdv to 0.
local land_t to 0.

local an to 0.
local anz to 0.

local twr to calc_max_twr()/2.

sac_follow({return ship:SRFRETROGRADE:vector.}).

until twr > calc_max_twr() {
    //set anz to fall_right()*10.

    //if false {
    //    if msmnt_liftr > 0 {
    //        set dragr to msmnt_dragr.
    //        set liftr to msmnt_liftr.
    //    }
    //}

    local q to ship:q.
    local drag to dragr*q*abs(an)*degt/ship:mass.
    desc_dist_atm_newton(base,
            dv, ddv,
            drag,
            {
                parameter ndv.
                parameter nddv.
                parameter nzdv.
                parameter t.

                set dv to ndv.
                set ddv to nddv.
                //log_log("dv = "+(zdv-nzdv)).
                set zdv to nzdv.
                set land_t to t.
            }).

    //set an to lift_an(dv, liftr, degt).
    set an to lift_an_a(dv, liftr, land_t).
    //set anz to -lift_an(zdv, liftr, degt).
    set anz to -lift_an_a(zdv, liftr, land_t).

    sac_toggle(r(anz, an, 0)).
    //sac_toggle(r(5, 0, 0)).

    if true {
        log_log("d = "+base_desc_dist()).
        log_log("an = "+an).
    //log_log("zdv = "+zdv).
    //log_log("anz = "+anz).
        log_log(" ").
    }

    set twr to gt_twr(twr).
}

function lift_an {
    parameter dv.
    parameter liftr.
    parameter degt.

    if ship:q=0 return 0.
    local an to dv*ship:mass/liftr/degt/ship:q.
    if an > 0 {
        set an to sqrt(an).
        if an > 10 set an to 10.
    } else {
        set an to -sqrt(-an).
        if an < -10 set an to -10.
    }
    return an.
}

function lift_an_a {
    parameter dv.
    parameter liftr.
    parameter t.

    if ship:q=0 return 0.
    local a to dv*4/t.

    local an to a*ship:mass/liftr/ship:q.
    set an to an*e^(-ship:altitude/stage_alt).// altitude correction
    if an > 10 set an to 10.
    else if an < -10 set an to -10.
    return an.
}

//msmnt_acc_stop().

//log "poehali" to "stage.csv".
//deletepath("stage.csv").
//log "v, a, c, t, y" to "stage.csv".

if conf_switch_active hud_text("Landing burn").

set anz to 0.
set degt to 1.//0.3.

local zddv to 1.

sac_start_following_toggle().

//msmnt_acc_start().
set liftr to 15. // TODO hack
set dragr to 120.

until base_alt() < 10+10 {
    set twr to gt_twr(twr).
    //set_throttle(((twr)*1.1/calc_max_twr())*10-9).
    local a to (twr-dragr*ship:q/ship:mass).
    local mtwr to calc_max_twr().
    if a > mtwr set a to mtwr.
    set_throttle(a/mtwr).

    gt_dist_newton(twr, 0,
            dv, ddv,
            {
                parameter ndv.
                parameter nddv.
                //parameter az.

                set dv to ndv.
                set ddv to nddv.
                //set anz to -calc_ang_2_vacc(az)*2.
            }).
    set an to lift_twr_an(dv, a, liftr, degt).

    gt_dist_newton(twr, -90,
            zdv, zddv,
            {
                parameter ndv.
                parameter nddv.
            //parameter az.

                set zdv to ndv.
                set zddv to nddv.
            //set anz to -calc_ang_2_vacc(az)*2.
            }).
    set anz to lift_twr_an(zdv, a, liftr, degt).
    sac_toggle(r(anz, an, 0)).
    //sac_toggle(r(anz, 0, 0)).
}
//msmnt_acc_stop().

function lift_twr_an {
    parameter dv.
    parameter twr.
    parameter liftr.
    parameter degt.

    local lifta to liftr*ship:q/ship:mass.
    set twr to torad(twr).
    
    //log_log("la = "+lifta).
    //log_log("twr = "+twr).

    local an to dv*(lifta-twr)/degt/(lifta+twr)^2.
    if an > 0 {
        set an to sqrt(an).
        if an > 10 set an to 10.
    } else {
        set an to -sqrt(-an).
        if an < -10 set an to -10.
    }
    return an.
}

sac_stop_following().

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

if conf_switch_active hud_text("Landed").

log_log("Distance "+(vectorexclude(heading(0, 90):vector, base:position)):mag).
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

    local dh to 0.
    local sa to 0.
    local v to 0.
    msmnt_measure({
        set dh to base_alt()-5-10.
        set sa to calc_srf_ang().
        set v to ship:velocity:surface:mag.
    }).

    //if dh < 1 return gt_g() - ship:verticalspeed - 1.

    local a to 90 + sa.
    if a > 90 return twr.
    return gt_nt_twr(twr, dh, a, v).
}

function gt_dist_self {
    parameter twr.

    local g to gt_g().
//local n to gt_n(calc_twr(), g).
    local n to gt_n(twr, g).
    local z to gt_z(90+calc_srf_ang()).
    if z = 0 return 0.
    local c to gt_c(n, ship:velocity:surface:mag, z).
    return gt_dist_0(g, n, c, z).
}

function gt_dist {
    parameter twr.
    parameter g.
    parameter ang.
    parameter v.

    local n to gt_n(twr, g).
    local z to gt_z(ang).
    if z = 0 return 0.
    local s to 1.
    if z < 0 {
        set s to -1.
        set z to -z.
    }
    local c to gt_c(n, v, z).
    return s*gt_dist_0(g, n, c, z).
}

function gt_dist_newton {
    parameter twr.
    parameter dir.
    parameter dv.
    parameter ddv.
    parameter ret.

    local g to 0.
    local up to 0.
    local north to 0.
    local sv to 0.
    local bp to 0.
    local facing to 0.
    local w to 0.
    local m to 0.
    msmnt_measure({
        set g to gt_g().//*0.7. // magic constant, it can be balance, but doesn't look this way, I may chekc it on the moon
        //set g to ship:body:mu/calc_abs_alt()^2. // didn't help
        set sv to ship:velocity:surface.
        set up to heading(0, 90):vector.
        set north to heading(dir, 0):vector.
        set bp to base:altitudeposition(ship:altitude).
        set facing to ship:facing:vector.
        set w to ship:angularvel.
        set m to ship:mass.
    }).

    set sv to vectorexclude(north, sv).
    //set bp to vectorexclude(north, bp).

    local fwd to vectorcrossproduct(north, up).

    local sa to 90-arccos(fwd*sv:normalized).

    local d to fwd*bp.
    local v to sv:mag.

    // TODO calculate it using ship parts
    local vw1 to 2.7.
    //local vw to 5. // magic balance
    local vw2 to 8.4.

    local m1 to 3.3712570667.
    local m2 to 2.9772133827.
    local vwa to (vw1-vw2)/(m1-m2).
    local vwb to vw1 - vwa*m1.
    local vw to vwa*m + vwb.

    local fa to 90-arccos(fwd*facing:normalized).
    //set d to d - torad(fa)*vw.

    local wx to vectorexclude(sv, north):normalized * w. // TODO remove exclude
    local dvw to wx*vw.

    if dir = 0 {
        local n to twr/g.
        local we to desc_rot_spd().
        local igtd to -2/3*v^3*we/(n - 1)/(2*n - 1)/g^2.
        set d to d - igtd.
    }

    local d1 to gt_dist(twr, g, sa+arctan2(dv+dvw-ddv/2, v), v)-d.
    local d2 to gt_dist(twr, g, sa+arctan2(dv+dvw+ddv/2, v), v)-d.

    set ddv to -d1*ddv/(d2-d1).
    set dv to dv+ddv.

    //if false {
    if dir = 0 {
        local n to twr/g.
        local we to desc_rot_spd().
        local igtd to -2/3*v^3*we/(n - 1)/(2*n - 1)/g^2.

        local gd to gt_dist(twr, g, sa+arctan2(dvw, v), v)-d. // a - 0.1
        stat_log("land",
        {return list("an", "anz", "fa", "sa", "dv", "gd", "d", "w", "m", "v", "da", "q", "twr", "ba", "dgd").},
                list(an, anz, fa, sa, dv, gd, d, wx, m, v, -fa-sa, ship:q, twr, todeg(desc_base_ang(base)), igtd)).
    }

    ret(dv, ddv).
}

function base_desc_dist {
    return desc_dist_self(base, 0, 0) + 6 + 4.
}

function base_dist {
    return heading(-90, 0):VECTOR *(ship:geoposition:altitudeposition(base:TERRAINHEIGHT) - base:position).
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
