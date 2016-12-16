package provide vui::core 2.0.0
package require scala 1.0.0


package require odfi::language 1.0.0

package require odfi::richstream 3.0.0
package require odfi::files 2.0.0




################
## VUI
################
namespace eval vui::core {


    odfi::language::Language default {

        :WithProperties {

            :property name type default {

            }

            :interface spec {
                
            }
        }

        :pset name {

            +exportToPublic

            

            :element : WithProperties name {

                +exportTo Element

                
                
            }

            :component : WithProperties name {

                +exportTo Component

                ## Language Map 
                ########################
                +var languageMap {
                    +multiple set true
                }

                +method onMap {name cl} {
                
                    set existing [lsearch -exact ${:languageMap} $cl]
                    
                    if {$existing==-1} {
                    
                        lappend :languageMap $name [list $cl]
                        
                    } else {
                    
                        set :languageMap [lreplace ${:languageMap} $existing $existing [concat [lindex ${:languageMap} existing] [list $cl]]]
                    }
                    
                }

                +method getOnMap name {
                    set existing [lsearch -exact ${:languageMap} $name]
                    if {$existing==-1} {
                        return ""
                    } else {
                        return [lindex  ${:languageMap} [expr $existing+1]]
                    }
                }
               
            }

            :implementation name prefix basenode {



                +method mapToScala args {

                    ## gather back Factories and Builder names 
                    set factories {}

                    ## Create a Package 
                    set mainSet [:parent]
                    set impl [current object]
                    set scala [[:parent] map {

                        #puts "Scal MAp main node is [$node info class]"
                        if {[$node isClass vui::core::Pset]} {
                            return [scala::package com.idyria.osi.vui.implementation.[string tolower [$impl name get]]]
                        } elseif {[$node isClass vui::core::Component] && [$node shade vui::core::Component isLeaf]} {

                            #####################
                            ## LEAF NODe Should be factored 
                            #########################

                            ## Create Factory Implementation 
                            $parent trait [$impl prefix get][$node name get]Factory {
                                
                                lappend factories [:name get]

                                :mapTypeParameter BT [$impl basenode get]
                                :addTrait [$mainSet name get].VUI[$node name get]Factory {
                                    :addTypeParameter BT
                                }

                                :def create[$node name get] : [$mainSet name get].VUI[$node name get]\[[:getTypeParameterMap BT],[$mainSet name get].VUI[$node name get]\[[:getTypeParameterMap BT],_\]\] = {
                                    throw new RuntimeException("<% return [$impl prefix get] %> for <% return [$node name get] %> Not Implemented")
                                }

                            }

                           
                        }  


                        ## Always return parent, so that we always stay in package
                        return $parent 

                    }]

                

                    ## Add Global Factory 
                    $scala class JFXFactory {
                        :abstract set true
                        :mapTypeParameter BT  [$impl basenode get]
                        :addTrait [$mainSet name get].AbstractVUIFactory {
                            :addTypeParameter BT
                        }

                        foreach fact $factories {
                            :addTrait $fact {

                            }
                        }
                    }

                    return $scala 
                   

                }

            }


            +method mapToScala args {

                ## gather back Factories and Builder names 
                set factories {}
                set builders {}

                set vuiScalaPackage [:map {

                    if {[$node isClass vui::core::Pset]} {
                        
                        puts "Creating package for [$node name get]"
                        return [scala::package [$node name get]]

                    } elseif {[$node isClass vui::core::Component]} {

                        
                        

                        #### Create Main Trait 
                        ######################
                        #puts "Creating Interface [$node name get] in [$package name get]"
                        set mainTrait [$parent trait VUI[$node name get] {

                            :setFBounded

                            :addTrait com.idyria.osi.vui.core.utils.ApplyTrait {
                                :setFBounded
                            }
                            
                            if {[[$node parent] isClass vui::core::Component]} {
                                #:addTrait $parent
                            }

                            ## Apply on map 
                            foreach cl [$node getOnMap scala] {
                                #puts "Applying on scala : $cl"
                                :apply $cl
                            }

                            ## Convert Properties 
                            [$node shade vui::core::Property children] foreach {
                              #  puts "Adding Fiedl: [[current object] info class]"
                                :field __[$it name get] {
                                    :type set [$it type get]
                                    :default set [$it default get]
                                    :visibility set private
                                }

                                :def  [$it name get] : [$it type get]  = { __<% return [$it name get] %> }
                                :def  [$it name get]_= [list [list v/[$it type get]]] = {
                                    __<% return [$it name get] %> = v
                                }

                                #:def get[string toupper [$it name get]  0 0] : [$it type get]  = { __<% return [$it name get] %> }
                                #:def set[string toupper [$it name get]  0 0] [list [list v/[$it type get]]] = {
                                #    __<% return [$it name get] %> = v
                                #}
                            }

                        }]
                        

                        #### Find package 
                        if {[$parent isClass scala::Type]} {
                            set package [$parent findParentInPrimaryLine {$it isClass scala::Package}]
                            puts "Creating Class in [$package name get]"
                            
                        } else {
                            set package $parent
                            $mainTrait addTypeParameter BT
                        }
                        $package addChild $mainTrait

                        ##################
                        ## Interface and Factory for terminal components 
                        ###############
                        if {[$node shade vui::core::Component isLeaf]} {

                            #### Create Interface for Factory 
                            $package trait VUI[$node name get]Factory {

                                lappend factories [:name get]

                                :addTypeParameter BT 

                                :def create[$node name get] : VUI[$node name get]\[BT,VUI[$node name get]\[BT,_\]\]
                            }
                            
                            #### Create Interface for Builder 
                            $package trait VUI[$node name get]Builder {

                                :addTypeParameter BT 

                                lappend builders [:name get]

                                :def [string tolower [$node name get]] : VUI[$node name get]\[BT,VUI[$node name get]\[BT,_\]\] = {
                                    //throw new RuntimeException("Not Implemented")
                                    com.idyria.osi.vui.core.VUIFactory.selectedImplementation[BT].create<% return [$node name get] %>
                                }
                            }
                        }
                        

                        return $mainTrait
                        
                    } elseif {[$node isClass vui::core::Element] } {

                        ####################
                        ## Elements are provided 
                        ####################

                        $parent class VUI[$node name get] {

                            ## Set Parent class 
                            if {[[$node parent] isClass vui::core::Element]} {
                                :addTrait VUI[[$node parent] name get] {

                                }
                            }

                            ## Set Properties 
                            [$node shade vui::core::Property children] foreach {
                              #  puts "Adding Fiedl: [[current object] info class]"
                                :field [$it name get] {
                                    :type set [$it type get]
                                    :default set [$it default get]
                                }
                            }
                            
                            
                        }

                        return $parent 
                    }

                }]

                ## Add Global Factory to be implemented 
                ##########
                $vuiScalaPackage trait AbstractVUIFactory {
                    #:addTypeParameter BT 

                    foreach fact $factories {
                        :addTrait $fact {
                            :addTypeParameter BT
                        }

                       
                    }


                    ## A Few Utilities
                    :addTrait com.idyria.osi.vui.core.utils.UtilsTrait {

                    }

                }

                ## Add Global Generic Builder 
                #####################
                $vuiScalaPackage trait VUIBuilder {
                    #:addTypeParameter BT 

                    :mapTypeParameter BT Any
                    foreach builder $builders {
                        :addTrait $builder {
                            :addTypeParameter BT
                        }
                    }

                }

                return $vuiScalaPackage
            }

            +method writeToScala folder {

                ## Map to Scala
                ########################
                set mapped [:mapToScala]

                $mapped writeToSourceFolder $folder 



            }
            ## EOf Write to scala

        }

    }

    

}
