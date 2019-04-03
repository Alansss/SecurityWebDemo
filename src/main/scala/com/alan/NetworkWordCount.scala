package com.alan

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object NetworkWordCount {
  def main(args: Array[String]) {
    // Create the context with a 1 second batch size
    // 创建SparkConf实例
    val sparkConf = new SparkConf().setAppName("NetworkWordCount").setMaster("local[4]")

    ///创建Spark Streaming Context，每隔1秒钟处理一批数据，那么这一秒收集的数据存放在哪，如何将收集的数据推送出去？是生产者主动推出去还是消费者每隔1秒钟来拉取一次数据
    val ssc = new StreamingContext(sparkConf, Seconds(5))

    val lines = ssc.socketTextStream("localhost",9999)
    //flatMap是把将每一行使用空格做分解，那么words对应的数据结构是怎么样的？
    ///words是个集合，每个集合元素依然是个集合，这个集合存放单词
    val wordCounts = lines.flatMap(_.split(" "))
      .map(x => (x, 1))
      .reduceByKey(_ + _)
    wordCounts.print()
    //启动计算作业
    ssc.start()

    //等待结束，什么时候结束作业，即触发什么条件会让作业执行结束
    ssc.awaitTermination()
  }
}