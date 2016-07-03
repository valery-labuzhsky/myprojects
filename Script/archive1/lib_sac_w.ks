@LAZYGLOBAL off.

set config:ipu to 2000.

local started to false.

local target to R(0, 0, 0).
global sac_prev_facing to ship:facing.

function sac_start {
  parameter t.
  global sac_pitch_arr to sac_init(50).
  global sac_yaw_arr to sac_init(50).
  global sac_roll_arr to sac_init(50).

  set sac_prev_facing to ship:facing.

  set target to t.
  set started to true.
  
  when started then {
    local f to ship:facing:inverse * target.
    local d to sac_prev_facing:inverse * ship:facing.
    set sac_prev_facing to ship:facing.
    set SHIP:CONTROL:PITCH to -sac_seek(sac_pitch_arr, d:pitch, -f:pitch, -SHIP:CONTROL:PITCH, false).
    set SHIP:CONTROL:YAW to sac_seek(sac_yaw_arr, d:yaw, -f:yaw, SHIP:CONTROL:YAW, true).
    set SHIP:CONTROL:ROLL to -sac_seek(sac_roll_arr, d:roll, -f:roll, -SHIP:CONTROL:ROLL, false).
    preserve.
  }
}

function sac_target {
  parameter t.
  set target to t.
}

function sac_stop {
  set started to false.
}

function sac_init {
  parameter ac.
  return list(0, 0, 0, time:seconds, ac, 0, 0, 0, 0, 2).
}

function log1 {
  parameter text.
  
  log text to log.txt.
  print text.
}

function sac_seek {
  parameter arr.
  parameter dx.
  parameter tx.
  parameter c.
  parameter deb.
  
  local state to arr[0].
  local ox to arr[1].
  local v to arr[2].
  local t to arr[3].
  local ac to arr[4].
  local za to arr[5].
  local t0 to arr[6].
  local x0 to arr[7].
  local v0 to arr[8].
  local w to arr[9].
  
  local t2 to time:seconds. 
  local tc to c.
  
  set dx to normX(dx, 0).
  local x to ox + dx.
  
  if t < t2 {
    local dt to t2 - t.
    local v2 to dx / dt.
    
    if state > 0 {
      
      if t0 = 0 {
	set t0 to t.
	set x0 to x.
	set v0 to v2.
      }
      
      local tune to false.
      if (t - t0 > 0.25) {
	set tune to true.
      }

      if tune {
	local tt to t - t0.
	if state = 1 {
	  local wt1 to 1 + w * tt.
	  local e0 to constant():e ^ (-w * tt).
	  local edx to (v0 * tt + wt1 * x0) * e0 - x.
	  local zx to edx / (wt1 * e0 - 1).
	  
	  local dwn to v0 + x0 * w.
	  if abs(dwn) > 0.1 {
	    local dw to edx / (e0 * tt * tt * dwn).
	    set dw to dw / 2.
	    local nw to w + dw.
	    local acc to nw * nw / (w * w).
	    if acc > 1.5 set acc to 1.5.
	    if acc < 0.75 set acc to 0.75.
	    local dac to (acc-1) * ac.
	    set za to za - c * dac.
	    set ac to ac * acc.
	  }
	  set za to za + zx * w * w * 0.5.
	} else if state = 2 {
	  local ca to c * ac + za.
	  local a to (v2 - v0) / tt.
	  set za to za + a - ca.
	  set state to state - 1.
	} 
	
	set tx to normX(tx, 0).
	set x to tx.

	set t0 to t.
	set x0 to x.
	set v0 to v2.
	
	//if deb set w to calcW(x, v2, ac, za).
      }

      local ta to calcTA(x, v2, w).      
      set tc to (ta - za)/ac.

      if deb {
	//log1("dt = " + dt).
	log1("dx = " + x).
	log1("v2 = " + v2).
	//log1("a2 = " + a2).
	log1("ac = " + ac).
	log1("w = " + w).
	log1("c = " + c).
	log1("za = " + za).
	log1(" ").
      }
      
      if abs(tc) >= 1 {
	if state = 1 set state to 2.
      }
    } else {
      set state to 1.
    }
    
    set v to v2.
    set ox to x.
    set t to t2.
    
    set arr[0] to state.
    set arr[1] to ox.
    set arr[2] to v.
    set arr[3] to t.
    set arr[4] to ac.
    set arr[5] to za.
    set arr[6] to t0.
    set arr[7] to x0.
    set arr[8] to v0.
    set arr[9] to w.

  }
  return tc.
}

function calcW {
  parameter x.
  parameter v.
  parameter ac.
  parameter za.
  
  log1("calcW").
  if x * v < 0 {
    local s to 1.
    if x < 0 {
      set s to -1.
      set x to -x.
      set v to -v.
    }
    local ax to ac + s * za.
    log1("ax = "+ax).
    if ax > 0 {
      local w to 2.
      if 2 * x * w < -3 * v {
	log1("precheck").
	return w.
      }
      until false {
	log1("cycle").
	local c1 to v + w * x.
	local t to (v + 2 * c1) / (w * c1).
	local a to w * (w * x + c1 * (w * t - 2)) * constant():e ^ (-w * t).
	log1("fa = "+a).
	if a < ax return w.
	set w to w / 2.
	if 2 * x * w < -3 * v return -3 * v / (2 * x).
      }
    } else {
      local w to (sqrt(v * v - 4 * x * ax) - v) / (2 * x).
      if w > 2 return 2.
      return w.
    }
  } else {
    return 2.
  }
}

function calcTA {
  parameter x.
  parameter v.
  parameter w.
  return -2 * v * w - x * w * w.
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
