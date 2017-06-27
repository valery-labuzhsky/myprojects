parameter main.

local conf_switch_active to false.
local conf_speedup to false.
local conf_autowarp to false.
local stage_alt to 30000.

function conf_stage {
    local this to main.
    if conf_switch_active set this to not main.

    set conf_speedup to conf_speedup and this.
    set conf_autowarp to conf_autowarp and this.
}
