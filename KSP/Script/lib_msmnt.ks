@lazyglobal off.

run once lib_calc.

local msmnt_lift_started to false.

local msmnt_log to true.

local msmnt_pos to 0.
local msmnt_ap to 0.
local msmnt_pe to 0.
local msmnt_m to 0.
local msmnt_t to 0.
local msmnt_v to 0.
local msmnt_srf to 0.
local msmnt_up to 0.
local msmnt_fcng to 0.
local msmnt_q to 0.
local msmnt_ta to 0.

local msmnt_e0 to 0.
local msmnt_w0 to 0.
local msmnt_t0 to 0.
local msmnt_ta0 to 0.
local msmnt_v0 to 0.

local msmnt_e to 0.
local msmnt_w to 0.

local msmnt_f to 0.
local msmnt_drag to 0.
local msmnt_lift to 0.
local msmnt_dragr to 0.
local msmnt_liftr to 0.

function msmnt_lift_start {
    set msmnt_lift_started to true.

    msmnt_lift_measure().
    set msmnt_e to -ship:body:mu/(msmnt_ap + msmnt_pe).
    set msmnt_w to sqrt(2*msmnt_ap*msmnt_pe*ship:body:mu/(msmnt_ap + msmnt_pe)).
    msmnt_lift_save().

    when msmnt_lift_started then {
        msmnt_lift_measure().

        set msmnt_e to -ship:body:mu/(msmnt_ap + msmnt_pe).
        local de to (msmnt_e - msmnt_e0)*msmnt_m.
        if de<>0 {
            local dt to msmnt_t - msmnt_t0.
            local dx to msmnt_v:mag*dt.
            local fe to de/dx.
            set msmnt_w to sqrt(2*msmnt_ap*msmnt_pe*ship:body:mu/(msmnt_ap + msmnt_pe)).
            local dw to (msmnt_w - msmnt_w0)*msmnt_m.
            local r to msmnt_pos:mag.
            local fw to dw/dt/r. // torad(vang(pos2, pos)).
            local vsin to msmnt_v:normalized*msmnt_up.
            local cs to sqrt(1 - vsin^2).

            local dvan to -msmnt_anomaly_dv_an(msmnt_ap, msmnt_pe, r).
            local ansin to sin(dvan).
            local ancos to cos(dvan).
            local dan to msmnt_ta - msmnt_ta0.
            if dan < -180 set dan to dan + 360.
            else if dan > 180 set dan to dan - 360.
            local fa to msmnt_anomaly_dv(msmnt_ap, msmnt_pe, r, torad(dan), dt)*msmnt_m/dt.

            local fh to ((fa-fw*ancos)*ansin + (fe-fw*cs)*vsin)/(ansin^2+vsin^2).

            local hor to VECTOREXCLUDE(msmnt_up, msmnt_v):normalized.
            set msmnt_f to fw * hor + fh * msmnt_up.
            set msmnt_drag to msmnt_f * -msmnt_srf:normalized.
            local liftv to LOOKDIRUP(msmnt_srf, msmnt_up):upvector.
            set msmnt_lift to msmnt_f * liftv.
            local an to -arcsin(msmnt_fcng * liftv).
            set msmnt_dragr to msmnt_drag/msmnt_q.
            set msmnt_liftr to msmnt_lift/msmnt_q/an.

            //local north to vectorcrossproduct(msmnt_up, msmnt_v):normalized.
            //set an to -arcsin(msmnt_fcng * north).
            //set msmnt_lift to (msmnt_v-msmnt_v0)*north/dt.
            //set msmnt_liftr to (msmnt_v-msmnt_v0)*north*msmnt_m/msmnt_q/an/dt.

            //CLEARVECDRAWS().
            //draw(50*fw*hor, rgb(1, 0, 0)).
            //draw(50*fe*msmnt_v:normalized, rgb(0, 0, 1)).
            //draw(50*fa*(msmnt_up*ansin + hor*ancos), rgb(1, 1, 0)).
            //draw(50*msmnt_f, rgb(0, 1, 0)).
            //draw(50*fh*msmnt_up, rgb(0, 0, 1)).

            local vel to ship:velocity:surface:mag.
            set vel to ship:velocity:orbit:mag.
            if msmnt_log {
                stat_create("measure", list("g", "q", "f", "v", "m", "h", "dk", "d", "l", "lk", "an")).
                stat_log("measure", list(ship:body:mu/r^2, ship:q,  msmnt_f:mag,  vel, msmnt_m,  ship:ALTITUDE, msmnt_dragr, msmnt_drag, msmnt_lift, msmnt_liftr, an)).
            }

            //log_log("fe = "+fe).
            //log_log("fw = "+fw).

            msmnt_lift_save().
        }

        preserve.
    }
}

function msmnt_acc_stop {
    set msmnt_lift_started to false.
}

function msmnt_lift_measure {
    msmnt_measure({
        set msmnt_pos to ship:body:position.
        set msmnt_ap to calc_abs_ap().
        set msmnt_pe to calc_abs_pe().
        set msmnt_m to ship:mass.
        set msmnt_srf to ship:velocity:surface.
        set msmnt_v to ship:velocity:orbit.
        set msmnt_t to time:seconds.
        set msmnt_up to heading(0, 90):vector.
        set msmnt_fcng to ship:facing:vector.
        set msmnt_q to ship:q.
        set msmnt_ta to ship:orbit:trueanomaly.
    }).
}

function msmnt_lift_save {
    set msmnt_e0 to msmnt_e.
    set msmnt_w0 to msmnt_w.
    set msmnt_t0 to msmnt_t.
    set msmnt_ta0 to msmnt_ta.
    set msmnt_v0 to msmnt_v.
}

function msmnt_measure {
    parameter f.

    local t to 0.
    until t = time:seconds {
        set t to time:seconds.
        f().
    }
}

function msmnt_anomaly_dv {
    parameter ap.
    parameter pe.
    parameter r.
    parameter dan.
    parameter dt.

    local vh to calc_hvel(ap, pe, r).
    local vv to calc_vvel(ap, pe, r).

    //local e to -ship:body:mu/(ap+pe).

    local kvh_ to (r*ap + r*pe + 2*ap*pe)*vv.
    local kvv_ to (r*ap + r*pe - 2*ap*pe)*vh.

    set dan to dan - vh*dt/r.

    //return dan*e^2*(ap - pe)^2/sqrt(mvh_^2 + mvv_^2)/(ap + pe)/pe/ap.
    //return (kvh_*vh_ + kvv_*vv_)*(ap + pe)/mu/(ap - pe)^2.
    return dan*ship:body:mu*(ap - pe)^2/(ap + pe)/sqrt(kvh_^2 + kvv_^2).
}

function msmnt_anomaly_dv_an {
    parameter ap.
    parameter pe.
    parameter r.

    local vh to calc_hvel(ap, pe, r).
    local vv to calc_vvel(ap, pe, r).

    local mvh_ to (r*ap + r*pe + 2*ap*pe)*vv.
    local mvv_ to (r*ap + r*pe - 2*ap*pe)*vh.

    return arctan2(mvv_, mvh_).
}