
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
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import com.idyria.osi.tea.errors.ErrorSupport

/**
 *
 */
class AView[BT, T <: VUISGNode[BT, _]] extends TLogSource with ListeningSupport with ErrorSupport {

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
    var currentView: AView[BT, _] = getProxy[AView[BT, _]].get
    while (currentView.parentView != None)
      currentView = currentView.parentView.get

    currentView
  }
  
  // Proxy
  //-----------------  
  
  var proxy : Option[_ <: AView[BT, _]] = None
  
  /**
   * Returns the Proxy or itself if excluseSelf not set
   */
  def getProxy[VT <: AView[BT, _]](implicit tag: ClassTag[VT]) : Option[VT] = {
   
    proxy match {
      // Return proxy or self if it is the right type
      case Some(proxied) if(tag.runtimeClass.isInstance(proxied)) => Some(proxied.asInstanceOf[VT])
      case None if (tag.runtimeClass.isInstance(this)) => Some(this.asInstanceOf[VT])
      case None => None
    }
    
  }
  
  // Classloader information
  //---------------------
  def getClassLoader = getClass.getClassLoader

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

  }

  /**
   * Add Trait as compile trait, and also as Import
   */
  def addCompileTrait(cl: Class[_]) = {

    //-- Add To compile traits
    compileTraits = (compileTraits :+ cl).distinct

    //-- Add to imports
    addCompileImport(cl)
  }

  // Instances Pool
  //-----------------------
  //def getInstance(source:URL)

  // Magic Compilation Find
  //--------------------
  var standardSearchPath = new File("src/main/scala")

  def createView[VT <: T](refObject: Option[VT], cl: Class[VT], listen: Boolean = true)(implicit tag: TypeTag[VT]): VT = {

    var targetClassFileName = cl.getCanonicalName.replace(".", File.separator) + ".scala"
    var targetFile = new File(standardSearchPath, targetClassFileName)

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
            var view = this.newInstance(refObject, viewClass.asInstanceOf[Class[VT]])

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

      case false => this.newInstance(refObject, cl)
    }
    /*} finally {
      Thread.currentThread().setContextClassLoader(startTHCL)
    }*/

  }

  def newInstance[VT <: T](refObject: Option[VT], cl: Class[VT])(implicit tag: TypeTag[VT]): VT = {

    println(s"Instanciating $cl with refObject $cl")

    // Try to find a construtor with 0 arguments
    cl.getConstructors.find {
      c => c.getParameterCount == 0
    } match {
      case Some(c) => c.newInstance().asInstanceOf[VT]

      // There are some arguments, so take the first construtor, but the refobject must be set
      case None if (refObject == None) =>
        throw new RuntimeException(s"Cannot instantiate class $cl with non empty constructor and no reference object ")
      case None =>

        var ref = refObject.get 
        
        
        println(s"Class needs constructor arguments, usign the first available constructor, tag is for " + tag.tpe.typeSymbol.name)
        println(s"Type: " + tag.mirror.classSymbol(refObject.get.getClass).toType)
        println(s"Type: " + idcompiler)

        var constructor = cl.getConstructors()(0)
        //var args = (0 until constructor.getParameterCount).map(i => null)

        cl.getDeclaredFields.foreach {
          f =>
            println(s"Param: " + f.getName)
        }

        tag.mirror.classSymbol(refObject.get.getClass).toType.paramLists.foreach {
          lst =>
            lst.foreach {
              s =>
                println(s"Symbol c p:" + s.name)
            }
        }

        // Find Constructor for current class
        // Create Array for constructor

        var args = tag.mirror.classSymbol(refObject.get.getClass).toType.members.collectFirst { case s if (s.name.toString == "<init>") => s.asMethod } match {
          case Some(constructorMember) =>
            println(s"Found Constructor")

            var r = constructorMember.paramLists.flatten.map {
              s =>
                
                // Take value from reference constructor
                var m = ref.getClass.getMethod(s.name.toString)

                println(s"Found parameter: " + s.name.toString() + " -> type" + tag.mirror.runtimeClass(s.typeSignature).asInstanceOf[Class[_]])

                //tag.mirror.runtimeClass(s.typeSignature).cast()
                try {
                  var res = m.invoke(refObject.get)
                  println(s"Reg content:" + res)
                  res
                } catch {
                  case e =>
                    e.printStackTrace()
                    throw e
                }

            }
            r.toArray

          case None => Array[Any]()
        }

        println(s"Res args:" + args.length)

        /*tag.tpe.members.foreach {
          s => 
          
            
            println(s"S: "+s.fullName+ "-> "+s.isConstructor)
            if(s.isConstructor) {
              println(s"--> ${s.asMethod.fullName}")
              s.asMethod.paramLists.foreach {
                lst => 
                  lst.foreach {
                    s => 
                      println(s"Symbol c p:"+s.name)
                  }
              }
            }
           
        }*/

        args.zipWithIndex.foreach {
          case (v, i) =>
            println(s"Arg $i $v , required ${constructor.getParameters()(0)}")
        }

        constructor.newInstance(args(0).asInstanceOf[Object]).asInstanceOf[VT]

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
            println(s"Could not find class: $packageName.$targetName")
            Thread.currentThread.getContextClassLoader.asInstanceOf[URLClassLoader].getURLs.foreach {
              url =>
              //println(s"Available source: $url")
            }
            throw e
        }
    }

  }

}

