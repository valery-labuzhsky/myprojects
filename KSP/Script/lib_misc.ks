@LAZYGLOBAL off.

function open_terminal {
  core:part:getmodule("kOSProcessor"):doevent("Open Terminal").
}

function close_terminal {
  open_terminal().
  log_log(core:part:getmodule("kOSProcessor"):ALLACTIONNAMES).
  core:part:getmodule("kOSProcessor"):doaction("Close Terminal", true).
}

function default {
  parameter par.
  parameter def.
  
  if par = "$<argstart>" return def.
  return par.
}

function omit {
  parameter par.
  
  return par = "$<argstart>".
}
