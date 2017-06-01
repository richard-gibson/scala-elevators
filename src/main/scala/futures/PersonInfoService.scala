package futures

import scala.concurrent.{ duration, Await, Future }
import scala.concurrent.ExecutionContext.Implicits.global
import duration._

case class Person(pin: Int,
                  firstName: String,
                  lastName: String,
                  addressId: Int)

case class Address(id: Int,
                   houseNo: Int,
                   street: String,
                   town: String,
                   postcode: String)

case class Relation(parentId: Int, childId: Int)

case class NotFoundException(e: Exception) extends Exception(e)

case class HouseInfo(address: Address, inhabitants: Seq[Person])

class PersonInfoService extends PersonInfo {

  import PersonInfoModel._

  override def personById(pin: Int): Future[Person] = ???

  override def addressById(id: Int): Future[Address] = ???

  override def addressesInTown(town: String): Future[Seq[Address]] = ???

  override def peopleByAddressId(addressId: Int): Future[Seq[Person]] = ???

  override def livingWithPerson(person: Person): Future[HouseInfo] = ???

  override def allHouseInfo: Future[Seq[HouseInfo]] = ???

  override def livingInTown(person: Person): Future[Seq[Person]] = ???
}

object PersonInfoModel {

  val people = Seq(
    Person(1, "bob", "smyth", 1),
    Person(2, "lynda", "smyth", 1),
    Person(3, "michael", "smyth", 2),
    Person(4, "jane", "smyth", 1),
    Person(5, "ivan", "jones", 3),
    Person(6, "geoff", "jones", 3),
    Person(7, "aaron", "johnston", 4),
    Person(8, "roisin", "hughes", 5),
    Person(9, "gareth", "hughes", 5),
    Person(10, "keith", "hughes", 5),
    Person(11, "barry", "hughes", 5),
    Person(12, "richard", "johnston", 4),
    Person(13, "emma", "johnston", 4),
    Person(14, "ben", "johnston", 4)
  )

  val addresses = Seq(
    Address(1, 145, "park st", "ballymena", "BT29 5JR"),
    Address(2, 20, "kells rd", "ballymena", "BT29 1AJ"),
    Address(3, 1, "river st", "comber", "BT23 5SR"),
    Address(4, 400, "antrim rd", "belfast", "BT15 2AJ"),
    Address(5, 15, "park rd", "belfast", "BT7 3LN")
  )

}
