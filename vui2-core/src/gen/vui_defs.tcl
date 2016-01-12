## Load Scala
set loc [file dirname [info script]]
source $loc/scala-1.0.0.tm

## Load VUI Core
source $loc/vui-2.0.0.tm


#################
## VUI Defs 
#################
set vui [vui::core::set com.idyria.osi.vui.core.definitions {


    ## Scenegraph
    ###############
    :component "SGNode" {
        
        :onMap scala {
           :addTrait com.idyria.osi.tea.listeners.ListeningSupport {

           }
        }
        #extends ListeningSupport

        ## Containers
        #######################
        #:component "SGGroup" {


        #}

        :component "Component" {

            :property visible Boolean false 

            :component "Control" {

                ## Interaction 
                ##################

                :component "Button" {


                }   

                :component "Inputs" {

                    :component "TextInput" {


                    } 

                    :component "Spinner" {


                    } 

                    :component "Slider" {


                    } 

                }

                ## Web 
                ###################
                :component "WebBrowser" {

                    :property content String "<html></html>"

                    :property view com.idyria.osi.vui.core.view.AView\[_,_\] _

                } 

            }

            :component "Container" {

                :component "Frame" {
                    :property title String "VUI Frame"
                    :property size Tuple2\[Double,Double\] "(0,0)"
                }

            }

            ## Events 
            #############
            :onMap scala {

                puts "Component ON MAp scala"

                :def onClick {cl/=>Any} : Unit = {
                    throw new RuntimeException("Not Implemented")
                } 
                :def onClickFork {cl/=>Any} : Unit = {
                    throw new RuntimeException("Not Implemented")
                } 
            }

        }



    }
    ## EOF SGNode 


    :element Event {

        :element MouseEvent {

            :property actualX Int 0 
            :property actualY Int 0 

            :element ClickEvent {
                :property clickCount Int 0
            }

            :element DragEvent {

            }
        }

    }
    


}]


set vuiScala [$vui mapToScala]
