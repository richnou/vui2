
package com.idyria.osi.vui.core.view

import java.net.URL
import com.idyria.osi.aib.core.utils.files.FileWatcher
import com.idyria.osi.tea.logging.TLogSource
import com.idyria.osi.tea.io.TeaIOUtils
import com.idyria.osi.aib.core.compiler.SourceCompiler
import com.idyria.osi.vui.core.definitions.VUISGNode
import com.idyria.osi.tea.listeners.ListeningSupport
import java.io.File
import java.net.URLClassLoader


/**
 *
 */
class AView[BT,T <: VUISGNode[BT,_]] extends TLogSource with ListeningSupport {

  

  // Recompilation interface
  //---------------
  def replaceWith(v: AView[BT,_ <: VUISGNode[BT,_]]) = {
    println(s"Requesting Change!")
    this.@->("view.replace", v)
  }

  // Parts
  //---------------
  /*
  var parts = Map[String, AView[T]]()

  def part(name: String)(cl: (WebApplication, HTTPRequest) => HTMLNode): AView[T] = {

    //-- Create AView[T] for part
    var p = new AView[T] {
      this.contentClosure = { v ⇒ cl(v.application, v.request) }
    }

    logFine[AView[T]](s"[RW] Inside view: $hashCode , registering part $name with prt View ${p.hashCode}")

    //logFine[AView[T]](s"Saving part: " + name+" -> "+p.hashCode+" -> "+p.contentClosure.hashCode())

    //-- Save
    parts = parts + (name -> p)

    this

  }*/

  /*
  /**
   * This method creates the part, renders it with current request, and place it into the result tree
   * The ID of resulting HTML node is set to part-$name
   */
  def placePart(name: String): HTMLNode = {

    logFine[AView[T]](s"[RW] Inside view: $hashCode looking up part $name")

    //- Create
    var view = parts.get(name) match {
      case Some(partView) => partView
      case None => throw new RuntimeException(s"Cannot place part $name because it has not been defined")
    }

    //- Render
    var resNode = view.render(application, request)
    resNode("id" -> s"part-$name")
    add(resNode)
    resNode


  }
*/
  // View  Composition
  //------------------

  var composedViews = List[AView[BT,T]]()

  /**
   * Shortcut to load a View File and create a view from it
   * The path is mapped to URL using application resource search
   */
  /* def compose(path: String): AView[T] = {

    application.searchResource(path) match {
      case Some(url) ⇒

        var parentView = compiler.compile(url).newInstance().asInstanceOf[AView[T]]
        parentView.application = this.application
        parentView.request = this.request

        logFine[AView[T]](s"[RW] Inside view: $hashCode , composing with $path -> ${parentView.hashCode}")

        // parentView.contentClosure(parentView)

        composedViews = composedViews :+ parentView
        parentView.nodesStack = this.nodesStack

        parentView
      /* this.contentClosure = parentView.contentClosure
        this.render
        this*/
      case None ⇒ throw new ViewRendererException(s"Could not render current view because searched view @$path could not be found ")
    }

  }*/

  def compose(baseClass: Class[_ <: AView[BT,T]]): AView[BT,T] = {

    // Instanciate
    //-------------
    var instance = baseClass.newInstance()

    // Set parameters
    //---------
    //instance.application = this.application
    //instance.request = this.request

    // Return
    //-----------
    instance
  }

  // Content/ Render
  //----------------
  var contentClosure: AView[BT,T] ⇒ T = null

  var renderedNode: Option[T] = None

  def content(cl: => T) = {
    this.contentClosure = {
      v => cl
    }
  }

  /**
   * Record Content Closure
   */
  /*def apply(cl: AView[T] => VUISGNode[Any,_]) = {
    this.contentClosure = cl
  }*/

  def render: T = {

    renderedNode match {
      case Some(n) => n
      case None =>
        logFine[AView[BT,T]](s"[RW] Rendering view: " + this.hashCode)
        var node = contentClosure(this)
        renderedNode = Some(node)
        this.@->("rendered", node)
        node
    }

    

  }
  
  def rerender : T = {
    this.renderedNode = None
    this.render
  }

}

abstract class AViewCompiler[BT,T <: AView[BT,_ <: VUISGNode[BT,_]]] extends SourceCompiler[Class[T]] {

  implicit def viewToSGNode(v: AView[BT,_ <: VUISGNode[Any,_]]): VUISGNode[Any,_] = v.render

  // Configured Imports
  //---------------
  var compileImports = List[Class[_]]()
  var compileImportPackages = List[Package]()

