
## Load VUI Defs 
##############
set loc [file dirname [info script]]
source $loc/../../../vui2-core/src/gen/vui_defs.tcl

## Create HTML VUI set 
############################
set htmlImpl [vui::core::set com.idyria.osi.vui.html {


    ##:implementation Button -> Button {

    ##}





}]

## Map to package
set htmlScala [$htmlImpl mapToScala]

## Add HTML Components as Scala directly for now
########################
$htmlScala apply {

    ## Common Definitions 
    ##################
    set HTMLElements  {
            Span        span 
            Div         div 
            Nav         nav 
            Ul          ul
            Li          li 
            Body        body 
            Head        head 
            Html        html 
            Stylesheet  {link  href/java.net.URL}
            A           {a href}
            Script      {script  src/java.net.URL language}

            P           {p}
            H1          {h1 textContent/String}
            H2          {h2 textContent/String}
            H3          {h3 textContent/String}
            H4          {h4 textContent/String}
            H5          {h5 textContent/String}
            H6          {h6 textContent/String}

        }

    ## HTML Classes 
    #######################
    :class HTMLNode {
        :setFBounded

        ## Fields 
        :constructorField nodeName : String 
        
        ## FIXME : Add text content as class field

        ## Add to VUI Component 
        :addTrait com.idyria.osi.vui.core.definitions.VUIComponent {
            :addTypeParameter BT
            :setFBounded
        }
        #set comp [$vuiScala findChildByProperty name "VUIComponent"]
        #$comp addChild [current object]

        ## Backing Type in HTMLNode is something of HTML W3CDOm 
        :addTypeParameter  "HT <: org.w3c.dom.html.HTMLElement"
        :mapTypeParameter BT "HT"

        

        ## DOM Events 
        ##########################

        ## Form Stuff
        ######################
        :class FormInput {
            :parentConstructorField "nodeName" "input"

            :class InputText {

            }

            :class InputPassword {

            }

            :class Select {

            }
        }

        ## Very Simple Components
        ######################
        foreach {comp elts} $HTMLElements {

            ## Utils: Prepare args names for constructor 
            set argNames [odfi::flist::MutableList new]
            foreach argDef  [lrange $elts 1 end] {
                $argNames += [lindex [split $argDef /] 0]
            }

            :class $comp {
                :setFBounded
                :parentConstructorField "nodeName" [lindex $elts 0]

                ## Add Attributes as constructorField
                ## If it is textContent , then add without var
                foreach attr [lrange $elts 1 end] {

                    set type ""
                    ::extractVars [split $attr /] name type
                    :constructorField $name {
                        :type set [expr [string length $type]==0 ? "{String}" : "{$type}"]
                        
                        if {[:name get]== "textContent" } {
                            :override set true
                            :name set __textContent
                        }
                    }
                }

                #### Arguments from constructor are used as attributes 
                #### textContent just sets textContent
                if {[$argNames size]>0} {
                    #set initCode [odfi::richstream::template::stringToString ]
                    
                    :initSection {
                        // Attributes
                        <% 
                            return [[$argNames map {
                                if {$it== "textContent" } {
                                    return "this.textContent = __textContent"
                                } else {
                                    return "this(\"$it\" -> $it)"
                                }
                                
                            }] mkString "\\n"]
                        %>
                    }
                   
                }
            }

            

            
            

            #### Component Factory
            [:parent] trait ${comp}Factory {

                :addTypeParameter "BT <: org.w3c.dom.html.HTMLElement"
                
                :addTypeParameter "NT <: ${comp}\[BT,_\]"
                

                :def create${comp} [list [lrange $elts 1 end]] : NT = {
                    (new <% return ${comp}\[BT,${comp}\[BT,_\]\][$argNames mkString { "(" "," ")"}] %>).asInstanceOf[NT]
                }

            }

            #### Component Builder 
            [:parent] trait ${comp}Builder {
                :addTrait ${comp}Factory {
                    :addTypeParameter BT
                    :addTypeParameter NT
                }
                :addTrait com.idyria.osi.vui.core.builders.TreeBuilder {
                    :addTypeParameter BT
                    :addTypeParameter TBNT
                }
                :addTypeParameter "BT <: org.w3c.dom.html.HTMLElement"
                :mapTypeParameter "BT" "BT"
                
                :addTypeParameter "NT <: ${comp}\[BT,_\]"
                :mapTypeParameter "NT" "NT"

                :mapTypeParameter "TBNT" HTMLNode\[BT,_\]
               
                :def [string tolower ${comp}] [list [lrange $elts 1 end] [list cl/=>Any]] : NT = {

                    // Create 
                    var node = create<% return ${comp}[$argNames mkString { "(" "," ")"}] %>
                    
                    // Return 
                    switchToNode(node,cl)

                    node
                }

            }

        }
    }


    ## Factory Main Trait 
    ###################
    ::ignore {
        :trait HTMLFactoryTrait {
            #:addTrait 
            :addTypeParameter "BT <: org.w3c.dom.html.HTMLElement"
            :mapTypeParameter "BT" "BT"

            #:addTypeParameter "NT <: HTMLNode\[BT,_\]"
            #:mapTypeParameter "NT" "NT"

            [[:parent] shade scala::Trait children] foreach {

                if {[string match "*Factory" [$it name get]]} {
                    :addTrait [$it name get] {
                        :addTypeParameter BT
                        :addTypeParameter [$it name get]_NT
                    }

                    :mapTypeParameter [$it name get]_NT [string map {Factory ""} [$it name get]]\[BT\]
                }
                
            }
        }
    }
    
    

    

    ## Standalone Package 
    ###############
    :package standalone {


        ## Our Components
        ########################
        :trait "StandaloneHTMLNode" {
            :addTypeParameter "BT <: org.w3c.dom.html.HTMLElement"
                :mapTypeParameter "BT" "BT"
            :addTrait com.idyria.osi.vui.html.HTMLNode {
                :addTypeParameter BT 
                :setFBounded
            }

        }


        foreach {comp elts} $HTMLElements {

            :class Standalone$comp {

                :addTypeParameter "BT <: org.w3c.dom.html.HTMLElement"
                :mapTypeParameter "BT" "BT"
                :setFBounded

                ##### Main Component is parent class (reconstruct it to ease programming)
                :setParentClass com.idyria.osi.vui.html.$comp {
                    
                    ##puts "IMproving for parent: [:info class]"
                    :addTypeParameter BT 
                    :setFBounded

                    ## Add Attributes as constructorField
                    foreach attr [lrange $elts 1 end] {

                        set type ""
                        ::extractVars [split $attr /] name type
                        #:parentConstructorField $name $name 
                        :constructorField $name {
                            :type set [expr [string length $type]==0 ? "{String}" : "{$type}"]
                        }
                    }

                }

                ###### Mixin Standalone HTMLNode
                :addTrait StandaloneHTMLNode {
                    :addTypeParameter BT 
                    :setFBounded
                }

                #:parentConstructorField "nodeName" [lindex $elts 0]
                
                ## Utils: Prepare args names for constructor 
                set argNames [odfi::flist::MutableList new]
                foreach argDef  [lrange $elts 1 end] {
                    $argNames += [lindex [split $argDef /] 0]
                }



                

                #### Component Factory
                [:parent] trait Standalone${comp}Factory {

                    :addTypeParameter "BT <: org.w3c.dom.html.HTMLElement"
                    :addTypeParameter "NT <: [[[:parent] parent] name get].${comp}\[BT,_\]"

                    :mapTypeParameter  FBT  BT
                    :mapTypeParameter  BOUND NT
                    #:addTypeParameter "NT <: ${comp}\[BT,_\]"
                    
                    :addTrait com.idyria.osi.vui.html.${comp}Factory {
                        :addTypeParameter FBT 
                        :addTypeParameter BOUND 

                    }

                    :override def create${comp} [list [lrange $elts 1 end]]  = {
                        //.asInstanceOf[NT]
                        (new <% return Standalone${comp}\[BT,Standalone${comp}\[BT,_\]\][$argNames mkString { "(" "," ")"}] %>).asInstanceOf[NT]
                    }

                }

                #### Component Builder 
                [:parent] trait Standalone${comp}Builder {

                    :addTrait Standalone${comp}Factory {
                        :addTypeParameter BT
                        :addTypeParameter NT
                    }
                    :addTrait com.idyria.osi.vui.core.builders.TreeBuilder {
                        :addTypeParameter BT
                        :addTypeParameter TBNT
                    }
                    :addTypeParameter "BT <: org.w3c.dom.html.HTMLElement"
                    :mapTypeParameter "BT" "BT"
                    
                    #:addTypeParameter "NT <: Standalone${comp}\[BT,_\]"
                    :mapTypeParameter "NT" "Standalone${comp}\[BT,_\]"

                    :mapTypeParameter "TBNT" com.idyria.osi.vui.html.HTMLNode\[BT,_\]
                   
                    :def [string tolower ${comp}] [list [lrange $elts 1 end] [list cl/=>Any]]  = {

                        // Create 
                        var node = create<% return ${comp}[$argNames mkString { "(" "," ")"}] %>
                        
                        // Return 
                        switchToNode(node,cl)

                        node
                    }

                }
                
            }

        }


        

        set overridenComponents {
            com.idyria.osi.vui.html.Body com.idyria.osi.vui.html.test.MyBody
        }

        ## Standalone HTML Builder for non fully overriding interfaces
        #################
        :trait StandaloneBasicHTMLBuilderTrait {

            :addTypeParameter "LT  <: org.w3c.dom.html.HTMLElement"
            :mapTypeParameter "BT" "LT"

            set basePackage [[[:parent] parent] name get]

            :addTrait [[[:parent] parent] name get].basic.BasicHTMLBuilderTrait {
                :addTypeParameter BT
            }

            ## Add Our Factories 
            
            [[[:parent] parent] shade scala::Trait children] foreach {

                if {[string match "*Factory" [$it name get]]&& [$it name get]!="AbstractVUIFactory"} {

                    :addTrait Standalone[$it name get] {
                        :addTypeParameter BT
                        :addTypeParameter [$it name get]_NT


                    
                    }

                    ## Map Type of Builder to Default Name oder our Type
                    set baseComponentName [string map {Factory ""} [$it name get]]
                    
                    #set ourType [lsearch -exact $overridenComponents $baseComponentName]
                    #if {$ourType!=-1} {
                    #    set baseComponentName [lindex $overridenComponents [expr $ourType+1]]
                    #} else {
                    #    set baseComponentName $baseComponentName\[org.w3c.dom.html.HTMLElement\]
                    #}
                    
                    :mapTypeParameter [$it name get]_NT "${basePackage}.$baseComponentName\[LT,${basePackage}.$baseComponentName\[LT,_\]\]"
                    #:mapTypeParameter BT        "org.w3c.dom.html.HTMLElement"
                }
                
            }
           
        }


        ## Global HTML Builder 
        #################
        :trait StandaloneHTMLBuilderTrait {
            #:addTrait com.idyria.osi.vui.core.builders.TreeBuilder {
            #    :addTypeParameter NT
            #}
            :addTypeParameter "LT  <: org.w3c.dom.html.HTMLElement"
            :mapTypeParameter "BT" "LT"

            #:addTypeParameter "NT <: com.idyria.osi.vui.html.HTMLNode\[BT,_\]"
            #:mapTypeParameter "NT" "NT"

            [[:parent] shade scala::Trait children] foreach {

                if {[string match "*Builder" [$it name get]]&& [$it name get]!="VUIBuilder"} {

                    :addTrait [$it name get] {
                        :addTypeParameter BT
                        #:addTypeParameter [$it name get]_NT
                    }

                    ## Map Type of Builder to Default Name oder our Type
                    set baseComponentName [string map {Builder ""} [$it name get]]
                    
                    #set ourType [lsearch -exact $overridenComponents $baseComponentName]
                    #if {$ourType!=-1} {
                    #    set baseComponentName [lindex $overridenComponents [expr $ourType+1]]
                    #} else {
                    #    set baseComponentName $baseComponentName\[org.w3c.dom.html.HTMLElement\]
                    #}
                    
                    #:mapTypeParameter [$it name get]_NT $baseComponentName\[BT,$baseComponentName\[BT,_\]\]
                    #:mapTypeParameter BT        "org.w3c.dom.html.HTMLElement"
                }
                
            }

        }


    }

    ## Basic Package 
    ###############
    :package basic {


        ## Basic HTML Builder 
        #################
        :trait BasicHTMLBuilderTrait {
            #:addTrait com.idyria.osi.vui.core.builders.TreeBuilder {
            #    :addTypeParameter NT
            #}
            :addTypeParameter "BT <: org.w3c.dom.html.HTMLElement"
            :mapTypeParameter "BT" "BT"

            #:addTypeParameter "NT <: com.idyria.osi.vui.html.HTMLNode\[BT,_\]"
            #:mapTypeParameter "NT" "NT"

            [[[:parent] parent] shade scala::Trait children] foreach {

                if {[string match "*Builder" [$it name get]] && [$it name get]!="VUIBuilder"} {
                    :addTrait com.idyria.osi.vui.html.[$it name get] {
                        :addTypeParameter BT
                        :addTypeParameter [$it name get]_NT
                    }
                    set baseCompName  [string map {Builder ""} com.idyria.osi.vui.html.[$it name get]]
                    :mapTypeParameter [$it name get]_NT $baseCompName\[BT,$baseCompName\[BT,_\]\]
                }
                
            }
		
            ## Add events
            #####################
            :def onClick {cl/=>Unit} = {
                currentNode.onClick{cl}
            }

        }


    }

    

}
## EOF Interface package 
set loc [file dirname [info script]]
$htmlScala writeToSourceFolder $loc/../../target/generated-sources/scala/
