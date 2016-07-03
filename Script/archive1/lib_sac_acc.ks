@LAZYGLOBAL off.

set config:ipu to 2000.

local sac_started to false.
local sac_stopped to true.

local sac_old_target to R(0, 0, 0).
local sac_cur_target to sac_old_target.
local sac_new_target to sac_cur_target.
local sac_pitch_arr to sac_init(50).
local sac_yaw_arr to sac_init(50).
local sac_roll_arr to sac_init(50).
local sac_t0 to time:seconds.
local sac_x0 to sac_cur_target.
local sac_v0 to ship:angularvel:vec.
local sac_tar_sp to V(0, 0, 0).
local sac_ltt to time:seconds.
local sac_ticks to 0.
local sac_ct to 0.25.
local sac_w to 2.
local sac_warpmode to WARPMODE.
local sac_warplevel to WARP.
local sac_warp to false.

local sac_ma0 to V(0, 0, 0).
local sac_mv0 to V(0, 0, 0).
local sac_mx0 to V(0, 0, 0).

function sac_reset {
  set sac_old_target to sac_new_target.
  set sac_cur_target to sac_new_target.
  set sac_t0 to time:seconds.
  set sac_x0 to ship:facing:inverse * sac_cur_target.
  set sac_v0 to ship:angularvel:vec.
  set sac_tar_sp to V(0, 0, 0).
  set sac_ltt to time:seconds.
  set sac_ticks to 0.
}

function sac_start {
  parameter t.

  set sac_new_target to t.
  set sac_started to true.
  set sac_stopped to false.
  
  sac_reset().
  
  when true then {
    local f to ship:facing:inverse * sac_cur_target.
    
    local v to ship:facing:inverse * ship:angularvel.
    
    set sac_ltt to time:seconds.
    set sac_ticks to sac_ticks + 1.
    
    local tt to time:seconds - sac_t0.
    if tt > sac_ct {
      local tune to true.
      if WARP > 0 {
	if WARPMODE = "PHYSICS" {
	  if WARP > sac_warplevel {
	    set sac_ct to sac_ct * (WARP - sac_warplevel + 1).
	    sac_reset().
	    set tune to false.
	  }
	} else {
	  sac_reset().
	  set tune to false.
	  set sac_warp to true.
	}
      } else if sac_ticks < 10 {
	set sac_ct to sac_ct * 12.5 / sac_ticks.
	sac_reset().
	set tune to false.
      } 
      set sac_warpmode to WARPMODE.
      set sac_warplevel to WARP.
      if sac_ticks > 15 {
	set sac_ct to min(2, sac_ct * 12.5 / sac_ticks).
      }	
      if sac_ct > 1 {
	sac_reset().
	set tune to false.
	set sac_warp to true.
      }
      
      if tune {
	set sac_warp to false.
	//sac_tune(sac_pitch_arr, tt, -sac_x0:pitch, sac_v0:x, -f:pitch, v:x, sac_tar_sp:x, -SHIP:CONTROL:PITCH, false).
	//sac_tune(sac_yaw_arr, tt, -sac_x0:yaw, sac_v0:y, -f:yaw, v:y, sac_tar_sp:y, SHIP:CONTROL:YAW, false).
	//sac_tune(sac_roll_arr, tt, -sac_x0:roll, sac_v0:z, -f:roll, v:z, sac_tar_sp:z, -SHIP:CONTROL:ROLL, false).
	
	sac_tune(sac_pitch_arr, tt, 0, sac_v0:x, -f:pitch, v:x, sac_mv0:x, -SHIP:CONTROL:PITCH, false).
	sac_tune(sac_yaw_arr, tt, 0, sac_v0:y, -f:yaw, v:y, sac_mv0:y, SHIP:CONTROL:YAW, false).
	sac_tune(sac_roll_arr, tt, 0, sac_v0:z, -f:roll, v:z, sac_mv0:z, -SHIP:CONTROL:ROLL, false).

	set sac_t0 to time:seconds.
	set sac_x0 to f.
	set sac_v0 to v:vec.
	set sac_old_target to sac_cur_target.
	
	set sac_cur_target to sac_new_target.
	set sac_tar_sp to sac_target_speed(f, ship:facing:inverse * sac_cur_target, sac_ct).

	set sac_mx0 to -1*sac_vect(ship:facing:inverse * sac_cur_target).
	set sac_ma0 to sac_calc_ma0(sac_mx0, sac_tar_sp, sac_ct*3).
	set sac_mv0 to sac_calc_mv0(sac_tar_sp, sac_ma0, sac_ct*3).
	
	sac_log(sac_mx0).
      }
      //sac_log("sac_ct = " + sac_ct).
      //sac_log("sac_ticks = " + sac_ticks).
      set sac_ticks to 0.
    } else {
      local of to ship:facing:inverse * sac_old_target.
      local mf to sac_middle_target(of, f, tt/sac_ct).

      local mx to sac_calc_mx(sac_mx0, sac_mv0, sac_ma0, tt).
      local mv to sac_calc_mv(sac_mv0, sac_ma0, tt).
      set SHIP:CONTROL:PITCH to -sac_seek(sac_pitch_arr, -f:pitch-mx:x, v:x, mv:x, sac_ma0:x, -SHIP:CONTROL:PITCH, false).
      set SHIP:CONTROL:YAW to sac_seek(sac_yaw_arr, -f:yaw-mx:y, v:y, mv:y, sac_ma0:y, SHIP:CONTROL:YAW, true).
      set SHIP:CONTROL:ROLL to -sac_seek(sac_roll_arr, -f:roll-mx:z, v:z, mv:z, sac_ma0:z, -SHIP:CONTROL:ROLL, false).

      //set SHIP:CONTROL:PITCH to -sac_seek(sac_pitch_arr, -mf:pitch, v:x, sac_tar_sp:x, -SHIP:CONTROL:PITCH, false).
      //set SHIP:CONTROL:YAW to sac_seek(sac_yaw_arr, -mf:yaw, v:y, sac_tar_sp:y, SHIP:CONTROL:YAW, false).
      //set SHIP:CONTROL:ROLL to -sac_seek(sac_roll_arr, -mf:roll, v:z, sac_tar_sp:z, -SHIP:CONTROL:ROLL, false).
    }
    
    if sac_started preserve.
    else set sac_stopped to true.
  }
}

