@LAZYGLOBAL off.

wait 3.
switch to 0.
log1("Poehali!").
delete log.txt from 0.

set ship:control:MAINTHROTTLE to 1.
stage.

set config:ipu to 2000.

local t to HEADING(90, 90).
local f to ship:facing:inverse * t.

print f.

local pitch to init(0, false).
local yaw to init(0, true).
local roll to init(0, false).

local lf to ship:facing.

when true then {
  local f to ship:facing:inverse * t.
  local d to lf:inverse * ship:facing.
  set lf to ship:facing.
  set SHIP:CONTROL:PITCH to -seek(pitch, d:pitch, -f:pitch, -SHIP:CONTROL:PITCH).
  set SHIP:CONTROL:YAW to seek(yaw, d:yaw, -f:yaw, SHIP:CONTROL:YAW).
  set SHIP:CONTROL:ROLL to -seek(roll, d:roll, -f:roll + 90, -SHIP:CONTROL:ROLL).
  preserve.
}

wait until false.

function init {
  parameter x.
  parameter deb.
  return list(0, normX(x, 0), 0, time:seconds, 50, 0, deb, 0, 0, 0, false).
}

function log1 {
  parameter text.
  
  log text to log.txt.
  print text.
}

function seek {
  parameter arr.
  parameter dx.
  parameter tx.
  parameter c.
  
  local state to arr[0].
  local ox to arr[1].
  local v to arr[2].
  local t to arr[3].
  local ac to arr[4].
  local za to arr[5].
  local deb to arr[6].
  local t0 to arr[7].
  local x0 to arr[8].
  local v0 to arr[9].
  local br to arr[10].
  
  local t2 to time:seconds. 
  local tc to c.
  
  set dx to normX(dx, 0).
  local x to ox + dx.
  
  if t < t2 {
    local dt to t2 - t.
    local v2 to dx / dt.
    
    if state > 0 {
      local bx to 0.
      local bv to 0.
      local ba to 0.
      
      if br {
	local tt to t - t0.
	set ba to v0 * v0 * 0.5 / x0.
	set bv to v0 + ba * tt.
	set bx to x0 + (bv - ba * tt / 2) * tt. 
      }
      
      local ta to calcTA(x - bx, v2 - bv).
      
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
	  if br {
	    set v0 to 0.
	    set x0 to 0.
	  }
	  local w0 to 2.
	  local wt1 to 1 + w0 * tt.
	  local e0 to constant():e ^ (-w0 * tt).
	  local edx to (v0 * tt + wt1 * x0) * e0 - x + bx.
	  local zx to edx / (wt1 * e0 - 1).
	  
	  local dwn to v0 + x0 * w0.
	  if abs(dwn) > 0.1 {
	    local dw to edx / (e0 * tt * tt * dwn).
	    set dw to dw / 2.
	    local nw to w0 + dw.
	    local acc to nw * nw / (w0 * w0).
	    if acc > 1.5 set acc to 1.5.
	    if acc < 0.75 set acc to 0.75.
	    local dac to (acc-1) * ac.
	    // a = c * ac + za
	    // a = c * (ac + dac) + za - c * dac
	    set za to za - c * dac.
	    set ac to ac * acc.
	  }
	  set za to za + zx * w0 * w0 * 0.5.
	} else if state = 2 {
	  local ca to c * ac + za.
	  local a to (v2 - v0) / tt.
	  set za to za + a - ca.
	  set state to state - 1.
	} 
	
	set tx to normX(tx, 0).
	set x to tx.

	if (x * v2 < 0) {
	  local s to 1.
	  if (x < 0) set s to -1.
	  local sx to s * x.
	  set ba to v2 * v2 * 0.5 / sx.
	  local ax to ac + s * za.
	  
	  if ax < 0 {
	    set br to true.
	  } else {
	    if ba > 0 and -s * v2 / ba < 0.5 {
	      set br to false.
	    } else if ba * 2 > ax {
	      set br to true.
	    } else if ba * 4 < ax {
	      set br to false.
	    }
	  }
	}
	
	if deb log1("tune").
	
	set t0 to t.
	set x0 to x.
	set v0 to v2.
      }

      set tc to (ta - za + ba)/ac.
      
      if abs(tc) >= 1 {
	if state = 1 set state to 2.
      }

      if deb {
	//log1("dt = " + dt).
	log1("dx = " + x).
	log1("v2 = " + v2).
	//log1("a2 = " + a2).
	log1("ac = " + ac).
	//log1("ta = " + ta).
	log1("c = " + c).
	log1("br = " + br).
	log1("za = " + za).
	log1(" ").
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
    set arr[6] to deb.
    set arr[7] to t0.
    set arr[8] to x0.
    set arr[9] to v0.
    set arr[10] to br.

  }
  return tc.
}

function calcTA {
  parameter x.
  parameter v.
  local t to 0.5.
  return -(v / t) - x / (t * t).
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
