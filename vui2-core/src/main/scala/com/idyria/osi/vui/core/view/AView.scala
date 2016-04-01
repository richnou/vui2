
package com.idyria.osi.vui.core.view

import java.io.File
import java.net.URL
import com.idyria.osi.aib.core.compiler.SourceCompiler
import com.idyria.osi.aib.core.utils.files.FileWatcher
import com.idyria.osi.tea.file.DirectoryUtilities
import com.idyria.osi.tea.io.TeaIOUtils
import com.idyria.osi.tea.listeners.ListeningSupport
import com.idyria.osi.tea.logging.TLogSource
import com.idyria.osi.vui.core.definitions.VUISGNode
import java.net.URLClassLoader
import scala.reflect.io.AbstractFile
import org.xml.sax.helpers.NewInstance
import com.idyria.osi.tea.compile.IDCompiler
import com.idyria.osi.tea.compile.ClassDomainSupport

/**
 *
 */
class AView[BT, T <: VUISGNode[BT, _]] extends TLogSource with ListeningSupport {

  // Recompilation interface
  //---------------
  def replaceWith(v: Class[_ <: AView[BT, _ <: VUISGNode[BT, _]]]) = {
    println(s"Requesting Change!")
    this.@->("view.replace", v)
  }

  // Tree
  //------------------
  var parentView: Option[AView[BT, _]] = None

  def getTopParentView = {
    var currentView: AView[BT, _] = this
    while (currentView.parentView != None)
      currentView = currentView.parentView.get

    currentView
  }

  // Content/ Render
  //----------------
  var contentClosure: AView[BT, T] ⇒ T = null

  var renderedNode: Option[T] = None

  def viewContent(cl: => T) = {
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
        logFine[AView[BT, T]](s"[RW] Rendering view: " + this.hashCode)
        var node = contentClosure(this)
        renderedNode = Some(node)
        this.@->("rendered", node)
        node
    }

  }

  def rerender: T = {
    this.renderedNode = None
    this.render
  }

}

abstract class AViewCompiler2[T <: AView[_, _]] extends ClassDomainSupport {

}

class AViewCompiler[BT, T <: AView[BT, _ <: VUISGNode[BT, _]]] extends ClassDomainSupport {

  implicit def viewToSGNode(v: AView[BT, _ <: VUISGNode[Any, _]]): VUISGNode[Any, _] = v.render

  // The Actual Live Compiler and its setup
  //------------------
  var idcompiler = new IDCompiler
  var tempSourceFolder = new File("target/generated-views")
  var outputClassesFolder = new File("target/generated-views-classes")

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

  var fileWatcher = new FileWatcher
  fileWatcher.start

  /**
   * Init Compiler and stuff
   */
  def initCompiler = {
    println(s"INITING COMPILER")
    //-- Create Temp Source Folder and Output
    this.tempSourceFolder.mkdirs()
    this.outputClassesFolder.mkdirs()

    DirectoryUtilities.deleteDirectoryContent(this.tempSourceFolder)
    DirectoryUtilities.deleteDirectoryContent(this.outputClassesFolder)

    //-- Setup Compiler 
    idcompiler.addSourceOutputFolders(tempSourceFolder -> outputClassesFolder)

    //compiler.settings2.outputDirs.add(this.tempSourceFolder.getAbsolutePath, this.outputClassesFolder.getAbsolutePath)
    //compiler.settings2.outputDirs.setSingleOutput(this.outputClassesFolder.getAbsolutePath)
    //compiler.settings2.outdir.default
    //compiler.settings2.outputDirs.setSingleOutput(this.outputClassesFolder.getPath)
    //compiler.settings2.outputDirs.setSingleOutput(AbstractFile.getDirectory(this.outputClassesFolder.getAbsoluteFile))
    //compiler.outputClassesFolder = this.outputClassesFolder
    //compiler.settings2.outputDirs.add(this.tempSourceFolder.getAbsolutePath, this.outputClassesFolder.getAbsolutePath)

    //compiler.recreate
    //enforceClassLoader()
  }

  /**
   * This Method makes sure the Current Thread Classloader has the Output Path into its output URLS
   * If not, it will force updating the classloader
   *
   * If a target class is provided, and this class is already into a ClassDomain, the current Thread ClassLoader is resolved to make sure it is correctly set
   */
  /*def enforceClassLoader(targetClass: Option[Class[_]] = None) = {

    //-- Add Output to CurrentClassLoader, or Update ClassLoader
    Thread.currentThread().getContextClassLoader match {
      case cl: ExtensibleURLClassLoader =>

        /*var eout = new File("eout")
      eout.mkdirs()
      compiler.settings2.outputDirs.setSingleOutput(eout.getAbsolutePath)
      
      println(s"Adding output to cl: "+this.compiler.settings2.outputDirs.getSingleOutput.get.file)*/
        // cl.addURL(new File(this.compiler.settings2.outdir.value).getAbsoluteFile.toURI().toURL())

        // cl.addURL(this.compiler.settings2.outputDirs.getSingleOutput.get.file.getAbsoluteFile.toURI().toURL())
        cl.addURL(this.outputClassesFolder.toURI().toURL())
      case other =>

        var cl = new ExtensibleURLClassLoader(Thread.currentThread().getContextClassLoader)
        cl.addURL(this.outputClassesFolder.toURI().toURL())
        Thread.currentThread().setContextClassLoader(cl)

