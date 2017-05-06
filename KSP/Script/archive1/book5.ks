wait 3.

set ship:control:MAINTHROTTLE to 1.
stage.

set config:ipu to 2000.

global t to HEADING(90, 90).
local f to ship:facing:inverse * t.
global pitch to init(-f:pitch, false).
global yaw to init(-f:yaw, true).

when true then {
  local f to ship:facing:inverse * t.
  set SHIP:CONTROL:PITCH to -seek(pitch, -f:pitch, -SHIP:CONTROL:PITCH).
  set SHIP:CONTROL:YAW to seek(yaw, -f:yaw, SHIP:CONTROL:YAW).
  preserve.
}

wait until false.

function init {
  parameter x.
  parameter deb.
  return list(0, normX(x, 0), 0, 0, time:seconds, 0, 10, 0, 0, 0, deb, 0, 0, 0, 0, 0, 0).
}

function seek {
  parameter arr.
  parameter x.
  parameter c.
  
  local state to arr[0].
  local ox to arr[1].
  local v to arr[2].
  local a to arr[3].
  local t to arr[4].
  local oc to arr[5].
  local acc to arr[6].
  local i to arr[7].
  local odc to arr[8].
  local za to arr[9].
  local deb to arr[10].
  local ia to arr[11].
  local iv to arr[12].
  local iae to arr[13].
  local idc to arr[14].
  local macc to arr[15].
  local ida to arr[16].
  
  local t2 to time:seconds. 
  local tc to c.
  
  	  if deb
	  {
	    //print "fa = " + fa.
	    //print "ta = " + ta.
	    //print "c = " + c.
	  }

  set x to normX(x, ox).
  
  if t < t2 {
    if state > 0 {//and state < 4 {
      local dt to t2 - t.

      local v2 to (x - ox) / dt.
      if state > 1 {
	local a2 to (v2 - v) / dt.
	
	local n to 1.
	if iae > 0 set n to 0.1 * acc / iae.
	if n > 0.5 set n to 0.5.
	local oia to ia.
	//set ia to (ia + acc * odc) * (1-n) + a2 * n.
	set iae to iae * (1-n) + abs(a2 - ia) * n.
	
	if deb {
	  //print "idc = " +idc.
	  //print "iae = " + iae.
	  //print "n = " + n.
	}

	//set iv to iv * 0.99 + v2 * 0.01 + ia * dt.
	set iv to v2.
	
	if i = 10 {
	  //set deb to false.
	}

	//set ida to ida * 0.9 + a2 - a.
	//local oidc to idc.
	//set idc to idc * 0.9 + odc.
	//if oidc * idc < 0 set ida to 0.

	if state = 3 {
	  if deb
	  {
	    print "dt = " + dt.
	    print "dx = " + x.
	    print "v2 = " + v2.
	    //print "iv = " + iv.
	    print "a2 = " + a2.
	    //print "ia = " + ia.
	    //print " ".
	  }

	  local dc to c - oc.
	  
	  set ida to a2 - ia.
	  set ia to a2.
	  set idc to dc.
	  
	    if deb {
	      print "ida = " + ida.
	      print "idc = " + idc.
	    }
	  
	  if abs(idc) > 0.01 {
	    local acc2 to ida / idc.
	    //set acc to acc * 0.9 + acc2 * 0.1.
	    if acc > acc2 set acc to acc * 0.9.
	    else if acc < acc2 set acc to acc * 1.1.
	  }
	  
	  //if abs(idc) > 0.01 and abs(idc) > 2 * abs(odc) {
	  if false {
	    local acc2 to ida / idc.
	    if acc2 > acc {
	      set acc to acc + acc * 0.01 * abs(idc).
	    } else if acc2 < acc {
	      set acc to acc - acc * 0.01 * abs(idc).
	    }
	    if deb {
	      print "ida = " + ida.
	      print "idc = " + idc.
	    }
	  }
	  
	  local ta to oc * acc + za.
	  //set za to za + ia - ta.
	  set za to za + ia - acc * c.
	  if deb
	  {
	    print "acc = " + acc.
	    //print "za = " + za.
	    //print " ".  
	  }
	  //local fa to c * acc + za.
	  local fa to ia.// + acc * dc.
	  set ta to calcTA(x, iv, acc, za).
	  if deb
	  {
	    //print "fa = " + fa.
	    //print "ta = " + ta.
	    print "c = " + c.
	    print " ".
	  }
	  set odc to dc.
	  if ta * v2 < 0 set ta to ta * 2.
	  set dc to (ta - fa)/acc.
	  //if dc > 0.01 set dc to 0.01.
	  //if dc < -0.01 set dc to -0.01.
	  //set dc to ((ta - za)/acc) - c.
  	  set tc to c + dc.
  	  set oc to c.
	}
	set a to a2.

	set v to v2.
	
	set ox to x.
	set t to t2.
      }
    }
    set i to i + 1.
    set state to state + 1.
    if state > 11 set state to 3.
    
    set arr[0] to state.
    set arr[1] to ox.
    set arr[2] to v.
    set arr[3] to a.
    set arr[4] to t.
    set arr[5] to oc.
    set arr[6] to acc.
    set arr[7] to i.
    set arr[8] to odc.
    set arr[9] to za.
    set arr[10] to deb.
    set arr[11] to ia.
    set arr[12] to iv.
    set arr[13] to iae.
    set arr[14] to idc.
    set arr[15] to macc.
    set arr[16] to ida.
  }
  return tc.
}

function calcTA {
  parameter x.
  parameter v.
  parameter acc.
  parameter za.
  local ta to calcMA(x, v).
  local ma to za - acc.
  local la to calcLA(v, ma).
  if ta > la {
    set ta to calcXA(x, v, ma).
  } else {
    set ma to za + acc.
    set la to calcLA(v, ma).
    if ta < la {
      set ta to calcXA(x, v, ma).
    } else if abs(x) > 0.01 {
      local ba to v * v / (2 * x).
      if abs(ba) > 0 {
	local bt to -v / ba.
	if bt > 0 and bt < 1 {
	  //return ba.
	}
      }
    }
  }
  return ta.
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

function calcSRA {
  parameter x.
  parameter v.
  parameter a.
  local t to 0.5.
  return a * a * t * t + 4 * a * v * t + 8 * a * x.
}

function calcXA {
  parameter x.
  parameter v.
  parameter ax.
  local t to 0.5.
  local sqrt to calcSRA(x, v, ax).
  if (sqrt < 0) {
     local ca to (-2 * v + ax * t) / ( 2 * t).
     return ca.
  } else {
    set sqrt to sqrt(sqrt).
    local a1 to (sqrt + ax * t - 2 * v) / t.
    local a2 to (-sqrt + ax * t - 2 * v) / t.
    local t1 to calcSA(v, a1, ax).
    local t2 to calcSA(v, a2, ax).
    if t1 < 0 return a2.
    else if t2 < 0 return a1.
    if (t1 < t2) {
      return a1.
    } else {
      return a2.
    }
  }
}

function calcSA {
  parameter v.
  parameter a.
  parameter ax.
  local t to 0.5.
  local ta to -a * t - v.
  if ax < 0 set ta to -ta.
  return ta.
}

function calcLA {
  parameter v.
  parameter ax.
  local t to 0.5.
  return (-ax - (v / t)).
}

function calcMA {
  parameter x.
  parameter v.
  
  local t to 0.5.
  return -(3 * v / (2 * t)) - x / (t * t).
}