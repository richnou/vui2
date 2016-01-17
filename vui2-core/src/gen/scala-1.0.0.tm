package provide scala 1.0.0

package require odfi::language 1.0.0

package require odfi::richstream 3.0.0
package require odfi::files 2.0.0



########
## Scala 
###############
namespace eval scala {

    odfi::language::Language default {




        :type name {

            ## Config 
            ##############
            +var abstract   false
            +var final      false


             ## Fields 
            ############
            :field name {
                +var visibility public 
                +var constructor false
                +var override false
                +var type  "String"     
                +var default ""    
            }

            ## Format :constructorField name : Type CL 
            +method constructorField args {


                :field [lindex $args 0] {
                    :constructor set true
                    if {[lindex $args 1]==":"} {
                        :type set  [lindex $args 2]
                        :apply [lindex $args 3]
                    } else {
                        :apply [lindex $args 1]
                    }
                    
                    
                }
            }


            +var parentConstructorFieldsMap {
                +multiple set true
            }

        

            +method parentConstructorField {name value} {
                set existing [lsearch -exact ${:parentConstructorFieldsMap} $name]
                if {$existing==-1} {
                    lappend :parentConstructorFieldsMap $name $value 
                } else {
                    set :parentConstructorFieldsMap [lreplace ${:parentConstructorFieldsMap} $existing $existing $value]
                }
            }

            +method getParentConstructorField name {
                set existing [lsearch -exact ${:parentConstructorFieldsMap} $name]
                if {$existing==-1} {
                    return ""
                } else {
                    return [lindex  ${:parentConstructorFieldsMap} [expr $existing+1]]
                }
            }

            ## Init Code 
            ###################
            :initSection body {
                +builder {
                    
                    set :body [odfi::richstream::template::stringToString ${:body}]

                    next

                }
            }

            ## Parent Class 
            ####################
            +method setParentClass {t {cl {}} } {
                #puts "Adding tait $t"
                set class [scala::Class new -name $t]
                $class addChild [current object]
                [current object] addParent $class

                $class apply $cl
                return $cl
            
            }

            ## Traits 
            ############
            +var traits {
                +multiple set true
            }  


            ## Create Trait object and add 
            +method addTrait {t {cl {}}} {
                #puts "Adding tait $t"
                set trait [scala::Trait new -name $t]
                lappend :traits  $trait
                $trait apply $cl
                return $trait
            
            }

            ## Type parameters 
            ########################
            +var typeParameters {
                +multiple set true
            }
            +var typeParametersMap {
                +multiple set true
            }

            +method addTypeParameter t {
                if {[lsearch -exact ${:typeParameters} $t]==-1} {
                    lappend :typeParameters $t
                }
                
            }

            +method mapTypeParameter {t type} {
                set existing [lsearch -exact ${:typeParametersMap} $t]
                if {$existing==-1} {
                    lappend :typeParametersMap $t $type 
                } else {
                    set :typeParametersMap [lreplace ${:typeParametersMap} $existing $existing $type]
                }
            }

            +method getTypeParameterMap t {
                set existing [lsearch -exact ${:typeParametersMap} $t]
                if {$existing==-1} {
                    return ""
                } else {
                    return [lindex  ${:typeParametersMap} [expr $existing+1]]
                }
            }

            ## FBounded 
            +var fBounded false

            +method setFBounded args {
                set :fBounded true

            }

            +method isFBounded args {
                return ${:fBounded}
            }

            # Methods 
            ###############
            :method name {
                +var returnType "-"
                +var body       ""
                +var override   false

                :argumentSet {
                    :argument name {
                        +var type "String"
                    }
                }
                
            }

            ## Def Spec: def name args? : returnType =? body?
            +method override {def args} {
                set d [eval ":def $args"]
                $d override set true
            }
            +method def args {

                #set name [lindex $args 0]
                #set rt   [lindex $args end]
                #puts "Defcalled with [llength $args] -> $args"
                switch [llength $args]  {
                    
                   
                    3 {
                         ## def name : RT
                        return [:method [lindex $args 0] {
                            :returnType set [lindex $args end]

                            #puts "Set Return type: [:returnType get] // [lindex $args 0]"
                            #exit 0
                        }]
                    }

                    
                    4 {
                        ## def name args : RT
                        ## def name args = body 
                        if {[lindex $args 2]=="="} {
                            return [:method [lindex $args 0] {
                                :body set [odfi::richstream::template::stringToString [lindex $args end]]

                                #puts "Called DEF with 6 args: [lindex $args 1] "

                                ## Set arguments 
                                set arguments [lindex $args 1]
                                foreach argSet $arguments {

                                    :argumentSet {

                                        foreach arg $argSet {
                                            #puts "-> Arg def: [split $arg /]"
                                            set argType ""
                                            ::extractVars [split $arg /] argName argType
                                            set argType [expr [string length $argType]==0 ? "{String}" : "{$argType}"]
                                            #puts "-> Arg: $argName : $argType"
                                            :argument $argName {
                                                :type set $argType
                                            }
                                        }

                                    }
                                    

                                }
                            }]
                        } else {
                            return [:method [lindex $args 0] {
                                :returnType set [[lindex $args end]

                                #puts "Called DEF with 6 args: [lindex $args 1] "

                                ## Set arguments 
                                set arguments [lindex $args 1]
                                foreach argSet $arguments {

                                    :argumentSet {

                                        foreach arg $argSet {
                                            #puts "-> Arg def: [split $arg /]"
                                            set argType ""
                                            ::extractVars [split $arg /] argName argType
                                            set argType [expr [string length $argType]==0 ? "{String}" : "{$argType}"]
                                            #puts "-> Arg: $argName : $argType"
                                            :argument $argName {
                                                :type set $argType
                                            }
                                        }

                                    }
                                    

                                }
                            }]
                        }


                    }

                    
                    5 {
                        ## def name : RT = body
                        return [:method [lindex $args 0] {
                            :returnType set [lindex $args 2]
                            :body set [odfi::richstream::template::stringToString [lindex $args end]]

                            #puts "Set Return type: [:returnType get]"
                        }]
                    }

                   
                    6 {
                         ## def name args : RT = body
                        return [:method [lindex $args 0] {
                            :returnType set [lindex $args 3]
                            :body set [odfi::richstream::template::stringToString [lindex $args end]]

                            #puts "Called DEF with 6 args: [lindex $args 1] "

                            ## Set arguments 
                            set arguments [lindex $args 1]
                            foreach argSet $arguments {

                                :argumentSet {

                                    foreach arg $argSet {
                                        #puts "-> Arg def: [split $arg /]"
                                        set argType ""
                                        ::extractVars [split $arg /] argName argType
                                        set argType [expr [string length $argType]==0 ? "{String}" : "{$argType}"]
                                        #puts "-> Arg: $argName : $argType"
                                        :argument $argName {
                                            :type set $argType
                                        }
                                    }

                                }
                                

                            }
                        } ]
                    }
                    default {
                        error "Called def with wrong number of arguments [llength $args]  (3-6), format is: :def name args? : returnType (= body)?"
                    }

                }

                

            }

           
            ## Code Output 
            ############################

            +method writeToSourceFolder folder {

                    ## Target File 
                    set targetFile $folder/[:name get].scala 

                    #puts "Writing Trait to $targetFile, with main line: [[:parent] info class]"


                    set localPackage [[:shade scala::Package getPrimaryParents] at 0]

                    ## Prepare parent classes / traits 
                    ###########
                    
                    #set parentsList [[odfi::flist::MutableList fromList [:traits get]] map {$it name get}]
                    #set parentsList [odfi::flist::MutableList new]
                    set parentsList         [:shade scala::Type parents]

                    ## Add Traits from manual add 
                    foreach t [:traits get] {
                        if {$t!=""} {

                            $parentsList import $t
                        }
                    }
                    


                    set parentsStringList   [$parentsList map {

                            set pPackages [$it shade scala::Package getPrimaryParents]
                            if {[$pPackages size]>0} {


                                set pPackage [$pPackages at 0]
                                if {[$pPackage name get]!=[$localPackage name get]} {
                                    return  [$pPackage name get].[$it name get]
                                } else {
                                    return  [$it name get]
                                }
                            } else {
                                #puts "**** Straight parent in [:name get]: [$t name get]"
                                return [$it name get]
                            }
                        

                    }]
                    #$parentsList += :shade scala::Type parents
                    #set parentsList [[:shade scala::Type parent] map {$it name get}]
                    

                    
                    

                    ## Propagate Type Parameters
                    #######################
                    #puts "TEsting for F-Bound: [lindex [:traits get] 0]  "
                    if {[$parentsList size]>0} {
                       
                        #set fi [lindex [:traits get] 0]

                        #puts "FI: [$fi info class]"

                        

                        ## Look for parents' TP and fetch them
                        set i 0
                        $parentsList foreach {
                            foreach parentTP [$it typeParameters get] {
                                #puts "FOUND PARENT TP $parentTP at $i" 
                                #$tpList addFirst $parentTP

                                ## If TP is not mapped, Propagate, otherwise map 
                                if {[:getTypeParameterMap $parentTP]==""} {
                                    :addTypeParameter $parentTP
                                    $parentsStringList setAt $i [concat [$parentsStringList at $i] [string map {+ ""} [lindex [split $parentTP " "] 0]]] 
                                } else {
                                    #puts "Inside ${:name}, there is a TP map -> [:getTypeParameterMap $parentTP] "
                                    set mappedValue [lindex [split [:getTypeParameterMap $parentTP] " "] 0]
                                    $parentsStringList setAt $i [concat [$parentsStringList at $i] "[string map {+ ""} $mappedValue]" ] 
                                }
                                
                            }

                            incr i
                        }

                        ## Propagate FBound if not final, otherwise Propagate Self (always at the end)
                        set firstParent [$parentsList at 0]
                        if {[$firstParent isFBounded] && ![:final get]} {
                            :setFBounded
                        }



                    }

                    ## Prepare Type Parameters 
                    ##############
                    ## Normal TP list 
                    set tpList [odfi::flist::MutableList fromList [:typeParameters get]]
                    ## instance TP List
                    set instTpList [[odfi::flist::MutableList fromList [:typeParameters get]] map {

                        return [lindex [split $it " "] 0]
                    }]

                    ## Add FBound at the end 
                    #puts "F-Bound: [:isFBounded] "
                    if {[:isFBounded] && ![:final get]} {
                        $tpList += +Self
                    }

                    ## Make TP String
                    set tpString [$tpList mkString {"\[" "," "\]"}]
                    set instTpString [$instTpList mkString {"\[" "," "\]"}]


                    ## Propagate FBound TP if not a leaf, otherwise Propagate Self (always at the end)
                    ## Create  this now after all local TP have been set
                    if {[$parentsList size]>0} {
                        $parentsList foreach {
                            {parent i} => 

                                #set firstParent [$parentsList at 0]
                                if {[$parent isFBounded] && [:final get]} {

                                    ## Prepare local Name
                                    $parentsStringList setAt $i [concat [$parentsStringList at $i] [string map {+ ""} "${:name}$instTpString"]]
                                } elseif {[$parent isFBounded]} {
                                    $parentsStringList setAt $i [concat [$parentsStringList at $i] Self]
                                }

                        }
                        
                    }

                    ## Remap Parent String  for type parameters 
                    ##############
                    ## First: Map content to Form type parameters 
                    set parentsStringList [$parentsStringList map {
                        if {[llength $it]==1} {
                            return $it 
                        } else {
                            return [lindex $it 0]\[[join [lrange $it 1 end] ,]\]
                        }
                    }]

                    ## Constructor Fields
                    ##############

                    ## Local
                    set constructorFields [[:shade scala::Field children] filter {$it constructor get}]
                #
                    set constructorFields [[$constructorFields map {
                        if {[$it override get]==true} {
                            return "[$it name get] : [$it type get] "
                        } else {
                            return "var [$it name get] : [$it type get] "
                        }
                        
                        
                    }] ]

                    ## From Parent 
                    set parentConstructorArgs ""
                    if {[$parentsList size] > 0} {

                        set parent [$parentsList at 0]

                        set parentConstructorFields [[$parent shade scala::Field children] filter {$it constructor get}] 

                        ## import Fields which don't have a set value, otherwise jsut set the value
                        set parentConstructorArgs [odfi::flist::MutableList new]
                        $parentConstructorFields foreach {
                            set mappedValue [:getParentConstructorField [$it name get]]
                            if {$mappedValue==""} {
                                $constructorFields += "[$it name get] : [$it type get]"
                                $parentConstructorArgs += "[$it name get]"
                            } else {
                                $parentConstructorArgs += "[$it name get] = \"$mappedValue\""
                            }
                        }
                        #$constructorFields import [$parentConstructorFields map {return "[$it name get] : [$it type get]"}]
                        set parentConstructorArgs  [$parentConstructorArgs mkString { "( " " , " " )" } ]
                        
                        $parentsStringList setAt 0 [$parentsStringList at 0]$parentConstructorArgs
                    }
                    set constructorFieldsString [$constructorFields mkString {"( " " ,"  " )"}]


                    ## Make parent String 
                    #############################
                    
                    set parentsString [$parentsStringList mkString {"extends " " with " ""}]

                    #puts "Writing ${:name} with traits: $parentsString"

                   


                    ## Write <% return  %> 
                    ################
                    odfi::richstream::template::stringToFile {

package <% return [:shade scala::Package formatHierarchyString {return [$it name get] } .] %> 

<% return [string trim [expr "[:abstract get]" == true ? "{abstract}" : "{}"]] %> <% return [string tolower [:+getClassSimpleName]] %> <% return ${:name}$tpString$constructorFieldsString %> <% return $parentsString %>  {

    <%
        if {[:isFBounded] && ![:final get]} {
            return "this:Self => "
        } else {
            return ""
        }
    %>


    // Class Fields 
    //---------------------
    <%
    set fields [[:shade scala::Field children] filterNot { $it constructor get}]
    return [[$fields map {

            set fieldType ""
            set private   ""
            if {[$it visibility get]=="private"} {
                set private "private"
            }
            if {[$it type get] != "-"} {
                set fieldType ": [$it type get]"
            }

            set default [$it default get]
            if {[llength $default]>1 || [$it type get]=="String"} {
                set default "\"$default\""
            }
            return [string trim "$private var [$it name get] $fieldType = $default"]
            
        }] mkString "\\n\\t"]

    %>

    // Init Section 
    //----------------------
    <%

    set initSections [:shade scala::InitSection children]
    set initsMapped [$initSections map {
            return [$it body get]    
    }]
    set str  [$initsMapped mkString "\\n\\t"]
    #::puts "Init Result in ${:name} -> $str"
    return $str
    

    %>

    // Methods
    //------------------
    <%
        set methods [:shade scala::Method children]

        return [[$methods map {

            ## Args Set
            set argumentSets [[$it shade scala::ArgumentSet children] map {

                ## Argument String 
                set argsMapped [[$it shade scala::Argument children] map {
                    return "[$it name get] : [$it type get]"
                }]
                #set argsString [$argsMapped mkString {"(" "," ")"}]

                return [$argsMapped mkString {"(" "," ")"}]
            }]
            set argumentSetsString [$argumentSets mkString {""}]
            set returnType ""
            if {[$it returnType get] != "-"} {
                set returnType ": [$it returnType get]"
            }

            set override [expr [$it override get]==true ? "{override }" : "{}"]
            
            if {[$it body get]=="{}"} {

                return "${override}def [$it name get]$argumentSetsString $returnType"
             } else {
                return "${override}def [$it name get]$argumentSetsString $returnType = {[$it body get]}"
             }
            
        }] mkString "\\n\\t"]
        

    %>

    // Imported Content 
    //----------------------
    <%
    set scriptFolder [file dirname [info script]]
    set bodyFile     $scriptFolder/${:name}.body.scala
    if {[file exists $scriptFolder/${:name}.body.scala]} {
        puts "// Imported from $bodyFile"
        puts [odfi::files::readFileContent $bodyFile]

    }
   # ::puts "Inside script: [info script]"
    %>
}


                    } $targetFile 


                    ## Write Children out 
                    :shade scala::Type eachChild {
                        #puts "Writing out"
                        $it writeToSourceFolder $packageFolder
                    }
                }
                ## EOF Write to source folder


        }

        :package name  {
            
            +exportToPublic
            +exportTo Package
           

            :trait : Type name {

                +exportTo Trait
            

                +method constructorField {name {cl {}}} {
                    error "Constructor Fields are not allowed in traits!"
                }

            }
            ## EOF Trait
            
            ## Class 
            ################################
            :class : Type name {

                +exportTo Class
                +exportTo Trait


            
            }


            :obj : Type name {

            }


            ## Package Write
            +method writeToSourceFolder tfolder {

                ## Create Folder 
                ::set folder [file normalize $tfolder]
                file mkdir $folder

                set fullName [join [concat [:shade scala::Package formatHierarchyString {return [$it name get] } .] [:name get]] .]
                puts "Package $fullName, current folder: $folder , lcoal name is [:name get]"
                if {[:parent]!=""} {
                    puts ", parent is [[:parent] name get]"
                }

                ## Create Package Folder 
                #set packageFolder $folder/[join [split $fullName .] /]
                ::set packageFolder $folder/[join [split [:name get] .] /]
                file mkdir $packageFolder

                ## Write Children out 
                :eachChild {
                    #puts "Writing out"
                    $it writeToSourceFolder $packageFolder
                }


            }

        }
        




    }

}
