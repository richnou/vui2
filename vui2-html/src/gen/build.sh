#!/bin/bash

thisloc=$(dirname "$(readlink -f ${BASH_SOURCE[0]})")

source $thisloc/../../../src/odfi/load_odfi.sh


## Run Script
tclsh $thisloc/gen_html.tcl
