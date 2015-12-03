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

[$jfxImpl mapToScala] writeToSourceFolder ../../target/generated-sources/scala/
