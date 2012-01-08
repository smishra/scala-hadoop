package us.evosys.hadoop.jobs

import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapreduce.{Reducer, Job, Mapper}
import org.apache.hadoop.conf.{Configured}
import org.apache.hadoop.fs.Path
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.slf4j.{Logger, LoggerFactory}
import scala.collection.JavaConversions._
import org.apache.hadoop.util.{ToolRunner, Tool}


/**
 * Created by IntelliJ IDEA.
 * User: smishra
 * Date: 1/4/12
 * Time: 8:40 AM
 * To change this template use File | Settings | File Templates.
 */


object WordCountDebug extends Configured with Tool with HImplicits {
  val logger: Logger = LoggerFactory.getLogger(getClass)

  def run(args: Array[String]): Int = {
    val conf = getConf
    conf.setQuietMode(false)

    val job: Job = new Job(conf, "Word Count")

    job.setJarByClass(this.getClass)

    job.setMapperClass(classOf[WordCountMapper])
    job.setMapOutputKeyClass(classOf[Text])
    job.setMapOutputValueClass(classOf[LongWritable])

    job.setCombinerClass(classOf[WordCountReducer])
    job.setReducerClass(classOf[WordCountReducer])

    job.setOutputKeyClass(classOf[Text])
    job.setOutputValueClass(classOf[LongWritable])

    for (i <- 0 to args.length - 2) {
      logger.info("processing {}: {}", i, args(i))
      FileInputFormat.addInputPath(job, args.apply(i))
    }

    FileOutputFormat.setOutputPath(job, args.last)

    job.waitForCompletion(true) match {
      case true => 0
      case false => 1
    }
  }

  def main(args: Array[String]) {
    System.exit(ToolRunner.run(this, args))
  }

}

class WordCountMapper extends Mapper[LongWritable, Text, Text, LongWritable] with HImplicits {
  val logger: Logger = LoggerFactory.getLogger(getClass)

  protected override def map(lnNumber: LongWritable, line: Text, context: Mapper[LongWritable, Text, Text, LongWritable]#Context): Unit = {
    logger.debug("processing line {} ", line)
    line.toLowerCase().split(" ") foreach (context.write(_, 1))

    /* samething using map
    line.toLowerCase().split(" ").map(t =>
      {
        logger.trace("token: {}", t)
        context.write(t, 1)
      })
    */
  }
}

class WordCountReducer extends Reducer[Text, LongWritable, Text, LongWritable] with HImplicits {
  val logger: Logger = LoggerFactory.getLogger(getClass)

  protected override def reduce(key: Text, value: java.lang.Iterable[LongWritable], context: Reducer[Text, LongWritable, Text, LongWritable]#Context): Unit = {
    logger.debug("reducing {}", key)
    context.write(key, value.reduceLeft(_ + _))
    //OR context.write(token, counts.foldLeft(0)((res, curr) => res + curr.toInt))
  }
}







