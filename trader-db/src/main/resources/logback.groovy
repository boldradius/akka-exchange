//appender("FILE", FileAppender) {
    //file = "tradedb.log"
    //append = false
    //encoder(PatternLayoutEncoder) {
        //pattern = "[%date{HH:mm:ss.SSS}] %logger{35} @ %level - %msg%n"
    //}
//}

appender("STDOUT", ConsoleAppender) {
    withJansi = true
    encoder(PatternLayoutEncoder) {
        pattern = "[%date{HH:mm:ss.SSS}] %highlight(%-5level) %cyan(%logger{15}) - %msg %n"
    }
}

// not needed for the docker setup for now
//root(DEBUG, ["FILE"])
root(INFO, ["STDOUT"])
