package AkkaActorAssignments

// The whole code is similar just we need to handle the exception by the help of Supervision
// --------   So here i am Restarting the stuff if i encounter any exception  -----------
import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor.{Actor, ActorLogging, ActorSystem, OneForOneStrategy, Props}

import java.io.{File, FileWriter}

// companion object for LoggerActor
object LoggerActor1{
  case class LogWarn(message:String)
  case class LogInfo(message:String)
  case class Rename_File(name:String)
}
// logging actor
class LoggerActor1 extends Actor with ActorLogging{
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

  import LoggerActor1._  // import the companion object so that we can use its case classes
val supervisor= OneForOneStrategy(){
  case _ : Exception =>Restart
  case _ : RuntimeException =>Stop
}
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
    case number:Int=>{
    log.info("Invalid case :-> Gonna throw an Exception")
      throw new Exception("Invalid case")
    }
    case mess: String => {
      log.info("Invalid case :-> Gonna throw an Runtime exception")
      throw new RuntimeException("Invalid case")
    }
  }
}



object FileLogWithSupervision extends App{

  import LoggerActor1._

  val system = ActorSystem("NewSystem")
  val logsystem1 = system.actorOf(Props[LoggerActor1], "logsystem1")

  // now send warn and info log messages
  logsystem1 ! LogWarn("It's a Warn message")
  logsystem1 ! LogInfo("It's a Info message")

  // lets rename the file
  logsystem1 ! Rename_File("newLog.log")

  // again send some messges
  logsystem1 ! LogWarn("It's a Warn message1")
  logsystem1 ! LogInfo("It's a Info message1")

  // ------ Error handling ------
  // lets say if I call the actor by wrong value (lets say by any anonymous number,message
 logsystem1 ! 6
  // terminate  the system
  //system.terminate()
}


/*   Output:->>   After hitting the wrong query it will restart again


[INFO] [05/11/2023 17:16:38.326] [NewSystem-akka.actor.default-dispatcher-3] [akka://NewSystem/user/logsystem1] Initiation is started
[INFO] [05/11/2023 17:16:38.326] [NewSystem-akka.actor.default-dispatcher-3] [akka://NewSystem/user/logsystem1] The warn message is:->> It's a Warn message
[INFO] [05/11/2023 17:16:38.326] [NewSystem-akka.actor.default-dispatcher-3] [akka://NewSystem/user/logsystem1] The info message is:->> It's a Info message
[INFO] [05/11/2023 17:16:38.327] [NewSystem-akka.actor.default-dispatcher-3] [akka://NewSystem/user/logsystem1] The current file renamed to :->> newLog.log
[INFO] [05/11/2023 17:16:38.327] [NewSystem-akka.actor.default-dispatcher-3] [akka://NewSystem/user/logsystem1] The warn message is:->> It's a Warn message1
[INFO] [05/11/2023 17:16:38.327] [NewSystem-akka.actor.default-dispatcher-3] [akka://NewSystem/user/logsystem1] The info message is:->> It's a Info message1
[INFO] [05/11/2023 17:16:38.327] [NewSystem-akka.actor.default-dispatcher-3] [akka://NewSystem/user/logsystem1] Invalid case :-> Gonna throw an Exception
[ERROR] [05/11/2023 17:16:38.329] [NewSystem-akka.actor.default-dispatcher-2] [akka://NewSystem/user/logsystem1] Invalid case
java.lang.Exception: Invalid case
  at AkkaActorAssignments.LoggerActor1$$anonfun$receive$1.applyOrElse(FileLogWithSupervision.scala:67)
  at akka.actor.Actor.aroundReceive(Actor.scala:517)
  at akka.actor.Actor.aroundReceive$(Actor.scala:515)
  at AkkaActorAssignments.LoggerActor1.aroundReceive(FileLogWithSupervision.scala:17)
  at akka.actor.ActorCell.receiveMessage(ActorCell.scala:588)
  at akka.actor.ActorCell.invoke(ActorCell.scala:557)
  at akka.dispatch.Mailbox.processMailbox(Mailbox.scala:258)
  at akka.dispatch.Mailbox.run(Mailbox.scala:225)
  at akka.dispatch.Mailbox.exec(Mailbox.scala:235)
  at akka.dispatch.forkjoin.ForkJoinTask.doExec(ForkJoinTask.java:260)
  at akka.dispatch.forkjoin.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1339)
  at akka.dispatch.forkjoin.ForkJoinPool.runWorker(ForkJoinPool.java:1979)
  at akka.dispatch.forkjoin.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:107)

[INFO] [05/11/2023 17:16:38.330] [NewSystem-akka.actor.default-dispatcher-2] [akka://NewSystem/user/logsystem1] Stopping the process
[INFO] [05/11/2023 17:16:38.331] [NewSystem-akka.actor.default-dispatcher-2] [akka://NewSystem/user/logsystem1] Initiation is started
[INFO] [05/11/2023 17:16:38.331] [NewSystem-akka.actor.default-dispatcher-2] [akka://NewSystem/user/logsystem1] The info message is:->> The current file is renamed to: ---> newLog.log
 */







