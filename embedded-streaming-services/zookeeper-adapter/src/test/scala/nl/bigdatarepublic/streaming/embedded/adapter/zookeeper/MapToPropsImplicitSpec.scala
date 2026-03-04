package nl.bigdatarepublic.streaming.embedded.adapter.zookeeper

import nl.bigdatarepublic.streaming.embedded.adapter.zookeeper.MapToPropsImplicit._
import org.scalatest.{FlatSpec, Matchers}

class MapToPropsImplicitSpec extends FlatSpec with Matchers {

  "toProps" should "convert a populated map to a Properties with all entries" in {
    val map = Map("host" -> "localhost", "port" -> "2181", "dataDir" -> "/tmp/zk")
    val props = map.toProps

    props.getProperty("host") shouldBe "localhost"
    props.getProperty("port") shouldBe "2181"
    props.getProperty("dataDir") shouldBe "/tmp/zk"
    props.size() shouldBe 3
  }

  it should "convert an empty map to an empty Properties" in {
    val props = Map.empty[String, String].toProps

    props.size() shouldBe 0
  }

  it should "include every key regardless of insertion order" in {
    val keys = (1 to 10).map(i => s"key$i" -> s"value$i").toMap
    val props = keys.toProps

    props.size() shouldBe 10
    keys.foreach { case (k, v) =>
      props.getProperty(k) shouldBe v
    }
  }

  it should "return a java.util.Properties instance" in {
    val props = Map("a" -> "b").toProps

    props shouldBe a[java.util.Properties]
  }

  it should "preserve values that contain special characters" in {
    val map = Map("url" -> "jdbc:postgresql://host:5432/db?user=u&password=p")
    val props = map.toProps

    props.getProperty("url") shouldBe "jdbc:postgresql://host:5432/db?user=u&password=p"
  }
}
