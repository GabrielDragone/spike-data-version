package br.com.gabrieldragone.spikedataversion

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class SpikeDataVersionApplication

fun main(args: Array<String>) {
	runApplication<SpikeDataVersionApplication>(*args)
}