      //println(s"Single Output: "+this.compiler.settings2.outputDirs.getSingleOutput.get.path)
      //cl.addURL(this.compiler.settings2.outputDirs.getSingleOutput.get.file.getAbsoluteFile.toURI().toURL())

    }
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

  def createView(cl: Class[_ <: T], listen: Boolean = true): T = {

    //-- Swtich CL 
    //var startTHCL = Thread.currentThread().getContextClassLoader
    //try {

    // Thread.currentThread().setContextClassLoader(cl.getClassLoader)
    /*cl.getClassLoader match {
      case cl: URLClassLoader =>
        println(s"Adding Support of Class Classloader to compilter")
        compiler.addCLSupport(cl)
      case _ =>
    }*/

    var targetClassFileName = cl.getCanonicalName.replace(".", File.separator) + ".scala"
    var targetFile = new File(new File("src/main/scala"), targetClassFileName)

    println(s"Looking for file: " + targetFile.getAbsolutePath + "-> " + targetFile.exists())

    targetFile = targetFile.exists() match {
      case false =>
        cl.getClassLoader match {
          case cl: URLClassLoader =>
            println(s"No File for class, but it is in a URL classloader, there is a change teh sources might not be local")
            cl.getURLs.foreach {
              u =>
                println(s"--- Available URL: -> $u -> ${u.getProtocol.startsWith("file")} && ${u.getPath} && ${new File(u.getPath).isDirectory()}")
            }

            cl.getURLs.find { url => url.getProtocol.startsWith("file") && url.getPath.endsWith("classes/") && new File(url.getPath).isDirectory() } match {
              case Some(url) =>
                println(s"--> Seems to be a good canditate -> " + url)

                //-- Search in target/classes/../../src/main/scala/path/to/class.scala
                var sourceFolderFile = new File(new File(url.getPath).getParentFile.getParentFile, List("src", "main", "scala").mkString(File.separator)).getAbsoluteFile

                var newFile = new File(sourceFolderFile, targetClassFileName).getAbsoluteFile
                println(s"--> looking into $newFile")
                newFile.exists() match {
                  case true =>
                    var outputFolderFile =
                      idcompiler.addSourceOutputFolders(sourceFolderFile -> new File(url.getPath))
                    newFile
                  case false => targetFile
                }

              case None =>
                targetFile
            }
          case _ =>
            targetFile
        }

      case true => targetFile
    }

    targetFile.exists() match {
      case true =>

        // Compile View and create Instance
        // Enforce Classloader during compilation
        //------------------
        this.withClassLoaderFor(cl) {
          this.withURLInClassloader(this.outputClassesFolder.toURI().toURL) {

            var viewClass = this.doCompile(targetFile.toURI().toURL())
            var view = viewClass.newInstance().asInstanceOf[T]

            // Set for change watch
            if (listen) {

              fileWatcher.onFileChange(targetFile) {
                try {

                  // Recompile
                  this.withClassLoaderFor(viewClass) {
                    var newClass = this.doCompile(targetFile.toURI().toURL())

                    // Update
                    view.replaceWith(newClass.asInstanceOf[Class[T]])
                  }

                } catch {
                  case e: Throwable =>
                    e.printStackTrace()
                }
              }
            }

            // Return new Instance
            view
          }
        }

      case false => cl.newInstance()
    }
    /*} finally {
      Thread.currentThread().setContextClassLoader(startTHCL)
    }*/

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
    var originalName = source.getPath.replace(".scala", "").split("/").last.replace(".", "_").map {
      case '.' => "_"
      case '/' => "_"
      case c => c
    }.mkString
    var targetName = originalName + "_" + System.currentTimeMillis()

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
        //TeaIOUtils.writeToFile(new File("test.scala"), newContent)
        //new File("test.scala")

        var outputFile = new File(this.tempSourceFolder, s"$originalName.scala")
        TeaIOUtils.writeToFile(outputFile, newContent)
        outputFile

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

        var outputFile = new File(this.tempSourceFolder, s"$originalName.scala")
        TeaIOUtils.writeToFile(outputFile, viewString)
        outputFile

    }

    /*logFine[AView[T]](s"VIEW IS AT: "+source.getPath)
    var targetName = source.getPath.split("/").last.replace(".", "_")*/

    println(s"***** Settings: " + idcompiler.settings2.hashCode())

    //compiler.settings2.outputDirs.
    //compiler.settings2.outputDirs.add(this.tempSourceFolder.getAbsolutePath, this.outputClassesFolder.getAbsolutePath)
    println(s"WWWCompiler for $source => ${idcompiler.settings2.outputDirs.outputs}")

    //var cl = new URLClassLoader(Array[URL]())

    // Compile and return 
    //------------

    // Get Class Name

    var packageName = """package ([\w0-9\._]+)""".r.findFirstMatchIn(scala.io.Source.fromFile(fileToCompile).mkString).get.group(1)

    this.idcompiler.compileFiles(Seq(fileToCompile)) match {
      case Some(error) =>
        println(s"Error: " + error.message);
        throw new RuntimeException(s"Failed for $source : " + error.message.toString())
      case None =>
        println(s"Success by compile")

        try {
          var resClass = Thread.currentThread.getContextClassLoader.loadClass(s"$packageName.$targetName")

          //resClass.asSubclass(Thread.currentThread.getContextClassLoader.loadClass(T))
          println(s"ResClass SuperClass: " + resClass.getSuperclass.getCanonicalName)
          //resClass.newInstance().asInstanceOf[T]
          resClass.asInstanceOf[Class[T]]

        } catch {
          case e: ClassNotFoundException =>
            println(s"Could not find class")
            Thread.currentThread.getContextClassLoader.asInstanceOf[URLClassLoader].getURLs.foreach {
              url =>
              //println(s"Available source: $url")
            }
            throw e
        }
    }

  }

}

