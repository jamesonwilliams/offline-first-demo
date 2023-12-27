package org.nosemaj.cra

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CraApplication

fun main(args: Array<String>) {
    runApplication<CraApplication>(*args)
}
