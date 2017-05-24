
set loc [file dirname [info script]]

## Load VUI Module 
source $loc/vui_defs.tcl

## Map to Scala And Genereate 
#####################################
$vuiScala writeToSourceFolder $loc/../../target/generated-sources/scala/

#source gen_html.tcl
