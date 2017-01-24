## Load VUI Defs 
##############
set loc [file dirname [info script]]
source $loc/../../../vui2-core/src/gen/vui_defs.tcl


## Get Core Set 
## Name: vui 
#################
set jfxImpl [$vui implementation JavaFX JFX javafx.scene.Node {

    #:factory Frame {#

    #}

}]

set loc [file dirname [info script]]
[$jfxImpl mapToScala] writeToSourceFolder $loc/../../target/generated-sources/scala/
