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
    
    local v to ship:facing * ship:angularvel.
    
    set sac_prev_facing to ship:facing.
    set SHIP:CONTROL:PITCH to -sac_seek(sac_pitch_arr, -d:pitch, -f:pitch, -SHIP:CONTROL:PITCH, false, v:x).
    set SHIP:CONTROL:YAW to sac_seek(sac_yaw_arr, -d:yaw, -f:yaw, SHIP:CONTROL:YAW, true, v:y).
    set SHIP:CONTROL:ROLL to -sac_seek(sac_roll_arr, -d:roll, -f:roll, -SHIP:CONTROL:ROLL, false, v:z).
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
  parameter c.
  parameter deb.
  parameter av.
  
  local state to arr[0].
  local ox to arr[1].
  local v to arr[2].
  local t to arr[3].
  local ac to arr[4].
  local za to arr[5].
  local t0 to arr[6].
  local x0 to arr[7].
  local v0 to arr[8].
  local dv to arr[9].
  
  local t2 to time:seconds. 
  local tc to c.
  
  set dx to normX(dx, 0).
  local x to ox + dx.
  
  if t < t2 {
    local dt to t2 - t.
    local v2 to dx / dt.
    set v2 to av * 180 / constant():pi.
    
    if false {
    local dv2 to v2 - v.
    if dv * dv2 < 0 {
      set v2 to v.
      set dv to dv2.
    } else if abs(dv) > abs(dv2) {
      set dv to 0.
    } else {
      set v2 to v + dv.
      set dv to dv2 - dv.
    }
    }
    
    if state > 0 {
      
      if t0 = 0 {
	set t0 to t.
	set x0 to x.
	set v0 to v2.
      }
      
      local ta to calcTA(x, v2).

      local tune to false.
      if (t - t0 > 0.25) {
	set tune to true.
      } else if false {
	local ota to calcTA(ox, v).
	if ota * ta < 0 {
	  local dac to ac / 2.
	  set za to za - c * dac.
	  set ac to ac + dac.
	}
      }

      if tune {
	local tt to t - t0.
	if state = 1 {
	  local w to 2.
	  local e0 to constant():e ^ (-w * tt).

	  local wt1 to 1 + w * tt.
	  local edx to x - (v0 * tt + wt1 * x0) * e0.
	  local zx to edx / (1 - wt1 * e0).

	  if true {
	  //Linear
	  local dwn to v0 + x0 * w.
	  if abs(dwn) > 0.1 {
	    local dw to -edx / (e0 * tt * tt * dwn).
	    set dw to dw / 2.
	    local nw to w + dw.
	    local acc to nw * nw / (w * w).
	    if acc > 1.5 set acc to 1.5.
	    if acc < 0.75 set acc to 0.75.
	    local dac to (acc-1) * ac.
	    set za to za - c * dac.
	    set ac to ac * acc.
	  }
	  }
	  
	  if false {
	  // Newton's method x
	  local c1 to v0 + w * x0.
	  local xt to (c1 * tt + x0) * e0.
	  local dxw to x0 * tt * e0 - tt * xt.
	  if abs(dxw) > 0.01 {
	    local dw to (x - xt) / dxw.
	    local nw to w + dw.
	    local acc to nw * nw / (w * w).
	    if acc > 1.5 set acc to 1.5.
	    if acc < 0.75 set acc to 0.75.
	    local dac to (acc-1) * ac.
	    set za to za - c * dac.
	    set ac to ac * acc.
	  }
	  }

	  if false {
	  // Newton's method v
	  set x0 to x0.
	  local c1 to v0 + w * x0.
	  local vt to (c1 - w * (c1 * tt + x0)) * e0.
	  local dvw to -tt * (v0 + 2 * w * x0) * e0 - tt * vt.
	  if abs(dvw) > 0.01 {
	    local dw to (v - vt) / dvw.
	    local nw to w + dw.
	    local acc to nw * nw / (w * w).
	    if acc > 1.5 set acc to 1.5.
	    if acc < 0.75 set acc to 0.75.
	    local dac to (acc-1) * ac.
	    set za to za - c * dac.
	    set ac to ac * acc.
	  }
	  }
	  
	  set za to za + zx * w * w * 0.5.
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
	} 
	
	if deb log1("tune").
	
	set tx to normX(tx, 0).
	set x to tx.

	set t0 to t.
	set x0 to x.
	set v0 to v2.
      }

      set tc to (ta - za)/ac.

      if deb {
	//log1("dt = " + dt).
	log1("dx = " + x).
	log1("v2 = " + (dx / dt)).
	log1("av = " + (av * 180 / constant():pi)).
	//log1("a2 = " + a2).
	log1("ac = " + ac).
	log1("c = " + c).
	log1("za = " + za).
	log1("ta = "+ta).
	log1(" ").
      }
      
      if tc >= 1 {
	if state = 1 set state to 2.
	//else if state = 3 set state to 4.
	//else if state = 5 set state to 6.
      } else if tc <= -1 {
	if state = 1 set state to 3.
	//else if state = 2 set state to 5.
	//else if state = 4 set state to 6.
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
    set arr[9] to dv.

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
