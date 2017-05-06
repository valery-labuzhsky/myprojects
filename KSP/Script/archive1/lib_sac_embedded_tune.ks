@LAZYGLOBAL off.

set config:ipu to 2000.

log "poehali" to lib_sac.log.
delete lib_sac.log.

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
    local d to ship:facing:inverse * sac_prev_facing.
    
    local v to ship:facing:inverse * ship:angularvel.
    
    set sac_prev_facing to ship:facing.
    set SHIP:CONTROL:PITCH to -sac_seek(sac_pitch_arr, -d:pitch, -f:pitch, v:x, -SHIP:CONTROL:PITCH, false).
    set SHIP:CONTROL:YAW to sac_seek(sac_yaw_arr, -d:yaw, -f:yaw, v:y, SHIP:CONTROL:YAW, false).
    set SHIP:CONTROL:ROLL to -sac_seek(sac_roll_arr, -d:roll, -f:roll, v:z, -SHIP:CONTROL:ROLL, false).
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
  return list(0, 0, 0, time:seconds, ac, 0, 0, 0, 0, 0).
}

function log1 {
  parameter text.
  
  log text to lib_sac.log.
  print text.
}

function sac_seek {
  parameter arr.
  parameter dx.
  parameter tx.
  parameter v.
  parameter c.
  parameter deb.
  
  local state to arr[0].
  local ox to arr[1].
  local ov to arr[2].
  local t to arr[3].
  local ac to arr[4].
  local za to arr[5].
  local t0 to arr[6].
  local x0 to arr[7].
  local v0 to arr[8].
  local zv to arr[9].
  
  local t2 to time:seconds. 
  local tc to c.
  
  local x to ox + normX(dx, 0).
  set x to normX(tx, 0).
  
  if t < t2 {
    local v2 to v * 180 / constant():pi.
    
    if state > 0 {
      
      if t0 = 0 {
	set t0 to t.
	set x0 to x.
	set v0 to v2.
      }
      
      local ta to calcTA(x, v2 - zv).

      local tune to false.
      if (t - t0 > 0.25) {
	set tune to true.
      }

      if tune {
	local tt to t - t0.
	if state = 1 {
	  set v0 to v0 - zv.
	  local w to 2.
	  local e0 to constant():e ^ (-w * tt).

	  local wt1 to 1 + w * tt.
	  local edx to x - (v0 * tt + wt1 * x0) * e0.
	  local zx to edx / (1 - wt1 * e0).

	  local c1 to v0 + w * x0.
	  local xt to (c1 * tt + x0) * e0.
	  local dxw to x0 * tt * e0 - tt * xt.
	  if abs(dxw) < 1 {
	    if dxw < 0 set dxw to -1.
	    else set dxw to 1.
	  }
	  local dw to (x - xt) / dxw.
	  local nw to w + dw.
	  local acc to nw * nw / (w * w).
	  if acc > 1.5 set acc to 1.5.
	  if acc < 0.75 set acc to 0.75.
	  local dac to (acc-1) * ac.
	  set za to za - c * dac.
	  set ac to ac * acc.	  
	  set za to za + zx * w * w * 0.5.
	  set zv to zv + x * 0.1.
	} else if state = 2 or state = 3 {
	  local a to (v2 - v0) / tt.
	  local da to a - c * ac - za.
	  local dac to da * c * 0.5.
	  if dac > 0.5 * ac set dac to 0.5 * ac.
	  if dac < -0.25 * ac set dac to -0.25 * ac.
	  set da to da - dac * c.
	  set ac to ac + dac.
	  set za to za + da.
	  set zv to 0.
	  set state to 1.
	} else {
	  set ac to ac * 2.
	  set zv to 0.
	  set state to 1.
	}
	
	if deb log1("tune").
	
	set x to normX(tx, 0).

	set t0 to t.
	set x0 to x.
	set v0 to v2.
      }

      set tc to (ta - za)/ac.

      if deb {
	//log1("dt = " + dt).
	log1("dx = " + x).
	log1("v2 = " + v2).
	//log1("a2 = " + a2).
	log1("ac = " + ac).
	log1("c = " + c).
	log1("za = " + za).
	log1("ta = "+ta).
	log1(" ").
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
    
    set ov to v2.
    set ox to x.
    set t to t2.
    
    set arr[0] to state.
    set arr[1] to ox.
    set arr[2] to ov.
    set arr[3] to t.
    set arr[4] to ac.
    set arr[5] to za.
    set arr[6] to t0.
    set arr[7] to x0.
    set arr[8] to v0.
    set arr[9] to zv.
  }
  return tc.
}

function calcTA {
  parameter x.
  parameter v.
  local w to 2.
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
