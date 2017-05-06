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
    set SHIP:CONTROL:PITCH to -sac_seek(sac_pitch_arr, -d:pitch, -f:pitch, v:x, -SHIP:CONTROL:PITCH, false).
    set SHIP:CONTROL:YAW to sac_seek(sac_yaw_arr, -d:yaw, -f:yaw, v:y, SHIP:CONTROL:YAW, true).
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
  return list(0, 0, 0, time:seconds, ac, 0, 0, 0, 0).
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
  
  local t2 to time:seconds. 
  local tc to c.
  
  local x to ox + normX(dx, 0).
  
  if t < t2 {
    local v2 to v * 180 / constant():pi.
    
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
      }

      if tune {
	local tt to t - t0.
	if state = 1 {
	  local w to 2.
	  local e0 to constant():e ^ (-w * tt).

	  if true {
	  local wt1 to 1 + w * tt.
	  local edx to x - (v0 * tt + wt1 * x0) * e0.
	  local zx to edx / (1 - wt1 * e0).

	  if false {
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
	  
	  if true {
	  // Newton's method x
	  //set x0 to x0 - zx/2.
	  //set x to x - zx/2.
	  
	  local c1 to v0 + w * x0.
	  local xt to (c1 * tt + x0) * e0.
	  local dxw to x0 * tt * e0 - tt * xt.
	  if abs(dxw) > 0.1 {
	    if deb log1("xt = " + xt).
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
	  set x0 to x0 - zx/2.
	  local c1 to v0 + w * x0.
	  local vt to (c1 - w * (c1 * tt + x0)) * e0.
	  local dvw to -tt * (v0 + 2 * w * x0) * e0 - tt * vt.
	  if abs(dvw) > 0.1 {
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

	  if false {
	  // Simple method
	  local da to zx * w * w * 0.5.
	  local dac to da * c * 0.5.
	  if dac > 0.5 * ac set dac to 0.5 * ac.
	  if dac < -0.25 * ac set dac to -0.25 * ac.
	  set da to da - dac * c.
	  set ac to ac + dac.
	  set za to za + da.
	  }
	  else
	  
	  set za to za + zx * w * w * 0.5.
	  }
	  
	  if false {
	  // Newton method x, v
	  //local w to 2.
	  //local e0 to constant():e ^ (-w * tt).
	  
	  local zx to za / (w ^ 2).
	  local dx0 to x0 - zx.
	  local dx to x - zx.
	  local c1 to v0 + dx0 * w.
	  
	  local f1 to (c1 * tt + dx0) * e0 - dx.
	  local f2 to (c1 - w * (c1 * tt + dx0)) * e0 - v2.
	  
	  local f1x to 1 - (1 + w * tt) * e0.
	  local f1w to -tt*tt * c1 * e0.
	  local f2x to w * w * tt * e0.
	  local f2w to (dx0 + tt * c1 * (tt * w - 2)) * e0.
	  
	  if false {
	  // classic way
	  local det to f1x * f2w - f1w * f2x.
	  
	  if deb log1("det = " + det).
	  
	  if abs(det) > 0.01 {
	  local dzx to (f2 * f2x - f1 * f2w) / det.
	  local dw to (f1 * f1w - f2 * f1x) / det.
	  
	  set za to za + dzx * w * w.

	  local nw to w + dw.
	  local acc to nw * nw / (w * w).
	  local dac to (acc-1) * ac.
	  set ac to ac * acc.
	  set za to za - c * dac.
	  }
	  
	  if false {
	  // double check way
	  local dzx to -f1 * 0.5 / f1x.
	  set za to za + dzx * w * w.
	  
	  if abs(f1w)>0.01 and abs(f2w)>0.01 {
	    local dw1 to -f1 / f1w.
	    local dw2 to -f2 / f2w.
	    if dw1 * dw2 > 0 {
	      local dw to dw1.
	      if abs(dw) > abs(dw2) set dw to dw2.

	      local nw to w + dw.
	      local acc to nw * nw / (w * w).
	      if acc > 1.5 set acc to 1.5.
	      if acc < 0.75 set acc to 0.75.
	      local dac to (acc-1) * ac.
	      set ac to ac * acc.
	      set za to za - c * dac.
	    }
	  }
	  }
	  
	  } else {
	  local dzx to -f1 / f1x.
	  set zx to zx + dzx.
	  set za to za + zx * w * w.
	  }
	  }
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
