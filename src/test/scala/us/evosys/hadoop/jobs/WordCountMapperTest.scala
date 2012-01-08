package us.evosys.hadoop.jobs


import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mrunit.mapreduce.MapDriver
import org.apache.hadoop.mapreduce.Mapper
import org.junit.Test
import org.slf4j.{Logger, LoggerFactory}


/**
 * Created by IntelliJ IDEA.
 * User: smishra
 * Date: 1/7/12
 * Time: 9:13 AM
 * To change this template use File | Settings | File Templates.
 */


class WordCountMapperTest extends HImplicits {
  val logger: Logger = LoggerFactory.getLogger(classOf[WordCountMapperTest])
  @Test
  def map = {
    val mapper: Mapper[LongWritable, Text, Text, LongWritable] = new WordCountMapper

    val mapDriver = new MapDriver[LongWritable, Text, Text,LongWritable]()
    mapDriver.getConfiguration.setQuietMode(false)
    mapDriver.setMapper(mapper)

    mapDriver.withInput(1, "world Hello World")
    mapDriver.withOutput("world", 1).withOutput("hello", 1).withOutput("world",1)
    val result = mapDriver.runTest()
    logger.debug("result: {}", result)
  }

}