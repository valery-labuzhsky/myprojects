@LAZYGLOBAL off.

log "funct" to "funct.ks".
deletepath("funct.ks").
log "parameter fun." to "funct.ks".
log "parameter par." to "funct.ks".

local funct_num to 0.

function funct_add {
  parameter name.
  parameter nargs.
  
  set name to name + "(".
  if nargs = 1 set name to name + "par".
  else from {local i to 0.} until i=nargs step {set i to i+1.} do {
    if i > 0 set name to name + ", ".
    set name to name + "par[" +i+"]".
  }
  log "if fun="+funct_num+" "+name +")." to "funct.ks".
  set funct_num to funct_num  + 1.
  return funct_num-1.
}