function sac_calc_mv {
  parameter v0.
  parameter a0.
  parameter t.
  
  return v0 + a0*t.
}

function sac_calc_mx {
  parameter x0.
  parameter v0.
  parameter a0.
  parameter t.
  
  return x0 + (v0 + a0*t/2)*t.
}

function sac_calc_mv0 {
  parameter v1.
  parameter a.
  parameter t.
  
  return v1 - a*t.
}

function sac_calc_ma0 {
  parameter x0.
  parameter v1.
  parameter t.
  
  return 2*(x0/t + v1)/t.
}

function sac_vect {
  parameter rot.
  return V(normX(rot:pitch, 0), normX(rot:yaw, 0), normX(rot:roll, 0)).
}

function sac_warping {
  return sac_warp.
}

function sac_target_speed {
  parameter of.
  parameter f.
  parameter t.

  return V(normX(f:pitch - of:pitch, 0)/t, normX(f:yaw - of:yaw, 0)/t, normX(f:roll - of:roll, 0)/t).
}

function sac_middle_target {
  parameter of.
  parameter f.
  parameter c.
  
  local bc to 1 - c.
  return R(normX(f:pitch, of:pitch) * c + of:pitch * bc, normX(f:yaw, of:yaw) * c + of:yaw * bc, normX(f:roll, of:roll) * c + of:roll * bc).
}

function sac_target {
  parameter t.
  set sac_new_target to t.
}

function sac_stop {
  set sac_started to false.
  wait until sac_stopped.
}

function sac_init {
  parameter ac.
  return list(0, ac, 0).
}

function sac_log {
  parameter text.
  
  log_log(text).
}

function sac_tune {
  parameter arr.
  parameter tt.
  parameter x0.
  parameter v0.
  parameter tx.
  parameter v.
  parameter zv.
  parameter c.
  parameter deb.
  
  local state to arr[0].
  local ac to arr[1].
  local za to arr[2].
  
  local t2 to time:seconds. 
  local grad to 180 / constant():pi.
  local v2 to v * grad.
  set x0 to normX(x0, 0).
  set v0 to v0 * grad.
  local x to normX(tx, x0 + v0*tt).

  if state = 1 {
    set v0 to v0 - zv.
    local e0 to constant():e ^ (-sac_w * tt).

    local wt1 to 1 + sac_w * tt.
    local edx to x - (v0 * tt + wt1 * x0) * e0.
    local zx to edx / (1 - wt1 * e0).

    local c1 to v0 + sac_w * x0.
    local xt to (c1 * tt + x0) * e0.
    local dxw to x0 * tt * e0 - tt * xt.
    if abs(dxw) < 1 {
      if dxw < 0 set dxw to -1.
      else set dxw to 1.
    }
    local dw to (x - xt) / dxw.
    local nw to sac_w + dw.
    local acc to nw * nw / (sac_w * sac_w).
    if acc > 1.5 set acc to 1.5.
    if acc < 0.75 set acc to 0.75.
    local dac to (acc-1) * ac.
    set za to za - c * dac.
    set ac to ac * acc.	  
    set za to za + zx * sac_w * sac_w * 0.5.
  } else if state = 2 or state = 3 {
    local a to (v2 - v0) / tt.
    local da to a - c * ac - za.
    local dac to da * c * 0.5.
    if dac > 0.5 * ac set dac to 0.5 * ac.
    if dac < -0.25 * ac set dac to -0.25 * ac.
    set da to da - dac * c.
    set ac to ac + dac.
    set za to za + da.
    set state to 1.
  } else {
    set ac to ac * 2.
    set state to 1.
  }
  
  if deb sac_log("tune").
  
  set arr[0] to state.
  set arr[1] to ac.
  set arr[2] to za.
}

function sac_seek {
  parameter arr.
  parameter tx.
  parameter v.
  parameter zv.
  parameter ma0.
  parameter c.
  parameter deb.
  
  local state to arr[0].
  local ac to arr[1].
  local za to arr[2].
  
  local tc to c.
  
  local v2 to v * 180 / constant():pi.
  
  local x to normX(tx, 0).
  
  if state > 0 {
    local ta to calcTA(x, v2 - zv) + ma0.
    set tc to (ta - za)/ac.

    if deb {
      //sac_log("dt = " + dt).
      sac_log("dx = " + x).
      sac_log("v2 = " + v2).
      sac_log("zv = " + zv).
      sac_log("ac = " + ac).
      sac_log("c = " + c).
      sac_log("za = " + za).
      sac_log("ta = "+ta).
      sac_log(" ").
    }
    
    if tc >= 1 {
      if state = 1 set state to 2.
      else if state = 3 set state to 4.
      else if state = 5 set state to 6.
    } else if tc <= -1 {
      if state = 1 set state to 3.
      else if state = 2 set state to 5.
      else if state = 4 set state to 6.
    }
  } else {
    set state to 1.
  }
  
  set arr[0] to state.
  set arr[1] to ac.
  set arr[2] to za.

  return tc.
}

function calcTA {
  parameter x.
  parameter v.
  
  return -2 * v * sac_w - x * sac_w * sac_w.
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
