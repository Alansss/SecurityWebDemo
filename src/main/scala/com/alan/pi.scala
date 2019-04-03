package com.alan

import org.apache.spark.sql.SparkSession

object pi {

  def main(args: Array[String]): Unit = {

    val sc = SparkSession.builder().appName("pi").master("local").getOrCreate().sparkContext

    //将内存数据读入Spark系统中，作为一个整体数据集
    var str = sc.parallelize(Array("One", "Two", "Three", "Four", "Five"))  //创建数据集
    str.foreach(println)

  }

}
