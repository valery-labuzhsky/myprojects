@LAZYGLOBAL off.
SET CONFIG:PAUSEONCOMPILE TO TRUE.

wait until NOT core:MESSAGES:EMPTY.
local message TO core:MESSAGES:POP.
print message.

run lib_stage.