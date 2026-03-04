package nl.bigdatarepublic.streaming.embedded.app

import com.typesafe.config.ConfigFactory
import nl.bigdatarepublic.streaming.embedded.app.ConfigToMapImplicit._
import org.scalatest.{FlatSpec, Matchers}

class ConfigToMapImplicitSpec extends FlatSpec with Matchers {

  // ---------------------------------------------------------------------------
  // toStringMap
  // ---------------------------------------------------------------------------

  "toStringMap" should "convert a flat config into a Map[String, String]" in {
    val config = ConfigFactory.parseString(
      """
        |host = "localhost"
        |port = "2181"
      """.stripMargin)

    val result = config.toStringMap

    result("host") shouldBe "localhost"
    result("port") shouldBe "2181"
  }

  it should "produce an empty map for an empty config" in {
    val result = ConfigFactory.empty().toStringMap

    result shouldBe empty
  }

  it should "strip double-quotes from keys that require quoting (e.g. keys containing dots)" in {
    // HOCON requires keys with dots to be quoted: "x.subkey" = value
    val config = ConfigFactory.parseString(
      """"x.subkey" = 42""")

    val result = config.toStringMap

    // The implicit strips quotes, so the key should be x.subkey not "x.subkey"
    result.keys should contain("x.subkey")
    result.keys should not contain "\"x.subkey\""
  }

  it should "unwrap config values to their string representation" in {
    val config = ConfigFactory.parseString(
      """
        |count = 10
        |flag  = true
        |ratio = 0.5
      """.stripMargin)

    val result = config.toStringMap

    result("count") shouldBe "10"
    result("flag")  shouldBe "true"
    result("ratio") shouldBe "0.5"
  }

  it should "return a Map whose values are all Strings" in {
    val config = ConfigFactory.parseString("""key = "value"""")
    val result = config.toStringMap

    result shouldBe a[Map[_, _]]
    result.values.foreach(_ shouldBe a[String])
  }

  // ---------------------------------------------------------------------------
  // toMap
  // ---------------------------------------------------------------------------

  "toMap" should "convert a flat config into a Map[String, AnyRef]" in {
    val config = ConfigFactory.parseString(
      """
        |name = "kafka"
        |replicas = 3
      """.stripMargin)

    val result = config.toMap

    result("name") shouldBe "kafka"
    result("replicas") shouldBe Integer.valueOf(3)
  }

  it should "produce an empty map for an empty config" in {
    val result = ConfigFactory.empty().toMap

    result shouldBe empty
  }

  it should "strip double-quotes from keys, same as toStringMap" in {
    val config = ConfigFactory.parseString(""""a.b" = 1""")
    val result = config.toMap

    result.keys should contain("a.b")
    result.keys should not contain "\"a.b\""
  }

  it should "return unwrapped (native Java) values rather than ConfigValue wrappers" in {
    val config = ConfigFactory.parseString("""flag = true""")
    val result = config.toMap

    // unwrapped boolean comes back as java.lang.Boolean, not a ConfigValue
    result("flag") shouldBe java.lang.Boolean.TRUE
    result("flag") should not be a[com.typesafe.config.ConfigValue]
  }
}
