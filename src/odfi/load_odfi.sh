#!/bin/bash

loc=$(dirname "$(readlink -f ${BASH_SOURCE[0]})")

## Make sure we have TCL
#########
sudo aptitude -y install tcl8.5 tcl8.6 itcl3 nsf

## make sure we have ODFI 
########
if [[ ! -d $loc/../../target/odfi ]] 
then
	git clone --branch dev --single-branch https://github.com/richnou/odfi-manager.git $loc/../../target/odfi
else
	p=$(pwd)
	cd $loc/../../target/odfi
	git pull 
	cd $p
fi

source $loc/../../target/odfi/load.bash
odfi install tcl/devlib

#source $loc/../../target/odfi/setup.linux.bash
#odfi install tcl/devlib/master
#odfi --update
## REload to make sure new packages are loaded
#source $loc/../../target/odfi/setup.linux.bash