local stat_files to list().

function stat_create {
    parameter name.
    parameter header.

    stat_files:add(name).

    local filename to name + ".csv".
    log "poehali" to filename.
    deletepath(filename).

    local text to stat_string(header).
    log text to filename.
}

function stat_log {
    parameter name.
    parameter header.
    parameter data.

    if not stat_files:contains(name) {
        stat_create(name, header()).
    }

    local filename to name + ".csv".

    local text to stat_string(data).
    log text to filename.
}

function stat_string {
    parameter data.

    local text to "".
    local first to true.
    for i in data {
        if not first {
            set text to text + ", " + i.
        } else {
            set text to text + i.
            set first to false.
        }
    }
    return text.
}