package com.colegio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Aplicación principal del Sistema de Gestión de Colegio Profesional
 *
 * @author Sistema de Colegio Profesional
 * @version 1.0.0
 */
@SpringBootApplication
@EnableScheduling
public class ColegioApplication {

    public static void main(String[] args) {
        SpringApplication.run(ColegioApplication.class, args);
    }
}