  def addCompileImport(cl: Class[_]): Unit = {
    compileImports.contains(cl) match {
      case false ⇒ compileImports = compileImports :+ cl
      case _ ⇒
    }
  }

  def addCompileImport(p: Package): Unit = {
    compileImportPackages.contains(p) match {
      case false ⇒ compileImportPackages = compileImportPackages :+ p
      case _ ⇒
    }
  }

  var compileTraits = List[Class[_]]()

  // Compiler Setup
  //---------------------

  //-- Add its output to URL classloader 
  /*Thread.currentThread().getContextClassLoader match {
    case cl : AIBApplicationClassloader => 
      
      var eout = new File("eout")
      eout.mkdirs()
      compiler.settings2.outputDirs.setSingleOutput(eout.getAbsolutePath)
      
      println(s"Adding output to cl: "+this.compiler.settings2.outputDirs.getSingleOutput.get.file)
     // cl.addURL(new File(this.compiler.settings2.outdir.value).getAbsoluteFile.toURI().toURL())
      
      cl.addURL(this.compiler.settings2.outputDirs.getSingleOutput.get.file.getAbsoluteFile.toURI().toURL())
    case _ => 
  }*/

  /**
   * Add Trait as compile trait, and also as Import
   */
  def addCompileTrait(cl: Class[_]) = {

    //-- Add To compile traits
    compileTraits = (compileTraits :+ cl).distinct
    /*compileTraits.contains(cl) match {
      case false ⇒ compileTraits = compileTraits :+ cl
      case _ ⇒
    }*/

    //-- Add to imports
    addCompileImport(cl)
  }

  // Instances Pool
  //-----------------------
  //def getInstance(source:URL)

  // Magic Compilation Find
  //--------------------

  var fileWatcher = new FileWatcher
  fileWatcher.start

  def createView(cl: Class[_ <: T], listen: Boolean = true): T = {

    var targetFile = new File(new File("src/main/scala"), cl.getCanonicalName.replace(".", File.separator) + ".scala")
    println(s"Looking for file: " + targetFile.getAbsolutePath + "-> " + targetFile.exists())

    targetFile.exists() match {
      case true =>

        // Create View
        var v = this.compile(targetFile.toURI().toURL()).newInstance()

        // Set for change watch
        if (listen) {
          fileWatcher.onFileChange(targetFile) {
            v.replaceWith(this.createView(cl, false))
          }
        }

        v.asInstanceOf[T]

      case false => cl.newInstance()
    }

  }

