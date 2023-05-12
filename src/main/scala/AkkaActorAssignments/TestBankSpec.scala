package AkkaActorAssignments

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

import scala.concurrent.duration._
class TestBankSpec extends TestKit(ActorSystem("TestBankSpec"))
with ImplicitSender
with WordSpecLike
with BeforeAndAfterAll
{
  // default stuff to shut down the system , so must have to write this
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }
  // import the companion object so that we can use it here
  import TestBankSpec._
  // here we do our testing
"Bank Actor" should {
  val bank = system.actorOf(Props[Bank], "bank")
  val person = system.actorOf(Props[Person], "person")
  "Receive output" in{
 person ! calculate1(bank)
  }
  "receive current balance" in{
    bank ! Status
    val res = expectMsgType[String]
    // testing the output be checking the current balance
    // output: All test case will pass
    assert(res=="Current Balance:=> 1001")
  }
  "withdrawing the amount" in{
    person ! calculate2(bank)
  }
  "check the current amount" in {
    bank ! Status
    val res = expectMsgType[String]
    // testing the output be checking the current balance
    // output: All test case will pass
    assert(res == "Current Balance:=> 901")
  }
  }

}
// creating a companion object
object TestBankSpec{
  case class deposit(amount: Int)

  case class withdraw(amount: Int)

  case object getBalance

  case class Successful(message: String)

  case class Denied(message: String)

  case object Status
  class Bank extends Actor {
    var balance = 0 // var type because we have to update it so it should be mutable

    override def receive: Receive = {
      case deposit(amount) => {
        // let's build some different scenarios
        //1. if the amount is negative so, no amount will be deposited
        if (amount < 0) {
          sender() ! Denied("Transaction denied dye to negative balance!")
        } else {
          balance += amount // update the balance
          sender() ! Successful(s"Amount: $amount Deposit Successfully")
        }
      }
      // case for the amount withdrawl
      case withdraw(amount) => {
        if (amount > balance) {
          sender() ! Denied("Insufficient balance")
        } else {
          balance -= amount // update the balance
          sender() ! Successful(s"Withdrawal: $amount Successfully")
        }
      }
      // case to fetch the current balance
      case getBalance => sender ! (s"Current Balance:=> $balance")
      case Status=> context.self ! s"Your bank balance is: $balance"
    }
  }

  // Person actor to communicate to the bank actor
  case class calculate1(ref: ActorRef) // for communication we need the actor reference
  case class calculate2(ref: ActorRef)
  case class calculate3(ref: ActorRef)
  case class calculate4(ref: ActorRef)
  class Person extends Actor with ActorLogging{
    var updation=0
    override def receive: Receive = {
      case calculate1(ref) => {
        ref ! deposit(1001)
      }
      case calculate2(ref) => {
        ref ! withdraw(100)
      }
      case message => println(message)
    }
  }
}
