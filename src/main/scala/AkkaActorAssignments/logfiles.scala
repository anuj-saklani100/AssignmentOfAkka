package AkkaActorAssignments

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

import java.io.{File, FileWriter}

// companion object for LoggerActor
object LoggerActor{
  case class LogWarn(message:String)
  case class LogInfo(message:String)
  case class Rename_File(name:String)
}
// logging actor
class LoggerActor extends Actor with ActorLogging{
  private val warnLogFile=new File("warn.log")
  private val infoLogFile=new File("info.log")
  private var currentLogFile:File=warnLogFile

  // instantiate the FileWriter----> (Opening closing file every time problem resolved by this)
  private var warnLogFileWriter: FileWriter = _
  private var infoLogFileWriter: FileWriter = _

  // initialization on start
  override def preStart(): Unit = {
    warnLogFileWriter= new FileWriter(warnLogFile,true)
    infoLogFileWriter=new FileWriter(infoLogFile,true)
    log.info("Initiation is started")
  }

  // close the stuff on termination
  override def postStop(): Unit = {
    warnLogFileWriter.close()
    infoLogFileWriter.close()
    log.info("Stopping the process")
  }

  import LoggerActor._  // import the companion object so that we can use its case classes

  override def receive: Receive = {
    case LogWarn(message)=>{
      warnLogFileWriter.write(s"[WARN] ->>> $message")
      warnLogFileWriter.flush()
      log.info(s"The warn message is:->> $message")
    }
    case LogInfo(message)=>{
      infoLogFileWriter.write(s"[INFO] ->>> $message")
      infoLogFileWriter.flush()
      log.info(s"The info message is:->> $message")
    }
    case Rename_File(name)=>{
      // make a new file with new name
      val newFile=new File(name)
      currentLogFile.renameTo(newFile)
      currentLogFile = newFile
      // replying to myself (log actor)
      self ! LogInfo(s"The current file is renamed to: ---> $name")
      log.info(s"The current file renamed to :->> $name")
    }
  }
}


object logfiles extends App{
import LoggerActor._
  val system=ActorSystem("NewSystem")
  val logsystem=system.actorOf(Props[LoggerActor],"logsystem")

  // now send warn and info log messages
  logsystem ! LogWarn("It's a Warn message")
  logsystem ! LogInfo("It's a Info message")

// lets rename the file
  logsystem ! Rename_File("newLog.log")