  // Compilation
  //-----------------
  def doCompile(source: URL): Class[T] = {

    //this.compiler = new EmbeddedCompiler

    /*logFine[AView[T]](s"In AView[T] domCpile -> "+Thread.currentThread().getId)
    
    Thread.currentThread().getContextClassLoader.getParent match {
      case urlcl : URLClassLoader => 
        urlcl.getURLs.foreach {
          url => logFine[AView[T]](s"----> compilingin with: "+url)
        }
        
      case _ => 
    }*/

    // Class Loading
    //---------------
    //var currentCL = Thread.currentThread().getContextClassLoader

    // File Name
    //--------------
    var targetName = source.getPath.replace(".scala", "").split("/").last.replace(".", "_").map {
      case '.' => "_"
      case '/' => "_"
      case c => c
    }.mkString
    targetName = targetName + "_" + System.currentTimeMillis()

    // If the file ends with Scala, assume it is complete
    //-----------------------------
    var fpath = source.getPath
    var fileToCompile = fpath match {

      //-- File is ready, just read content, and replace class name with target name
      case path if (path.endsWith("scala")) =>

        // Init content
        var content = scala.io.Source.fromInputStream(source.openStream).mkString

        // Get Class Name
        var typeName = """class ([\w0-9_]+)""".r.findFirstMatchIn(content).get.group(1)

        // Replace 
        var newContent = content.replaceAll(s"""^?$typeName(\\s|\\.)""", targetName + "$1")
        //var newContent = content.replaceFirst("""class ([\w0-9_]+) """,s"class $targetName ")
        // newContent = content.replaceFirst("""object ([\w0-9_]+) """,s"object $targetName ")

        // Write
        TeaIOUtils.writeToFile(new File("test.scala"), newContent)
        new File("test.scala")

      //-- File is incomplete, create a compilable version
      case path =>

        var closureContent = scala.io.Source.fromInputStream(source.openStream).mkString

        //-- Prepare traits
        var traits = this.compileTraits.size match {
          case 0 => ""
          case _ => this.compileTraits.map(cl ⇒ cl.getCanonicalName()).mkString("with ", " with ", "")
        }

        var viewString = s"""
        
      package wwwviews
    
        import com.idyria.osi.wsb.webapp.view._  
        import  com.idyria.osi.wsb.webapp.injection.Injector._
        import com.idyria.osi.wsb.webapp.injection._
        
        ${this.compileImports.map { i ⇒ s"import ${i.getCanonicalName()}" }.mkString("\n")}
        
        ${this.compileImportPackages.map { p ⇒ s"import ${p.getName()}._" }.mkString("\n")}
        
        class $targetName extends AView[T] $traits {    
        
          
       //   this.viewSource = \"$source\"
      
        this.contentClosure = {
            view =>  
              
              $closureContent
          
          }
        
      }
        
        """
        TeaIOUtils.writeToFile(new File("test.scala"), viewString)
        new File("test.scala")

    }

    /*logFine[AView[T]](s"VIEW IS AT: "+source.getPath)
    var targetName = source.getPath.split("/").last.replace(".", "_")*/

    println(s"WWWCompiler for $source => ${this.compiler.settings2.outdir.value}")

    // Read Content of file
    //---------
    /*  var closureContent = scala.io.Source.fromInputStream(source.openStream).mkString

    // Compile as Object
    //------------------------------

    //logFine[AView[T]](s"Adding imports: ${compileImports.map { i ⇒ s"import ${i.getCanonicalName()}" }.mkString("\n")}")
    // logFine[AView[T]](s"Adding traits: ${ compileTraits.map(cl ⇒ cl.getCanonicalName()).mkString("with ", " with ", "")}")

    //-- Prepare traits
    var traits = AView[T].compileTraits.size match {
      case 0 => ""
      case _ => AView[T].compileTraits.map(cl ⇒ cl.getCanonicalName()).mkString("with ", " with ", "")
    }

    var viewString = s"""
    
  package wwwviews

    import com.idyria.osi.wsb.webapp.view._  
    import  com.idyria.osi.wsb.webapp.injection.Injector._
    import com.idyria.osi.wsb.webapp.injection._
    
    ${AView[T].compileImports.map { i ⇒ s"import ${i.getCanonicalName()}" }.mkString("\n")}
    
    ${AView[T].compileImportPackages.map { p ⇒ s"import ${p.getName()}._" }.mkString("\n")}
    
    class $targetName extends AView[T] $traits {    
    
      
   //   this.viewSource = \"$source\"
  
    this.contentClosure = {
        view =>  
          
          $closureContent
      
      }
    
  }
    
    """
    TeaIOUtils.writeToFile(new File("test.scala"), viewString)
    // Compile as Clousre, and apply to a new AView[T]
    //---------------
    /*var closure = s"""    
v.contentClosure =  { view => 
   $closureContent
}
"""

    var wwwview = new AView[T]
    //sview.sourceURL = source

    compiler.bind("v", wwwview)

    //compiler.compile(new File(source.getFile))
    try {

      compiler.interpret(closure)

    } catch {
      case e: Throwable ⇒

        logFine[AView[T]](s"Compilation error in SView source file: @$source")
        throw new ViewRendererException(s"An error occured while preparing SView @$source: ${e.getMessage()}", e)
    }*/
    //
*/
    var cl = new URLClassLoader(Array[URL]())

    // Compile and return 
    //------------

    // Get Class Name
    var packageName = """package ([\w0-9\._]+)""".r.findFirstMatchIn(scala.io.Source.fromFile(new File("test.scala")).mkString).get.group(1)

    this.compiler.compileFiles(Seq(fileToCompile)) match {
      case Some(error) => throw throw new RuntimeException(s"Failed for $source : " + error.message.toString())
      case None =>

        Thread.currentThread.getContextClassLoader.loadClass(s"$packageName.$targetName").asInstanceOf[Class[T]]
    }

  }

  /*compiler.interpret(viewString)

    compiler.imain.valueOfTerm("viewInstance") match {
      case None =>

        throw new RuntimeException("Nothing compiled: " + compiler.interpreterOutput.getBuffer().toString())

      case Some(wwwview) =>
        wwwview.asInstanceOf[AView[T]]
    }*/

  /* var wwwview = compiler.imain.valueOfTerm("viewInstance").get.asInstanceOf[AView[T]]

    logFine[AView[T]]("Compiling view: " + source + " to " + wwwview.hashCode())

    // Save as compiled Source
    //------------

    wwwview*/

}

