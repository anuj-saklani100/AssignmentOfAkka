package AkkaActorAssignments

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object BankSystem extends App{
val system=ActorSystem("BankSystem")

  case class Deposit(amount:Int)
  case class Withdraw(amount:Int)
  case object GetBalance
  case class Successful(message:String)
  case class Denied(message:String)
  class Bank extends Actor{
    var balance=0     // var type because we have to update it so it should be mutable
    override def receive: Receive ={
      case Deposit(amount)=>{
        // let's build some different scenarios
        //1. if the bank has negative balance so, no amount will be deposited
        if(amount<0){
          sender () ! Denied("Transaction denied dye to negative balance!")
        }else{
          balance += amount     // update the balance
          sender() ! Successful(s"Amount: $amount Deposit Successfully")
        }
      }
      // case for the amount withdrawl
      case Withdraw(amount)=>{
        if (amount>balance) {
          sender() ! Denied("Insufficient balance")
        } else {
          balance -= amount // update the balance
          sender() ! Successful(s"Withdrawal: $amount Successfully")
        }
      }
      // case to fetch the current balance
      case GetBalance=> sender ! (s"Current Balance:=> $balance")
    }
  }


  // Person actor to communicate to the bank actor
  case class calculate(ref:ActorRef)    // for communication we need the actor reference
  class Person extends Actor{
    override def receive: Receive = {
      case calculate(ref)=>{
        // Lets give a positive input
        ref ! Deposit(10000)
        // lets check the balance
        ref ! GetBalance
        // lets give a negative input
        ref ! Withdraw(90000)   // withdraw is not even possible
        // lets give a positive input
        ref ! Withdraw(800)    // withdraw is possible
        // lets check the new balance
        ref ! GetBalance
      }
      case message=> println(message)
    }
  }

  // now initiating the actors
  val bank=system.actorOf(Props[Bank],"bank")
  val person=system.actorOf(Props[Person],"person")
  person ! calculate(bank)

/*   --------Expected Output: ---------
Successful(Amount: 10000 Deposit Successfully)
Current Balance:=> 10000
Denied(Insufficient balance)
Successful(Withdrawal: 800 Successfully)
Current Balance:=> 9200

 */

  // to terminate the process we can use
  // system.terminate()
}