  // again send some messges
  logsystem ! LogWarn("It's a Warn message1")
  logsystem ! LogInfo("It's a Info message1")
  // terminate  the system
  system.terminate()
}
/* Output:---->
/Users/anujsaklani/Library/Java/JavaVirtualMachines/corretto-1.8.0_372/Contents/Home/bin/java -javaagent:/Applications/IntelliJ IDEA CE.app/Contents/lib/idea_rt.jar=50451:/Applications/IntelliJ IDEA CE.app/Contents/bin -Dfile.encoding=UTF-8 -classpath /Users/anujsaklani/Library/Java/JavaVirtualMachines/corretto-1.8.0_372/Contents/Home/jre/lib/charsets.jar:/Users/anujsaklani/Library/Java/JavaVirtualMachines/corretto-1.8.0_372/Contents/Home/jre/lib/ext/cldrdata.jar:/Users/anujsaklani/Library/Java/JavaVirtualMachines/corretto-1.8.0_372/Contents/Home/jre/lib/ext/dnsns.jar:/Users/anujsaklani/Library/Java/JavaVirtualMachines/corretto-1.8.0_372/Contents/Home/jre/lib/ext/jaccess.jar:/Users/anujsaklani/Library/Java/JavaVirtualMachines/corretto-1.8.0_372/Contents/Home/jre/lib/ext/localedata.jar:/Users/anujsaklani/Library/Java/JavaVirtualMachines/corretto-1.8.0_372/Contents/Home/jre/lib/ext/nashorn.jar:/Users/anujsaklani/Library/Java/JavaVirtualMachines/corretto-1.8.0_372/Contents/Home/jre/lib/ext/sunec.jar:/Users/anujsaklani/Library/Java/JavaVirtualMachines/corretto-1.8.0_372/Contents/Home/jre/lib/ext/sunjce_provider.jar:/Users/anujsaklani/Library/Java/JavaVirtualMachines/corretto-1.8.0_372/Contents/Home/jre/lib/ext/sunpkcs11.jar:/Users/anujsaklani/Library/Java/JavaVirtualMachines/corretto-1.8.0_372/Contents/Home/jre/lib/ext/zipfs.jar:/Users/anujsaklani/Library/Java/JavaVirtualMachines/corretto-1.8.0_372/Contents/Home/jre/lib/jce.jar:/Users/anujsaklani/Library/Java/JavaVirtualMachines/corretto-1.8.0_372/Contents/Home/jre/lib/jfr.jar:/Users/anujsaklani/Library/Java/JavaVirtualMachines/corretto-1.8.0_372/Contents/Home/jre/lib/jsse.jar:/Users/anujsaklani/Library/Java/JavaVirtualMachines/corretto-1.8.0_372/Contents/Home/jre/lib/management-agent.jar:/Users/anujsaklani/Library/Java/JavaVirtualMachines/corretto-1.8.0_372/Contents/Home/jre/lib/resources.jar:/Users/anujsaklani/Library/Java/JavaVirtualMachines/corretto-1.8.0_372/Contents/Home/jre/lib/rt.jar:/Users/anujsaklani/IdeaProjects/AssignmentOfAkka/target/scala-2.12/classes:/Users/anujsaklani/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/com/typesafe/akka/akka-actor_2.12/2.5.13/akka-actor_2.12-2.5.13.jar:/Users/anujsaklani/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/com/typesafe/akka/akka-testkit_2.12/2.5.13/akka-testkit_2.12-2.5.13.jar:/Users/anujsaklani/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/com/typesafe/config/1.3.2/config-1.3.2.jar:/Users/anujsaklani/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/modules/scala-java8-compat_2.12/0.8.0/scala-java8-compat_2.12-0.8.0.jar:/Users/anujsaklani/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/modules/scala-xml_2.12/1.0.6/scala-xml_2.12-1.0.6.jar:/Users/anujsaklani/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.12.7/scala-library-2.12.7.jar:/Users/anujsaklani/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-reflect/2.12.7/scala-reflect-2.12.7.jar:/Users/anujsaklani/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/scalactic/scalactic_2.12/3.0.5/scalactic_2.12-3.0.5.jar:/Users/anujsaklani/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/scalatest/scalatest_2.12/3.0.5/scalatest_2.12-3.0.5.jar AkkaActorAssignments.logfiles
[INFO] [05/11/2023 16:57:25.152] [NewSystem-akka.actor.default-dispatcher-4] [akka://NewSystem/user/logsystem] Initiation is started
[INFO] [05/11/2023 16:57:25.152] [NewSystem-akka.actor.default-dispatcher-4] [akka://NewSystem/user/logsystem] The warn message is:->> It's a Warn message
[INFO] [05/11/2023 16:57:25.152] [NewSystem-akka.actor.default-dispatcher-4] [akka://NewSystem/user/logsystem] The info message is:->> It's a Info message
[INFO] [05/11/2023 16:57:25.153] [NewSystem-akka.actor.default-dispatcher-4] [akka://NewSystem/user/logsystem] The current file renamed to :->> newLog.log
[INFO] [05/11/2023 16:57:25.153] [NewSystem-akka.actor.default-dispatcher-4] [akka://NewSystem/user/logsystem] The warn message is:->> It's a Warn message1
[INFO] [05/11/2023 16:57:25.153] [NewSystem-akka.actor.default-dispatcher-4] [akka://NewSystem/user/logsystem] The info message is:->> It's a Info message1
[INFO] [05/11/2023 16:57:25.153] [NewSystem-akka.actor.default-dispatcher-4] [akka://NewSystem/user/logsystem] The info message is:->> The current file is renamed to: ---> newLog.log
[INFO] [05/11/2023 16:57:25.155] [NewSystem-akka.actor.default-dispatcher-4] [akka://NewSystem/user/logsystem] Stopping the process

 */